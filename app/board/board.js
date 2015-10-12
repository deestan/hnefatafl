'use strict';

angular.module('myApp.board', ['ngRoute', 'myApp.ai', 'myApp.rules'])

.config(['$routeProvider', function($routeProvider) {
  $routeProvider.when('/board', {
    templateUrl: 'board/board.html',
    controller: 'BoardCtrl'
  });
}])

.controller('BoardCtrl', ['$scope', 'ai', 'rules', function($scope, ai, rules) {
  $scope.aiValueChanged = function() {
    setTimeout($scope.makeAiMove, 500);
  }

  $scope.selectPiece = function(index, event) {
    $scope.selectedIndex = index;
    jQuery('.piece').removeClass('selected');
    if (index != undefined) {
      if ($scope.pieces[index].black == !($scope.turn % 2))
        return $scope.selectedIndex = undefined;
      jQuery(event.target).closest('.piece').addClass('selected');
    }
  }

  $scope.selectSquare = function(event) {
    var el = jQuery(event.target);
    var col = el.index();
    var row = el.closest('tr').index();
    if ($scope.selectedIndex != undefined) {
      if ($scope.movePiece($scope.selectedIndex, row, col))
        if (!$scope.ended)
          setTimeout($scope.makeAiMove, 200);
      $scope.selectPiece(undefined);
    }
  }

  var aiMoveTimeout;
  $scope.makeAiMove = function() {
    if (aiMoveTimeout) return;
    if ($scope.turn % 2) {
      if (!$scope.blackAi) return;
    } else {
      if (!$scope.whiteAi) return;
    }
    var move = ai.bestMove($scope);
    aiMoveTimeout = setTimeout(function() {
      aiMoveTimeout = null;
      $scope.movePiece(move.pieceIndex, move.row, move.col);
      $scope.$apply();
      setTimeout($scope.makeAiMove, 500);
    }, 500);
  }
  
  $scope.movePiece = function(pieceIndex, row, col) {
    if ($scope.ended) return;

    var piece = $scope.pieces[pieceIndex];
    if (!piece) return;
    if (piece.dead) return;

    if (!rules.validMove($scope, pieceIndex, row, col)) {
      return;
    }
    
    rules.applyMove($scope, pieceIndex, row, col);

    return true;
  }

  $scope.turn = 0;
  $scope.blackAi = true;
  $scope.pieces = [
    // top arrow
    { row: 0, col: 3, black: true },
    { row: 0, col: 4, black: true },
    { row: 0, col: 5, black: true },
    { row: 0, col: 6, black: true },
    { row: 0, col: 7, black: true },
    { row: 1, col: 5, black: true },
    // bottom arrow
    { row: 10, col: 3, black: true },
    { row: 10, col: 4, black: true },
    { row: 10, col: 5, black: true },
    { row: 10, col: 6, black: true },
    { row: 10, col: 7, black: true },
    { row: 9, col: 5, black: true },
    // left arrow
    { row: 3, col: 0, black: true },
    { row: 4, col: 0, black: true },
    { row: 5, col: 0, black: true },
    { row: 6, col: 0, black: true },
    { row: 7, col: 0, black: true },
    { row: 5, col: 1, black: true },
    // right arrow
    { row: 3, col: 10, black: true },
    { row: 4, col: 10, black: true },
    { row: 5, col: 10, black: true },
    { row: 6, col: 10, black: true },
    { row: 7, col: 10, black: true },
    { row: 5, col: 9, black: true },

    // king's inner guard ring
    { row: 4, col: 4, white: true },
    { row: 4, col: 5, white: true },
    { row: 4, col: 6, white: true },
    { row: 5, col: 6, white: true },
    { row: 6, col: 6, white: true },
    { row: 6, col: 5, white: true },
    { row: 6, col: 4, white: true },
    { row: 5, col: 4, white: true },
    // king's outer guards
    { row: 3, col: 5, white: true },
    { row: 7, col: 5, white: true },
    { row: 5, col: 3, white: true },
    { row: 5, col: 7, white: true },

    // king
    { row: 5, col: 5, whiteKing: true }
  ];
}]);
