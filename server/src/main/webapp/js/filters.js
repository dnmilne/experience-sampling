app

.filter('fromNow', function() {


	return function(date) {
		return moment(date, 'YYYY-MM-DD HH:mm:ss Z').fromNow() ;
	}
    
})

