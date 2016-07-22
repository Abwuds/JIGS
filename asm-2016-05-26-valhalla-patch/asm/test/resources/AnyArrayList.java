import java.util.Map;
import java.util.AbstractMap;
/**
 * Created by Baxtalou on 19/05/2016.
 */
public class AnyArrayList<any T> {
    T[] array;
    int top;;

    @SuppressWarnings("unchecked")
    public AnyArrayList() {
        array = (T[]) new T[10];
    }

    public int size() {
        return top;
    }

    public int capacity() {
        return array.length;
    }

    public void add(T t) {
        array[top++] = t;
    }

    public T get(int position) {
        return array[position];
    }

    public T[] getT() {
        return array;
    }

    public void setT(T[] array) {
        this.array = array;
    }


    // Problem with the metafactory generation.
    public String toString() {
        return "array : " + array;
    }
}
