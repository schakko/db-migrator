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
	 * @param unapplied_migrations hash[Version:{path, insert_sql_migration}] migrations to apply. "insert_sql_migrations "
	 * 		defines if an INSERT statement should be appended after a migration script was added to the temporary migration script
	 */
	def prepare(unapplied_migrations) {
		def size = unapplied_migrations.size()

		def keys = unapplied_migrations.keySet().sort { a, b -> a.isHigherThan(b) ? 1 : -1}
		
		keys.each{
			version ->
				++total_migrations
				def file = unapplied_migrations[version]
				def inFile = new File(file.path)
				println "\033[1;33m[migration] " + total_migrations + " / " + size + " " + inFile.getName() + " scheduled for applying\033[0m"
				def content = inFile.readLines()
				pw.println("-- db-migrator:FILE:" + inFile.getAbsolutePath())

				content.each{line -> 
					pw.println(line)
				}
				
				if (file.sql_insert_migration) {
					pw.println("INSERT INTO migrations (major, minor, filename) VALUES('" + version.major + "', '" + version.minor + "', '" + inFile.getName() + "');") 
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
