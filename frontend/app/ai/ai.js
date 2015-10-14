'use strict';

angular.module('myApp.ai', [])
.factory('ai', ['$http', function($http) {
  function findBestMoveOffloaded(board, callback) {
    $http({
      method: 'POST',
      url: 'http://localhost:8080/ponder',
      data: { pieces: board.pieces,
              turn: board.turn
            }
    }).then(function good(response) {
      callback(response.data);
    }, function bad(response) {
      callback(new Error(response));
    });
  }

  return { bestMove: findBestMoveOffloaded };
}]);
