package com.petruchcho.javaprolog;

import android.view.View.OnClickListener;
import android.widget.Button;

public class FieldCell {

	public interface OnCellValueChangeListener {
		public void onCellValueChanged(int x, int y, char value);
	}

	private final int x, y;
	private final Button button;
	private char value;
	private OnCellValueChangeListener listener;

	public FieldCell(int x, int y, Button button) {
		super();
		this.x = x;
		this.y = y;
		this.button = button;
		value = ' ';
	}

	public char getValue() {
		return value;
	}

	public void setValue(char value) {
		this.value = value;
		button.setText(String.valueOf(value));
		listener.onCellValueChanged(x, y, value);
		button.setEnabled(false);
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
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
