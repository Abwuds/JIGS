import java.util.Map;
import java.util.AbstractMap;
/**
 * Created by Baxtalou on 19/05/2016.
 */
public class ContainerJava10__I<any T, U extends Runnable, any X> {
    T t;
    X x;
    U u;

    public ContainerJava10__I(T t, U u, X x) {
        this.t = t;
        this.x = x;
        this.u = u;
        // This Object Constant_CLASS of the constant pool as to have
        // its own, non modifiable UTF8 Constant_UTF8 entry.
        Object o = new Object();
        // Testing invocation imbricated.
        Map.Entry<String, Object> objectEntry = new AbstractMap.SimpleEntry<>("Hello", new Object());
        // Testing invocation of primitive type.
        InnerJava10<int> ii = new InnerJava10<int>(1);
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
