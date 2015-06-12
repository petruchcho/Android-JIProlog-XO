package com.petruchcho.javaprolog.strategy;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.petruchcho.javaprolog.field.CellCoordinates;
import com.ugos.jiprolog.engine.JIPEngine;
import com.ugos.jiprolog.engine.JIPErrorEvent;
import com.ugos.jiprolog.engine.JIPEvent;
import com.ugos.jiprolog.engine.JIPEventListener;
import com.ugos.jiprolog.engine.JIPTerm;

import java.io.IOException;

public abstract class XOAbstractPrologStrategy extends XOAbstractStrategy {

    private JIPEngine jipEngine = new JIPEngine();
    private Context context;

    private static final String TAG = "AbstractPrologStrategy";

    public XOAbstractPrologStrategy(@NonNull Context context, XOStrategyEventsListener eventsListener) {
        super(eventsListener);
        initProlog(context);
    }

    private void initProlog(@NonNull Context context) {
        this.context = context;
        jipEngine.reset();
        try {
            jipEngine.consultStream(context.getAssets().open(getFileName()), getFileName());
        } catch (IOException e) {
            if (eventsListener != null) {
                eventsListener.onError(e);
            }
        }
    }

    @Override
    public final void makeMove(final Move move) throws Exception {
        String question = buildQuestion(move);
        JIPEngine jip = getJipEngine();
        jip.closeAllQueries();

        if (jip.getEventListeners().size() == 0) {
            jip.addEventListener(new JIPEventListener() {
                @Override
                public void solutionNotified(JIPEvent jipEvent) {
                    if (eventsListener != null) {
                        eventsListener.moveMade(move.getPlayer(), useSolution(jipEvent.getTerm(), move));
                    }
                    Log.d(TAG, "Solution notified");
                    jipEngine.closeAllQueries();
                }

                @Override
                public void termNotified(JIPEvent jipEvent) {

                }

                @Override
                public void openNotified(JIPEvent jipEvent) {

                }

                @Override
                public void moreNotified(JIPEvent jipEvent) {
                    Log.d(TAG, "More notified");
                }

                @Override
                public void endNotified(JIPEvent jipEvent) {
                    Log.d(TAG, "End notified");
                }

                @Override
                public void closeNotified(JIPEvent jipEvent) {
                    Log.d(TAG, "Close notified");
                }

                @Override
                public void errorNotified(JIPErrorEvent jipErrorEvent) {

                }
            });
        }

        jip.openQuery(jip.getTermParser().parseTerm(question));
    }

    @Override
    public void clearState() {
        initWithField(new Player[3][3]);
    }

    public JIPEngine getJipEngine() {
        return jipEngine;
    }

    protected abstract String getFileName();

    protected abstract String buildQuestion(Move move);

    protected abstract CellCoordinates useSolution(JIPTerm solution, Move move);
}
