package fr.upem.any;

public class Class6Child<any T> extends Class6<T> {

    public Class6Child(T t) {
        super(t);
    }

    public T getT() {
        return t;
    }
}