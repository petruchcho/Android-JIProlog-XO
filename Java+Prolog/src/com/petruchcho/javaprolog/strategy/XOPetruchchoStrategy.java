package com.petruchcho.javaprolog.strategy;

import android.content.Context;
import android.support.annotation.NonNull;

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

    private boolean solutionWasUsed = false;

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
    public void makeMove(final Move move) throws Exception {
        int lastOpponentX = move.getLastOpponentMove().getX();
        int lastOpponentY = move.getLastOpponentMove().getY();

        String question = String.format(
                "?- move('%s', %s, %s, %s, X, Y, Message)",
                getPlayerCharacter(move.getPlayer()), lastOpponentX, lastOpponentY, move.getMoveNumber());

        final JIPEngine jip = getJipEngine();
        jip.closeAllQueries();
        if (jip.getEventListeners().size() == 0) {
            jip.addEventListener(new JIPEventListener() {
                @Override
                public void solutionNotified(JIPEvent jipEvent) {
                    if (eventsListener != null) {
                        eventsListener.moveMade(move.getPlayer(), useSolution(jipEvent.getTerm(), move));
                    }
                    jip.closeAllQueries();
                }

                @Override
                public void termNotified(JIPEvent jipEvent) {

                }

                @Override
                public void openNotified(JIPEvent jipEvent) {

                }

                @Override
                public void moreNotified(JIPEvent jipEvent) {

                }

                @Override
                public void endNotified(JIPEvent jipEvent) {

                }

                @Override
                public void closeNotified(JIPEvent jipEvent) {

                }

                @Override
                public void errorNotified(JIPErrorEvent jipErrorEvent) {

                }
            });
        }

        jip.openQuery(jip.getTermParser().parseTerm(question));
    }

    private CellCoordinates useSolution(JIPTerm solution, Move move) {
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
