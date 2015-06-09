package com.petruchcho.javaprolog.strategy;

import android.content.Context;
import android.support.annotation.NonNull;

import com.petruchcho.javaprolog.field.CellCoordinates;
import com.ugos.jiprolog.engine.JIPEngine;
import com.ugos.jiprolog.engine.JIPQuery;
import com.ugos.jiprolog.engine.JIPTerm;
import com.ugos.jiprolog.engine.JIPVariable;

import java.util.Hashtable;

public class XOPetruchchoStrategy extends XOAbstractPrologStrategy {

    private static XOAbstractStrategy instance;

    public static XOAbstractStrategy getInstance(Context context) {
        if (instance == null) {
            instance = new XOPetruchchoStrategy(context);
        }
        return instance;
    }

    public XOPetruchchoStrategy(@NonNull Context context) {
        super(context);
    }

    @Override
    public void updateCellValue(Player player, CellCoordinates cellCoordinates) {
        JIPEngine jip = getJipEngine();
        int x = cellCoordinates.getX();
        int y = cellCoordinates.getY();

        jip.retract(jip.getTermParser().parseTerm(
                String.format("a([%s, %s], ' ').", x + 1, y + 1)));
        jip.asserta(jip.getTermParser().parseTerm(
                String.format("a([%s, %s], '%s').", x + 1, y + 1,
                        getPlayerCharacter(player))));
    }

    @Override
    public CellCoordinates makeMove(Move move) throws Exception {
        int lastOpponentX = move.getLastOpponentMove().getX();
        int lastOpponentY = move.getLastOpponentMove().getY();

        String question = String.format(
                "?- move('%s', %s, %s, %s, X, Y, Message)",
                getPlayerCharacter(move.getPlayer()), lastOpponentX, lastOpponentY, move.getMoveNumber());

        JIPEngine jip = getJipEngine();

        JIPQuery query = jip.openSynchronousQuery(jip.getTermParser()
                .parseTerm(question));
        JIPTerm solution;

        while ((solution = query.nextSolution()) != null) {
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
                    }
                }
            }
        }
        return null;
    }

    @Override
    protected String getPlayerCharacter(Player player) {
        return player == Player.X ? "X" : "O";
    }

    @Override
    protected String getFileName() {
        return "petruchcho_strategy.pl";
    }
}
