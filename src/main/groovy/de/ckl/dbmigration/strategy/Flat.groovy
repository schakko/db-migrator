package de.ckl.dbmigration.strategy
import de.ckl.dbmigration.Version

/**
 * This strategy can be used for migration scripts which resides in flat directory structure:
 * <pre>
 * migrations/
 *	20120102-001.sql
 * 	20120105-001.sql
 *	20120215-001.sql
 * </pre>
 * It is important that you use the same file naming structure on every migration script!
 */
class Flat {
	/**
	 * finds all unapplied migrations since a give version
	 * @param version current installed migration version
	 * @param dir directory in which all migrations are located
	 * @param guard guard object which cheks for constraints
	 * @return hash[<Version>:<String:path-of-migration-script>]
	 */
	def find_unapplied_migrations_since(version, dir, guard) {
		def r = [:]
		
		dir.eachFile{
			file ->	
				def name = file.getName(), _major = '', _minor = '';
				name.find(/(\d*)([-|_])/) { match, arg, sep -> _major = arg }
				name.find(/([-|_])(\d*)/) { match, sep, arg -> _minor = arg }

				def file_version = [major: _major, minor: _minor] as Version
println file.getAbsolutePath()
				// suffix allowed or other constraints?
				if (guard.is_migration_allowed(file, version, file_version)) {
					r[file_version] = file.getAbsolutePath()
					println "[migration] " + name + " is a potential candidate for migration"
				}
		}
		
		return r
	}
}
