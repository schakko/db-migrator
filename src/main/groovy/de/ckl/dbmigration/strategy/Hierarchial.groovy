package de.ckl.dbmigration.strategy
import de.ckl.dbmigration.Version

class Hierarchial {
	def find_unapplied_migrations_since(version, dir) {
		def r = [:]
		
		dir.eachDir{
			majorDirs ->	
				def name = file.getName(), major = name.find(/(\d{$Version.major_maxlength})/), minor = '';
				name.find(/([-|_])(\d{1,$Version.minor_maxlength})/) { match, sep, _minor -> minor = _minor }

				def file_version = [major: major, minor: minor] as Version

				if (file_version.isHigherThan(version)) {
					r[file_version] = file.getAbsolutePath()
					println "[migration] " + name + " is a potentiaal candidate for migration"
				}
		}
		
		return r
	}
}