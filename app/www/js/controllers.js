angular.module('xp.controllers', ['restangular'])





.controller('LoginCtrl', function($scope, $location, Auth, Users, Syncer, Restangular) {

	$scope.me = {} ;

	$scope.login = function() {

		console.log($scope.me) ;

		$scope.error = null ;

		if (!$scope.me.screenname) {
			$scope.error = { message: "You must specify a screen name" } ;
			return ;
		}

		if (!$scope.me.password) {
			$scope.error = { message: "You must specify a password"} ;
			return ;
		}

		Auth.setCredentials($scope.me.screenname, $scope.me.password) ;

		Restangular.one("users", "me").get().then(
				function (me) {
					console.log(me) ;
					Users.setMe(me) ;

					Syncer.sync().then(
						function (response) {
							$location.path('/') ;
						},
						function (error) {
							$scope.error = error ;
						}
					) ;
				},
				function (error) {
					console.log(error) ;
					$scope.error = error ;
				}
			) ;


	}

	$scope.register = function() {

		$location.path("/register") ;
	}

})


.controller('RegisterCtrl', function($scope, $location, Restangular, Users, Syncer) {

	$scope.credentials = {} ;

	$scope.register = function() {

		$scope.error = null ;

		if (!$scope.credentials.screenName) {
			$scope.error = { message: "You must specify a screen name" } ;
			return ;
		}

		if (!$scope.credentials.password) {
			$scope.error = { message: "You must specify a password"} ;
			return ;
		}

		if ($scope.credentials.password != $scope.credentials.password2) {
			$scope.error = { message: "Passwords do not match"} ;
			return ;
		}

		Restangular.all("users", "me").post(
			{
				screenName: $scope.credentials.screenName,
				password: $scope.credentials.password
			}
		).then(
			function (result) {
				console.log(result) ;

				Users.setMe(result) ;

				Syncer.sync().then(
					function (response) {
						$location.path('/') ;
					},
					function (error) {
						$scope.error = error ;
					}
				) ;

			},
			function (error) {
				$scope.error = { message: error.data.message } ;
				console.log(error) ;

			}
		)


	}

	$scope.cancel = function() {
		$location.path("/login") ;
	}

})




.controller('SettingsCtrl', function($scope, $location, $ionicPopup, Users, Experiences, Syncs, Syncer) {

	$scope.me = Users.getMe() ;
	if (!$scope.me)
		$location.path("/login") ;

	$scope.latestSync = Syncs.getMostRecent() ;


	$scope.getExperiencesJson = function() {
		return angular.toJson(Experiences.getAll(), true) ;
	}

	

	$scope.isSyncing = function() {
		return Syncer.isSyncing() ;
	}

	$scope.sync = function() {
		Syncer.sync().then(
			function(result) {
				console.log(result) ;
				$scope.latestSync = Syncs.getMostRecent() ;
			}, 
			function (error) {
				console.log(error) ;
			}
		) ;
	}


	$scope.signout = function() {

		console.log("Asking to sign out")

		   var confirmPopup = $ionicPopup.confirm({
		     title: 'Signing Out',
		     template: 'Are you sure you want to sign out? You will loose all data entered on this device since your last sync' 
		   });
		   confirmPopup.then(function(res) {
		     if(res) {
		     	Users.clearAll() ;
		     	Experiences.clearAll() ;
		     	Syncs.clearAll() ;

		     	$location.path("/login") ;
		     } 
		   });
 	} ;
	

})




.controller('ExperiencesCtrl', ["MoodGrid","$scope", "$location", "Auth", "Experiences","$filter", function(MoodGrid,$scope, $location, Auth, Experiences, $filter) {

	if (!Auth.hasCredentials()) {
		console.log("redirecting to login") ;
		$location.path("/login") ;
	} else {
		updateExperiences() ;
	}
	
	function updateExperiences() {
		$scope.experiences = Experiences.getAll() ;
		//alert($scope.experiences.size);
	}

	$scope.getMoodStyle = function(mood) 
	{

		var m ;
		if (mood)
			m = mood ;
		else
			m = {valence:0, arousal:0} ;
		return {color: MoodGrid.getColor(m.valence, m.arousal)}
	}
	// $scope.addTag = function(index) {

	// 			console.log("adding " + $scope.newTag) ;

	// 			if (!scope.newTag)
	// 				return ;

	// 			/*var exp;
	// 			var i=0;
	// 			for (i = 0; i < experiences.length; i++) { 
 //   					 var temp=experiences[i];
 //   					 if (temp.id==id)
 //   					 {
 //   					 	exp=temp;
 //   					 }
	// 			}
	// 			if(!exp)
	// 				return;*/
	// 			var exp = _.clone($scope.experiences[index]) ;

	// 			if (!exp.tags)
	// 				exp.tags = [] ;

	// 			exp.tags.push(scope.newTag) ;
	// 			exp.tags = _.uniq(exp.tags) ;
				
	// 			$scope.experiences[i]=exp;
				
				
	// 		}

	// 		$scope.removeTag = function(tag,id) {

	// 			if (!tag)
	// 				return ;

	// 			var exp = _.clone($scope.experiences[id]) ;

	// 			exp.tags = _.without(exp.tags, tag) ;

				
	// 		}
}]) 

.controller('ExperienceDetailCtrl', function($scope, $stateParams, $location, Auth, Experiences) {

	if (!Auth.hasCredentials()) {
		console.log("redirecting to login") ;
		$location.path("/login") ;
	}

	$scope.experience = Experiences.get($stateParams.experienceId) ;
})


.controller('NewExperienceCtrl', function($scope, $location, Auth, Experiences) {

	if (!Auth.hasCredentials()) {
		console.log("redirecting to login") ;
		$location.path("/login") ;
	}
	
	$scope.experience = {} ;

})



.controller('DashCtrl', function($scope) {
})

.controller('FriendsCtrl', function($scope, Friends) {
  $scope.friends = Friends.all();
})



.controller('AccountCtrl', function($scope) {
});
