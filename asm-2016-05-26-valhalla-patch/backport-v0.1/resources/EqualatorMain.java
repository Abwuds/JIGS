import java.util.Map;
import java.util.AbstractMap;
/**
 * Created by Baxtalou on 19/05/2016.
 */
public class EqualatorMain {

    public static void main(String[] args) {
        Holder<double> h = new Holder<double>(4.2);
        Holder<double> h2 = new Holder<double>(4.2);
        Equalator<double> eq = new Equalator<double>(h, h2);
        System.out.println(Boolean.toString(eq.isEquals()));
        h.f();

        QuadrupleHolder<double, float, byte, char> quadrupleHolder = new QuadrupleHolder<double, float, byte, char>(6.6, 2.2f, (byte)3, 'a');
        quadrupleHolder.print();
    }
}
