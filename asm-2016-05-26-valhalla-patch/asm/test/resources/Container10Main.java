
/**
 * Created by Baxtalou on 19/05/2016.
 */
public class Container10Main {
    public static void main(String[]args){
        String e = new String("Hello Java World");
        System.out.println("Hello");
        Container10<int, String, float> c = new Container10<int, String, float>(6, 2.3f);
        System.out.println(c.getT());
    }
}
