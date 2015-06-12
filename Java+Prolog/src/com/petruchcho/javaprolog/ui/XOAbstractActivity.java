package com.petruchcho.javaprolog.ui;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;

import com.petruchcho.javaprolog.field.CellCoordinates;
import com.petruchcho.javaprolog.field.Field;
import com.petruchcho.javaprolog.field.FieldCell;
import com.petruchcho.javaprolog.strategy.Move;
import com.petruchcho.javaprolog.strategy.XOAbstractStrategy;

import java.util.Map;

abstract class XOAbstractActivity extends Activity implements FieldCell.OnCellValueChangeListener, XOAbstractStrategy.XOStrategyEventsListener {

    private static final int DELAY_BETWEEN_MOVES = 2000;
    private XOAbstractStrategy.Player currentPlayer = XOAbstractStrategy.Player.X;
    private CellCoordinates lastMove = new CellCoordinates(-1, -1);

    protected Field field;
    protected boolean isResultDeclared;

    public enum Controller {
        HUMAN, ANDROID
    }

    private Map<XOAbstractStrategy.Player, Controller> controllerForPlayer;
    private Map<XOAbstractStrategy.Player, XOAbstractStrategy> strategyForPlayer;

    @Override
    final protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        initStrategies();
        initViews();
        if (getControllerForPlayer(currentPlayer) == Controller.ANDROID) {
            makeMove(createMoveForStrategy(getCurrentPlayer(), getLastMove(), 0));
        }
        //makeMove(createMoveForStrategy(getCurrentPlayer(), new CellCoordinates(-1, -1), 0));
    }

    protected abstract int getLayoutId();

    protected abstract void initViews();

    private void initStrategies() {
        controllerForPlayer = initDefaultControllerForPlayer();
        strategyForPlayer = initDefaultStrategyForPlayer();
    }

    protected void declareResult(String message) {
        field.setEnabled(false);
        isResultDeclared = true;
    }

    protected abstract Map<XOAbstractStrategy.Player, Controller> initDefaultControllerForPlayer();

    protected abstract Map<XOAbstractStrategy.Player, XOAbstractStrategy> initDefaultStrategyForPlayer();

    protected Controller getControllerForPlayer(XOAbstractStrategy.Player p) {
        return controllerForPlayer.get(p);
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

    protected void setLastMove(CellCoordinates lastMove) {
        this.lastMove = lastMove;
    }

    public CellCoordinates getLastMove() {
        return lastMove;
    }

    protected void clean() {
        for (Map.Entry<XOAbstractStrategy.Player, Controller> entry : controllerForPlayer.entrySet()) {
            if (entry.getValue() == Controller.ANDROID) {
                getStrategyForPlayer(entry.getKey()).clearState();
            }
        }
        field.clean();
    }

    protected abstract void handleError(Exception e);

    protected XOAbstractStrategy getStrategyForPlayer(XOAbstractStrategy.Player p) {
        return strategyForPlayer.get(p);
    }

    protected void setStrategyForPlayer(XOAbstractStrategy.Player p, XOAbstractStrategy strategy) {
        strategyForPlayer.put(p, strategy);
    }

    protected void setControllerForPlayer(XOAbstractStrategy.Player p, Controller c) {
        controllerForPlayer.put(p, c);
    }

    protected abstract void updateCell(int x, int y, XOAbstractStrategy.Player value);

    protected abstract void isPaused(boolean isPaused);

    protected void swapCurrentPlayer() {
        currentPlayer = currentPlayer.getOpponent();
        if (getControllerForPlayer(currentPlayer) == Controller.ANDROID) {
            isPaused(true);
            if (getControllerForPlayer(currentPlayer.getOpponent()) == Controller.ANDROID) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        makeMove(createMoveForStrategy(currentPlayer, lastMove, 0));
                    }
                }, DELAY_BETWEEN_MOVES);
            } else {
                makeMove(createMoveForStrategy(currentPlayer, lastMove, 0));
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
}
