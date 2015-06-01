package com.petruchcho.javaprolog;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.petruchcho.javaprolog.FieldCell.OnCellValueChangeListener;
import com.ugos.jiprolog.engine.JIPEngine;
import com.ugos.jiprolog.engine.JIPQuery;
import com.ugos.jiprolog.engine.JIPTerm;
import com.ugos.jiprolog.engine.JIPVariable;

public class MainActivity extends Activity implements OnCellValueChangeListener {

	private static final String prologFileName = "xo2.pl";

	private List<FieldCell> cells;
	private JIPEngine jip;
	private int moveNumber = 0;

	private char androidChar = 'x';

	private TextView debugText;
	private Button playAgainButton;
	private ToggleButton switchButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initCells();
		initProlog();
		debugText = (TextView) findViewById(R.id.debug_text);

		playAgainButton = (Button) findViewById(R.id.play_again_button);
		playAgainButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				clean();
				if (androidChar == 'x')
					makeMove(-1, -1);
			}
		});

		switchButton = (ToggleButton) findViewById(R.id.xo_switch);
		switchButton.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				clean();
				if (isChecked) {
					androidChar = 'x';
					makeMove(-1, -1);
				} else {
					androidChar = '0';
				}
			}
		});

		makeMove(-1, -1);
	}

	private void clean() {
		initProlog();
		for (FieldCell cell : cells) {
			cell.setValue('-');
			cell.setEnabled(true);
		}
		debugText.setText("");
		moveNumber = 0;
	}

	private void initProlog() {
		jip = new JIPEngine();
		try {
			jip.consultStream(getAssets().open(prologFileName), prologFileName);
//			for (int i = 1; i <= 3; i++)
//				for (int j = 1; j <= 3; j++) {
//					jip.asserta(jip.getTermParser().parseTerm(
//							String.format("p(-, [%s,%s]).", i, j)));
//				}
		} catch (IOException e) {
			makeToast(e);
		}
	}

	private void makeMove(int lastOX, int lastOY) {
		// String question = String.format(
		// "?- move('%s', %s, %s, %s, X, Y, Message)",
		// String.valueOf(androidChar), lastOX + 1, lastOY + 1, 0);
		String question = String.format("?- hod(%s, P).",
				String.valueOf(androidChar));
		JIPQuery query = jip.openSynchronousQuery(jip.getTermParser()
				.parseTerm(question));
		JIPTerm solution;
		boolean found = false;
		int solutionCount = 0;
		while ((solution = query.nextSolution()) != null && !found) {
			solutionCount++;
			Hashtable<String, JIPVariable> map = solution.getVariablesTable();
			String point = map.get("P").getValue().toString();
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
			for (FieldCell cell : cells) {
				if (cell.getX() == x - 1 && cell.getY() == y - 1 && !found) {
					cell.setValue(androidChar);
					found = true;
					break;
				}
			}
			// Hashtable<String, JIPVariable> map =
			// solution.getVariablesTable();
			// String message = map.get("Message").getValue().toString();
			// if (message.equals("'Continue'") ||
			// message.contains("this move")) {
			// int x = Integer.parseInt(map.get("X").getValue().toString());
			// int y = Integer.parseInt(map.get("Y").getValue().toString());
			// for (FieldCell cell : cells) {
			// if (cell.getX() == x - 1 && cell.getY() == y - 1) {
			// cell.setValue(androidChar);
			// found = true;
			// break;
			// }
			// }
			//
			// if (message.contains("Win this move")) {
			// declareResult("Андроид победил!");
			// return;
			// } else if (moveNumber == 9) {
			// declareResult("Ничья!");
			// return;
			// }
			//
			// } else {
			// if (message.contains("Win")) {
			// declareResult("Андроид уже победил!");
			// } else if (message.contains("Lose")) {
			// declareResult("Андроид проиграл..");
			// } else {
			// declareResult("Ничья!");
			// return;
			// }
			// return;
			// }
			found = true;
		}
		//debugText.setText(""+solutionCount);
	}

	private void initCells() {
		cells = new ArrayList<FieldCell>();
		Button[][] buttons = new Button[][] {
				{ (Button) findViewById(R.id.button1),
						(Button) findViewById(R.id.button2),
						(Button) findViewById(R.id.button3) },
				{ (Button) findViewById(R.id.button4),
						(Button) findViewById(R.id.button5),
						(Button) findViewById(R.id.button6) },
				{ (Button) findViewById(R.id.button7),
						(Button) findViewById(R.id.button8),
						(Button) findViewById(R.id.button9) }, };
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				FieldCell cell = new FieldCell(i, j, buttons[i][j]);
				cell.setOnCLickListener(humanMoveListener);
				cell.setOnCellValueChangedListener(this);
				cells.add(cell);
			}
		}
	}

	private void declareResult(String message) {
		// Toast.makeText(this, message, Toast.LENGTH_LONG).show();
		debugText.setText(message);
		for (FieldCell cell : cells) {
			cell.setEnabled(false);
		}
	}

	private void makeToast(Exception e) {
		Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
	}

	private OnClickListener humanMoveListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			FieldCell targetCell = null;
			for (FieldCell cell : cells) {
				if (v.getId() == cell.getId()) {
					targetCell = cell;
				}
			}
			targetCell.setValue(androidChar == 'x' ? '0' : 'x');
			makeMove(targetCell.getX(), targetCell.getY());
		}
	};

	@Override
	public void onCellValueChanged(int x, int y, char value) {
		// jip.retract(jip.getTermParser().parseTerm(
		// String.format("a([%s, %s], ' ').", x + 1, y + 1)));
		// jip.asserta(jip.getTermParser().parseTerm(
		// String.format("a([%s, %s], '%s').", x + 1, y + 1,
		// String.valueOf(value))));
		jip.retract(jip.getTermParser().parseTerm(
				String.format("p(-, [%s, %s]).", x + 1, y + 1)));
		jip.asserta(jip.getTermParser().parseTerm(
				String.format("p(%s, [%s, %s]).", String.valueOf(value), x + 1,
						y + 1)));
		moveNumber++;
	}

}
