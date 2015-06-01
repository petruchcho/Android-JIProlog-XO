package com.petruchcho.javaprolog.Field;

import android.view.View.OnClickListener;
import android.widget.Button;

public final class FieldCell {

    public interface OnCellValueChangeListener {
        void onCellValueChanged(int x, int y, char value);
    }

    private final CellCoordinates coordinates;
    private final Button button;
    private char value;
    private OnCellValueChangeListener listener;

    public FieldCell(int x, int y, Button button) {
        super();
        coordinates = new CellCoordinates(x, y);
        this.button = button;
        value = ' ';
    }

    public char getValue() {
        return value;
    }

    public void setValue(char value) {
        this.value = value;
        button.setText(String.valueOf(value));
        listener.onCellValueChanged(coordinates.getX(), coordinates.getY(), value);
        button.setEnabled(false);
    }

    public int getX() {
        return coordinates.getX();
    }

    public int getY() {
        return coordinates.getY();
    }

    public int getId() {
        return button.getId();
    }

    public void setOnCLickListener(OnClickListener listener) {
        button.setOnClickListener(listener);
    }

    public void setOnCellValueChangedListener(OnCellValueChangeListener listener) {
        this.listener = listener;
    }

    public void setEnabled(boolean enabled) {
        button.setEnabled(enabled);
    }
}
