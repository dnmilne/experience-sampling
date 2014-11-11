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




.factory('Moods', ['Restangular', function (Restangular) {

    var moodMap ;
    var moodTree ;
    var ready = false ;

    var distance = function(moodA, moodB){
        return Math.pow(moodA.averageValence - moodB.averageValence, 2) +  Math.pow(moodA.averageArousal - moodB.averageArousal, 2);
    } ;




    Restangular.all("moods").getList().then(
        function (moods) {
            console.log(moods) ;

            moodTree = new kdTree(moods, distance, ["averageValence", "averageArousal"]);

            moodMap = {} ;
            _.each(moods, function(mood) {
                moodMap[mood.name] = mood ;
            }) ;

            ready = true ;
        },
        function (error) {
            console.log(error) ;
        }
    ) ;


    return {

        isReady: function() {
            return ready ;
        },
        getMood: function(moodName) {
            return moodMap[moodName] ;
        },
        getMoodsNear: function(v, a) {

            console.log(v + "   -   " + a) ;

            var nearestPoints = moodTree.nearest({ averageValence: v, averageArousal: a }, 10);

            var nearestMoods = [] ;

            _.each(nearestPoints, function(point) {
                nearestMoods.push(point[0]) ;
            }) ;

            console.log(nearestMoods) ;

            return nearestMoods ;
        }
    }



}]) ;






