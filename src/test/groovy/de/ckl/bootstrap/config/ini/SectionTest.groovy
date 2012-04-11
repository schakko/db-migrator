package de.ckl.boostrap.config.ini

import groovy.util.GroovyTestCase
import de.ckl.bootstrap.config.ini.*

class SectionTest extends GroovyTestCase {
	def o = null
	
	void setUp() {
		o = new Section()
	}

	void test_find_in_overwritten_value_in_section_with_parent() {
		o.params["testkey"] = "testvalue"
		o.parents.push(new Section(params: ["testkey":"failvalue"]))

		assertEquals("testvalue", o.find("testkey"))
	}

	void test_defaults_to_null_if_key_is_not_found() {
		o.params["key"] = "value"
		assertEquals(null, o.find("unavailable_key"))
	}

	void test_hierarchy_of_grandfather_father_son() {
		def grandfather = new Section(name: "grandfather", params: ["testkey": "value"]);
		def father = new Section(name: "father", parents: [grandfather])
		o.parents = [father]
		o.name = "son"

		assertEquals("value", o.find("testkey"))
	}

	void test_circularity_fails_with_exception() {

		def b = new Section(parents: [o])
		o.parents = [b]

		try {
			o.find("bla")
			fail("should throw an IllegalStateException")
		}
		catch (e) {
			assert e in IllegalStateException
		}
	}
}
