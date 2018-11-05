package win.shangyh.pros.concurent;

public class TestSequence {

    public static void main(String[] args) {
        NumberRange nr=new NumberRange();
        nr.setUpper(10);
        Thread t1=new Thread(){
            @Override
            public void run() {
                nr.setLower(6);
            }
        };
        t1.start();

        Thread t2=new Thread(){
            @Override
            public void run() {
                nr.setUpper(3);
            }
        };

        t2.start();

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println(nr.lower.get());
        System.out.println(nr.upper.get());
    }
}