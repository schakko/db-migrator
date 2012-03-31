package de.ckl.dbmigration.target.mssql
import groovy.util.GroovyTestCase

class ExecutorTest extends GroovyTestCase {
	def o = null
	
	void setUp() {
		o = new Executor()
	}
	
	void test_build_exec_command_defaultOpts() {
		def r = o.build_exec_command("cmd")
		println r
		assertTrue(r.contains("osql"))
		assertTrue(r.contains("-S localhost"))
		assertTrue(r.contains("-U Administrator"))
		assertTrue(r.contains("-Q cmd"))
	}

	void test_build_exec_command_setCustomOpts() {
		o.username = 'username'
		o.host = 'host'
		o.password = 'password'
		o.args = '--ssl-enabled=true'
		def r = o.build_exec_command("cmd", true)

		assertTrue(r.contains("osql"))
		assertTrue(r.contains("-S host"))
		assertTrue(r.contains("-U username"))
		assertTrue(r.contains("-P password"))
		assertTrue(r.contains("--ssl-enabled=true"))
		assertTrue(r.contains("-V 10"))
		assertTrue(r.contains("-Q cmd"))
	}
}
