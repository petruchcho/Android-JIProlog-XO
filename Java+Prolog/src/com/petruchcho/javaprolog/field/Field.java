package com.petruchcho.javaprolog.field;

import com.petruchcho.javaprolog.strategy.XOAbstractStrategy;

import java.util.List;

public class Field {

    private List<FieldCell> cells;

    public Field(List<FieldCell> cells) {
        this.cells = cells;
    }

    public FieldCell getCell(CellCoordinates coordinates) {
        for (FieldCell cell : cells) {
            if (cell.getCoordinates().equals(coordinates)) {
                return cell;
            }
        }
        return null;
    }

    public FieldCell getCell(int x, int y) {
        return getCell(new CellCoordinates(x, y));
    }

    public FieldCell getCell(int id) {
        for (FieldCell cell : cells) {
            if (cell.getId() == id) {
                return cell;
            }
        }
        return null;
    }

    public void setEnabled(boolean enabled) {
        for (FieldCell cell : cells) {
            cell.setEnabled(enabled);
        }
    }

    public XOAbstractStrategy.Player[][] getField() {
        XOAbstractStrategy.Player[][] field = new XOAbstractStrategy.Player[3][3];
        for (int i = 1; i <= 3; i++) {
            for (int j = 1; j <= 3; j++) {
                field[i - 1][j - 1] = getCell(i, j).getValue();
            }
        }
        return field;
    }

    public void clean() {
        for (FieldCell cell : cells) {
            cell.clean();
        }
    }
}
