package com.petruchcho.javaprolog.strategy;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.petruchcho.javaprolog.field.CellCoordinates;
import com.ugos.jiprolog.engine.JIPEngine;
import com.ugos.jiprolog.engine.JIPErrorEvent;
import com.ugos.jiprolog.engine.JIPEvent;
import com.ugos.jiprolog.engine.JIPEventListener;
import com.ugos.jiprolog.engine.JIPQuery;
import com.ugos.jiprolog.engine.JIPTerm;
import com.ugos.jiprolog.engine.JIPVariable;

import java.util.Hashtable;

public class XOPetruchchoStrategy extends XOAbstractPrologStrategy {

    public static final String NAME = "Ponomarev Strategy";

    public XOPetruchchoStrategy(@NonNull Context context, XOStrategyEventsListener eventsListener) {
        super(context, eventsListener);
    }

    @Override
    public void updateCellValue(Player player, CellCoordinates cellCoordinates) {
        Log.d(NAME, "Update cell value at " + cellCoordinates.getX() + " " + cellCoordinates.getY());
        JIPEngine jip = getJipEngine();
        int x = cellCoordinates.getX();
        int y = cellCoordinates.getY();

        jip.retract(jip.getTermParser().parseTerm(
                String.format("a([%s, %s], ' ').", x, y)));
        jip.asserta(jip.getTermParser().parseTerm(
                String.format("a([%s, %s], '%s').", x, y,
                        getPlayerCharacter(player))));
    }

    @Override
    protected String buildQuestion(Move move) {
        int lastOpponentX = move.getLastOpponentMove().getX();
        int lastOpponentY = move.getLastOpponentMove().getY();

        return String.format(
                "?- move('%s', %s, %s, %s, X, Y, Message)",
                getPlayerCharacter(move.getPlayer()), lastOpponentX, lastOpponentY, move.getMoveNumber());
    }

    @Override
    protected CellCoordinates useSolution(JIPTerm solution, Move move) {
        Hashtable map = solution.getVariablesTable();
        String message = ((JIPVariable) map.get("Message")).getValue().toString();
        if (message.equals("'Continue'") || message.contains("this move")) {
            int x = Integer.parseInt(((JIPVariable) map.get("X")).getValue().toString());
            int y = Integer.parseInt(((JIPVariable) map.get("Y")).getValue().toString());
            if (message.contains("this move")) {
                if (eventsListener != null) {
                    eventsListener.onGameOverWithWinner(move.getPlayer());
                }
            }
            return new CellCoordinates(x, y);
        } else {
            if (eventsListener != null) {
                if (message.contains("Win")) {
                    eventsListener.onGameOverWithWinner(move.getPlayer());
                } else if (message.contains("Lose")) {
                    eventsListener.onGameOverWithWinner(move.getPlayer().getOpponent());
                } else {
                    eventsListener.onDraw();
                }
            }
            return null;
        }
    }

    @Override
    protected String getPlayerCharacter(Player player) {
        if (player == null) return " ";
        return player == Player.X ? "X" : "O";
    }

    @Override
    public void initWithField(Player[][] field) {
        JIPEngine jip = getJipEngine();
        for (int x = 1; x <= 3; x++) {
            for (int y = 1; y <= 3; y++) {
                jip.retract(jip.getTermParser().parseTerm(
                        String.format("a([%s, %s], '%s').", x, y,
                                getPlayerCharacter(null))));

                jip.retract(jip.getTermParser().parseTerm(
                        String.format("a([%s, %s], '%s').", x, y,
                                getPlayerCharacter(Player.X))));

                jip.retract(jip.getTermParser().parseTerm(
                        String.format("a([%s, %s], '%s').", x, y,
                                getPlayerCharacter(Player.O))));

                jip.asserta(jip.getTermParser().parseTerm(
                        String.format("a([%s, %s], '%s').", x, y,
                                getPlayerCharacter(field[x - 1][y - 1]))));
            }
        }
    }

    @Override
    protected String getFileName() {
        return "petruchcho_strategy.pl";
    }
}
