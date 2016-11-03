public class QuadrupleHolder<any T, any U, any V, any W> {
    T t;
    U u;
    V v;
    W w;

    public QuadrupleHolder(T t, U u, V v, W w) {
        this.t = t;
        this.u = u;
        this.v = v;
        this.w = w;
    }

    public void print() {
        System.out.println("The 4 values held are :\n\tt : " + t.toString() + "\n\tu : " + u.toString() + "\n\tv : " + v.toString()
        + "\n\tw : " + w.toString());
    }
}