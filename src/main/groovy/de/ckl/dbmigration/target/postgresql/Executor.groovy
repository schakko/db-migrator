package de.ckl.dbmigration.target.postgresql

/**
 * Low-level interface for interacting between the migration layer and the DBMS.
 */
class Executor {
	def host = 'localhost', 
		database = '', 
		username = 'postgres', 
		password = '', 
		command = 'psql', args = ''

	def build_exec_command_default(verbose = false) {
		def sb = new StringBuffer()
		sb.append(command)
		
		sb.append(" -F=';'")

		if (host) {
			sb.append(" --host=")
			sb.append(host)
		}
		
		if (username) {
			sb.append(" --username=")
			sb.append(username)
		}

		if (!password)	{				
			sb.append(" -w")
		}
		
		if (args) {
			sb.append(" ")
			sb.append(args)
			sb.append(" ")
		}

		if (database) {
			sb.append(" --dbname=")
			sb.append(database)
		}

		return sb
	}
	
	/**
	 * Executes the given path as a MSSQL script
	 * @param path absolute path to the SQL script
	 * @return Output from command line
	 */
	def exec_file(path) {
		def sb = build_exec_command_default(true)
		sb.append(" --file=")
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

		// sending the password through a stream does not work because of some weird reasons
		def proc = (password) ? (cmd.execute(['PGPASSWORD=' + password], new File('.'))) : (cmd.execute())

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
		// trouble with getopt & psql - using the "-c 'SQL'" switch does not work under Ubuntu 12.xx and PostgreSQL 9.1
		// each SQL statement has to be executed as a temporary file
		def tempFile = File.createTempFile("psql-stat.sql", ".tmp")
		tempFile.deleteOnExit()
		BufferedWriter out = new BufferedWriter(new FileWriter(tempFile));
		out.write(command);
		out.close();

		def r = exec_file(tempFile.getAbsolutePath())

		return r
	}
}