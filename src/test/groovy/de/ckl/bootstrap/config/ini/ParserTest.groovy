package de.ckl.boostrap.config.ini

import groovy.util.GroovyTestCase
import de.ckl.bootstrap.config.ini.*

class ParserTest extends GroovyTestCase {
	def o = null
	def sep = ""

	void setUp() {
		o = new Parser()
		sep = System.getProperty("line.separator")
	}

	void test_ignore_comment_lines_and_parse_global_section() {
		def config = ";comment = 1" + sep + "key = value_before" + sep + "; key = failvalue" + sep + "key = value_after" + sep + "key2 = param2  "
		def r = o.parse(config)

		assertEquals(1, r.size())
		assertTrue(r.containsKey("__global__"))
		assertEquals(2, r["__global__"].params.size())
		assertEquals("value_after", r["__global__"].params["key"]);
		assertEquals("param2", r["__global__"].params["key2"]);
	}

	void test_inherit_from_global_section() {
		def config = "key=value" + sep + ";[ignore this section]" + sep + " [ section1 ]" + sep + "key2=value2"
		def r = o.parse(config)

		assertEquals(2, r.size())
		assertEquals("value", r["__global__"].params["key"])
		assertTrue(r.containsKey("section1"))
		assertEquals("value2", r["section1"].find("key2"))
		assertEquals("value", r["section1"].find("key"))
	}

	void test_inherit_from_parent_section_and_from_global() {
		def config = "global_key=value" + sep + ";[ignore this section]" + sep + " [ section1 ]" + sep + "key_in_section_1=value1" + sep + "[section2 : section1]" + sep + "key_in_section_2=value2"
		def r = o.parse(config)

		assertEquals(3, r.size())
		assertTrue(r.containsKey("section2"))
		assertEquals("value2", r["section2"].find("key_in_section_2"))
		assertEquals("value1", r["section2"].find("key_in_section_1")) //!!!
		assertEquals("value", r["section2"].find("global_key"))
	}

	void test_fixture() {
		o.file = new File("../fixtures/ini/test_cache.ini")
		o.use_section = "section2"

		assertEquals("global_value", o.get("global_key"))
		assertEquals("section2", o.get("overwrite_value"))
		assertEquals("section2_value", o.get("section2_key"))
		assertEquals("section1_value", o.get("section1_key"))
		assertEquals("default", o.get("invalid_key", "default"))
	}

	void test_cache() {
		o.file = new File("../fixtures/ini/test_cache.ini")
		o.use_section = "section2"
		
		assertEquals("section2_value", o.get("section2_key"))
		o.cache["section2_key"] = "new_value"
		assertEquals("new_value", o.get("section2_key"))
	}

	void test_replacement() {
		o.file = new File("../fixtures/ini/test_cache.ini")
		o.use_section = "section2"

		assertEquals("prefix section2_value suffix", o.replace('prefix ${section2_key} suffix'))
		assertEquals("prefix section2_value suffix section1_value", o.replace('prefix ${section2_key} suffix ${section1_key}'))
	}
}
