angular.module('xp.services', ['base64'])




.factory('$localStorage', ['$window', function($window) {
  return {
    set: function(key, value) {
      $window.localStorage[key] = value;
    },
    get: function(key, defaultValue) {
      return $window.localStorage[key] || defaultValue;
    },
    setObject: function(key, value) {
      $window.localStorage[key] = JSON.stringify(value);
    },
    getObject: function(key) {
      return JSON.parse($window.localStorage[key] || '{}');
    },
    getArray: function(key) {
      return JSON.parse($window.localStorage[key] || '[]');
    }
  }
}])






.factory('Auth', ['$localStorage', '$http', '$base64', function ($localStorage, $http, $base64) {

    // initialize to whatever is in the cookie, if anything

    var authdata = $localStorage.get("authdata", null) ;

    if (authdata)
      $http.defaults.headers.common['Authorization'] = 'Basic ' + authdata;
 
    return {
      hasCredentials: function() {

        if (authdata)
          return true ;

        return false ;
      }, 
        setCredentials: function (username, password) {
            var encoded = $base64.encode(username + ':' + password);
            $http.defaults.headers.common.Authorization = 'Basic ' + encoded;

            console.log($http.defaults.headers.common) ;

            $localStorage.set("authdata", encoded) ;
        },
        clearCredentials: function () {
            document.execCommand("ClearAuthenticationCache");
            $localStorage.set("authdata", undefined) ;
            $http.defaults.headers.common.Authorization = 'Basic ';
        }
    };
}])





.factory('Users', ['$localStorage', function($localStorage) {

  return {

    getMe: function() {

      var me = $localStorage.getObject('me') ;

      console.log(me) ;

      if (me && me.screenName)
        return me;
      else
        return null ;

    },
    setMe: function(me) {
      $localStorage.setObject('me', me) ;
    },
    clearAll: function() {
      $localStorage.setObject('me', null) ;
    }
  }


}])


.factory('Experiences', ['$localStorage', function($localStorage) {

  var experiences = $localStorage.getObject('experiences') ;

  return {
    getAll: function() {
      return experiences;
    },
    getCreatedLocally: function() {

      return _.filter(experiences, function(experience){ 

        return experience.createdLocally ;

      }) ;
    },
    getModifiedLocally: function() {

      return _.filter(experiences, function(experience) {

        return experience.modifiedLocally ;

      }) ;
    },
    getUpdatedSinceLastSync: function(lastSync) {

      return _.filter(experiences, function(experience) {

        if (experience.id.startsWith("_"))
          return false ;

        if (!lastSync || !lastSync.syncedAt)
          return true ;

        return (experience.modifedAt > lastSync.syncedAt)
          return true ;

      }) ;
    },
    getLastModifiedAt: function() {

      var lastModifiedAt = null ;

      _.each(experiences, function(experience) {

        if (!lastModifiedAt || lastModifiedAt < experience.modifiedAt)
          lastModifiedAt = experience.modifiedAt ;

      }) ;

      return lastModifiedAt ;
    },
    get: function(id) {
      return experiences[id];
    },
    getNextTempId: function() {

      var largestTmpId = 0 ;

      _.each(experiences, function(experience) {

        if (!experience.id.startsWith("_"))
          return ;

        var tmpId = Number(experience.id.substr(1)) ;
        largestTmpId = Math.max(largestTmpId, tmpId) ;
      }) ;

      return "_" + (largestTmpId + 1) ;
    },
    save: function(experience) {

      if (!experiences[experience.id]) {
        experience.createdAt = moment().toISOString() ;
        experience.createdLocally = true ;
      } else {

        if (!experience.createdLocally)
          experience.modifiedLocally = true ;
      }

      experiences[experience.id] = experience ;

      $localStorage.setObject('experiences', experiences) ;
    },
    clobber: function(experience) {

      experiences[experience.id] = experience ;
      $localStorage.setObject('experiences', experiences) ;
    },
    clear: function(experience) {

      delete experiences[experience.id] ;
      $localStorage.setObject('experiences', experiences) ;
    },
    clearId: function(id) {
      delete experiences[id] ;
      $localStorage.setObject('experiences', experiences) ;
    },
    clearAll: function() {
      experiences = {} ;

      $localStorage.setObject('experiences', experiences) ;
    }
  }
}])




