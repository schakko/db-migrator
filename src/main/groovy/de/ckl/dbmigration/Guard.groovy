package de.ckl.dbmigration

/**
 * Helper class for applying constraints on migration files
 */
class Guard {
	/**
	 * default: allow only migration files with suffix ".sql"
	 */
	def suffix = ".sql"
	
	/**
	 * If a migration file can be applied, it returns true
	 * @param file File object
	 * @param current_version Version object of current installed version
	 * @param file_version Version of given file
	 * @return boolean
	 */
	def is_migration_allowed(file, current_version, file_version) {
		if (!file) {
			return false
		}
		
		if (suffix && suffix.size() > 0) {
			if (!file.getAbsolutePath().endsWith(suffix)) {
				return false
			}
		}
		
		return file_version.isHigherThan(current_version)
	}
}