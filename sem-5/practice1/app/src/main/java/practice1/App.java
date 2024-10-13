package practice1;

public class App {
    public static void main(String[] args) throws InterruptedException {
        var pingSignal = new Object();
        var pongSignal = new Object();

        var pingThread = new Thread(() -> {
            try {
                for (int i = 0; i < 10; i++) {
                    System.out.print("PING ");
                    synchronized (pingSignal) {
                        pingSignal.notifyAll();
                    }
                    synchronized (pongSignal) {
                        pongSignal.wait();
                    }
                }
            } catch (InterruptedException e) {
                // ...
            }
        });

        var pongThread = new Thread(() -> {
            try {
                synchronized (pingSignal) {
                    pingSignal.wait();
                }

                for (int i = 0; i < 10; i++) {
                    System.out.print("PONG ");
                    synchronized (pongSignal) {
                        pongSignal.notifyAll();
                    }
                    if (i != 9) {
                        synchronized (pingSignal) {
                            pingSignal.wait();
                        }
                    }
                }
            } catch (InterruptedException e) {
                // ...
            }
        });

        pingThread.start();
        pongThread.start();

        pingThread.join();
    }
}
