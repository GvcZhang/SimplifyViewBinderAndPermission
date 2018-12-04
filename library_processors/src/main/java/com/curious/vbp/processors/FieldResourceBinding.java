package com.curious.vbp.processors;

public final class FieldResourceBinding {
    private final int id;
    private final String name;
    private final String method;

    public FieldResourceBinding(int id, String name, String method) {
        this.id = id;
        this.name = name;
        this.method = method;
    }


    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getMethod() {
        return method;
    }
}
