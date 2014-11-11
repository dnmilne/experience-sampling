var app = angular.module('xp', ['restangular','ui.bootstrap', 'ngRoute', 'ngSanitize', 'ngCookies', 'base64']) ;




//Route provider

app.config(['$routeProvider', function($routeProvider) {
    $routeProvider
    .when('/', {
        templateUrl: 'partials/pages/main.html',
        controller: 'MainCtrl'
    })
    .when('/api', {
        templateUrl: 'partials/pages/api.html', 
        controller: 'ApiCtrl'
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