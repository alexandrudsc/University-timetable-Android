package com.developer.alexandru.orarusv;

/**
 * Created by alexandru on 9/17/16.
 * Simple class holding describing an element: Course, prof.
 */
public class Elem {
    public int id;
    public String name;
    public String link;
    Elem () {

    }
    Elem(int id){
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof Elem && this.id == ((Elem) o).id;
    }

    @Override
    public String toString() {
        return name;
    }
}
