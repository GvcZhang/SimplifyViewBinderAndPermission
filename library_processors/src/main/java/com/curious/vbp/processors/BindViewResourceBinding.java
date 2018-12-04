package com.curious.vbp.processors;

public final class BindViewResourceBinding {
    private final int resId;
    private final String name;
    //强制类型转换的类型
    private final String type;

    public BindViewResourceBinding(int resId, String name, String type) {
        this.resId = resId;
        this.name = name;
        this.type = type;
    }

    public int getResId() {
        return resId;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }
}
