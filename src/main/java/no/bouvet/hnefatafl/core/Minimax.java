package no.bouvet.hnefatafl.core;

import com.google.common.base.Optional;

import java.util.List;
import java.util.Vector;

public class Minimax {
	private enum Direction {
		MIN,
		MAX
	}

    private final IBlackEvaluator blackEvaluator;
	private final BoardStateStack stack;
    private final boolean playingForBlack;

	public Minimax(BoardStateStack stack, IBlackEvaluator blackEvaluator) {
		this.stack = stack;
        this.blackEvaluator = blackEvaluator;
        this.playingForBlack = stack.board.blackTurn();
	}

	public List<Move> bestMove(int depth) {
		List<Move> moves = getValidMoves();
		List<Move> bestMoves = new Vector<Move>();
		int bestScore = Integer.MIN_VALUE;
		for (Move m : moves) {
			applyMove(m);
			try {
				int score = search(depth - 1, Direction.MIN, Integer.MIN_VALUE, Integer.MAX_VALUE);
				if (score > bestScore) {
					bestMoves = new Vector<Move>();
					bestScore = score;
				}
				if (score == bestScore)
					bestMoves.add(m);
			} finally {
				stack.undoMove();
			}
		}

        return bestMoves;
	}

	private int search(int depth, Direction direction, int alpha, int beta) {
		if (depth == 0)
			return blackEvaluator.evaluate(stack.boardPieces) * (playingForBlack ? 1 : -1);

		List<Move> moves = getValidMoves();
		int bestScore = (direction == Direction.MAX)
			? Integer.MIN_VALUE
			: Integer.MAX_VALUE;
		for (Move m : moves) {
			applyMove(m);
			try {
				if (direction == Direction.MAX) {
					int score = search(depth - 1, Direction.MIN, alpha, beta);
					if (score > alpha)
						alpha = score;
					if (score > bestScore)
						bestScore = score;
				} else {
					int score = search(depth - 1, Direction.MAX, alpha, beta);
					if (score < beta)
						beta = score;
					if (score < bestScore)
						bestScore = score;
				}
				if (beta <= alpha)
					break;
			} finally {
				stack.undoMove();
			}
		}

		return bestScore;
	}

	private int capturedNeighbor(Piece moved, int dRow, int dCol) {
		int victimRow = moved.getRow() + dRow;
		int victimCol = moved.getCol() + dCol;
		int helperRow = victimRow + dRow;
		int helperCol = victimCol + dCol;
		int helperPerp1Row = victimRow + dCol;
		int helperPerp1Col = victimCol + dRow;
		int helperPerp2Row = victimRow - dCol;
		int helperPerp2Col = victimCol - dRow;
		int victimIndex = -1;
		Piece victim = null;
		Piece helper = null;
		Piece helperPerp1 = null;
		Piece helperPerp2 = null;
		boolean helpByHostileSquare =
			(helperRow == 0 && helperCol == 0) ||
			(helperRow == 10 && helperCol == 0) ||
			(helperRow == 0 && helperCol == 10) ||
			(helperRow == 10 && helperCol == 10) ||
			(helperRow == 5 && helperCol == 5);			
		for (int pIndex = 0; pIndex < stack.boardPieces.size(); pIndex++) {
			Piece p = stack.boardPieces.get(pIndex);
			if (p.isDead())
				continue;
			int row = p.getRow();
			int col = p.getCol();
			if (row == victimRow && col == victimCol) {
				victim = p;
				victimIndex = pIndex;
			}
			else if (row == helperRow && col == helperCol)
				helper = p;
			else if (row == helperPerp1Row && col == helperPerp1Col)
				helperPerp1 = p;
			else if (row == helperPerp2Row && col == helperPerp2Col)
				helperPerp2 = p;
		}

		if (victim == null)
			return -1;

		// test for victim different from attacker
		if (victim.isBlack() == moved.isBlack())
			return -1;

		// test for valid helper
		boolean validHelper = ((helper != null && helper.isBlack() == moved.isBlack()) ||
							   helpByHostileSquare);
		if (!validHelper)
			return -1;

		// king need to be surrounded
		if (victim.isWhiteKing())
			if (helper == null || helperPerp1 == null || helperPerp2 == null || !helperPerp1.isBlack() || !helperPerp2.isBlack())
				return -1;

		return victimIndex;
	}

	private void applyMove(Move move) {
		Piece p = stack.boardPieces.get(move.pieceIndex);
		stack.newMove(move);
		// Apply captures
		int captured;
		captured = capturedNeighbor(p, -1, 0);
		if (captured != -1) stack.capture(captured);
		captured = capturedNeighbor(p,  1, 0);
		if (captured != -1) stack.capture(captured);
		captured = capturedNeighbor(p, 0, -1);
		if (captured != -1) stack.capture(captured);
		captured = capturedNeighbor(p, 0,  1);
		if (captured != -1) stack.capture(captured);
	}
	
	private List<Move> getValidMoves() {
		boolean blackTurn = (stack.board.getTurn() % 2 == 1);
		List<Move> moves = new Vector<Move>();
		for (int pieceIndex = 0; pieceIndex < stack.boardPieces.size(); pieceIndex++) {
			Piece piece = stack.boardPieces.get(pieceIndex);
			if (piece.isDead())
				continue;
			if (piece.isBlack() != blackTurn)
				continue;
			for (int toRow = 0; toRow < 11; toRow ++)
				addMoveIfValid(moves, pieceIndex, toRow, piece.getCol());
			for (int toCol = 0; toCol < 11; toCol ++)
				addMoveIfValid(moves, pieceIndex, piece.getRow(), toCol);
		}
		return moves;
	}

	private void addMoveIfValid(List<Move> list, int pieceIndex, int toRow, int toCol) {
		Piece p = stack.boardPieces.get(pieceIndex);
		int fromRow = p.getRow();
		int fromCol = p.getCol();

		// Assume correct side is moving.

		// Actually move.
		if (fromRow == toRow && fromCol == toCol)
			return;

		// Peons cannot enter escape squares.
		if (!p.isWhiteKing())
			if ((toRow == 0 && toCol == 0) ||
				(toRow == 0 && toCol == 10) ||
				(toRow == 10 && toCol == 0) ||
				(toRow == 10 && toCol == 10))
				return;

		// Assume diagonal moves not attempted.

		// Do not move through or onto other pieces.
		int dRow = 0;
		int dCol = 0;
		if (fromRow < toRow) dRow = 1;
		if (fromRow > toRow) dRow = -1;
		if (fromCol < toCol) dCol = 1;
		if (fromCol > toCol) dCol = -1;
		int testRow = fromRow;
		int testCol = fromCol;
		while (testRow != toRow || testCol != toCol) {
			testRow += dRow;
			testCol += dCol;
			for (Piece otherPiece : stack.boardPieces)
				if (!otherPiece.isDead() &&
					otherPiece.getRow() == testRow &&
					otherPiece.getCol() == testCol)
					return;
		}

		// All move tests passed.
		list.add(new Move(pieceIndex, toRow, toCol));
	}
}
