'use strict';

angular.module('myApp.prettyCheckbox', [])

.directive('prettyCheckbox', [function() {
  return {
    restrict: 'E',
    scope: {
      checked: "=ngModel"
    },
    templateUrl: 'components/pretty-checkbox/pretty-checkbox.html',
    link: function(scope, elem, attr) {
      scope.prettyCheckboxChange = function() {
        jQuery(elem).removeClass('checked');
        if (scope.checked)
          jQuery(elem).addClass('checked');
      }
      scope.checked = false;
    }
  }
}])
