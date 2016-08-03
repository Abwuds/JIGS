public class Holder<any E> {
    E element;

    public Holder(E e) {
        element = e;
    }

    public void f() {
        f2();
    }

    public void f2() {
        System.out.println("Hello from f2.");
    }
}