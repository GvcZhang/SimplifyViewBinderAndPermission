package com.curious.vbp.lib;

import android.app.Activity;
import android.content.Context;
import android.view.View;

public enum Finder {


    ACTIVITY {
        @Override
        public Context getContext(Object source) {
            return (Activity) source;
        }

        @Override
        public View findView(Object source, int id) {
            return ((Activity) source).findViewById(id);
        }
    },
    VIEW {
        @Override
        public Context getContext(Object source) {
            return ((View) source).getContext();
        }

        @Override
        public View findView(Object source, int id) {
            return ((View) source).findViewById(id);
        }
    },;


    public abstract Context getContext(Object source);

    public abstract View findView(Object source, int id);


    public <T> T castFindView(Object source, int id) {
        View view = findView(source, id);
        T result = cast(view);
        return result;
    }

    @SuppressWarnings("unchecked")
    private <T> T cast(View view) {
        return (T) view;
    }


}
