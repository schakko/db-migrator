package de.ckl.dbmigration.target.mysql
import groovy.util.GroovyTestCase
import de.ckl.dbmigration.*

class GuardTest extends GroovyTestCase {
	def o = null
	
	void setUp() {
		o = new Guard()
	}

	void test_is_migration_allowed()
	{
		def current = [major: '20120307', minor:'001'] as Version, file = [major: '20120308', minor:'0001'] as Version
		
		o.suffix = null
		assertTrue(o.is_migration_allowed(new File('../fixtures/mysql/flat/200001_001.sql'), current, file))
		
		o.suffix = '.sql'
		assertTrue(o.is_migration_allowed(new File('../fixtures/mysql/flat/200001_001.sql'), current, file),)
		assertFalse(o.is_migration_allowed(new File('../fixtures/mysql/flat/200001_002.ignored'), current, file))
	}
}