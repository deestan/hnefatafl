'use strict';

angular.module('myApp.ai', ['myApp.rules'])
.factory('ai', ['rules', function(rules) {
  function allMoves(board) {
    var validPieces = [];
    board.pieces.forEach(function(piece, pieceIndex) {
      if (piece.dead) return;
      if (!(board.turn % 2) == !piece.black)
        validPieces.push({ index: pieceIndex });
    });

    var moves = [];
    validPieces.forEach(function(piece, pieceIndex) {
      for (var row=0; row < 11; row++)
        for (var col=0; col < 11; col++)
          if (rules.validMove(board, piece.index, row, col))
            moves.push({pieceIndex: piece.index, row: row, col: col});
    });

    return moves;
  }

  function evaluateBoardForBlack(board) {
    var score = 0;
    if (rules.isWhiteWin(board))
      score += -100;
    if (rules.isBlackWin(board))
      score += 100;
    board.pieces.forEach(function(piece) {
      if (piece.dead) return;
      if (piece.black) {
        score += 1;
      } else {
        score -= 1;
      }
    });
    return score;
  }

  function search(depth, board, minimize, playingForBlack) {
    if (depth == 0) {
      var val = evaluateBoardForBlack(board);
      return playingForBlack ? val : -val;
    }

    var bestValue = minimize ? Infinity : -Infinity;
    function isBetterThan(score, otherScore) {
      if (minimize)
        return score < otherScore;
      return score > otherScore;
    }

    var moves = allMoves(board);
    var bestMove;
    for (var i=0; i < moves.length; i++) {
      var move = moves[i];
      var newState = applyMove(board, move);
      var moveValue = search(depth - 1, newState, !minimize, playingForBlack);
      if (isBetterThan(moveValue, bestValue)) {
        bestMove = move;
        bestValue = moveValue;
      }
    }
    return bestValue;
  }

  function applyMove(board, move) {
    var newBoard = {
      pieces: angular.copy(board.pieces),
      turn: board.turn,
      ended: board.ended
    };
    rules.applyMove(newBoard, move.pieceIndex, move.row, move.col);
    return newBoard;
  }

  function findBestMove(board) {
    var playingForBlack = !!board.turn;
    var moves = allMoves(board);
    var bestMoves = [], bestValue = -Infinity;
    for (var i=0; i < moves.length; i++) {
      var move = moves[i];
      var newState = applyMove(board, move);
      var moveValue = search(1, newState, true, playingForBlack);
      if (moveValue > bestValue) {
        bestMoves = [move];
        bestValue = moveValue;
      }
      if (moveValue == bestValue) {
        bestMoves.push(move);
      }
    }
    return bestMoves[Math.random() * bestMoves.length >> 0];
  }

  return { bestMove: findBestMove };
}]);
