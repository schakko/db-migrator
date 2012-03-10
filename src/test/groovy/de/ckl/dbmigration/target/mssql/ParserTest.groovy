package de.ckl.dbmigration.target.mssql
import groovy.util.GroovyTestCase
import de.ckl.dbmigration.*
import de.ckl.dbmigration.strategy.*

class ParserTest extends GroovyTestCase {
	def o = null
	
	void setUp() {
		o = new Parser()
	}
	
	void test_parse_defaultSettings() {
		def r = o.parse(['--username=user', '--database=database'], new Migrator())
		
		assertEquals('.' + r.separatorOpts + 'all' + r.separatorOpts + 'false', r.directories)
		assertEquals('user', r.dbinterface.executor.username)
		assertEquals('', r.dbinterface.executor.password)
		assertEquals('mysql', r.dbinterface.executor.command)
		assertEquals('database', r.dbinterface.executor.database)
		assertTrue(r.strategy instanceof Flat)
		assertEquals('', r.dbinterface.executor.args)
		assertTrue(r.guard instanceof Guard)
		assertEquals('.sql', r.guard.suffix)
	}
	
	void test_parse_customSettings() {
		def r = o.parse(['--username=user', 
			'--database=database', 
			'--command=mysql-custom-command', 
			'--password=custom-password',
			'--host=remote-host',
			'--args="--custom-mysql-args=bla"',
			'--suffix=.bla',
			'--strategy=hierarchial',
			'c:/temp,latest,true;'
		], new Migrator())
		
		assertEquals('c:/temp' + r.separatorOpts + 'latest' + r.separatorOpts + 'true;', r.directories)
		assertEquals('user', r.dbinterface.executor.username)
		assertEquals('custom-password', r.dbinterface.executor.password)
		assertEquals('mysql-custom-command', r.dbinterface.executor.command)
		assertEquals('database', r.dbinterface.executor.database)
		assertTrue(r.strategy instanceof Hierarchial)
		assertEquals('--custom-mysql-args=bla', r.dbinterface.executor.args)
		assertTrue(r.guard instanceof Guard)
		assertEquals('.bla', r.guard.suffix)
	}
}