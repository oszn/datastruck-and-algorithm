package invoke.demo;

public class Human extends People implements Person {
    private int name;

    @Override
    public void wakeup() {
        System.out.println("human");
    }

    @Override
    public void hello() {
        System.out.println("say hello to Human");
    }
    @Override
    public void hi(){
        System.out.println("hi human");
    }
}
