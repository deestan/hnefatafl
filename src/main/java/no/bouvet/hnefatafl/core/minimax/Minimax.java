package no.bouvet.hnefatafl.core.minimax;

import no.bouvet.hnefatafl.core.Move;
import no.bouvet.hnefatafl.core.Rules;

import java.util.List;
import java.util.Vector;

public class Minimax {
	private enum Direction {
		MIN,
		MAX
	}

    private final IEvaluator blackEvaluator;
	private final IStateStack stack;
    private final Rules rules;

	public Minimax(IStateStack stack, IEvaluator blackEvaluator, Rules rules) {
		this.stack = stack;
        this.blackEvaluator = blackEvaluator;
        this.rules = rules;
	}

	public List<Move> bestMove(int depth) {
		List<Move> moves = rules.getValidMoves();
		List<Move> bestMoves = new Vector<Move>();
		int bestScore = Integer.MIN_VALUE;
		for (Move m : moves) {
		 	stack.pushMove(m);
			try {
				int score = search(depth - 1, Direction.MIN, Integer.MIN_VALUE, Integer.MAX_VALUE);
				if (score > bestScore) {
					bestMoves = new Vector<Move>();
					bestScore = score;
				}
				if (score == bestScore)
					bestMoves.add(m);
			} finally {
				stack.popMove();
			}
		}

        return bestMoves;
	}

	private int search(int depth, Direction direction, int alpha, int beta) {
		if (depth == 0)
			return blackEvaluator.evaluate(stack.currentState());

		List<Move> moves = rules.getValidMoves();
		int bestScore = (direction == Direction.MAX)
			? Integer.MIN_VALUE
			: Integer.MAX_VALUE;
		for (Move m : moves) {
			stack.pushMove(m);
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
				stack.popMove();
			}
		}

		return bestScore;
	}
}
