package de.ckl.dbmigration
import groovy.util.GroovyTestCase

class MigratorTest extends GroovyTestCase {
	def o = null
	
	void setUp() {
		o = new Migrator()
	}
	
	void test_create_dir_element_handlesInvalidDir()
	{
		def opts = 'invaliddir'
		
		try {
			def r = o.create_dir_element(opts)
			assertTrue(false)
		}
		catch(e) {
			assertTrue(e.getMessage().contains("is not valid"))
		}
	}
	
	void test_create_dir_element_handlesParameterDefaults()
	{
		def opts = '../fixtures' + o.separatorOpts + 'bla' + o.separatorOpts + 'no'
		def r = o.create_dir_element(opts)
		
		assertTrue(r.latest_only == false) // default
		assertTrue(r.files == null)
		assertTrue(r.sql_insert_migration == false)
	}
	
	void test_create_dir_element_handlesCustomParameters()
	{
		def opts = '../fixtures' + o.separatorOpts + 'latest' + o.separatorOpts + 'yes'
		def r = o.create_dir_element(opts)
		assertTrue(r.latest_only == true)
		assertTrue(r.sql_insert_migration == true)
	}
}