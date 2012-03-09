db-migrator
===========
db-migrator provides an easy interface to apply SQL migration scripts to a database of your choice.
I chose Groovy because it is platform independent (only Java runtime required) and you don't have to install Groovy on your customers server.
Just add grovy-all*.jar to your project and Groovy runs.

Why database migrations?
------------------------
Please take a look at
 * http://www.codinghorror.com/blog/2008/02/get-your-database-under-version-control.html
 * http://odetocode.com/blogs/scott/archive/2008/01/30/three-rules-for-database-work.aspx
 * http://wap.ecw.de/archives/1665
 * http://blog.schauderhaft.de/2012/01/15/tipps-for-testing-database-code/
 
to get an idea why this is useful ;-)

How to use?
-----------
Currently the db-migrator only supports MySQL DBMS but can be easily extended to support the DBMS of your choice.
One important rule: you strictly need workflow and a directory/file name structure for migration scripts.
db-migrator provides two strategies which are most commonly used.

1. Flat structure
Your migration scripts must reside in one directory, for example
/migrations
	/20120307-001_any_name_of_file.sql
	/20120308-001_blah.sql
	/20120308-002_blah.sql
/views
	/20120307-001_v1.sql
	/20120308-002_v2.sql
/coredata
	/20120305-001_bla.sql
	/20120307-001_data.sql
/fixtures
	/20120307-001_fixtures.sql

To apply every (!) script from current installed migration to the newest available, you must use
	groovy <path-to/>migrate-mysql -u user -p password -d database --strategy=flat ./migrations
Your SQL scripts inside /migrations directory must use an ALTER TABLE syntax

To apply only the latest script for coredata, you must use
	groovy <path-to/>migrate-mysql -u user -p password -d database --strategy=flat ./coredata,latest
Only the latest available (20120307-001_data.sql) migration script will be applied.
Your SQL scripts should contain a DELETE FROM * at the beginning of the SQL script to get into a consistent state

To automatically install the migration without writing an INSET INTO migrations(...) at the end of every migration script, you must append a true after the directory
	groovy <path-to/>migrate-mysql -u user -p password -d database --strategy=flat ./coredata,latest,true

If you want to apply a migration over more than one directory, you must separate the directories with a semicolon
	groovy <path-to/>migrate-mysql -u user -p password -d database --strategy=flat ./migrations,all,true;./views,all,true;./coredata,latest,true

Important: By default you must use the filename convention yyyymmdd-<number>bla.sql

2. Hierarchial structure
Your migration scripts resides in subdirectory:
/migrations
	/major
		/minor
			0001_blah.sql

Use the strategy 'hierarchial' to handle this convention
	groovy <path-to/>migrate-mysql -u user -p password -d database --strategy=hierarchial ./migrations,latest,true

	
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