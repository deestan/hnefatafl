'use strict';

angular.module('myApp.ai', ['myApp.rules'])
.factory('ai', ['rules', function(rules) {
  // returnMoveFactor of 10 means return every 10th valid move
  function allMoves(board, returnMoveFactor) {
    var validPieces = [];
    var skipper = (Math.random() * returnMoveFactor) >> 0;
    if (skipper >= returnMoveFactor)
      skipper = returnMoveFactor;
    board.pieces.forEach(function(piece, pieceIndex) {
      if (piece.dead) return;
      if (!(board.turn % 2) == !piece.black)
        validPieces.push({ black: piece.black, whiteKing: piece.whiteKing, index: pieceIndex });
    });

    var moves = [];
    function addMove(move, force) {
      if (skipper == 0 || force) {
        moves.push(move);
        skipper += returnMoveFactor;
      }
      skipper -= 1;
    }
    validPieces.forEach(function(piece, pieceIndex) {
      for (var row=0; row < 11; row++)
        for (var col=0; col < 11; col++)
          if (rules.validMove(board, piece.index, row, col))
            addMove({pieceIndex: piece.index, row: row, col: col}, piece.whiteKing);
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

  // Variables that are "global" per search
  var alphaBetaPruning = {
    min: undefined,
    max: undefined
  };
  var playingForBlack;

  function search(depth, board, minimize) {
    if (depth == 0) {
      var val = evaluateBoardForBlack(board);
      if (!playingForBlack)
        val = -val;
      if (val < alphaBetaPruning.min)
        alphaBetaPruning.min = val;
      if (val > alphaBetaPruning.max)
        alphaBetaPruning.max = val;
      return val;
    }

    var bestValue = minimize ? Infinity : -Infinity;
    function isBetterThan(score, otherScore) {
      if (minimize)
        return score < otherScore;
      return score > otherScore;
    }

    var moves = allMoves(board, [undefined, 100, 100, 1][depth]);

    if (moves.length == 0) {
      // Evaluate at this depth
      return search(0, board, minimize);
    }
    
    var bestMove;
    for (var i=0; i < moves.length; i++) {
      var move = moves[i];
      var newState = applyMove(board, move);
      var moveValue = search(depth - 1, newState, !minimize);
      if (isBetterThan(moveValue, bestValue)) {
        bestMove = move;
        bestValue = moveValue;
      }
      // Alpha-Beta pruning
      if (minimize) {
        if (val < alphaBetaPruning.max)
          return bestValue;
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
    alphaBetaPruning.min = Infinity;
    alphaBetaPruning.max = -Infinity;
    playingForBlack = !!board.turn;
    var moves = allMoves(board, 1);
    var bestMoves = [], bestValue = -Infinity;
    for (var i=0; i < moves.length; i++) {
      var move = moves[i];
      var newState = applyMove(board, move);
      var moveValue = search(3, newState, true);
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
