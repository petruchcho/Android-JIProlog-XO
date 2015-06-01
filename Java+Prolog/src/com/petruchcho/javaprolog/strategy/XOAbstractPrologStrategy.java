package com.petruchcho.javaprolog.strategy;

import android.content.Context;
import android.support.annotation.NonNull;

import com.ugos.jiprolog.engine.JIPEngine;

import java.io.IOException;

public abstract class XOAbstractPrologStrategy extends XOAbstractStrategy{

    private JIPEngine jipEngine;

    public XOAbstractPrologStrategy(@NonNull Context context) {
        initProlog(context);
    }

    private void initProlog(@NonNull Context context) {
        jipEngine = new JIPEngine();
        try {
            jipEngine.consultStream(context.getAssets().open(getFileName()), getFileName());
        } catch (IOException e) {
            if (eventsListener != null) {
                eventsListener.onError(e);
            }
        }
    }

    public JIPEngine getJipEngine() {
        return jipEngine;
    }

    protected abstract String getFileName();
}
