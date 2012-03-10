package de.ckl.dbmigration.target.mysql
import de.ckl.dbmigration.strategy.Factory
import de.ckl.dbmigration.*

/**
 * Parser for MySQL
 */
class Parser extends AbstractParser {
	def create_custom_opts(cli) {
		// overwrite some help texts and defaults
		cli.u(args:1, longOpt:'username', required:true, argName:'database-username', 'a valid MySQL username (required)')
		cli.c(args:1, longOpt:'command', argName:'command', 'Path to mysql if not in environment path')
		cli.d(args:1, longOpt:'database', required:true, argName:'mysql-database', 'MySQL database (required)')

		return cli
	}
	
	def init_migrator(migrator) {
		return de.ckl.dbmigration.target.Factory.create('mysql', migrator)
	}
}