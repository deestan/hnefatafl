'use strict';

angular.module('myApp.ai', [])
.factory('ai', ['$http', function($http) {
  function findBestMoveOffloaded(board) {
    return $http({
      method: 'POST',
      url: '/api/ponder',
      data: { pieces: board.pieces,
              turn: board.turn
            }
    });
  }

  return { bestMove: findBestMoveOffloaded };
}]);
