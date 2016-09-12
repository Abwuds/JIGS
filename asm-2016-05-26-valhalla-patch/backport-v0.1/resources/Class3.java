package fr.upem.any;

/**
 * Created by Baxtalou on 19/05/2016.
 */
public class Class3 {
    public static <any T, any V> void methodStatic3(T t, V v) {
        System.out.println(t.toString() + v.toString());
    }
}
