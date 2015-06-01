package com.petruchcho.javaprolog.strategy;

import com.petruchcho.javaprolog.field.CellCoordinates;

public class Move {

    public static class Builder {

        final XOAbstractStrategy.Player player;

        private CellCoordinates lastOpponentMove;
        private int moveNumber;

        public Builder(XOAbstractStrategy.Player player) {
            this.player = player;
        }

        public Builder setLastOpponentMove(CellCoordinates coordinates) {
            this.lastOpponentMove = coordinates;
            return this;
        }

        public Builder setMoveNumber(int moveNumber) {
            this.moveNumber = moveNumber;
            return this;
        }

        public Move build() {
            return new Move(this);
        }
    }

    private XOAbstractStrategy.Player player;
    private CellCoordinates lastOpponentMove;
    private int moveNumber;


    public Move(Builder builder) {
        this.player = builder.player;
        this.lastOpponentMove = builder.lastOpponentMove;
        this.moveNumber = builder.moveNumber;
    }

    public int getMoveNumber() {
        return moveNumber;
    }

    public CellCoordinates getLastOpponentMove() {
        return lastOpponentMove;
    }

    public XOAbstractStrategy.Player getPlayer() {
        return player;
    }
}
