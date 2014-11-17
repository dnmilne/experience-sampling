app


.controller('MainCtrl', function($scope, $location, $modal, Auth, Restangular, MoodGrid) {

	$scope.filters = {} ;
	$scope.newExperience = {};

	if (Auth.hasCredentials()) {
		Restangular.one("users","me").get().then(
			function (data) {
				console.log(data) ;
				$scope.me = data ;
				updateExperiences() ;
			},
			function (error) {
				console.log(error) ;
				Auth.clearCredentials() ;
				$location.path("/login") ;
			}) ;
	} else {
		$location.path("/login") ;
	}

	$scope.$watch('filters', function() {

		console.log("filters changed") ;

		if (Auth.hasCredentials())
			updateExperiences() ;
	}, true)


	function updateExperiences() {

		var params = {} ;

		if ($scope.filters.tags)
			params.tags = $scope.filters.tags ;

		if ($scope.filters.moodBefore)
			params.moodBeforeNear = [$scope.filters.moodBefore.valence, $scope.filters.moodBefore.arousal] ;

		if ($scope.filters.moodAfter)
			params.moodAfterNear = [$scope.filters.moodAfter.valence, $scope.filters.moodAfter.arousal] ;


		console.log(params) ;

		Restangular.all("experiences").getList(params).then(
		function (data) {
			$scope.experiences = data ;
		});
	}

	$scope.logout = function() {

		Auth.clearCredentials() ;
		$location.path("/login") ;
	}

	$scope.getStyle = function(mood) {

		if (!mood) return ;

		return {
			color: MoodGrid.getColor(mood.valence, mood.arousal) 
		}
	}

	


	$scope.setMoodBefore = function() {

		var modalInstance = $modal.open({
			templateUrl: 'partials/directives/selectMood.html',
			controller: 'SetMoodCtrl',
			size: 'sm',
			resolve: {
				 mood: function() {
				 	return $scope.newExperience.moodBefore;
				}
			}
		});

		modalInstance.result.then(function (mood) {
			$scope.newExperience.moodBefore = mood ;
		}) ;
	}

	$scope.setMoodAfter = function() {

		var modalInstance = $modal.open({
			templateUrl: 'partials/directives/selectMood.html',
			controller: 'SetMoodCtrl',
			size: 'sm',
			resolve: {
				 mood: function() {
				 	return $scope.newExperience.moodAfter;
				}
			}
		});

		modalInstance.result.then(function (mood) {
			$scope.newExperience.moodAfter = mood ;
		}) ;
	}


	$scope.canAddExperience = function() {

		if (!$scope.newExperience.description)
			return false ;

		if (!$scope.newExperience.moodBefore)
			return false ;

		if (!$scope.newExperience.moodAfter)
			return false ;

		return true ;
	}


	$scope.addExperience = function() {
		
		if (!$scope.canAddExperience())
			return ;

		Restangular.all("experiences").post(
			$scope.newExperience
		).then(
			function (data) {
				console.log(data) ;
				$scope.newExperience = {} ;
				updateExperiences() ;
			},
			function (error) {
				$scope.error = error ;
				console.log(error) ;
			}
		) ; 
	}








	$scope.setFilterMoodBefore = function(mood) {

		if (mood) {
			$scope.filters.moodBefore = mood ;
			return ;
		}

		var modalInstance = $modal.open({
			templateUrl: 'partials/directives/selectMood.html',
			controller: 'SetMoodCtrl',
			size: 'sm',
			resolve: {
				 mood: function() {
				 	return $scope.filters.moodBefore;
				}
			}
		});

		modalInstance.result.then(function (mood) {
			$scope.filters.moodBefore = mood ;
		}) ;
	}

	$scope.clearFilterMoodBefore = function() {
		$scope.filters.moodBefore = undefined ;
	}


	$scope.setFilterMoodAfter = function(mood) {

		if (mood) {
			$scope.filters.moodAfter = mood ;
			return ;
		}


		var modalInstance = $modal.open({
			templateUrl: 'partials/directives/selectMood.html',
			controller: 'SetMoodCtrl',
			size: 'sm',
			resolve: {
				 mood: function() {
				 	return $scope.filters.moodAfter;
				}
			}
		});

		modalInstance.result.then(function (mood) {
			$scope.filters.moodAfter = mood ;
		}) ;
	}

	$scope.clearFilterMoodAfter = function() {
		$scope.filters.moodAfter = undefined ;
	}

	$scope.addTagFilter = function(tag) {

		console.log("adding tag filter " + tag) ;

		if (!tag) {
			tag = $scope.newTagFilter ;
			$scope.newTagFilter = undefined ;
		}

		if (!tag)
			return ;

		

		var newTags = [] ;
		if ($scope.filters.tags)
			_.each($scope.filters.tags, function(tag) {
				newTags.push(tag) ;
			}) ;

		newTags.push(tag) ;
		newTags = _.uniq(newTags) ;

    	$scope.filters.tags = newTags ;
    }

    $scope.removeTagFilter = function(tag) {
    	$scope.filters.tags = _.without($scope.filters.tags, tag) ;
    }

	
})



.controller('LoginCtrl', function($scope, $location, Auth, Restangular) {

	$scope.mode = 'login' ;



	$scope.login = function() {

		$scope.error = null ;

		if (!$scope.screenName) {
			$scope.error = {message:"You must specify a screen name"} ;
			return ;
		}

		if (!$scope.password) {
			$scope.error = {message:"You must specify a password"} ;
			return ;
		}

		Auth.setCredentials($scope.screenName, $scope.password) ;

		Restangular.one("users","me").get().then(
			function (data) {
				$location.path("/") ;
			},
			function (error) {
				console.log(error) ;
				$scope.error = error ;
			}
		) ;
	}
})


.controller('RegisterCtrl', function($scope, $location, Auth, Restangular) {

	$scope.mode = 'register' ;

	$scope.register = function() {

		$scope.error = null ;


		if (!$scope.screenName) {
			$scope.error = {message:"You must specify a screen name"} ;
			return ;
		}

		if (!$scope.email) {
			$scope.error = {message:"You must specify an email address"} ;
			return ;
		}

		if (!$scope.password) {
			$scope.error = {message:"You must specify a password"} ;
			return ;
		}

		if (!$scope.password2 || $scope.password != $scope.password2) {
			$scope.error = {message:"Passwords do not match"} ;
			return ;
		}

		$scope.unauthorized = false ;

		Restangular.all("users").post(
		{
			screenName:$scope.screenName,
			email:$scope.email, 
			password:$scope.password
		}
		).then(
			function (data) {
				$scope.me = data ;
				Auth.setCredentials($scope.screenName, $scope.password) ;
				$location.path("/") ;
			},
			function (error) {
				$scope.error = error ;
			}
		) ; 
	}
}) 




.controller('SetMoodCtrl', function($scope, $modalInstance, MoodGrid, mood) {

	$scope.mood = _.clone(mood) ;
	
	$scope.getStyle = function(mood) {

		if (!mood) return ;

		return {
			color: MoodGrid.getColor(mood.valence, mood.arousal) 
		}
	}

	$scope.ok = function () {
		$modalInstance.close($scope.mood);
	};

	$scope.cancel = function () {
		$modalInstance.dismiss('cancel');
	};

})








