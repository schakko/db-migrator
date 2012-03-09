package de.ckl.dbmigration.target.mysql
import groovy.util.GroovyTestCase
import de.ckl.dbmigration.*
import de.ckl.dbmigration.strategy.*

class FlatTest extends GroovyTestCase {
	def o = null,
		v = null,
		g = null
	
	void setUp() {
		o = new Flat()
		v = new Version([major: '20120307', minor: '001'])
		g = new Guard()
	}
	
	void test_find_unapplied_migrations_since()
	{
		def r = o.find_unapplied_migrations_since(v, new File('../fixtures/mysql/flat/'), g)
		def keys = r.keySet()
		
		def neededversions = [new BigInteger(210001010004), 
			new BigInteger(210001010001), 
			new BigInteger(210001010002), 
			new BigInteger(210001010005)
		]
		
		for (k in keys) {
			assertTrue(neededversions.contains(k.get_version()))
			neededversions.remove(k.get_version())
		}
		
		assertEquals(0, neededversions.size())
	}
}