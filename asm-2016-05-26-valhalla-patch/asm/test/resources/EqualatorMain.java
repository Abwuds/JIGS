import java.util.Map;
import java.util.AbstractMap;
/**
 * Created by Baxtalou on 19/05/2016.
 */
public class EqualatorMain {

    public static void main(String[] args) {
        Holder<int> h = new Holder<int>(3);
        Holder<int> h2 = new Holder<int>(3);
        Equalator<int> eq = new Equalator<int>(h, h2);
        System.out.println(h.element);
        System.out.println(Boolean.toString(eq.isEquals()));
        System.out.println("Changin value to : " + Integer.toString(7));
        h.element = 7;
        System.out.println("New value is : " + Integer.toString(h.element));
        System.out.println(Boolean.toString(eq.isEquals()));


        Holder<String> hh = new Holder<String>("Hello ");
        Holder<String> hh2 = new Holder<String>("Hello ");
        // TODO to access erased field, code the Front which behaves like an erased front.
        // hh.element = "Hello ";
        // hh2.element = "Hello ";
        Equalator<String> eqq = new Equalator<String>(hh, hh2);
        // System.out.println(hh2.element);
        System.out.println("Result is : " + Boolean.toString(eqq.isEquals()));
        hh.f();
    }
}
