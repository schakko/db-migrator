package de.ckl.dbmigration.target.postgresql
import groovy.util.GroovyTestCase

class InterfaceTest extends GroovyTestCase {
	def o = null
	
	void setUp() {
		o = new DBInterface()
	}
	
	void test_find_latest_migration_handleUnknownReturn()
	{
		o.executor = [ exec_command: { return "line1\nline2\nline3\nline4\nline5" } ]
		
		try {
			def r = o.find_latest_migration()
			fail("Invalid")
		}
		catch(e) {
			assertTrue(e.getMessage().contains("ould not filter"))
		}
	}
	
	void test_find_latest_migration_handleMigrationsTableNotAvailable()
	{
		def state = 1

		o.executor = [ exec_command: { 
			switch (state) {
				case 1:
					state++
					throw new Exception("ERROR:  relation \"migrationss\" does not exist\nLINE 1: SELECT major, minor FROM migrationss LIMIT 1\n")
				case 2:
					state++
					return "ok"
				case 3:
					return " major | minor \n-------+-------\n(0 rows)\n\n"
			}
		}]
		
		try {
			def r = o.find_latest_migration()
			assertEquals(0, r.get_version())
		}
		catch(e) {
			fail(e.getMessage())
		}
	}

	void test_find_latest_migration_noVersionAvailable() {
		o.executor = [ exec_command: { return " major | minor \n-------+-------\n(0 rows)\n\n" } ]
		
		try {
			def r = o.find_latest_migration()
			assertEquals(0, r.get_version())
		}
		catch(e) {
			fail(e.getMessage())
		}
	}

	void test_find_latest_migration_detectVersion()
	{
		o.executor = [ exec_command: { return " major | minor \n-------+-------\n 20120307     | 1\n(1 row)\n" } ]

		try {
			def r = o.find_latest_migration();
			assertEquals(201203070001, r.get_version())
		}
		catch(e) {
			fail(e.getMessage())
		}
	}
}
