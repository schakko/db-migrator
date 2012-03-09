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
		command = 'mysql', args = ''

	/**
	 * creates a string which can be executed
	 * @param cmd SQL statement to execute, use single quotation
	 * @param verbose Enable/Disable verbose mode of mysql command
	 * @return String
	 */
	def build_exec_command(cmd, verbose = false) {
		def sb = new StringBuffer()
		sb.append(command)
		
		if (host) {
			sb.append(" --host=")
			sb.append(host)
		}
		
		sb.append(" --password=")
		sb.append(password)
		
		if (username) {
			sb.append(" --user=")
			sb.append(username)
		}
		
		sb.append(" ")
		sb.append(args)
		sb.append(" ")

		sb.append(" --vertical")

		if (verbose) {
			sb.append(" --verbose")
		}
		
		sb.append(" -e ")
		sb.append("\"")
		sb.append(cmd)
		sb.append("\"")
		sb.append(" ")
		sb.append(database)

		return sb.toString()
	}
	
	/**
	 * Executes the given path as a MySQL script
	 * @param path absolute path to the SQL script
	 * @return Output from command line
	 */
	def exec_file(path) {
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
		return exec(build_exec_command(command))
	}
}