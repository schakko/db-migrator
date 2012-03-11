package de.ckl.dbmigration.target.postgresql
import de.ckl.dbmigration.strategy.Factory
import de.ckl.dbmigration.*

/**
 * Parser for MSSQL
 */
class Parser extends AbstractParser {
	def create_custom_opts(cli) {
		// overwrite some help texts and defaults
		cli.u(args:1, longOpt:'password', argName:'database-password', 'Password will be passed via environment variable PGPASSWORD; this could be security issue on some systems')
		return cli
	}
	
	def init_migrator(migrator) {
		return de.ckl.dbmigration.target.Factory.create('postgresql', migrator)
	}
}
