// Ionic Starter App

var serverUrl = "http://130.56.251.29/xp/api/"

// angular.module is a global place for creating, registering and retrieving Angular modules
// 'starter' is the name of this angular module example (also set in a <body> attribute in index.html)
// the 2nd parameter is an array of 'requires'
// 'starter.services' is found in services.js
// 'starter.controllers' is found in controllers.js
angular.module('xp', ['ionic', 'restangular', 'angular-mood','xp.controllers', 'xp.services', 'xp.filters'])

.run(function($ionicPlatform) {
  $ionicPlatform.ready(function() {
    // Hide the accessory bar by default (remove this to show the accessory bar above the keyboard
    // for form inputs)
    if(window.cordova && window.cordova.plugins.Keyboard) {
      cordova.plugins.Keyboard.hideKeyboardAccessoryBar(true);
    }
    if(window.StatusBar) {
      // org.apache.cordova.statusbar required
      StatusBar.styleDefault();
    }
  });
})

.config(function($stateProvider, $urlRouterProvider) {

  // Ionic uses AngularUI Router which uses the concept of states
  // Learn more here: https://github.com/angular-ui/ui-router
  // Set up the various states which the app can be in.
  // Each state's controller can be found in controllers.js
  $stateProvider

    .state('login', {
      url: "/login",
      templateUrl: "templates/login.html",
      controller: 'LoginCtrl'
    })
    .state('register', {
      url: "/register",
      templateUrl: "templates/register.html",
      controller: 'RegisterCtrl'
    })

    // setup an abstract state for the tabs directive
    .state('tab', {
      url: "/tab",
      abstract: true,
      templateUrl: "templates/tabs.html"
    })

    // Each tab has its own nav history stack:


    .state('tab.settings', {
      url: "/settings",
      views: {
        'tab-settings': {
          templateUrl: "templates/tab-settings.html",
          controller: 'SettingsCtrl'
        }
      }
    })

    .state('tab.experiences', {
      url: '/experiences',
      views: {
        'tab-experiences': {
          templateUrl: 'templates/tab-experiences.html',
          controller: 'ExperiencesCtrl'
        }
      }
    })
    .state('tab.experience-detail', {
      url: '/experiences/:experienceId',
      views: {
        'tab-experiences': {
          templateUrl: 'templates/experience-detail.html',
          controller: 'ExperienceDetailCtrl'
        }
      }
    })

    .state('tab.newExperience', {
      url: '/newExperience',
      views: {
        'tab-newExperience': {
          templateUrl: 'templates/tab-newExperience.html',
          controller: 'NewExperienceCtrl'
        }
      }
    })






    ;

  // if none of the above states are matched, use this as the fallback
  $urlRouterProvider.otherwise('/tab/experiences');

})



.config(function(RestangularProvider) {

  //all api calls start with this prefix
  RestangularProvider.setBaseUrl(serverUrl);


})   





