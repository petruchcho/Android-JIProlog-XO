package com.petruchcho.javaprolog.ui;

import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
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

    private ProgressBar progress;
    private Spinner spinnerX, spinnerO;
    private Handler handler = new Handler();
    private Button acceptButton;
    private Button playAgainButton;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initViews() {
        initCells();
        playAgainButton = (Button) findViewById(R.id.play_again_button);
        playAgainButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                updateStrategies();
                clean();
                if (getControllerForPlayer(getCurrentPlayer()) == Controller.ANDROID) {
                    makeMove();
                }
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

        acceptButton = (Button) findViewById(R.id.accept_button);
        acceptButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                updateStrategies();
                if (getControllerForPlayer(getCurrentPlayer()) == Controller.ANDROID) {
                    makeMove();
                }
            }
        });
    }

    @Override
    protected List<Class<? extends XOAbstractStrategy>> getAvailableStrategies() {
        return new ArrayList<Class<? extends XOAbstractStrategy>>() {{
            add(XOTyugashovStrategy.class);
            add(XOPetruchchoStrategy.class);
        }};
    }

    @Override
    protected void declareResult(String message) {
        super.declareResult(message);
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        playAgainButton.setEnabled(true);
        progress.setVisibility(View.GONE);
    }

    private void updateStrategies() {
        changeStrategyForPlayer(XOAbstractStrategy.Player.X, spinnerX.getSelectedItem().toString());
        changeStrategyForPlayer(XOAbstractStrategy.Player.O, spinnerO.getSelectedItem().toString());
    }

    private void changeStrategyForPlayer(XOAbstractStrategy.Player player, String name) {
        switch (name) {
            case XOPetruchchoStrategy.NAME: {
                XOAbstractStrategy strategy = getStrategy(XOPetruchchoStrategy.class);
                if (strategy == null) {
                    return;
                }
                strategy.initWithField(field.getField());
                setStrategyForPlayer(player, XOPetruchchoStrategy.class);
                setControllerForPlayer(player, Controller.ANDROID);
            }
            break;
            case XOTyugashovStrategy.NAME: {
                XOAbstractStrategy strategy = getStrategy(XOTyugashovStrategy.class);
                if (strategy == null) {
                    return;
                }
                strategy.initWithField(field.getField());
                setStrategyForPlayer(player, XOTyugashovStrategy.class);
                setControllerForPlayer(player, Controller.ANDROID);
            }
            break;
            default: {
                setControllerForPlayer(player, Controller.HUMAN);
            }
        }
        isPaused(false);
    }

    @Override
    protected Map<XOAbstractStrategy.Player, Controller> initDefaultControllerForPlayer() {
        return new HashMap<XOAbstractStrategy.Player, Controller>() {{
            put(XOAbstractStrategy.Player.X, Controller.ANDROID);
            put(XOAbstractStrategy.Player.O, Controller.HUMAN);
        }};
    }

    @Override
    protected Map<XOAbstractStrategy.Player, Class<? extends XOAbstractStrategy>> initDefaultStrategyForPlayer() {
        return new HashMap<XOAbstractStrategy.Player, Class<? extends XOAbstractStrategy>>() {{
            put(XOAbstractStrategy.Player.X, XOTyugashovStrategy.class);
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
                cell.setOnClickListener(humanMoveListener);
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

    @Override
    public void onCurrentPlayerWin() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                declareResult(getCurrentPlayer().name() + " is a winner!");
            }
        });
    }

    @Override
    public void onCurrentPlayerLose() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                declareResult(getCurrentPlayer().getOpponent().name() + " is a winner!");
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
    public void moveMade(final CellCoordinates coordinates) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (coordinates == null) {
                    return;
                }
                final int x = coordinates.getX();
                final int y = coordinates.getY();
                setLastMove(new CellCoordinates(x, y));

                updateCell(x, y, getCurrentPlayer());

            }
        });
    }
}
