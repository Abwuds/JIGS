import java.util.Map;
import java.util.AbstractMap;
/**
 * Created by Baxtalou on 19/05/2016.
 */
public class EqualatorMain {

    public static void main(String[] args) {
        Holder<float> h = new Holder<float>(4.2f);
        Holder<float> h2 = new Holder<float>(4.2f);
        Equalator<float> eq = new Equalator<float>(h, h2);
        System.out.println(Boolean.toString(eq.isEquals()));
        h.f();

        QuadrupleHolder<float, float, byte, char> quadrupleHolder = new QuadrupleHolder<float, float, byte, char>(6.6f, 2.2f, (byte)3, 'a');
        quadrupleHolder.print();
    }
}
