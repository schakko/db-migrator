package de.ckl.dbmigration.target.postgresql
import groovy.util.GroovyTestCase

class ExecutorTest extends GroovyTestCase {
	def o = null
	
	void setUp() {
		o = new Executor()
	}

	void test_build_exec_command_defaultOpts() {
//		def r = o.build_exec_command("cmd")
//		assertTrue(r.matches(/mysql\s+\-\-host=localhost\s+\-\-password=\s+\-\-user=root\s+\-\-vertical\s+\-e\s+\"cmd\"\s+/));
	}

	void test_build_exec_command_setCustomOpts() {
/*		o.username = 'username'
		o.host = 'host'
		o.password = 'password'
		o.args = '--ssl-enabled=true'
		
		def r = o.build_exec_command("cmd", true)
		assertTrue(r.matches(/mysql\s+\-\-host=host\s+\-\-password=password\s+\-\-user=username\s+\-\-ssl\-enabled=true\s+\-\-vertical\s+\-\-verbose\s+\-e\s+\"cmd\"\s+/));
*/	}
}
