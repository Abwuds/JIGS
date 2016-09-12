package fr.upem.any;

import java.lang.Runnable;

/**
 * Created by Baxtalou on 19/05/2016.
 */
public class ContainerJava8<T extends Runnable> {
    T t;

    public ContainerJava8(T t) {
        this.t = t;
    }

    public T getT() {
        return t;
    }

    public String toString() {
        return "t" + t;
    }
}
