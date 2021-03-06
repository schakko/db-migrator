package de.ckl.dbmigration.target.mssql
import de.ckl.dbmigration.Version

/**
 * Interface for SQL routines
 */
class DBInterface {
	class FilterException extends Exception {
	}
	
	def executor = null,
		sql_major_col = 'major',
		sql_minor_col = 'minor',
		sql_latest_migration = 'SELECT TOP 1 ' + sql_major_col + ', ' + sql_minor_col + ' FROM migrations ORDER BY ' + sql_major_col + ' DESC, ' + sql_minor_col + ' DESC',
		sql_create_migration = 'CREATE TABLE migrations(id INT NOT NULL PRIMARY KEY IDENTITY, installed_on DATETIME NOT NULL DEFAULT getDate(), ' + sql_major_col + ' char(8), ' + sql_minor_col + ' char(8), filename nvarchar(max))'
	
	/**
	 * Returns the latest migration found in database
	 *
	 * @return Verson object
	 * @throws Exception if major/minor version was not correctly returned from migrations table
	 * @throws Exception if migrations table could not be created
	 * @throws Exception if the SQL statements failed or anything else happened
	 */
	def find_latest_migration() {
		try {
			def lines = executor.exec_command(sql_latest_migration).split("\n")
			def _major = '0', _minor = '0'

			if (lines.size() >= 5) {
				if (!lines[0].matches(/\s+$sql_major_col\s+$sql_minor_col/)) {
					throw new FilterException()
				}
				
				lines[2].find(/\s+(\d*)\s(\d+)/){ match, major, minor -> 
					_major = major 
					_minor = minor
				}
			}
			
			def r = [major: _major, minor: _minor] as Version
			
			return r
		}
		catch (e) {
			println "[error] could not retrieve latest revision from database: " + e.getMessage()
			
			if (e instanceof FilterException) {
				throw new Exception("Could not filter output")
			}
			
			println "[create] trying to create migration table ..."

			try { 
				executor.exec_command(sql_create_migration)
				println "[create] migrations table successfully created"
				
				return find_latest_migration()
			}
			catch (eCreate) {
				throw new Exception("Could not create migrations table: " + eCreate.getMessage())
			}
		}
	}
}