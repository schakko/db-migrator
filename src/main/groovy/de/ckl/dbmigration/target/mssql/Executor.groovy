package de.ckl.dbmigration.target.mssql

/**
 * Low-level interface for interacting between the migration layer and the DBMS.
 */
class Executor {
	def host = 'localhost', 
		database = '', 
		username = 'Administrator', 
		password = '', 
		command = 'osql', args = ''

	def build_exec_command_default(verbose = false) {
		def r = []
		r.push(command)
		
		// -S => server
		if (host) {
			r.push("-S " + host)
		}
		
		if (username) {
			// -U => logon ID
			r.push("-U " + username)

			if (password) {
				r.push("-P " + password)
			}
		
		} else {
			// if no username is set, use trusted connection (-E)
			r.push("-E")
		}
		
		r.push(args)

		if (verbose) {
			r.push("-V 10")
		}
		
		// -d => use database name
		if (database) {
			r.push("-d " + database)
		}
		
		return r
	}
	
	/**
	 * creates a string which can be executed
	 * @param cmd SQL statement to execute, use single quotation
	 * @param verbose Enable/Disable verbose mode of mysql command
	 * @return Array
	 */
	def build_exec_command(cmd, verbose = false) {
		def r = build_exec_command_default(verbose)
		
		// -Q => execute SQL command and exit
		r.push("-Q " + cmd)

		return r
	}
	
	/**
	 * Executes the given path as a MSSQL script
	 * @param path absolute path to the SQL script
	 * @return Output from command line
	 */
	def exec_file(path) {
		def sb = build_exec_command_default(verbose)
		// -i => path to SQL file
		sb.append(" -i ")
		sb.append(path)

		return exec(sb.toString()) 
	}
	
	/**
	 * Executes a given command
	 * @return String text from STDOUT
	 * @throws Exception if exitValue != 0
	 */
	def exec(cmd) {
		println "[command] executing: " + cmd
		def proc = cmd.execute()
		def text = proc.text
		def err = proc.err.text
		
		proc.getErrorStream().close()
		proc.getInputStream().close()
		proc.getOutputStream().close()
		
		proc.waitFor()
		
		if (proc.exitValue())
			if (err)
				throw new Exception(err)
			else 
				throw new Exception("Command did not exit normal but although did not return any error text. Is the executed command correct? Normal text stream follows:\n" + text)
		
		return text
	}
	
	/**
	 * Executes a single command
	 * @param command Command to execute
	 * @return String
	 */
	def exec_command(command) {
		return exec(build_exec_command(command))
	}
}
