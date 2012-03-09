package de.ckl.dbmigration.target.mysql

/**
 * A semi-transactional class for executing all found migration
 */
class Applier {
	def dbinterface = null,
		tmpFile = null,
		os = null,
		pw = null
	
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
			
			println "[migration] output will be written to " + tmpFile.getAbsolutePath()
			pw.println("-- migration file was created at " + new Date())
			pw.println("-- all migrations are concated to one big transaction so that a consistent state will be reached after finishing the migration")
			pw.println("SET autocommit = 0;")
			pw.println("START TRANSACTION;")
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
			println "[migration] no migrations available"
			return
		}

		if (latest_only) { 
			println "[migration] only the latest migration will be applied"
			keys = [ks.pop()]
		} else {
			keys = ks
		}
		
		def size = keys.size()
		
	
		def os = null, pw = null, tmpFile = null

		def num = 1
		keys.each{
			key ->
				def inFile = new File(unapplied_migrations[key])
				println "[migration] " + num + " / " + size + " " + inFile.getName() + " scheduled for applying"
				def content = inFile.readLines()
				pw.println("-- " + inFile.getName())

				content.each{line -> 
					pw.println(line)
				}
				
				if (sql_insert_migration) {
					pw.println("INSERT INTO migrations (major, minor, file) VALUES('" + key.major + "', '" + key.minor + "', '" + inFile.getName() + "');") 
				}
				
				num++
		}
	}
	
	/**
	 * closes the temporary file and executes it
	 */
	def commit() {
		pw.println("COMMIT;")
		
		pw.close()
		os.close()

		println dbinterface.executor.exec_file(tmpFile.getAbsolutePath())
		
		tmpFile.delete()
		println "[cleanup] Temporary file containing all statements deleted"
	}
}