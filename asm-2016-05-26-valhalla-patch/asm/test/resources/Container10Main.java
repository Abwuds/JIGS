
/**
 * Created by Baxtalou on 19/05/2016.
 */
public class Container10Main {
    public static void main(String[] args) {
        Runnable r = new Runnable() {
            @Override
            public void run() {
            }
        };
        System.out.println("Hello");
        Container10<String, Runnable, String> c = new Container10<String, Runnable, String>("Arg1", r, "Arg3");
        System.out.println(c.getT());
        System.out.println(c.getU());
        System.out.println(c.getX());
        c.setT("newArg1");
        System.out.println(c.getT());
       // System.out.println(c.getT() + " " + c.getX() + " " + c.getU());
        varargsTest(new Object(),new Object(),new Object(),new Object());
    }


    public static void varargsTest(Object... arg) {
        for (Object o : arg) {
            System.out.println(o);
        }
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
