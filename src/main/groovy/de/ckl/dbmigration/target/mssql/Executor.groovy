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
		def sb = new StringBuffer()
		sb.append(command)
		
		if (host) {
			sb.append(" -S ")
			sb.append(host)
		}
		
		if (username) {
			sb.append(" -U ")
			sb.append(username)

			if (password) {
				sb.append(" -P ")
				sb.append(password)
			}
		
		} else {
			// if no username is set, use trusted connection
			sb.append(" -E")
		}
		
		sb.append(" ")
		sb.append(args)
		sb.append(" ")

		if (verbose) {
			sb.append(" -V 10")
		}
		
		if (database) {
			sb.append("-d ")
			sb.append(database)
		}
		
		return sb
	}
	
	/**
	 * creates a string which can be executed
	 * @param cmd SQL statement to execute, use single quotation
	 * @param verbose Enable/Disable verbose mode of mysql command
	 * @return String
	 */
	def build_exec_command(cmd, verbose = false) {
		def sb = build_exec_command_default(verbose)
		
		sb.append(" -Q ")
		sb.append("\"")
		sb.append(cmd)
		sb.append("\"")
		sb.append(" ")

		return sb.toString()
	}
	
	/**
	 * Executes the given path as a MSSQL script
	 * @param path absolute path to the SQL script
	 * @return Output from command line
	 */
	def exec_file(path) {
		def sb = build_exec_command_default(verbose)
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