'use strict';

angular.module('myApp.viewRules', ['ngRoute'])

.config(['$routeProvider', function($routeProvider) {
  $routeProvider.when('/view-rules', {
    templateUrl: 'view-rules/view-rules.html',
    controller: 'ViewRulesCtrl'
  });
}])
