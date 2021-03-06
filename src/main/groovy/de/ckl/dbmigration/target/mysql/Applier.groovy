package de.ckl.dbmigration.target.mysql

/**
 * Applies a SQL script in a single transaction to a MSSQL database
 */
class Applier extends de.ckl.dbmigration.target.Applier {
	def append_begin_transaction() {
		pw.println("SET autocommit=0;")
		pw.println("START TRANSACTION;")
	}
	
	def append_commit_transaction() {
		pw.println("COMMIT;")
	}
}