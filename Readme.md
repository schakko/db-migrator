db-migrator
===========
db-migrator provides an easy interface to apply SQL migration scripts to a database of your choice.
I chose Groovy because it is platform independent (only Java runtime required) and you don not have to install Groovy on your customers server.
Just add grovy-all*.jar to your project and Groovy runs.

Features
--------

 * Support for Microsoft SQL Server (osql), MySQL (mysql) and PostgreSQL (psql)
 * Easy handling and customizing
 * Execution of flat and hierarchial migration directory structure 
 
Why database migrations?
------------------------
Please take a look at
 
 * http://www.codinghorror.com/blog/2008/02/get-your-database-under-version-control.html
 * http://odetocode.com/blogs/scott/archive/2008/01/30/three-rules-for-database-work.aspx
 * http://www.schakko.de/2011/12/23/databases-in-developers-environment-versioning-and-migrations/
 * http://blog.schauderhaft.de/2012/01/15/tipps-for-testing-database-code/
 
to get an idea why this is useful ;-)

Other tools
-----------

 * http://www.liquibase.org


How to use?
-----------
Currently the db-migrator only supports MySQL DBMS but can be easily extended to support the DBMS of your choice.
One important rule: you strictly need a defined workflow and a directory/file name structure for migration scripts.
db-migrator provides two strategies which are most commonly used.

db-migrator, by default, only defines two rules: 

 * Every migration file *must* begin with the format "yyyymmdd-ddd". The first part defines the date, the second a incremental number. Both together defines the *version*. Everything after the incremental number can be defined as you like.
 * Every *version must* be unique. The migration files "20120426-001_migration.sql" and "20120426-001_unittest.sql" are *not* unique. "20120426-001_migration.sql" and "20120427-001_migration.sql" are unqiue! If you break this rule, db-migrator will throw a warning and the first found migration is used.



Strategies
----------

### Flat structure ###
Your migration scripts must reside in one directory, for example

	/migrations
		/20120307-001_any_name_of_file.sql
		/20120308-001_blah.sql
		/20120308-002_blah.sql
	/views
		/20120307-101_v1.sql
		/20120308-102_v2.sql
	/coredata
		/20120305-201_bla.sql
		/20120307-201_data.sql
	/fixtures
		/20120307-901_fixtures.sql

To apply every (!) script from current installed migration to the newest available, you must use

	groovy <path-to/>migrate-mysql -u user -p password -d database --strategy=flat ./migrations

Your SQL scripts inside /migrations directory must use an ALTER TABLE syntax

To apply only the latest script for coredata, you must use

	groovy <path-to/>migrate-mysql -u user -p password -d database --strategy=flat ./coredata,latest

Only the latest available (20120307-001_data.sql) migration script will be applied.
Your SQL scripts should contain a DELETE FROM * at the beginning of the SQL script to get into a consistent state

To automatically install the migration without writing an INSERT INTO migrations(...) at the end of every migration script, you must append a true after the directory

	groovy <path-to/>migrate-mysql -u user -p password -d database --strategy=flat ./coredata,latest,true

If you want to apply a migration over more than one directory, you must separate the directories with a semicolon

	groovy <path-to/>migrate-mysql -u user -p password -d database --strategy=flat ./migrations,all,true;./views,all,true;./coredata,latest,true

Important: By default you must use the filename convention yyyymmdd-<number>bla.sql

### Hierarchial structure ###
Your migration scripts resides in subdirectory:

	/migrations
		/major
			/minor
				0001_blah.sql

Use the strategy 'hierarchial' to handle this convention

	groovy <path-to/>migrate-mysql -u user -p password -d database --strategy=hierarchial ./migrations,latest,true


Shell script usage
------------------

	#!/bin/sh
	HOST=localhost
	USER=root
	PASSWORD=
	DBMIGRATOR=/path/to/migrate-mysql.groovy
	DATABASE=your_database
	DIR=`dirname $0`
	if [ "$1" = "unittest" ]; then
		DATABASE="$DATABASE_test"
	fi

	EXEC="groovy -cp `dirname $DBMIGRATOR` $DBMIGRATOR -d $DATABASE -h $HOST -u $USER -p $PASSWORD "
	EXEC_MIGRATIONS="$EXEC $DIR/migrations,all,true"
	R=`$EXEC_MIGRATIONS`

	echo -e "$R"
	if [ "$1" = "unittest" ]; then
		`$EXEC $DIR/fixtures/unittest,latest`
	fi

Usage with Zend Framework
-------------------------
db-migrator provides a simple interface for .ini files which can be used for Zend Framework applications.
Every option in your command line can be replaced with a settings key inside your application.ini or routes.ini.
To use this feature, you must provide the parameter -i - an absolute path to your ini file.

	#!/bin/sh
	SECTION="production"
	DBMIGRATOR=/path/to/migrate-mysql.groovy
	DIR=`dirname $0`

	if [ "$1" = "testing" ]; then
	        SECTION="testing"
	fi

	EXEC="groovy -cp `dirname $DBMIGRATOR` $DBMIGRATOR  -i $DIR/../application/configs/application.ini -x $SECTION -d \${resources.db.params.dbname} -h \${resources.db.params.host} -u \${resources.db.params.username} -p \${resources.db.params.password}"
	R=`$EXEC "$DIR/migrations,all,true;$DIR/fixtures/coredata,all,true"`

	echo -e "$R"

	if [ "$1" = "testing" ]; then
        	R=`$EXEC $DIR/fixtures/unittest,latest,false`
        	echo -e "$R"
	fi


Internals
---------
Every script which has to be applied is concated to a temporary SQL script which is executed inside a transaction, so that ALL or NONE migrations are applied.

Contact
-------
schakkonator [AT] googlemail [DOT] com / http://twitter.com/schakko

License
-------
Copyright (C) 2012  Christopher Klein

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.
