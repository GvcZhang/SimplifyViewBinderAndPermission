package com.curious.vbp.lib;


public interface ViewBinder<T> {


    void bind(Finder finder, Object source, T target);


}
