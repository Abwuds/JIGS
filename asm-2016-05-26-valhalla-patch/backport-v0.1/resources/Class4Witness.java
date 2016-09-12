package fr.upem.any;

/**
 * Created by Baxtalou on 19/05/2016.
 */
public class Class4Witness {
    public class Classe4Inner<T> {
        T v;

        public Classe4Inner(T v) {
            this.v = v;
        }

        public T getV() {
            return v;
        }
    }
}
