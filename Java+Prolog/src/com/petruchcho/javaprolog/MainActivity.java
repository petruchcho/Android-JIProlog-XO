package com.petruchcho.javaprolog;

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

import com.petruchcho.javaprolog.Field.CellCoordinates;
import com.petruchcho.javaprolog.Field.FieldCell;
import com.petruchcho.javaprolog.Field.FieldCell.OnCellValueChangeListener;
import com.petruchcho.javaprolog.strategy.Move;
import com.petruchcho.javaprolog.strategy.XOAbstractStrategy;
import com.petruchcho.javaprolog.strategy.XOPetruchchoStrategy;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity implements OnCellValueChangeListener {

    private List<FieldCell> cells;

    private TextView debugText;
    private ToggleButton switchButton;

    private XOAbstractStrategy strategy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        strategy = XOPetruchchoStrategy.getInstance(this);
        strategy.setEventsListener(new XOAbstractStrategy.XOStrategyEventsListener() {
            @Override
            public void onGameOverWithWinner(XOAbstractStrategy.Player player) {
                declareResult(String.format("%s is winner!", player));
            }

            @Override
            public void onDraw() {
                declareResult("It's a draw!");
            }

            @Override
            public void onError(Exception e) {
                makeToast(e);
            }
        });

        initCells();
        debugText = (TextView) findViewById(R.id.debug_text);

        findViewById(R.id.play_again_button).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                clean();
            }
        });

        switchButton = (ToggleButton) findViewById(R.id.xo_switch);
        switchButton.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                clean();
            }
        });
        switchButton.setVisibility(View.GONE); // TODO

        makeMove(-1, -1);
    }

    private void clean() {

    }

    private void makeMove(int lastOpponentX, int lastOpponentY) {
        Move move = new Move.Builder(XOAbstractStrategy.Player.X)
                .setLastOpponentMove(new CellCoordinates(lastOpponentX, lastOpponentY))
                .build();
        try {
            CellCoordinates coordinates = strategy.makeMove(move);
            int x = coordinates.getX();
            int y = coordinates.getY();
            for (FieldCell cell : cells) {
                if (cell.getX() == x - 1 && cell.getY() == y - 1) {
                    cell.setValue(XOAbstractStrategy.Player.X);
                }
            }
        } catch (Exception e) {
            makeToast(e);
        }
    }

    private void initCells() {
        cells = new ArrayList<>();
        Button[][] buttons = new Button[][]{
                {(Button) findViewById(R.id.button1),
                        (Button) findViewById(R.id.button2),
                        (Button) findViewById(R.id.button3)},
                {(Button) findViewById(R.id.button4),
                        (Button) findViewById(R.id.button5),
                        (Button) findViewById(R.id.button6)},
                {(Button) findViewById(R.id.button7),
                        (Button) findViewById(R.id.button8),
                        (Button) findViewById(R.id.button9)},};
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
            targetCell.setValue(XOAbstractStrategy.Player.O); // TODO
            makeMove(targetCell.getX(), targetCell.getY());
        }
    };

    @Override
    public void onCellValueChanged(XOAbstractStrategy.Player value, CellCoordinates coordinates) {
        strategy.updateCellValue(value, coordinates);
    }
}
