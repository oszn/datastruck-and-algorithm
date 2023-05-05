package invoke.demo;

public class WakeProxy implements Person {
    private Person p;

    public WakeProxy(Person obj) {
        this.p = obj;
    }

    @Override
    public void wakeup() {
        p.wakeup();
    }

    public static void main(String[] args) {
        Human h=new Human();
        WakeProxy w=new WakeProxy(h);
        w.wakeup();
    }
}
