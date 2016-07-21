import java.util.Map;
import java.util.AbstractMap;
/**
 * Created by Baxtalou on 19/05/2016.
 */
public class AnyArrayList<any T> {
    T[] t;

    @SuppressWarnings("unchecked")
    public AnyArrayList(int size) { // TODO remove this fake argument.
        System.out.println("Inside the constructor ()AnyArrayList;");
        this.t = (T[]) new T[10];
    }

    public int size() {
        return t.length;
    }

    public T[] getT() {
        return t;
    }

    public void setT(T[] t) {
        this.t = t;
    }

    public String toString() {
        return "t : " + t;
    }
}
