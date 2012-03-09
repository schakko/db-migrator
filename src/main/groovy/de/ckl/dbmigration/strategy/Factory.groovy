package de.ckl.dbmigration.strategy

/**
 * Just a simple factory. I admit, a class "Factory" inside a package "strategy" sounds ... interesting.
 */
class Factory {
	static create(strategy) {
		switch (strategy) {
			case 'hierarchial':
				return new Hierarchial()
			default:
				return new Flat()
		}
	}
}