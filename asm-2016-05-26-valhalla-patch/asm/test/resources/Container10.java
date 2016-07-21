import java.util.Map;
import java.util.AbstractMap;
/**
 * Created by Baxtalou on 19/05/2016.
 */
public class Container10<any T, U extends Runnable, any X> {
    T t;
    X x;
    U u;

    public Container10(T t, U u, X x) {
        this.t = t;
        this.x = x;
        this.u = u;
        // This Object Constant_CLASS of the constant pool as to have
        // its own, non modifiable UTF8 Constant_UTF8 entry.
        Object o = new Object();
        // Testing invocation imbricated.
        Map.Entry<String, Object> objectEntry = new AbstractMap.SimpleEntry<>("Hello", new Object());
        System.out.println("Inside the Back constructor fox !");
    }

    public Container10(U u, X x) {
        this.x = x;
        this.u = u;
    }

    public T getT() {
        return t;
    }

    public X getX() {
        return x;
    }

    public U getU() {
        return u;
    }

    public void setT(T t) {
        this.t = t;
    }

    public void setX(X x) {
        this.x = x;
    }

    public void setU(U u) {
        this.u = u;
    }

    public String toString() {
        return "t : " + t + " u " + u + " x " + x;
    }

    public class InnerJava10<any E> {
        E ee;

        public InnerJava10(E ee) {
            this.ee = ee;
        }
    }
}
