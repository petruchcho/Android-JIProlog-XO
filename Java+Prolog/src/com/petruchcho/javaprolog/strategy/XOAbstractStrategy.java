package com.petruchcho.javaprolog.strategy;

import com.petruchcho.javaprolog.field.CellCoordinates;

public abstract class XOAbstractStrategy {

    public enum Player {
        X, O;

        public Player getOpponent() {
            return this == X ? O : X;
        }
    }

    public interface XOStrategyEventsListener {
        void onCurrentPlayerWin();

        void onCurrentPlayerLose();

        void onDraw();

        void onError(Exception e);

        void moveMade(CellCoordinates coordinates);
    }

    protected static final int SIZE_N = 3;
    protected static final int SIZE_M = 3;

    protected XOStrategyEventsListener eventsListener;

    public XOAbstractStrategy(XOStrategyEventsListener eventsListener) {
        this.eventsListener = eventsListener;
    }

    public abstract void clearState();

    public abstract void updateCellValue(Player player, CellCoordinates cellCoordinates);

    public abstract void makeMove(Move move) throws Exception;

    protected abstract String getPlayerCharacter(Player player);

    public abstract void initWithField(Player[][] field);
}
