package no.bouvet.hnefatafl.core.minimax;

import no.bouvet.hnefatafl.core.Move;

public interface IStateStack<T> {
    void pushMove(Move move);

    void popMove();

    T currentState();
}
