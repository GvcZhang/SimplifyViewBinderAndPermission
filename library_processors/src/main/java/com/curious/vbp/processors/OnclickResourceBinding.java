package com.curious.vbp.processors;

public final class OnclickResourceBinding {
    private final int[] resIds;
    private final String methodName;

    public OnclickResourceBinding(int[] resIds, String methodName) {
        this.resIds = resIds;
        this.methodName = methodName;
    }

    public int[] getResIds() {
        return resIds;
    }

    public String getMethodName() {
        return methodName;
    }
}
