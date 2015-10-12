'use strict';

// Declare app level module which depends on views, and components
angular.module('myApp', [
  'ngRoute',
  'myApp.board',
  'myApp.view2',
  'myApp.ai',
  'myApp.rules',
  'myApp.version',
  'myApp.prettyCheckbox'
]).
config(['$routeProvider', function($routeProvider) {
  $routeProvider.otherwise({redirectTo: '/board'});
}]);
