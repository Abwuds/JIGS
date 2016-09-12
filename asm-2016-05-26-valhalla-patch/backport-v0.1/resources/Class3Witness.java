package fr.upem.any;

/**
 * Created by Baxtalou on 19/05/2016.
 */
public class Class3Witness {
    public static <T, V> void methodStatic2(T t, V v) {
        System.out.println(t.toString() + v.toString());
    }
}
