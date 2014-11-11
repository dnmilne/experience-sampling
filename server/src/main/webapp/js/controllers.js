app


.controller('MainCtrl', function($scope, Auth, Restangular) {

	$scope.loginOrRegister = "login" ;


	$scope.mood = { 
		"name" : "neutral", 
		"valence" : 0, 
		"arousal" : 0
	} ;

	if (Auth.hasCredentials()) {
		Restangular.one("users","me").get().then(
			function (data) {
				console.log(data) ;
				$scope.me = data ;
				updateExperiences() ;
			},
			function (error) {
				console.log(error) ;
				$scope.error = error ;
				$scope.unauthorized = true ;
				Auth.clearCredentials() ;
			}) ;
	} else {
		$scope.unauthorized = true ;
	}


	$scope.login = function() {

		if (!$scope.email) {
			$scope.error = "You must specify an email address" ;
			return ;
		}

		if (!$scope.password) {
			$scope.error = "You must specify a password" ;
			return ;
		}

		$scope.unauthorized = false ;

		Auth.setCredentials($scope.email, $scope.password) ;

		Restangular.one("users","me").get().then(
			function (data) {
				console.log(data) ;
				$scope.me = data ;

				updateExperiences() ;
			},
			function (error) {
				console.log(error) ;
				$scope.error = error ;
				$scope.unauthorized = true ;
			}
		) ;
	}

	$scope.register = function() {

		if (!$scope.email) {
			$scope.error = "You must specify an email address" ;
			return ;
		}

		if (!$scope.password) {
			$scope.error = "You must specify a password" ;
			return ;
		}

		if (!$scope.password2 || $scope.password != $scope.password2) {
			$scope.error = "Passwords do not match" ;
			return ;
		}

		$scope.unauthorized = false ;

		Restangular.all("users").post(
			{
				email:$scope.email, 
				password:$scope.password
			}
		).then(
			function (data) {
				$scope.me = data ;

				
				Auth.setCredentials($scope.me.email, $scope.me.password) ;
				updateExperiences() ;
			},
			function (error) {
				$scope.error = error ;
				$scope.unauthorized = true ;
			}
		) ; 
	}

	function updateExperiences() {
		console.log("updating experiences" ) ;

		Restangular.all("experiences").getList().then(
		function (data) {
			$scope.experiences = data ;
		});
	}

	$scope.logout = function() {

		Auth.clearCredentials() ;
		$scope.unauthorized = true ;
		$scope.me = undefined ;
	}

	


	$scope.$watch("mood", function() {

		Restangular.all("moods").getList(
			{near: [$scope.mood.valence, $scope.mood.arousal]}
		).then(

			function (data) {
				$scope.nearbyMoods = data
			}, 
			function (error) {
				console.log(error) ;
				$scope.error = error ;
			}) ;

	}, true) ;


})

































.controller('ApiCtrl', function($scope, $http, $modal) {

	$scope.apis = [] ;

	$http.get('/api/api-docs').success(function(data) {

		_.each(data.apis, function(api) {

			$scope.apis.push(api) ;

			$http.get('/api/api-docs' + api.path).success(function(data) {
				api.details = data ;
			}) ;
		}) ;
	}) ;

	$scope.getMethodClass = function(verb) {
		switch(verb) {
			case 'GET' : 
				return "label-success" ;
			case 'POST' : 
				return "label-primary" ;
			case 'DELETE' : 
				return "label-danger"
		}
	} ;

	$scope.formatPath = function(path) {
  		return path.replace(/\{([^}]*)\}/mg, "<span class='text-muted'>{$1}</span>");
  	}
})


.controller('ModalApiObjectCtrl', function ($scope, $modalInstance, object, models) {

     	$scope.object = object ;
    	$scope.models = models ;

    	$scope.getEnumDescription = function(property) {
      		return getEnumDescription(property.enum) ;
      	}
}) ;



