package de.ckl.dbmigration.target.mysql
import groovy.util.GroovyTestCase

class ExecutorTest extends GroovyTestCase {
	def o = null
	
	void setUp() {
		o = new Executor()
	}
	
	void test_build_exec_command_defaultOpts() {
		def r = o.build_exec_command("cmd")

		assertTrue(r.contains("mysql"))
		assertTrue(r.contains("--host=localhost"))
		assertTrue(r.contains("--password="))
		assertTrue(r.contains("--user=root"))
		assertTrue(r.contains("--vertical"))
		assertTrue(r.contains("--execute=cmd"))
	}

	void test_build_exec_command_setCustomOpts() {
		o.username = 'username'
		o.host = 'host'
		o.password = 'password'
		o.args = '--ssl-enabled=true'
		
		def r = o.build_exec_command("cmd", true)
		assertTrue(r.contains("mysql"))
		assertTrue(r.contains("--host=host"))
		assertTrue(r.contains("--password=password"))
		assertTrue(r.contains("--user=username"))
		assertTrue(r.contains("--ssl-enabled=true"))
		assertTrue(r.contains("--vertical"))
		assertTrue(r.contains("--verbose"))
		assertTrue(r.contains("--execute=cmd"))
	}

	void test_get_linenumber_of_error()
	{
		assertEquals(0, o.get_linenumber_of_error("asdad asdasd"))
		def err = " ERROR 1146 (42S02) at line 1121 in file: '/tmp/migration9211691449145648009.sql': Table 'asd.dd' doesn't exist"
		assertEquals(1121, o.get_linenumber_of_error(err))
	}
}
