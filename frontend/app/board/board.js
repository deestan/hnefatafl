'use strict';

angular.module('myApp.board', ['ngRoute', 'myApp.ai', 'myApp.rules'])

.config(['$routeProvider', function($routeProvider) {
  $routeProvider.when('/board', {
    templateUrl: 'board/board.html'
    //controller: 'BoardCtrl'
  });
}])

.controller('BoardCtrl', ['$scope', 'ai', 'rules', function($scope, ai, rules) {
  var socket = io("http://localhost:8001/");

  socket.on('board', function(board) {
    askingForBoard = false;
    $scope.pieces = board.pieces;
    $scope.turn = board.turn;
    $scope.ended = board.ended;
    $scope.$apply();
    makeAiMove();
  });

  var askingForBoard = false;
  function requestBoard() {
    askingForBoard = true;
    socket.emit('request-board', '');
  }

  socket.on('request-board', function() {
    if (askingForBoard) return;
    emitBoard();
  });

  function emitBoard() {
    socket.emit('board', { pieces: $scope.pieces, turn: $scope.turn, ended: $scope.ended });
  }

  $scope.selectPiece = function(index, event) {
    $scope.selectedIndex = index;
    jQuery('.piece').removeClass('selected');
    if (index != undefined) {

      // Is it the correct player's turn, and is it human controlled?
      if ($scope.pieces[index].black) {
        if (($scope.turn % 2) || $scope.ai.black)
          return $scope.selectedIndex = undefined;
      } else {
        if (!($scope.turn % 2) || $scope.ai.white)
          return $scope.selectedIndex = undefined;
      }

      jQuery(event.target).closest('.piece').addClass('selected');
    }
  };

  $scope.selectSquare = function(event) {
    var el = jQuery(event.target);
    var col = el.index();
    var row = el.closest('tr').index();
    if ($scope.selectedIndex != undefined) {
      if ($scope.movePiece($scope.selectedIndex, row, col))
        makeAiMove();
      $scope.selectPiece(undefined);
    }
  }

  function isAiMove() {
    if ($scope.turn % 2) {
      if (!$scope.ai.white) return false;
    } else {
      if (!$scope.ai.black) return false;
    }
    return true;
  }

  var aiMoveWait;
  var makeAiMove = function makeAiMove() {
    if (aiMoveWait) return;
    if ($scope.ended) return;
    if (!isAiMove()) return;
    ai.bestMove($scope).then(
        function success(result) {
          $scope.ai.serverError = null;
          if (!isAiMove()) return;
          var move = result.data;
          $scope.movePiece(move.pieceIndex, move.row, move.col);
          setTimeout(makeAiMove, 0);
        },
        function failure(error) {
          $scope.ai.serverError = null;
          if (error.status == 503) {
            $scope.ai.serverError = { busy: true };
          } else {
            $scope.ai.serverError = { down: true };
            console.error("Unexpected error from server.");
          }
          setTimeout(makeAiMove, 1000);
        }
    ).finally(function anyways() {
        aiMoveWait = false;
    });
    aiMoveWait = true;
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

    emitBoard();

    return true;
  }

  $scope.turn = 0;
  $scope.ai = {
    black: false,
    white: false
  };
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

  $scope.gameId = 'farts';

  function aiToggled() {
    $scope.ai.serverError = null;
    makeAiMove();
  }

  requestBoard();

  $scope.$watch("ai.black", aiToggled);
  $scope.$watch("ai.white", aiToggled);
}]);
