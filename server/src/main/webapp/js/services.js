app



.factory('Auth', ['$cookies', '$http', '$base64', function ($cookies, $http, $base64) {

    // initialize to whatever is in the cookie, if anything
    if ($cookies.authdata)
    	$http.defaults.headers.common['Authorization'] = 'Basic ' + $cookies.authdata;
 
    return {
    	hasCredentials: function() {

    		if ($cookies.authdata)
    			return true ;

    		return false ;
    	}, 
        setCredentials: function (username, password) {
            var encoded = $base64.encode(username + ':' + password);
            $http.defaults.headers.common.Authorization = 'Basic ' + encoded;

            console.log($http.defaults.headers.common) ;

            $cookies.authdata = encoded;
        },
        clearCredentials: function () {
            document.execCommand("ClearAuthenticationCache");
            $cookies.authdata = undefined ;
            $http.defaults.headers.common.Authorization = 'Basic ';
        }
    };
}])





