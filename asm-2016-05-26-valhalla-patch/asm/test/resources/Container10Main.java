
/**
 * Created by Baxtalou on 19/05/2016.
 */
public class Container10Main {
    public static void main(String[] args) {
        // testContainer10();
        testAnyArrayList();
    }


    public static void testAnyArrayList() {
        AnyArrayList<Object> list = new AnyArrayList<Object>();
        System.out.println("List size : " + list.size());
        AnyArrayList<int> list2 = new AnyArrayList<int>();
        System.out.println("List2 size : " + list2.size());
    }


    public static void testContainer10() {
        Runnable r = new Runnable() {
            @Override
            public void run() {
            }
        };
        System.out.println("Hello");
        Container10<int, Runnable, float> c = new Container10<int, Runnable, float>(9, r, 42.2f);
        System.out.println(c.getT());
        System.out.println(c.getU());
        System.out.println(c.getX());
        c.setT(99);
        System.out.println(c.getT());
        // Old problem when calling toString here : System.out.println(c.getT() + " " + c.getX() + " " + c.getU());
    }


    /* Problem when compiling this with :
            javac -XDstringConcat=inline Container10Main.java -Xlint:unchecked
    public static void main(String[]args){
        Runnable r = new Runnable() {
            @Override
            public void run() {
            }
        };
        System.out.println("Hello");
        Container10<int, Runnable, float> c = new Container10<int, Runnable, float>(42, r, 66.6f);
        System.out.println(c.getT());
        System.out.println(c.getU());
        System.out.println(c.getX());
        c.setT(18);
        System.out.println(c.getT());
        System.out.println(c.getT() + " " + c.getX() + " " + c.getU());
    }
     */
}
