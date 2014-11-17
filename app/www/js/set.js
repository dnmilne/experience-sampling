function Set(onEmptiedCallback) {
	this.items = {} ;
	this.onEmptied = onEmptiedCallback ;
}

Set.prototype = {
	constructor: Set,
	getEntries: function() {
		return _.keys(this.items) ;
	},
	add: function(val) {
		this.items[val] = true ;
	},
	remove: function(val) {
		delete this.items[val] ;

		if (this.isEmpty() && this.onEmptied)
			this.onEmptied() ;
	},
	isEmpty: function() {
		return _.isEmpty(this.items) ;
	}
}