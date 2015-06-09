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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends XOAbstractActivity {

    private TextView debugText;
    private ToggleButton switchButton;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initViews() {
        initCells();
        debugText = (TextView) findViewById(R.id.debug_text);

        findViewById(R.id.play_again_button).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                clean();
            }
        });

        switchButton = (ToggleButton) findViewById(R.id.xo_switch);
        //switchButton.setOnCheckedChangeListener((buttonView, isChecked) -> clean());
        switchButton.setVisibility(View.GONE); // TODO
    }

    @Override
    protected void declareResult(String message) {
        debugText.setText(message);
        field.setEnabled(false);
    }

    @Override
    protected Map<XOAbstractStrategy.Player, Controller> initDefaultControllerForPlayer() {
        return new HashMap<XOAbstractStrategy.Player, Controller>() {{
            put(XOAbstractStrategy.Player.X, Controller.ANDROID);
            put(XOAbstractStrategy.Player.O, Controller.HUMAN);
        }};
    }

    @Override
    protected Map<XOAbstractStrategy.Player, XOAbstractStrategy> initDefaultStrategyForPlayer() {
        return new HashMap<XOAbstractStrategy.Player, XOAbstractStrategy>() {{
            XOAbstractStrategy strategy = new XOPetruchchoStrategy(MainActivity.this);
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
            put(XOAbstractStrategy.Player.X, strategy);
            put(XOAbstractStrategy.Player.O, null);
        }};
    }

    @Override
    protected void handleError(Exception e) {
        makeToast(e);
    }

    @Override
    protected void updateCell(int x, int y, XOAbstractStrategy.Player value) {
        field.getCell(x, y).setValue(value);
    }

    @Override
    protected void isPaused(boolean isPaused) {

    }

    private void clean() {

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

    private void makeToast(Exception e) {
        Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
    }

    protected View.OnClickListener humanMoveListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            FieldCell targetCell = field.getCell(v.getId());
            targetCell.setValue(getCurrentPlayer());
            setLastMove(targetCell.getCoordinates());
            swapCurrentPlayer();
        }
    };
}
