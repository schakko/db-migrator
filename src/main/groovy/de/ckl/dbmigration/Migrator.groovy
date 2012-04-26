package de.ckl.dbmigration

/**
 * Main entry class for running all migrations which have to be applied.
 */
class Migrator {
	def dbinterface = null, 
	
		/**
		 * applier concats all migration scripts and delegates the execution to the executor object
		 */
		applier = null,

		/**
		 * directory/file layout strategy for all migration files
		 */
		strategy = null,
		
		/**
		 * allow/disallow migration scripts
		 */
		guard = null,

		/**
		 * separator for command line options
		 */
		separatorPath = ";",
		separatorOpts = ",",
		directories = '.' + separatorOpts + 'all' + separatorOpts + 'false'
	
	/**
	 * splits every path and tries to find unapplied migrations
	 */
	def run() {
		def dirs = directories.split(separatorPath)
		def version = dbinterface.find_latest_migration(), use_version = version
		
		def stack = []
		
		for (pathdef in dirs) {
			stack.push(create_dir_element(pathdef))
		}
		
		def merged_migrations = merge_migrations_from_directories(stack, version)
	
		if (merged_migrations.size() == 0) {
			println "\033[1;32m[migration] no migrations available - project is up-to-date :-) \033[0m"
			return
		}

		applier.begin()
		applier.prepare(merged_migrations)

		try {
			applier.commit()
			applier.cleanup()
			println "\033[1;32m[migration] " + applier.total_migrations + " migrations applied. Project is now up-to-date :-)"
		}
		catch (e) {
			println "\033[1;31m[error] Migration failed: " + e.getMessage()

			if (dbinterface.executor.getClass().metaClass.hasMetaMethod("get_linenumber_of_error")) {
				def errorInLine = dbinterface.executor.get_linenumber_of_error(e.getMessage())
				def stacktrace = get_sql_stacktrace(applier.tmpFile.readLines(), (errorInLine - 1), 5, 2)

				println "\033[1;31m[debug] error occured somewhere in file: " + stacktrace.file 
				println "\033[0m... lines of aggregated SQL script ..."
				def curLine = stacktrace.beginline

				stacktrace.lines.each{ item ->
					if (curLine == errorInLine) {
						print "\033[1;31m"
					}

					println "\t" + curLine + "\t" + item  
					curLine++
				}
				println ""
			}

			println "\033[1;31m[error] SQL-script has not been deleted for debugging purposes (" + applier.tmpFile.getAbsolutePath() + ")"
		}

		print "\033[0m"
	}

	def get_sql_stacktrace(lines, idx, lines_before, lines_after) {
		def output = [ ],
			endline = ((idx + lines_after) > (lines.size() - 1)) ? (lines.size() - 1) : (idx + lines_after),
			beginline = ((endline - lines_before) < 0) ? 0 : (endline - lines_before), 
			sep = System.getProperty("line.separator"),
			referenceFile = null 

		while (endline >= 0) {
			def currentline = lines.get(endline)

			if (endline >= beginline) {
				output.add(0, currentline)
			}

			def matcher = currentline =~ /db-migrator:FILE:(.*)/
			if (matcher) {
				referenceFile = matcher[0][1]
			}

			if ((endline <= beginline) && matcher) {
				break
			}

			endline--
		}

		return ["lines": output, "file": referenceFile, "beginline": ++beginline]
	}
	
	/**
	 * Merges multiple directories together so that one hashmap with all versions to apply is returned
	 * @param array of pathdef
	 * @param version current Version
	 * @return hash[Version:path]
	 */
	def merge_migrations_from_directories(directories, version) {
		def r = [:]

		for (pathdef in directories) {
			println "[merging] Searching directory '" + pathdef.dir.getName() + "'"
			
			// if latest file should be used, the latest migration inside the database is not relevant
			def use_version = pathdef.latest_only ? (new Version()) : version
  
			def candidates = strategy.find_unapplied_migrations_since(use_version, pathdef.dir, guard)
			
			def ks = candidates.keySet().sort { a, b -> a.isHigherThan(b) ? 1 : -1}
			def versions = [:]
		
			if (ks.size() == 0) {
				continue
			}

			if (pathdef.latest_only) { 
				println "[migration] only the latest migration will be applied"
				def latest_version = ks.pop()

				versions[latest_version] = candidates[latest_version]
			} else {
				versions = candidates
			}

			versions.each { key, value -> 				
				if (r.containsKey(key)) {
					println "\033[1;33m[merging] You have a duplicate in both directories: " + r[key].path + " <-> " + value + ". First one will be used. \033[0m"
				}
				else {
					r[key] = [path:value, sql_insert_migration: pathdef.sql_insert_migration]
				}
			}
		}

		return r
	}

	/** 
	 * @param pathdef a path definition which must have the format "path[,range[add_insert]]". 
	 *				"range" can be "all" (apply *every* migration since the installed migration)
	 *				or "latest" (apply the latest/newest migration since the installed migration).
	 *				add_insert can be (true,1,enabled,on,yes,y) which means that to concated SQL script an INSERT statement 
	 *				for the migration table will be appended. You don't have to worry about writing the INSERT statements on your own.
	 * @return hash[dir: <File>, latest_only: <boolean>, files: null, sql_insert_migration: <boolean>]
	*/
	def create_dir_element(pathdef) {
		def opts = pathdef.split(separatorOpts)
		def dir = locate_migration_dir(opts[0])
		
		def latest_only = false
		def sql_insert_migration = false
			
		if (opts.size() >= 2) {
			if (opts[1] == 'latest') {
				latest_only = true
			}
			
			if (opts.size() >= 3) {
				if (opts[2] in ['1', 'true', 'enabled', 'on', 'yes', 'y']) {
					sql_insert_migration = true
				}
			}
		}
		
		return [dir: dir, latest_only: latest_only, files: null, sql_insert_migration: sql_insert_migration]
	}
	
	/**
	 * returns a File object from given path
	 * @param path c:\temp or /tmp ...
	 * @return File
	 * @throws Exception if directory is not available
	 */
	def locate_migration_dir(path) {
		def r = new File(path)
		
		if (!r.isDirectory()) {
			throw new Exception("The path " + r.getAbsolutePath() + " is not valid")
		}
		
		return r
	}
}
