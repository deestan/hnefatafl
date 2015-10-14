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
		 	stack.applyMove(m);
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
			stack.applyMove(m);
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
}
