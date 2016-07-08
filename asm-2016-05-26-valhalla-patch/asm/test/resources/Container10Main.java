
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
        Container10<int, Runnable, float> c = new Container10<int, Runnable, float>(6, r, 2.3f);
        System.out.println(c.getT());
    }
}
