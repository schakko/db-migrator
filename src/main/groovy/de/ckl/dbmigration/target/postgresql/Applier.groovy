package de.ckl.dbmigration.target.postgresql

/**
 * Applies a SQL script in a single transaction to a PostgreSQL database
 */
class Applier extends de.ckl.dbmigration.target.Applier {
	def append_begin_transaction() {
		pw.println("BEGIN;")
	}
	
	def append_commit_transaction() {
		pw.println("COMMIT;")
	}
}
