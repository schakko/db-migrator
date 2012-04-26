package de.ckl.dbmigration

/**
 * Version entity to make migrations comparable
 */
class Version {
	def major = '0', minor = '0'
	static minor_maxlength = 4
	private version = null

	public Version()
	{
	}

	public Version(BigInteger _version)
	{
		version = _version
	}

	/**
	 * @return BigInteger a version number
	 */
	def get_version() {
		if (version == null) {
			version = new BigInteger(major + minor.padLeft(minor_maxlength, "0"))
		}
		
		return version;
	}
	
	/**
	 * @param other_version compares this version against other_version
	 * @return true if this is higher than other (means larger, newer)
	 */
	def isHigherThan(other_version) {
		return (get_version() > other_version.get_version())
	}
	
	/**
	 * string representation of version
	 * @return
	 */
	def String toString() {
		return get_version()
	}

	def boolean equals(obj) {
		if (obj == null) {
			return false
		}

		if (!(obj instanceof Version)) {
			return false
		}

		return this.toString().equals(obj.toString())
	}
	
	def int hashCode() {
		return get_version()
	}
}
