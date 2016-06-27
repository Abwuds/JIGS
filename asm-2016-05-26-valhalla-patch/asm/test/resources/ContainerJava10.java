package com.test.specialization;
/**
 * Created by Baxtalou on 19/05/2016.
 */
public class ContainerJava10<any T, U, any X> {
    T t;
    X x;
    U u;

    public ContainerJava10(T t, U u, X x) {
        this.t = t;
        this.x = x;
        this.u = u;
        Object o = new Object();
    }

    public T getT() {
        return t;
    }

    public X getX() {
        return x;
    }

    public U getU() {
        return u;
    }

    public String toString() {
        return "t : " + t + " u " + u + " x " + x;
    }
}
