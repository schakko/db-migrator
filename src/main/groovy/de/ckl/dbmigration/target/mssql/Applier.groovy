package de.ckl.dbmigration.target.mssql

/**
 * Applies a SQL script in a single transaction to a MSSQL database
 */
class Applier extends de.ckl.dbmigration.target.Applier {
	def append_begin_transaction() {
		pw.println("BEGIN TRANSACTION;")
	}
	
	def append_commit_transaction() {
		pw.println("COMMIT;")
	}
}