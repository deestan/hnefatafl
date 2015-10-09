'use strict';

angular.module('myApp.rules', [])
.factory('rules', function() {
  function pieceAt(board, row, col) {
    for (var i=0; i < board.pieces.length; i++) {
      var piece = board.pieces[i];
      if (piece.row == row && piece.col == col && !piece.dead)
        return piece;
    }
  }

  function validMove(board, pieceIndex, row, col) {
    var pieces = board.pieces;
    var turn = board.turn;
    var piece = pieces[pieceIndex];

    function checkOwnPiece() {
      return (!(turn % 2) ==
              !piece.black)
    }
    if (!checkOwnPiece()) return;

    function checkOrthogonal() {
      return ((piece.row == row) != (piece.col == col));
    }
    if (!checkOrthogonal()) return;

    function checkActualMove() {
      return (piece.row != row || piece.col != col);
    }
    if (!checkActualMove()) return;

    function checkNoJump() {
      var d_row = 0, d_col = 0;
      var test_row = piece.row;
      var test_col = piece.col;
      if (row < piece.row) d_row = -1;
      if (row > piece.row) d_row =  1;
      if (col < piece.col) d_col = -1;
      if (col > piece.col) d_col =  1;
      while (test_row != row || test_col != col) {
        test_row += d_row;
        test_col += d_col;
        if (pieceAt(board, test_row, test_col))
          return false;
      }
      return true;
    }
    if (!checkNoJump()) return;

    function checkBlackToEscapeSquare() {
      if (!piece.black) return true;
      if (col == 0 && row == 0) return;
      if (col == 0 && row == 10) return;
      if (col == 10 && row == 0) return;
      if (col == 10 && row == 10) return;
      return true;
    }
    if (!checkBlackToEscapeSquare()) return;
    
    return true;
  }

  function isWhiteWin(board) {
    var isWin = false;
    board.pieces.forEach(function(piece) {
      if (piece.whiteKing && !piece.dead)
        if ((piece.row ==  0 && piece.col ==  0) ||
            (piece.row ==  0 && piece.col == 10) ||
            (piece.row == 10 && piece.col ==  0) ||
            (piece.row == 10 && piece.col == 10))
          isWin = true;
    });
    return isWin;
  }

  function isBlackWin(board) {
    var isWin = true;
    board.pieces.forEach(function(piece) {
      if (piece.whiteKing && !piece.dead)
        isWin = false;
    });
    return isWin;
  }

  function applyMove(board, pieceIndex, row, col) {
    var piece = board.pieces[pieceIndex];
    piece.row = row;
    piece.col = col;
    board.turn += 1;

     function allies(piece1, piece2) {
      if (piece1.black && piece2.black) return true;
      if (piece1.white && piece2.white) return true;
      if (piece1.white && piece2.whiteKing) return true;
      if (piece1.whiteKing && piece2.white) return true;
      return false;
    }

    function killSurrounded(d_row, d_col) {
      var victim = pieceAt(board, row + d_row, col + d_col);
      if (!victim) return;
      var helper = pieceAt(board, row + d_row * 2, col + d_col * 2);
      if (!helper) return;
      if (!allies(piece, helper)) return;
      if (allies(piece, victim)) return;
      victim.dead = true;
    }
    killSurrounded(-1,  0);
    killSurrounded( 1,  0);
    killSurrounded( 0, -1);
    killSurrounded( 0,  1);

    function checkWhiteWin() {
      if (isWhiteWin(board))
        board.ended = { whiteWin: true };
    }
    checkWhiteWin();

    function checkBlackWin() {
      if (isBlackWin(board))
        board.ended = { blackWin: true };
    }
    checkBlackWin();
  }

  return { validMove: validMove,
           isBlackWin: isBlackWin,
           isWhiteWin: isWhiteWin,
           applyMove: applyMove
         };
});
