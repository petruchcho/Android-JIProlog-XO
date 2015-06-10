package com.petruchcho.javaprolog.strategy;

import android.content.Context;
import android.support.annotation.NonNull;

import com.petruchcho.javaprolog.field.CellCoordinates;
import com.petruchcho.javaprolog.field.FieldCell;
import com.ugos.jiprolog.engine.JIPEngine;
import com.ugos.jiprolog.engine.JIPTerm;
import com.ugos.jiprolog.engine.JIPVariable;

import java.util.Hashtable;

public class XOTyugashovStrategy extends XOAbstractPrologStrategy {

    public XOTyugashovStrategy(@NonNull Context context) {
        super(context);
    }

    @Override
    protected String getFileName() {
        return "tyugashov.pl";
    }

    @Override
    protected String buildQuestion(Move move) {
        return String.format("?- hod(%s, P).", String.valueOf(move.getPlayer()));
    }

    @Override
    protected CellCoordinates useSolution(JIPTerm solution, Move move) {
        Hashtable map = solution.getVariablesTable();
        String point = ((JIPVariable) map.get("P")).getValue().toString();
        int x = -1, y = -1;
        for (char c : point.toCharArray()) {
            if (Character.isDigit(c)) {
                if (x < 0) {
                    x = c - '0';
                } else {
                    y = c - '0';
                }
            }
        }
        return x < 0 ? null : new CellCoordinates(x, y);
    }

    @Override
    public void updateCellValue(Player player, CellCoordinates cellCoordinates) {
        JIPEngine jip = getJipEngine();
        int x = cellCoordinates.getX();
        int y = cellCoordinates.getY();

        jip.retract(jip.getTermParser().parseTerm(
                String.format("p(-, [%s, %s]).", x, y)));
        jip.asserta(jip.getTermParser().parseTerm(
                String.format("p(%s, [%s, %s]).", getPlayerCharacter(player), x,
                        y)));
    }

    @Override
    protected String getPlayerCharacter(Player player) {
        return player == Player.X ? "x" : "0";
    }
}
