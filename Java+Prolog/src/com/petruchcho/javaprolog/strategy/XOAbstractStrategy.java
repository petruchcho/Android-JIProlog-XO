package com.petruchcho.javaprolog.strategy;

import com.petruchcho.javaprolog.Field.CellCoordinates;

public abstract class XOAbstractStrategy {

    public enum Player {
        X, O
    }

    public interface XOStrategyEventsListener {
        void onGameOverWithWinner(Player player);

        void onDraw();

        void onError(Exception e);
    }

    private static final int SIZE_N = 3;
    private static final int SIZE_M = 3;

    protected XOStrategyEventsListener eventsListener;

    public void setEventsListener(XOStrategyEventsListener eventsListener) {
        this.eventsListener = eventsListener;
    }

    public abstract void updateCellValue(Player player, CellCoordinates cellCoordinates);

    public abstract void clearState();

    public abstract CellCoordinates makeMove(Move move);

    protected abstract String getPlayerCharacter(Player player);
}
