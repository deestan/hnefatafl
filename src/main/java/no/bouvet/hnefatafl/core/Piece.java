package no.bouvet.hnefatafl.core;

public class Piece {
    public static class Builder {
        public Piece piece;
        public Builder() {
            piece = new Piece();
        }
        public Builder row(int row) {
            piece.row = row;
            return this;
        }
        public Builder col(int col) {
            piece.col = col;
            return this;
        }
        public Builder isBlack() {
            piece.isBlack = true;
            return this;
        }
        public Builder isWhite() {
            piece.isWhite = true;
            return this;
        }
        public Builder isWhiteKing() {
            piece.isWhiteKing = true;
            return this;
        }
    }

	private int row;
	private int col;
	private boolean isWhite;
	private boolean isBlack;
	private boolean isWhiteKing;
	private boolean isDead;

	public int getRow() { return row; }
	public void setRow(int val) { row = val; }

	public int getCol() { return col; }
	public void setCol(int val) { col = val; }

	public boolean isWhite() { return isWhite; }
	public void setWhite(boolean val) { isWhite = val; }

	public boolean isBlack() { return isBlack; }
	public void setBlack(boolean val) { isBlack = val; }

	public boolean isWhiteKing() { return isWhiteKing; }
	public void setWhiteKing(boolean val) { isWhiteKing = val; }

	public boolean isDead() { return isDead; }
	public void setDead(boolean val) { isDead = val; }
}
