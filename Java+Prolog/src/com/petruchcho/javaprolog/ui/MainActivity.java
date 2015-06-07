package com.petruchcho.javaprolog.ui;

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

import com.petruchcho.javaprolog.R;
import com.petruchcho.javaprolog.field.CellCoordinates;
import com.petruchcho.javaprolog.field.Field;
import com.petruchcho.javaprolog.field.FieldCell;
import com.petruchcho.javaprolog.field.FieldCell.OnCellValueChangeListener;
import com.petruchcho.javaprolog.strategy.Move;
import com.petruchcho.javaprolog.strategy.XOAbstractStrategy;
import com.petruchcho.javaprolog.strategy.XOPetruchchoStrategy;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity implements OnCellValueChangeListener {

    private Field field;

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

        makeMove(new CellCoordinates(-1, -1));
    }

    private void clean() {

    }

    private void makeMove(CellCoordinates lastOpponentMove) {
        Move move = new Move.Builder(XOAbstractStrategy.Player.X)
                .setLastOpponentMove(lastOpponentMove)
                .build();
        try {
            CellCoordinates coordinates = strategy.makeMove(move);
            int x = coordinates.getX();
            int y = coordinates.getY();
            FieldCell target = field.getCell(x - 1, y - 1);
            target.setValue(XOAbstractStrategy.Player.X);
        } catch (Exception e) {
            makeToast(e);
        }
    }

    private void initCells() {
        List<FieldCell> cells = new ArrayList<>();
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
        field = new Field(cells);
    }

    private void declareResult(String message) {
        debugText.setText(message);
        field.setEnabled(false);
    }

    private void makeToast(Exception e) {
        Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
    }

    private OnClickListener humanMoveListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            FieldCell targetCell = field.getCell(v.getId());
            targetCell.setValue(XOAbstractStrategy.Player.O); // TODO
            makeMove(targetCell.getCoordinates());
        }
    };

    @Override
    public void onCellValueChanged(XOAbstractStrategy.Player value, CellCoordinates coordinates) {
        strategy.updateCellValue(value, coordinates);
    }
}
