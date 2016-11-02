/**
 * Created by Jefferson Mangue on 25/10/2016.
 */
public class IfTests {

    public void T() {

        String k = "PH";
        if (k == "1") {
            f1();
        } else if (k == "2" || k == "3") {
            f2();
        } else if (k == "4" || k == "5") {
            f3();
        }
    }

    public void f1() {
        System.out.println("Hello from f1");
    }

    public void f2() {
        System.out.println("Hello from f2");
    }

    public void f3() {
        System.out.println("Hello from f3");
    }
}
