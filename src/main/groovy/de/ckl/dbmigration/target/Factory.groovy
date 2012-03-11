package de.ckl.dbmigration.target

import de.ckl.dbmigration.Guard

class Factory {
	static create(targetName, migrator) {
		migrator.guard = new Guard()

		switch (targetName) {
			case 'mysql':
				migrator.dbinterface = new de.ckl.dbmigration.target.mysql.DBInterface()
				migrator.dbinterface.executor = new de.ckl.dbmigration.target.mysql.Executor()
				migrator.applier = [dbinterface: migrator.dbinterface] as de.ckl.dbmigration.target.mysql.Applier
				break
			case 'mssql':
				migrator.dbinterface = new de.ckl.dbmigration.target.mssql.DBInterface()
				migrator.dbinterface.executor = new de.ckl.dbmigration.target.mssql.Executor()
				migrator.applier = [dbinterface: migrator.dbinterface] as de.ckl.dbmigration.target.mssql.Applier
				break
			case 'postgresql':
				migrator.dbinterface = new de.ckl.dbmigration.target.postgresql.DBInterface()
				migrator.dbinterface.executor = new de.ckl.dbmigration.target.postgresql.Executor()
				migrator.applier = [dbinterface: migrator.dbinterface] as de.ckl.dbmigration.target.postgresql.Applier
				break
	
			default:
				throw new Exception("Target name " + targetName + " is not valid")
		}

		return migrator
	}
}
