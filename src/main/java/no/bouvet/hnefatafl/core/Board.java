package no.bouvet.hnefatafl.core;

import java.util.List;

import no.bouvet.hnefatafl.core.Piece;

public class Board {
	private int turn;
	private List<Piece> pieces;

	public int getTurn() { return turn; }
	public void setTurn(int val) { turn = val; }

	public List<Piece> getPieces() { return pieces; }
	public void setPieces(List<Piece> val) { pieces = val; }

    public boolean blackTurn() { return this.turn % 2 == 1; }
    public boolean whiteTurn() { return this.turn % 2 == 0; }
}