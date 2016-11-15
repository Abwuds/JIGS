public class DoubleHolder<any T, any U> {
    T t;
    U u;

    public DoubleHolder(T t, U u) {
        this.t = t;
        this.u = u;
    }

    public void print() {
        System.out.println("The 4 values held are :\n\tt : " + t.toString() + "\n\tu : " + u.toString());
    }


    public T add(T t, T u) {
        int i = 2;
        double v = 5;
        T res = t;
        int j = 8;
        this.t = t;
        System.out.println("Add result : " + (i + j));
        return res;
    }
}