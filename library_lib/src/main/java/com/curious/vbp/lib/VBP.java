package com.curious.vbp.lib;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.view.View;

import java.util.*;


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

    @SuppressWarnings("unchecked")
    private static void _bind(Finder finder, Object source, Object target) {

        Class<?> clazz = target.getClass();
        ViewBinder<Object> viewBinder = BINDERS.get(clazz);
        if (viewBinder == null) {
            try {
                Class<?> tmp = Class.forName(clazz.getName() + SUFFIX);
                viewBinder = (ViewBinder<Object>) tmp.newInstance();
                BINDERS.put(clazz, viewBinder);
            } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
                e.printStackTrace();
            }
        }
        viewBinder.bind(finder, source, target);
    }

    public static void unbind(Object source) {
        BINDERS.remove(source.getClass());
    }


    /*****permission part******/


    public interface PermissionListener {
        void onGrant();

        void onDenied(List<String> deniedPermissions);
    }

    public static class Builder {


        private Set<String> permissions = new HashSet<>();
        private PermissionListener permissionListener;
        private Context context;
        private String rationaleTitle;
        private String rationaleMessage;
        private String neverAskReason;
        private String neverAskTitle;

        public Builder(Context context) {
            this.context = context;
        }

        public Builder withPermission(String... args) {
            Collections.addAll(permissions, args);
            return this;
        }

        public Builder withListener(PermissionListener listener) {
            this.permissionListener = listener;
            return this;
        }


        public Builder withRationale(String title, String message) {
            this.rationaleMessage = message;
            this.rationaleTitle = title;
            return this;
        }

        public Builder withNeverAskReason(String title,String reason) {
            this.neverAskReason = reason;
            this.neverAskTitle = title;
            return this;
        }


        public void check() {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                if (permissionListener != null) {
                    permissionListener.onGrant();
                }
            } else {
                boolean allGrant = true;
                ArrayList<String> result = new ArrayList<>();
                for (String permission : permissions) {
                    if (context.checkSelfPermission(permission) == PackageManager.PERMISSION_DENIED) {
                        result.add(permission);
                        allGrant = false;
                    }
                }
                if (allGrant) {
                    if (permissionListener != null) {
                        permissionListener.onGrant();
                    }
                } else {
                    if (permissionListener != null) {
                        VbpPermissionActivity.permissionListener = permissionListener;
                    }
                    Intent intent = new Intent(context, VbpPermissionActivity.class);
                    intent.putStringArrayListExtra(VbpPermissionActivity.EXTRA_PERMISSIONS, result);
                    intent.putExtra(VbpPermissionActivity.EXTRA_RATIONALE_MESSAGE, rationaleMessage);
                    intent.putExtra(VbpPermissionActivity.EXTRA_RATIONALE_TITLE, rationaleTitle);
                    intent.putExtra(VbpPermissionActivity.EXTRA_NEVER_ASK_REASON, neverAskReason);
                    context.startActivity(intent);
                }
            }
        }

    }

    public static Builder with(Context ctx) {
        return new Builder(ctx);
    }


}
