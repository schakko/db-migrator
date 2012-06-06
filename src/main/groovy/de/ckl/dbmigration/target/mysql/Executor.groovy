package de.ckl.dbmigration.target.mysql

/**
 * Low-level interface for interacting between the migration layer and the DBMS.
 * For simplification I decided to use the mysql command for interaction.
 */
class Executor {
	def host = 'localhost', 
		database = '', 
		username = 'root', 
		password = '', 
		command = 'mysql',
		args = ''

	/**
	 * creates a string which can be executed
	 * @param cmd SQL statement to execute, use single quotation
	 * @param verbose Enable/Disable verbose mode of mysql command
	 * @return Array
	 */
	def build_exec_command(cmd, verbose = false) {
		def r = []
		r.push(command)
		
		if (host) {
			r.push("--host=" + host)
		}
		
		r.push("--password=" + password)
		
		if (username) {
			r.push("--user=" + username)
		}
		
		if (args) {
			r.push(args)
		}

		// --vertical => Print the output of a query (rows) vertically 
		r.push("--vertical")

		if (verbose) {
			r.push("--verbose")
		}
		
		// --execute => Executes given SQL command
		r.push('--execute=' + cmd) 

		r.push(database)

		return r
	}
	
	/**
	 * Executes the given path as a MySQL script
	 * @param path absolute path to the SQL script
	 * @return Output from command line
	 */
	def exec_file(path) {
		// \.source executes the given file; piping does not always work
		return exec(build_exec_command("\\.source " + path, true)) 
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
		if (!command.endsWith(";")) {
			command = command + ";"
		}

		return exec(build_exec_command(command))
	}

	/**
	 * Returns the line in which the error occured
	 * @param string error
	 * @return integer
	 */
	def get_linenumber_of_error(error) {
		def matcher = (error =~ /ERROR .* at line (\d+)./)
		if (matcher) {
			if (matcher.count > 0) { 
				return matcher[0][1].toInteger()
			}
		}

		return 0
	}
}
