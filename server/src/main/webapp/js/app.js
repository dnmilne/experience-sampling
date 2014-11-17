var app = angular.module('xp', ['restangular','ui.bootstrap', 'ngRoute', 'ngSanitize', 'ngCookies', 'base64', 'angular-mood', 'angular-swagger']) ;




//Route provider

app.config(['$routeProvider', function($routeProvider) {
    $routeProvider
    .when('/', {
        templateUrl: 'partials/pages/main.html',
        controller: 'MainCtrl'
    })
    .when('/login', {
        templateUrl: 'partials/pages/loginOrRegister.html', 
        controller: 'LoginCtrl'
    })
    .when('/register', {
        templateUrl: 'partials/pages/loginOrRegister.html', 
        controller: 'RegisterCtrl'
    })
    .when('/mood', {
        templateUrl: 'partials/pages/mood.html'
    })
    .when('/api', {
        templateUrl: 'partials/pages/api.html'
    })
    .otherwise({
        redirectTo: '/'}
        );
}]);


//Restangular

app.config(function(RestangularProvider) {

    //all api calls start with this prefix
    RestangularProvider.setBaseUrl('api/');

}) ;    