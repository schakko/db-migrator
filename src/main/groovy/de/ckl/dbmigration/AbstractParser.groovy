package de.ckl.dbmigration
import de.ckl.dbmigration.strategy.Factory
import de.ckl.dbmigration.target.Factory
import de.ckl.dbmigration.Guard
import de.ckl.bootstrap.config.ini.*

/**
 * Provides a interface between command line and migration classes.
 * Every DBMS has it's own parser for applying custom opts.
 */
class AbstractParser {
	def ini_parser = null

	def parse(args, migrator) {
		def cli = new CliBuilder(usage: new File(System.getProperty("script.name")).getName() + ' [options] [dir1[,$range[,$add_insert=[true|false]]][;dir2[,$range[,$add_insert=[true|false]]]...]]', header:'"$range" can be "all" or "latest". "all" will apply all migrations since the latest available migration in database. "latest" means that the latest version inside the directory is taken.\nIf you set "$add_insert" to true, an INSERT statement will automatically created for your migration table. Default is "no", so you have to append the INSERT INTO migrations... in every migration script on your own\n\nIf you use this migration tool with an application which contains the settings inside a .ini file (e.g. Zend Framework) you can pass all information via ${name_of_key} inside your .ini file. You must use the option -i for this\n\nHints for specific DBMS:' + get_additional_header() + '\n\nOptions')

		cli = create_default_opts(cli)
		
		cli = create_custom_opts(cli)

		def options = cli.parse(args)

		if (options == null)
			System.exit(0)
		
		ini_parser = new Parser()

		migrator = init_migrator(migrator)
		
		set_default_options_for_migrator(options, migrator)
		set_custom_options_for_migrator(options, migrator)
		
		migrator.strategy = de.ckl.dbmigration.strategy.Factory.create(options.strategy)
		
		return migrator
	}
	
	def init_migrator() {
		throw new Exception("init_migrator() must be overwritten!")
	}
	
	def get_additional_header() {
		return "None"
	}
	
	def create_default_opts(cli) {
		cli.u(args:1, longOpt:'username', argName:'database-username', 'database username')
		cli.p(args:1, longOpt:'password', argName:'database-password', 'database password')
		cli.c(args:1, longOpt:'command', argName:'command', 'Path to osql if not in environment path')
		cli.h(args:1, longOpt:'host', argName:'database-host', 'Host')
		cli.d(args:1, longOpt:'database', argName:'database', 'database - can be left if default database for user is set')
		cli.a(args:1, longOpt:'args', argName:'args', 'Additional arguments which will added to osql command')
		cli.S(args:1, longOpt:'suffix', argName:'migration-suffix', 'Use only files with this suffix as migration scripts and ignore other resources (default: .sql)')
		cli.s(args:1, longOpt:'strategy', argName:'migration-strategy', 'can be "flat" or "hierarchial". flat means, that all scripts must be available inside this directory in form of yyyymmdd[-|_]<migration-number>-<name>.suffix. "hierarchial" means a directory structure in form of <major>\\<minor>\\<migration-number>[-|_]<name>.suffix')
		cli.i(args:1, longOpt:'ini', argName:'INI configuration file', 'an .ini file from which to read all relevant settings')
		cli.x(args:1, longOpt:'section', argName:'INI section name', 'name of the section in which to look for')

		return cli
	}
	
	def create_custom_opts(cli) {
		return cli
	}

	def resolve_option(string) {
		if (ini_parser.file) {
			return ini_parser.replace(string)
		}

		return string
	}

	def set_default_options_for_migrator(options, migrator) {
		if (options.ini)
			ini_parser.file = new File(options.ini)
		if (options.section)
			ini_parser.use_section = options.section

		if (options.arguments().size != 0)
			migrator.directories = resolve_option(options.arguments()[0])
		if (options.suffix)
			migrator.guard.suffix = resolve_option(options.suffix)
		if (options.host) 
			migrator.dbinterface.executor.host = resolve_option(options.host)
		if (options.password) 	
			migrator.dbinterface.executor.password = resolve_option(options.password)
		if (options.database)
			migrator.dbinterface.executor.database = resolve_option(options.database)
		if (options.command)
			migrator.dbinterface.executor.command = resolve_option(options.command)
		if (options.args)
			migrator.dbinterface.executor.args = resolve_option(options.args)
		if (options.username)
			migrator.dbinterface.executor.username = resolve_option(options.username)
			
		return migrator
	}
	
	def set_custom_options_for_migrator(options, migrator) {
		return migrator
	}
}
