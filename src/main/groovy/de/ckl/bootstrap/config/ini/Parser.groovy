package de.ckl.bootstrap.config.ini

/**
 * a simple .ini file parser for PHP ini files.
 * This parser supports inheritance like ([child_section : father_section]) and skips comment lines started by a semicolon.
 * Comments after a parameter are *not* filtered
 */
class Parser {
	def use_section = "__global__"
	def file = null
	def sections = [:]
	def cache = [:]
	def is_parsed = false

	/**
	 * Returns the key from ini file
	 * @param key Name of key
	 * @param default_to fallback to this value if key can not be found
	 * @return string
	 */ 
	def get(key, default_to = null) {
		if (!is_parsed) {
			sections = parse(file.getText())	
			is_parsed = true
		}

		if (cache[key]) {
			return cache[key]
		}

		def r = default_to

		if (sections.containsKey(use_section)) {
			def found = sections[use_section].find(key, [])

			if (found) {
				r = found
			}
		}

		cache[key] = r

		return r
	}

	/**
	 * parses a couple of lines
	 * @param lines multiple lines
	 * @return Map<String, Section>
	 */
	def parse(lines) {
		def r = [:]
		def last_section = new Section("name": "__global__")

		lines.eachLine{line ->
			line = line.trim()
			
			if (line.startsWith(";")) {
				return
			}

			if (line.startsWith("[")) {
				r[last_section.name] = last_section
				last_section = new Section("parents": [r["__global__"]])
				def idxSep = 0

				if ((idxSep = line.indexOf(":")) < 0) {
					idxSep = line.indexOf("]")
				}
				else {
					line.substring((idxSep + 1), line.indexOf("]")).split(",").each{parent ->
						def curparent = r[parent.trim()];
						last_section.parents.push(curparent)
					}
				}

				last_section.name = line.substring(1, idxSep).trim()
				return
			}

			def idxSep  = line.indexOf("=")

			if (idxSep > 0) {
				def key = line.substring(0, idxSep).trim()
				def value = line.substring(++idxSep).trim()
	 
				last_section.params[key] = value
			}
		}

		r[last_section.name] = last_section

		return r
	}

	/**
	 * replaces parameters like ${key_name}
	 * @param string
	 * @param default_to fallback if key can not be found
	 * @return string
	 */
	def replace(string, default_to = null) {
		def bla = string.replaceAll(/(\$\{([^\}]*)\})/, {	
			return get(it[2], default_to)
		})
	}
}
