package no.bouvet.hnefatafl.core.minimax;

import no.bouvet.hnefatafl.core.Piece;

import java.util.List;

public interface IEvaluator<T> {
    int evaluate(T gameState);
}
