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

  function evaluateBoard(board, forBlack) {
    var score = 0;
    var goodForBlack = forBlack ? 1 : -1;
    if (rules.isWhiteWin(board))
      score += -100 * goodForBlack;
    if (rules.isBlackWin(board))
      score += 100 * goodForBlack;
    board.pieces.forEach(function(piece) {
      if (piece.dead) return;
      if (piece.black) {
        score += goodForBlack;
      } else {
        score -= goodForBlack;
      }
    });
    return score;
  }

  function search(depth, board, blackTurn) {
    if (depth == 0) {
      return evaluateBoard(board, !blackTurn);
    }

    var moves = allMoves(board, blackTurn);
    var bestMove, bestValue = -Infinity;
    for (var i=0; i < moves.length; i++) {
      var move = moves[i];
      var newState = applyMove(board, move);
      var moveValue = search(depth - 1, newState, !blackTurn);
      if (moveValue > bestValue) {
        bestMove = move;
        bestValue = moveValue;
      }
    }
    return bestValue;
  }

  function applyMove(board, move) {
    var newBoard = {
      pieces: angular.copy(board.pieces),
      turn: board.turn
    };
    rules.applyMove(newBoard, move.pieceIndex, move.row, move.col);
    return newBoard;
  }

  function bestMove(board) {
    var moves = allMoves(board);
    var bestMoves, bestValue = -Infinity;
    for (var i=0; i < moves.length; i++) {
      var move = moves[i];
      var newState = applyMove(board, move);
      var moveValue = search(1, newState, !!board.turn);
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
  return { bestMove: bestMove };
}]);
