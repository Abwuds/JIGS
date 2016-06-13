
/**
 * Created by Baxtalou on 19/05/2016.
 */
public class ContainerJava10<any T, U, any X> {
    T v;
    X e;

    public ContainerJava10(T v, X e) {
        this.v = v;
        this.e = e;
        Object o = new Object();
    }

    public T getV() {
        return v;
    }

    public X getE() {
        return e;
    }
        
}
