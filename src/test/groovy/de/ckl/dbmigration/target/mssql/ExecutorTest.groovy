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
		assertTrue(r.matches(/osql\s+\-S\s+localhost\s+\-U\s+Administrator\s+\-Q\s+\"cmd\"\s+/));
	}

	void test_build_exec_command_setCustomOpts() {
		o.username = 'username'
		o.host = 'host'
		o.password = 'password'
		o.args = '--ssl-enabled=true'
		
		def r = o.build_exec_command("cmd", true)

		assertTrue(r.matches(/osql\s+\-S\s+host\s+\-U\s+username\s+\-P\s+password\s+\-\-ssl\-enabled=true\s+\-V\s+10\s+\-Q\s+\"cmd\"\s+/));
	}
}
