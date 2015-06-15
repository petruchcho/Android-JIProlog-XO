package com.petruchcho.javaprolog.strategy;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.petruchcho.javaprolog.field.CellCoordinates;
import com.petruchcho.javaprolog.field.FieldCell;
import com.ugos.jiprolog.engine.JIPEngine;
import com.ugos.jiprolog.engine.JIPTerm;
import com.ugos.jiprolog.engine.JIPVariable;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

public class XOTyugashovStrategy extends XOAbstractPrologStrategy {

    public static final String NAME = "Tyugashov Strategy";

    public XOTyugashovStrategy(@NonNull Context context, XOStrategyEventsListener eventsListener) {
        super(context, eventsListener);
        clearState();
    }

    @Override
    protected String getFileName() {
        return "tyugashov.pl";
    }

    @Override
    protected String buildQuestion(Move move) {
        return String.format("?- hod(%s,[X,Y]).", getPlayerCharacter(move.getPlayer()));
    }

    @Override
    protected CellCoordinates useSolution(JIPTerm solution) {
        Hashtable map = solution.getVariablesTable();
        String point = solution.toString();
        List<Integer> coordinates = new ArrayList<>();
        int x = -1, y = -1;
        for (char c : point.toCharArray()) {
            if (Character.isDigit(c)) {
                coordinates.add(c - '0');
            }
        }

        if (coordinates.size() < 2) return null;
        x = coordinates.get(coordinates.size() - 2);
        y = coordinates.get(coordinates.size() - 1);

        return x < 0 ? null : new CellCoordinates(y, x);
    }

    @Override
    public void updateCellValue(Player player, CellCoordinates cellCoordinates) {
        Log.d(NAME, "Update cell value at " + cellCoordinates.getX() + " " + cellCoordinates.getY());
        JIPEngine jip = getJipEngine();
        int x = cellCoordinates.getX();
        int y = cellCoordinates.getY();

        jip.retract(jip.getTermParser().parseTerm(
                String.format("p(-,[%s,%s]).", y, x)));
        jip.asserta(jip.getTermParser().parseTerm(
                String.format("p(%s,[%s,%s]).", getPlayerCharacter(player), y,
                        x)));
    }

    @Override
    protected String getPlayerCharacter(Player player) {
        if (player == null) return "-";
        return player == Player.X ? "x" : "0";
    }

    @Override
    public void initWithField(Player[][] field) {
        JIPEngine jip = getJipEngine();
        for (int x = 1; x <= 3; x++) {
            for (int y = 1; y <= 3; y++) {
                jip.retract(jip.getTermParser().parseTerm(
                        String.format("p(%s,[%s,%s]).", getPlayerCharacter(null), y, x)));

                jip.retract(jip.getTermParser().parseTerm(
                        String.format("p(%s,[%s,%s]).", getPlayerCharacter(Player.X), y, x)));

                jip.retract(jip.getTermParser().parseTerm(
                        String.format("p(%s,[%s,%s]).", getPlayerCharacter(Player.O), y, x)));

                jip.asserta(jip.getTermParser().parseTerm(
                        String.format("p(%s,[%s,%s]).", getPlayerCharacter(field[x - 1][y - 1]), y, x)));
            }
        }
    }
}
