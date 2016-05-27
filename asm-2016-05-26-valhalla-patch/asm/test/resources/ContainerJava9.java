package fr.upem.any;

import java.lang.Runnable;

/**
 * Created by Baxtalou on 19/05/2016.
 */
public class ContainerJava9<T extends Runnable> {
    T v;

    public ContainerJava9(T v) {
        this.v = v;
    }

    public T getV() {
        return v;
    }
}
