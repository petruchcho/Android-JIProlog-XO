package com.petruchcho.javaprolog.field;

import android.view.View.OnClickListener;
import android.widget.Button;

import com.petruchcho.javaprolog.strategy.XOAbstractStrategy;

public final class FieldCell {

    public interface OnCellValueChangeListener {
        void onCellValueChanged(XOAbstractStrategy.Player value, CellCoordinates coordinates);
    }

    private final CellCoordinates coordinates;
    private final Button button;
    private XOAbstractStrategy.Player value;
    private OnCellValueChangeListener listener;

    public FieldCell(int x, int y, Button button) {
        super();
        coordinates = new CellCoordinates(x, y);
        this.button = button;
    }

    public XOAbstractStrategy.Player getValue() {
        return value;
    }

    public void setValue(XOAbstractStrategy.Player value) {
        this.value = value;
        button.setText(String.valueOf(value));
        listener.onCellValueChanged(value, coordinates);
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
