import de.ckl.dbmigration.Version
import de.ckl.dbmigration.target.mssql.*
import de.ckl.dbmigration.Migrator

try {
	def migrator = new Parser().parse(args, new Migrator())

	// Migration options
	migrator.run()
	
	/*
	// a migration script dedicated to your project
	def latest_migration = migrator.find_latest_migration()
	println "[migration] Current installed migration: " + latest_migration

	println "[migration] Applying schema changes..."
	unapplied_migrations = migrator.find_flat_unapplied_migrations_since(latest_migration, 'migrations')
	migrator.applier.apply(unapplied_migrations, false)
	
	println "[migration] Applying core-data changes..."
	unapplied_coredata = migrator.find_flat_unapplied_migrations_since(latest_migration, 'coredata')
	migrator.applier.apply(unapplied_coredata, true)
	*/
}
catch (e) {
	println e
	println "Check your arguments and retry"
}