.factory('Syncer', ['$q', 'Auth', 'Users', 'Experiences', 'Syncs', 'Restangular', function($q, Auth, Users, Experiences, Syncs, Restangular) {

  //this will contain an identifier string for each asyncronous request
  //they are added when we start a request, and removed when the request succeeds or fails.
  //if it is non-empty, it means, the sync is still in progress
  var pendingRequests ;

  var failedRequests ;
  var conflictingExperiences ;

  function handleModifiedExperienceFromServer(experience) {

    var existingExperience = Experiences.get(experience.id) ;

    if (existingExperience && existingExperience.dirty) {
      conflictingExperiences.add(experience.id) ;
    }

    Experiences.clobber(experience) ;
  }

  function handleExperienceCreatedLocally(experience, me) {

      console.log("posting locally created experience") ;
      console.log(experience) ;

    var exp = _.clone(experience) ;
    exp.tempId = experience.id ;
    exp.id = undefined ;

    pendingRequests.add("localCreate_" + exp.tempId) ;


    Restangular.all("experiences").post(
      exp
    ).then(
      function (postedExperience) {
        Experiences.clobber(postedExperience) ;
        Experiences.clearId(exp.tempId) ;

        pendingRequests.remove("localCreate_" + exp.tempId) ;
      },
      function (error) {
        console.log(error) ;

        pendingRequests.remove("localCreate_" + exp.tempId) ;
        failedRequests.add("localCreate_" + exp.tempId) ;
      }
    ) ;
  }

  function handleExperienceModifiedLocally(experience, me) {

    pendingRequests.add("localMod_" + experience.id) ;

    Restangular.all("experiences").post(
      experience
    ).then(
      function (postedExperience) {

        console.log("received posted experience: " + angular.toJson(postedExperience)) ;

        Experiences.clobber(postedExperience) ;
        pendingRequests.remove("localMod_" + experience.id) ;
      },
      function (error) {
        console.log(error) ;

        pendingRequests.remove("localMod_" + experience.id) ;
        failedRequests.add("localMod_" + experience.id) ;
      }
    ) ;
  }

  return {

    isSyncing : function() {
      return (pendingRequests && !pendingRequests.isEmpty()) ;
    },
    sync: function() {

      // this is an asynchronous operation, so use angular's promise structure
      // from https://docs.angularjs.org/api/ng/service/$q
      var deferred = $q.defer();

      //immediately reject sync if one is already in progress
      if (pendingRequests && !pendingRequests.isEmpty()) {
        deferred.reject({message:"Sync is already in progress"}) ;
        return deferred.promise ;
      }

      //immediately reject sync if not authenticated
      if (!Auth.hasCredentials()) {
        deferred.reject({message:"Not signed in"}) ;
        return deferred.promise ;
      }

      var me = Users.getMe() ;

      // this bi-directional sync is implemented according to Sergey Kosik's algorithm described at 
      // http://havrl.blogspot.com.au/2013/08/synchronization-algorithm-for.html

      failedRequests = new Set() ;
      conflictingExperiences = new Set() ;

      pendingRequests = new Set(function() {

        //this function is the callback for when pending requests has been emptied.
        //It's the only way we know the sync is finished
        console.log("Sync completed") ;

        if (!failedRequests.isEmpty()) {
          deferred.reject({message:"At least one request to server failed", failedRequests:failedRequests.getEntries()}) ;
        } else {
          Syncs.setMostRecent({syncedAt : Date()}) ; 
          deferred.resolve({message:"Sync completed successfully", conflicts:conflictingExperiences.getEntries()}) ;
        } 
      }) ;

      //Now actually do the sync!

      //calculate last time we did a sync
      var lastModified = Experiences.getLastModifiedAt() ;
      if (lastModified != undefined)
        lastModified = formatDate(lastModified) ;

      console.log(lastModified) ;

      //get all experiences created or modified on server since we last did a sync
      pendingRequests.add("serverMods") ;

      Restangular.all("experiences").getList(
        {
          modifiedAfter:lastModified
        }
      ).then(

        function(experiencesModifiedOnServer) {

          //handle each experience modified on server
          _.each(experiencesModifiedOnServer, function (experience) {
            console.log("Received experience from server: " + angular.toJson(experience)) ;
            handleModifiedExperienceFromServer(experience) ;
          }) ;

          //handle all experiences created locally since last sync
          _.each(Experiences.getCreatedLocally(), function(experience) {
            handleExperienceCreatedLocally(experience, me) ;
          }) ;

          //handle all experiences modified locally since last sync
          _.each(Experiences.getModifiedLocally(), function(experience) {
            handleExperienceModifiedLocally(experience, me) ;
          }) ;

          pendingRequests.remove("serverMods") ;

        },
        function(error) {
          console.log(error) ;
          pendingRequests.remove("serverMods") ;
          failedRequests.add("serverMods") ;
        }
      ) ;


      return deferred.promise ;
    }
  }
}]) 







.factory('Syncs', ['$localStorage', function($localStorage) {

  var mostRecentSync = $localStorage.getObject('mostRecentSync') ;

  return {

    getMostRecent: function() {
      return mostRecentSync ;
    },
    setMostRecent: function(sync) {
      mostRecentSync = sync ;
      $localStorage.setObject('mostRecentSync', mostRecentSync) ;
    },
    clearAll: function() {
      mostRecentSync = {} ;
      $localStorage.setObject('mostRecentSync', mostRecentSync) ;
    },
    

  }
}])












/**
 * A simple example service that returns some data.
 */
.factory('Friends', function() {
  // Might use a resource here that returns a JSON array

  // Some fake testing data
  var friends = [
    { id: 0, name: 'Scruff McGruff' },
    { id: 1, name: 'G.I. Joe' },
    { id: 2, name: 'Miss Frizzle' },
    { id: 3, name: 'Ash Ketchum' }
  ];

  return {
    all: function() {
      return friends;
    },
    get: function(friendId) {
      // Simple index lookup
      return friends[friendId];
    }
  }
});
