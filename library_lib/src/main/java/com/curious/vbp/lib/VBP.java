package com.curious.vbp.lib;

import android.app.Activity;
import android.view.View;

import java.util.LinkedHashMap;
import java.util.Map;


/**
 * View Binder Utils
 */
public class VBP {

    static final Map<Class<?>, ViewBinder<Object>> BINDERS = new LinkedHashMap<>();
    public static final String SUFFIX = "$$VBP";

    public static void bind(Activity target) {
        _bind(Finder.ACTIVITY, target, target);
    }

    public static void bind(Activity target, Object source) {
        _bind(Finder.ACTIVITY, source, target);
    }

    public static void bind(View source, Object target) {
        _bind(Finder.VIEW, source, target);
    }

    private static void _bind(Finder finder, Object source, Object target) {

        Class<?> clazz = target.getClass();
        ViewBinder<Object> viewBinder = BINDERS.get(clazz);
        if (viewBinder == null) {
            try {
                Class<?> tmp = Class.forName(clazz.getName() + SUFFIX);
                viewBinder= (ViewBinder<Object>) tmp.newInstance();
                BINDERS.put(clazz, viewBinder);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            }
        }
        viewBinder.bind(finder, source, target);
    }

    public static void unbind(Object source) {
        BINDERS.remove(source.getClass());
    }
}
