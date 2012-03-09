package de.ckl.dbmigration.target.mysql
import de.ckl.dbmigration.strategy.Factory
import de.ckl.dbmigration.Guard

/**
 * Provied a interface between command line and migration classes.
 * Every command line parameter will be parsed and the needed objects will be created.
 * I chose this class behaviour because the coherence between all classes in this package is very high and every DBMS has its own argments
 */
class Parser {
	/**
	 * Instantiates all needed objects inside the given migrator object
	 *
	 * @param args Command line arguments
	 * @param migrator An instantiated Migrator object
	 * @return Migrator
	 */
	def parse(args, migrator) {
		def cli = new CliBuilder(usage: 'migrate-mysql [options] [dir1[,range[,add_insert]][;dir2[,range[,add_insert]]...]]', header:'"range" can be "all" or "latest". "all" will apply all migrations since the latest available migration in database. "latest" means that the latest version inside the directory is taken.\nIf you set "add_insert", an INSERT statement will automatically created for your migration table. Default is "no", so you have to append the INSERT INTO migrations... in every migration script on your own\n\nOptions')
		cli.u(args:1, longOpt:'username', required:true, argName:'mysql-username', 'a valid MySQL username')
		cli.p(args:1, longOpt:'password', argName:'mysql-password', 'a valid MySQL password')
		cli.c(args:1, longOpt:'command', argName:'mysql-command', 'Path to mysql if not in environment path')
		cli.h(args:1, longOpt:'host', argName:'mysql-host', 'MySQL host')
		cli.d(args:1, longOpt:'database', required:true, argName:'mysql-database', 'MySQL database')
		cli.a(args:1, longOpt:'args', argName:'mysql-args', 'Additional arguments which will added to mysql command')
		cli.S(args:1, longOpt:'suffix', argName:'migration-suffix', 'Use only files with this suffix as migration scripts and ignore other resources (default: .sql)')
		cli.s(args:1, longOpt:'strategy', argName:'migration-strategy', 'can be "flat" or "hierarchial". flat means, that all scripts must be available inside this directory in form of yyyymmdd[-|_]<migration-number>-<name>.suffix. "hierarchial" means a directory structure in form of <major>\\<minor>\\<migration-number>[-|_]<name>.suffix')
		cli.help(longOpt:'help', 'this help')

		def options = cli.parse(args)
		def executor = new Executor()
		def guard = new Guard()
		def dbinterface = new DBInterface()

		if (options == null)
			System.exit(0)
		
		if (options.arguments().size != 0)
			migrator.directories = options.arguments()[0]
		if (options.suffix)
			guard.suffix = options.suffix
		if (options.host) 
			executor.host = options.host
		if (options.password) 	
			executor.password = options.password
		if (options.database)
			executor.database = options.database
		if (options.command)
			executor.command = options.command
		if (options.args)
			executor.args = options.args

		migrator.strategy = Factory.create(options.strategy)
		
		executor.username = options.username
		dbinterface.executor = executor
		migrator.dbinterface = dbinterface
		migrator.guard = guard
		migrator.applier = [dbinterface: dbinterface] as Applier

		return migrator
	}
}