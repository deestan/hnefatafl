package no.bouvet.hnefatafl.core;

public class Move {
    public final int pieceIndex;
    public final int toRow;
    public final int toCol;

    public Move(int pieceIndex, int toRow, int toCol) {
        this.pieceIndex = pieceIndex;
        this.toRow = toRow;
        this.toCol = toCol;
    }

    @Override
    public String toString() {
        return "{\"pieceIndex\": " + pieceIndex + ", " +
                "\"row\": " + toRow + ", " +
                "\"col\": " + toCol + "}";
    }
}
