package com.petruchcho.javaprolog.ui;

import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.petruchcho.javaprolog.R;
import com.petruchcho.javaprolog.field.CellCoordinates;
import com.petruchcho.javaprolog.field.Field;
import com.petruchcho.javaprolog.field.FieldCell;
import com.petruchcho.javaprolog.strategy.XOAbstractStrategy;
import com.petruchcho.javaprolog.strategy.XOPetruchchoStrategy;
import com.petruchcho.javaprolog.strategy.XOTyugashovStrategy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends XOAbstractActivity {

    private ProgressBar progress;
    private Spinner spinnerX, spinnerO;

    private Handler handler = new Handler();

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initViews() {
        initCells();

        findViewById(R.id.play_again_button).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {clean();
            }
        });

        progress = (ProgressBar) findViewById(R.id.progress);

        spinnerX = (Spinner) findViewById(R.id.spinner_x);
        spinnerX.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item,
                new String[]{XOTyugashovStrategy.NAME, XOPetruchchoStrategy.NAME, "Human"}));
        spinnerX.setSelection(0);

        spinnerO = (Spinner) findViewById(R.id.spinner_o);
        spinnerO.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item,
                new String[]{XOTyugashovStrategy.NAME, XOPetruchchoStrategy.NAME, "Human"}));
        spinnerO.setSelection(2);

        final AdapterView.OnItemSelectedListener strategyChangeListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                XOAbstractStrategy.Player player = view.getId() == spinnerX.getId() ? XOAbstractStrategy.Player.X : XOAbstractStrategy.Player.O;
                String strategy = ((CheckedTextView) view).getText().toString();
                changeStrategyForPlayer(player, strategy);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        };

        Button acceptButton = (Button) findViewById(R.id.accept_button);
        // TODO Not thread safe
        acceptButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                changeStrategyForPlayer(XOAbstractStrategy.Player.X, spinnerX.getSelectedItem().toString());
                changeStrategyForPlayer(XOAbstractStrategy.Player.O, spinnerO.getSelectedItem().toString());
                if (getControllerForPlayer(getCurrentPlayer()) == Controller.ANDROID) {
                    makeMove(createMoveForStrategy(getCurrentPlayer(), getLastMove(), 0));
                }
            }
        });
    }

    @Override
    protected void declareResult(String message) {
        super.declareResult(message);
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    // TODO Not thread safe
    private void changeStrategyForPlayer(XOAbstractStrategy.Player player, String name) {
        switch (name) {
            case XOPetruchchoStrategy.NAME: {
                XOAbstractStrategy strategy = new XOPetruchchoStrategy(MainActivity.this, MainActivity.this);
                strategy.initWithField(field.getField());
                setStrategyForPlayer(player, strategy);
                setControllerForPlayer(player, Controller.ANDROID);
            }
            break;
            case XOTyugashovStrategy.NAME: {
                XOAbstractStrategy strategy = new XOTyugashovStrategy(MainActivity.this, MainActivity.this);
                strategy.initWithField(field.getField());
                setStrategyForPlayer(player, strategy);
                setControllerForPlayer(player, Controller.ANDROID);
            }
            break;
            default: {
                setControllerForPlayer(player, Controller.HUMAN);
            }
        }
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
            // TODO Not thread safe
            XOAbstractStrategy strategy = new XOTyugashovStrategy(MainActivity.this, MainActivity.this);
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
        field.setEnabled(!isPaused);
        progress.setVisibility(isPaused ? View.VISIBLE : View.GONE);
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
                FieldCell cell = new FieldCell(i + 1, j + 1, buttons[i][j]);
                cell.setOnCLickListener(humanMoveListener);
                cell.setOnCellValueChangedListener(this);
                cells.add(cell);
            }
        }
        field = new Field(cells);
    }

    private void makeToast(Exception e) {
        Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        Log.e("PrologApp", Arrays.toString(e.getStackTrace()));
    }

    protected View.OnClickListener humanMoveListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            FieldCell targetCell = field.getCell(v.getId());
            if (targetCell.getValue() != null) {
                return;
            }
            setLastMove(targetCell.getCoordinates());
            targetCell.setValue(getCurrentPlayer());
        }
    };

    @Override
    public void onGameOverWithWinner(final XOAbstractStrategy.Player player) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                declareResult(String.format("%s is winner!", player));
            }
        });
    }

    @Override
    public void onDraw() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                declareResult("It's a draw!");
            }
        });
    }

    @Override
    public void onError(final Exception e) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                makeToast(e);
            }
        });
    }

    @Override
    public void moveMade(final XOAbstractStrategy.Player player, final CellCoordinates coordinates) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (coordinates == null) {
                    return;
                }
                final int x = coordinates.getX();
                final int y = coordinates.getY();
                setLastMove(new CellCoordinates(x, y));

                updateCell(x, y, player);

            }
        });
    }
}
