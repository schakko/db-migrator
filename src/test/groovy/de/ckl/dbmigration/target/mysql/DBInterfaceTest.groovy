package de.ckl.dbmigration.target.mysql
import groovy.util.GroovyTestCase

class InterfaceTest extends GroovyTestCase {
	def o = null
	
	void setUp() {
		o = new DBInterface()
	}
	
	void test_find_latest_migration_handleNoConnection()
	{
		o.executor = [ exec_command: {throw new Exception("Unknown database 'bla'") }]
		try {
			o.find_latest_migration()
			assertTrue(false)
		}
		catch (e) {
			assertTrue(e.getMessage().contains("Unknown database"))
		}
	}
	
	void test_find_latest_migration_handleUnknownReturn()
	{
		o.executor = [ exec_command: { return "line1\nline2\nline3" } ]
		
		try {
			def r = o.find_latest_migration()
			fail("Invalid")
		}
		catch(e) {
			assertTrue(e.getMessage().contains("ould not filter"))
		}
	}
	
	void test_find_latest_migration_detectVersion()
	{
		o.executor = [ exec_command: { return "********\nmajor: 20120307\nminor: 1" } ]

		try {
			def r = o.find_latest_migration();
			assertEquals(201203070001, r.get_version())
		}
		catch(e) {
			fail(e.getMessage())
		}
	}

	void test_find_latest_migration_createMigrationTable()
	{
		def call = 1
		
		// state mock
		o.executor = [ exec_command: {
			switch (call) {
				case 1:
					call++ 
					throw new Exception("Table 'migrations' doesn't exist")
				case 2:
					call++
					return 1 // table generated
				case 3:
					return "*******\nmajor: 20120307\nminor: 001"
			}
		}]
		
		try {
			o.find_latest_migration()
			assertTrue(true)
		}
		catch(e) {
			fail("Exception " + e.getMessage() + " not expected")
		}
	}
}