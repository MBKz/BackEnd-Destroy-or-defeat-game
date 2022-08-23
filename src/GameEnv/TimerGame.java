package GameEnv;

public class TimerGame extends Thread {
    protected int Timer = 1;
    @Override
    public void run() {
        while (true) {
            try {
                sleep(1000);
                Timer++;
            } catch (InterruptedException e) {
                System.out.println("Exception in Timer Class!");
            }
        }
    }
    public int getTimer() {
        return Timer;
    }
}
