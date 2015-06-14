package com.petruchcho.javaprolog.ui;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.petruchcho.javaprolog.field.CellCoordinates;
import com.petruchcho.javaprolog.field.Field;
import com.petruchcho.javaprolog.field.FieldCell;
import com.petruchcho.javaprolog.strategy.Move;
import com.petruchcho.javaprolog.strategy.XOAbstractStrategy;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

abstract class XOAbstractActivity extends Activity implements FieldCell.OnCellValueChangeListener, XOAbstractStrategy.XOStrategyEventsListener {

    private static final int DELAY_BETWEEN_MOVES = 2000;
    private static final String TAG = "XOAbstractActivity";

    protected Field field;
    protected boolean isResultDeclared;

    private XOAbstractStrategy.Player currentPlayer = XOAbstractStrategy.Player.X;
    private CellCoordinates lastMove = new CellCoordinates(-1, -1);
    private List<XOAbstractStrategy> strategiesHolder;
    private Map<XOAbstractStrategy.Player, Controller> controllerForPlayer;
    private Map<XOAbstractStrategy.Player, Class<? extends XOAbstractStrategy>> strategyForPlayer;

    @Override
    final protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        initViews();
        initStrategiesDefault();
        initStrategies();
    }

    private void initStrategies() {
        isPaused(true);
        strategiesHolder = new ArrayList<>();
        for (Class clazz : getAvailableStrategies()) {
            new InitStrategyTask().execute(clazz);
        }
    }

    protected abstract int getLayoutId();

    protected abstract void initViews();

    protected abstract List<Class<? extends XOAbstractStrategy>> getAvailableStrategies();

    private void initStrategiesDefault() {
        controllerForPlayer = initDefaultControllerForPlayer();
        strategyForPlayer = initDefaultStrategyForPlayer();
    }

    protected void declareResult(String message) {
        field.setEnabled(false);
        isResultDeclared = true;
    }

    protected final XOAbstractStrategy getStrategy(Class<? extends XOAbstractStrategy> clazz) {
        for (XOAbstractStrategy strategy : strategiesHolder) {
            if (strategy.getClass().equals(clazz)) {
                return strategy;
            }
        }
        throw new IllegalStateException();
    }

    protected abstract Map<XOAbstractStrategy.Player, Controller> initDefaultControllerForPlayer();

    protected abstract Map<XOAbstractStrategy.Player, Class<? extends XOAbstractStrategy>> initDefaultStrategyForPlayer();

    protected Controller getControllerForPlayer(XOAbstractStrategy.Player p) {
        return controllerForPlayer.get(p);
    }

    protected final void makeMove() {
        if (getControllerForPlayer(getCurrentPlayer()) == Controller.ANDROID) {
            makeMove(createMoveForStrategy(getCurrentPlayer(), getLastMove(), 0));
        }
    }

    protected final void makeMove(Move move) {
        try {
            isPaused(true);
            getStrategyForPlayer(move.getPlayer()).makeMove(move);
        } catch (Exception e) {
            handleError(e);
        }
    }

    protected XOAbstractStrategy.Player getCurrentPlayer() {
        return currentPlayer;
    }

    protected Move createMoveForStrategy(XOAbstractStrategy.Player player, CellCoordinates lastOpponentMove, int moveNumber) {
        return new Move.Builder(player).setLastOpponentMove(lastOpponentMove).setMoveNumber(moveNumber).build();
    }

    public CellCoordinates getLastMove() {
        return lastMove;
    }

    protected void setLastMove(CellCoordinates lastMove) {
        this.lastMove = lastMove;
    }

    protected void clean() {
        for (Map.Entry<XOAbstractStrategy.Player, Controller> entry : controllerForPlayer.entrySet()) {
            if (entry.getValue() == Controller.ANDROID) {
                getStrategyForPlayer(entry.getKey()).clearState();
            }
        }
        field.clean();
        currentPlayer = XOAbstractStrategy.Player.X;
    }

    protected abstract void handleError(Exception e);

    protected XOAbstractStrategy getStrategyForPlayer(XOAbstractStrategy.Player p) {
        return getStrategy(strategyForPlayer.get(p));
    }

    protected void setStrategyForPlayer(XOAbstractStrategy.Player p, Class<? extends XOAbstractStrategy> clazz) {
        strategyForPlayer.put(p, clazz);
    }

    protected void setControllerForPlayer(XOAbstractStrategy.Player p, Controller c) {
        controllerForPlayer.put(p, c);
    }

    protected abstract void updateCell(int x, int y, XOAbstractStrategy.Player value);

    protected abstract void isPaused(boolean isPaused);

    protected void swapCurrentPlayer() {
        Log.d(TAG, "Swap current player to " + currentPlayer.getOpponent().name());
        currentPlayer = currentPlayer.getOpponent();
        if (getControllerForPlayer(currentPlayer) == Controller.ANDROID) {
            isPaused(true);
            if (getControllerForPlayer(currentPlayer.getOpponent()) == Controller.ANDROID) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        makeMove();
                    }
                }, DELAY_BETWEEN_MOVES);
            } else {
                makeMove();
            }
        } else {
            isPaused(false);
        }
    }

    @Override
    public void onCellValueChanged(XOAbstractStrategy.Player value, CellCoordinates coordinates) {
        for (Map.Entry<XOAbstractStrategy.Player, Controller> entry : controllerForPlayer.entrySet()) {
            if (entry.getValue() == Controller.ANDROID) {
                getStrategyForPlayer(entry.getKey()).updateCellValue(value, coordinates);
            }
        }
        swapCurrentPlayer();
    }

    public enum Controller {
        HUMAN, ANDROID
    }

    private class InitStrategyTask extends AsyncTask<Class<? extends XOAbstractStrategy>, Void, XOAbstractStrategy> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(XOAbstractStrategy strategy) {
            super.onPostExecute(strategy);
            strategiesHolder.add(strategy);
            if (strategiesHolder.size() == getAvailableStrategies().size()) {
                isPaused(false);
                makeMove();
            }
        }

        @SafeVarargs
        @Override
        protected final XOAbstractStrategy doInBackground(Class<? extends XOAbstractStrategy>... params) {
            Class<? extends XOAbstractStrategy> clazz = params[0];
            try {
                Constructor<? extends XOAbstractStrategy> constructor = clazz.getConstructor(Context.class, XOAbstractStrategy.XOStrategyEventsListener.class);
                return constructor.newInstance(XOAbstractActivity.this, XOAbstractActivity.this);
            } catch (Throwable e) {
                Log.d(TAG, e.getMessage());
            }
            return null;
        }
    }
}
