package com.petruchcho.javaprolog.field;

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
}
