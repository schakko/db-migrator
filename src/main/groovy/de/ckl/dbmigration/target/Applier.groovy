package de.ckl.dbmigration.target

/**
 * A semi-transactional class for executing all found migration
 */
class Applier {
	def dbinterface = null,
		tmpFile = null,
		os = null,
		pw = null,
		total_migrations = 0	

	/**
	 * Creates a temporary file which contains all SQL statements of the migration scripts.
	 *
	 * @throws Exception if concated SQL script could not be created
	 */
	def begin() {
		try {
			tmpFile = File.createTempFile("migration", ".sql")
			os = new FileOutputStream(tmpFile)
			pw = new PrintWriter(os)
			total_migrations = 0
			
			println "[migration] output will be written to " + tmpFile.getAbsolutePath()
			pw.println("-- migration file was created at " + new Date())
			pw.println("-- all migrations are concated to one big transaction so that a consistent state will be reached after finishing the migration")
			append_begin_transaction()
		}
		catch (e) {
			throw new Exception("Sorry, but a temporary file could not be created: " + e.getMessage())
		}
	}
	
	/**
	 * Prepares all unapplied migrations and copies the content of the migration scripts to the temporary migration script
	 * @param unapplied_migrations hash[Version:path] migrations to apply
	 * @param latest_only boolean if only the latest/newest migration should be applied or all scripts
	 * @param sql_insert_migration boolean if a INSERT statement should be appended after a migration script was added to the temporary migration script
	 */
	def prepare(unapplied_migrations, latest_only = false, sql_insert_migration = true) {
		def ks = unapplied_migrations.keySet().sort { a, b -> a.isHigherThan(b) ? 1 : -1}
		def keys = null
		
		if (ks.size() == 0) {
			println "\033[1;32m[migration] no migrations available - project is up-to-date :-) \033[0m"
			return
		}

		if (latest_only) { 
			println "[migration] only the latest migration will be applied"
			keys = [ks.pop()]
		} else {
			keys = ks
		}
		
		def size = keys.size()
		
		keys.each{
			key ->
				++total_migrations
				def inFile = new File(unapplied_migrations[key])
				println "\033[1;33m[migration] " + total_migrations + " / " + size + " " + inFile.getName() + " scheduled for applying\033[0m"
				def content = inFile.readLines()
				pw.println("-- db-migrator:FILE:" + inFile.getAbsolutePath())

				content.each{line -> 
					pw.println(line)
				}
				
				if (sql_insert_migration) {
					pw.println("INSERT INTO migrations (major, minor, filename) VALUES('" + key.major + "', '" + key.minor + "', '" + inFile.getName() + "');") 
				}
		}

		return true
	}
	
	/**
	 * closes the temporary file and executes it
	 */
	def commit() {
		append_commit_transaction()
		
		pw.close()
		os.close()

		println dbinterface.executor.exec_file(tmpFile.getAbsolutePath())

	}

	def cleanup() {
		tmpFile.delete()		
		println "[cleanup] Temporary file containing all statements deleted"
	}

	def append_begin_transaction() {
	}
	
	def append_commit_transaction() {
	}

}
