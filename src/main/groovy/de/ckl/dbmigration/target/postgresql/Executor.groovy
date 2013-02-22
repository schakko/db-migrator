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
		def r = []
		r.push(command)
		
		// -F => Set field separaotr, default is "|"
		r.push("-F=';'")

		if (host) {
			r.push("--host=" + host)
		}
		
		if (username) {
			r.push("--username=" + username)
		}

		if (!password)	{
			// -w => never ask for password
			r.push("-w")
		}
		
		if (args) {
			r.push(args)
		}

		if (database) {
			r.push(" --dbname=" + database)
		}

		return r
	}
	
	/**
	 * Executes the given path as a MSSQL script
	 * @param path absolute path to the SQL script
	 * @return Output from command line
	 */
	def exec_file(path) {
		def r = build_exec_command_default(true)
		r.push(" --file=" + path)

		return exec(r) 
	}
	
	/**
	 * Executes a given command
	 * @return String text from STDOUT
	 * @throws Exception if exitValue != 0
	 */
    def exec(cmd) {
		println "[command] executing: " + cmd
		
		def joinedCommand = cmd.join(" ")
		def processBuilder = (System.getProperty("os.name").toLowerCase().contains("win")) ? new ProcessBuilder(joinedCommand) : new ProcessBuilder("sh", "-c", joinedCommand)
		
		// sending the password through a stream does not work because of some weird reasons
		// we have to set the environment variable PGPASSWORD (as described in man page) to pass the password to the SQL command
		if (password) {
			processBuilder.environment().put("PGPASSWORD", password)
		}
		
		processBuilder.environment().put("ON_ERROR_STOP", "1")
		processBuilder.redirectErrorStream(true)
		
		def proc = null, text = "", err = ""

		try {
			proc = processBuilder.start()
			proc.waitFor()
			text = proc.text
			err = proc.err.text
		}
		catch (Exception e) {
			println joinedCommand + " exits with value: " + proc.exitValue()
		
			if (proc.exitValue() > 0) {
				if (err)
					throw new Exception(e.getMessage() + ": " + err)
				else
					throw new Exception(e.getMessage() + ": Command did not exit normal but although did not return any error text. Is the executed command correct? Normal text stream follows:" + text)
			}
		}
		
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
