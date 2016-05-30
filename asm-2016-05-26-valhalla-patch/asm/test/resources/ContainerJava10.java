package fr.upem.any;

/**
 * Created by Baxtalou on 19/05/2016.
 */
public class ContainerJava10<any T> {
    T v;

    public ContainerJava10(T v) {
        this.v = v;
    }

    public ContainerJava10<T> clone() {
        return new ContainerJava10<T>(v);
    }

    public T getV() {
        return v;
    }
}
