package de.ckl.dbmigration.target.mssql
import de.ckl.dbmigration.strategy.Factory
import de.ckl.dbmigration.*

/**
 * Parser for MSSQL
 */
class Parser extends AbstractParser {
	def create_custom_opts(cli) {
		// overwrite some help texts and defaults
		cli.u(args:1, longOpt:'username', argName:'database-username', 'a valid username for Microsoft SQL server. If none is set, a trusted connection (osql parameter -E) is used. If you specify a username, you need to add a password!')
		cli.c(args:1, longOpt:'command', argName:'command', 'Path to osql if not in environment path')
		cli.d(args:1, longOpt:'database', argName:'database', 'database - can be left if your MSSQL user has a default database set')
		cli.w(args:1, longOpt:'windows-auth', argName:'use-windows-auth', 'use Windows authentication - no user/password must be set')
		return cli
	}

	def set_custom_options_for_migrator(options, migrator) {
	
		if (options.w) {
			migrator.dbinterface.executor.username = ''
			migrator.dbinterface.executor.password = ''
		}
		
		// check for password if username is set, otherwise osql waits on STDIN for password input
		if (migrator.dbinterface.executor.username && !migrator.dbinterface.executor.password) {
			throw new Exception("You must apply a password for your database username!")
		}
		
		return migrator
	}
	
	def init_migrator(migrator) {
		return de.ckl.dbmigration.target.Factory.create('mssql', migrator)
	}
}