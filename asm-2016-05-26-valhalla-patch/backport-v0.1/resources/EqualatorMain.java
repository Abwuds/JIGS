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
        System.out.println(Boolean.toString(eq.isEquals()));
        h.f();
    }
}
