package de.ckl.dbmigration.target.mysql
import de.ckl.dbmigration.Version

/**
 * Interface for SQL routines
 */
class DBInterface {
	def executor = null,
		sql_major_col = 'major',
		sql_minor_col = 'minor',
		sql_latest_migration = 'SELECT ' + sql_major_col + ', ' + sql_minor_col + ' FROM migrations ORDER BY ' + sql_major_col + ' DESC, ' + sql_minor_col + ' DESC LIMIT 1',
		sql_create_migration = 'CREATE TABLE migrations(id INT NOT NULL auto_increment, installed_on TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, ' + sql_major_col + ' char(8), ' + sql_minor_col + ' char(8), filename longtext, PRIMARY KEY(id))'
	
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

			if (lines.size() >= 3) {
				_major = lines[1].find(/$sql_major_col: (\d*)/){ match, major -> _major = major }
				_minor = lines[2].find(/$sql_minor_col: (\d*)/){ match, minor -> _minor = minor }

				if (!_major || !_minor) {
					throw new Exception("Could not filter major/minor version from returned SQL statement")
				}
			}
			
			def r = [major: _major, minor: _minor] as Version
			
			return r
		}
		catch (e) {
			println "[error] could not retrieve latest revision from database: " + e.getMessage()
			
			if (e.getMessage().trim().matches(".*migrations.*doesn.t.*exist")) {
				println "[create] migrations table does not exist... creating"
				
				try {
					executor.exec_command(sql_create_migration)
					println "[create] migrations table successfully created"
				}
				catch (eCreate) {
					throw new Exception("Could not create migrations table: " + eCreate.getMessage())
				}
				
				return find_latest_migration()
			}
			else {
				throw new Exception(e.getMessage())
			}
		}
	}
}