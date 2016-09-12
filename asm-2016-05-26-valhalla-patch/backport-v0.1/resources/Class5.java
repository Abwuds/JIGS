package fr.upem.any;

/**
 * Created by Baxtalou on 19/05/2016.
 */
public class Class5<any T> {
    T mother;

    public Class5(T mother) {
        this.mother = mother;
    }

    public T getMother() {
        return mother;
    }

    public class Classe5Inner {
        T child;

        public Classe5Inner(T child) {
            this.child = child;
        }

        public T getChild() {
            return child;
        }
    }
}
