package de.ckl.dbmigration
import groovy.util.GroovyTestCase
import de.ckl.dbmigration.strategy.Flat

class MigratorTest extends GroovyTestCase {
	def o = null

	void setUp() {
		o = new Migrator()
		o.guard = new Guard()
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

	void test_merge_migrations_from_directories()
	{
		def pathdef = [o.create_dir_element('../fixtures/merge_directories/unittest'),
					o.create_dir_element('../fixtures/merge_directories/coredata'),
					o.create_dir_element('../fixtures/merge_directories/migrations')]
		o.strategy = new Flat()
		def r = o.merge_migrations_from_directories(pathdef, new Version())
		assertEquals(7, r.size())
		def v = new Version(201206060001)
		assertTrue(r[v].path.endsWith("conflict.sql"))
	}

	void test_merge_migrations_from_directories_correctOrderLatestOnly()
	{
		def pathdef = [o.create_dir_element('../fixtures/merge_directories/unittest'),
					o.create_dir_element('../fixtures/merge_directories/coredata'),
					o.create_dir_element('../fixtures/merge_directories/migrations,latest,true')]
		o.strategy = new Flat()
		def r = o.merge_migrations_from_directories(pathdef, new Version())

		assertEquals(4, r.size())

		assertFalse(r.containsKey(new Version(201201010001)))
		assertFalse(r.containsKey(new Version(201201010002)))
		assertFalse(r.containsKey(new Version(201201040001)))
		assertTrue(r[new Version(201206060001)].path.endsWith("conflict.sql"))
	}

}
