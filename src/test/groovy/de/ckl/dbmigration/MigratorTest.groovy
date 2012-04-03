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

	void test_get_sql_stacktrace_without_magic_tag()
	{
		def sep = System.getProperty("line.separator")
		def r = o.get_sql_stacktrace(new ArrayList(["hello", "world", "iam", "evil", "jared"]), 3, 2, 0)
		assertEquals(["world", "iam", "evil"], r.lines)
		assertNull(r.file)
	}

	void test_get_sql_stacktrace_with_overflow()
	{
		def sep = System.getProperty("line.separator")
		def r = o.get_sql_stacktrace(new ArrayList(["hello", "world", "iam", "evil", "jared"]), 5, 2, 2)
		assertEquals(["iam", "evil", "jared"], r.lines)
		assertNull(r.file)
	}

	void test_get_sql_stacktrace_with_magic_tag()
	{
		def sep = System.getProperty("line.separator")
		def r = o.get_sql_stacktrace(new ArrayList(["hello", "world", "iam", "-- db-migrator:FILE:my.file", "evil", "jared"]), 4, 2, 0)
		assertEquals(["iam", "-- db-migrator:FILE:my.file", "evil"], r.lines)
		assertEquals("my.file", r.file)
	}
}
