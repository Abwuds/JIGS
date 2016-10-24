import java.util.Map;
import java.util.AbstractMap;
/**
 * Created by Baxtalou on 19/05/2016.
 */
public class EqualatorMain {

    public static void main(String[] args) {
        System.out.println("Here");
        Holder<int> h = new Holder<int>(3);
        System.out.println("Here");
        Holder<int> h2 = new Holder<int>(3);
        System.out.println("Here");
        Equalator<int> eq = new Equalator<int>(h, h2);
        System.out.println("Here");
        System.out.println(Boolean.toString(eq.isEquals()));
        h.f();
    }
}
