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
    private final Rules rules;
    private final boolean playingForBlack;

	public Minimax(BoardStateStack stack, IBlackEvaluator blackEvaluator, Rules rules) {
		this.stack = stack;
        this.blackEvaluator = blackEvaluator;
        this.rules = rules;
        this.playingForBlack = stack.board.blackTurn();
	}

	public List<Move> bestMove(int depth) {
		List<Move> moves = rules.getValidMoves();
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

		List<Move> moves = rules.getValidMoves();
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
}
