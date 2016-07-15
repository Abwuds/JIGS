
/**
 * Created by Baxtalou on 19/05/2016.
 */
public class Container10Main {
    public static void main(String[]args){
        Runnable r = new Runnable() {
            @Override
            public void run() {
            }
        };
        System.out.println("Hello");
        Container10<String, Runnable, String> c = new Container10<String, Runnable, String>("hello", r, "fox");
        System.out.println(c.getT());
        System.out.println(c.getU());
        System.out.println(c.getX());
        c.setT("NewFieldValue");
        System.out.println(c.getT());
        System.out.println(c.getT() + " " + c.getX() + " " + c.getU());
    }
}
