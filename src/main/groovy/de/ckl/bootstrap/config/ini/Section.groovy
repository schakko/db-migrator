package de.ckl.bootstrap.config.ini

/**
 * Represents a simple ini section
 */
class Section {
	def parents = []
	def params = [:]
	def name = "unknown"
	
	/**
	 * Retrieves a key inside this section. If key can not be found the parent sections are used
	 * @param key name of key to retrieve
	 * @param array contains the sections which have been alredy search
	 * @return string or null if key could not be found
	 */
	def find(key, already_searched_sections = []) {
		if (already_searched_sections.contains(name)) {
			throw new IllegalStateException("Circularity inside your configuration found (" + already_searched_sections.pop() + " -> " + name + ")")
		}

		already_searched_sections.push(name)

		if (params.containsKey(key)) {
			return params[key]
		}

		def r = null

		parents.reverseEach{parent -> 
			if (r == null) {
				r = parent.find(key, already_searched_sections)
			}
		}
		already_searched_sections.pop()

		return r
	}
}
