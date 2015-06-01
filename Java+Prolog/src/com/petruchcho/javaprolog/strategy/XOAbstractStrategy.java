package com.petruchcho.javaprolog.strategy;

import android.content.Context;
import android.support.annotation.NonNull;

import com.petruchcho.javaprolog.Field.CellCoordinates;
import com.ugos.jiprolog.engine.JIPEngine;

import java.io.IOException;

public abstract class XOAbstractStrategy {

    public enum Player {
        X, O
    }

    public interface XOStrategyEventsListener {
        void onGameOverWithWinner(Player player);

        void onDraw();

        void onError(Exception e);
    }

    private JIPEngine jipEngine;
    private XOStrategyEventsListener eventsListener;

    public XOAbstractStrategy(@NonNull Context context) {
        initProlog(context);
    }

    private void initProlog(@NonNull Context context) {
        jipEngine = new JIPEngine();
        try {
            jipEngine.consultStream(context.getAssets().open(getFileName()), getFileName());
        } catch (IOException e) {
            if (eventsListener != null) {
                eventsListener.onError(e);
            }
        }
    }

    public abstract void updateCellValue(Player player, CellCoordinates cellCoordinates);

    public abstract void clearState();

    public abstract CellCoordinates makeMove(Move move);

    protected abstract String getPlayerCharacter(Player player);

    protected abstract String getFileName();
}
