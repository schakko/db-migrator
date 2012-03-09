import groovy.util.GroovyTestSuite
import junit.framework.Test
import junit.textui.TestRunner
import org.codehaus.groovy.runtime.ScriptTestAdapter

def gsuite = new GroovyTestSuite()
gsuite.addTestSuite(gsuite.compile("de/ckl/dbmigration/MigratorTest.groovy"))
gsuite.addTestSuite(gsuite.compile("de/ckl/dbmigration/GuardTest.groovy"))
gsuite.addTestSuite(gsuite.compile("de/ckl/dbmigration/strategy/FlatTest.groovy"))
gsuite.addTestSuite(gsuite.compile("de/ckl/dbmigration/target/mysql/ExecutorTest.groovy"))
gsuite.addTestSuite(gsuite.compile("de/ckl/dbmigration/target/mysql/DBInterfaceTest.groovy"))
gsuite.addTestSuite(gsuite.compile("de/ckl/dbmigration/target/mysql/ParserTest.groovy"))

TestRunner.run(gsuite)