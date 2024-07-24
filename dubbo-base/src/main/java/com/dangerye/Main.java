package com.dangerye;

public class Main {

    private static int test0 = 0;
    private int test1 = 0;
    private volatile int test2 = 0;

    private Main() {
        super();
    }

    public static void main(String[] args) throws Exception {
        final Main main = new Main();
        System.out.println("Hello world!");
        new Thread(() -> {
            while (main.test1 < 100) {
            }
            System.out.println("test1 done." + System.nanoTime());
        }).start();
        new Thread(() -> {
            while (main.test2 < 100) {
            }
            System.out.println("test2 done." + System.nanoTime());
        }).start();
        new Thread(() -> {
            while (test0 < 100) {
            }
            System.out.println("test0 done." + System.nanoTime());
        }).start();
        new Thread(() -> {
            for (int i = 0; i < 200; i++) {
                main.test1++;
                main.test2++;
                test0++;
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ignored) {
                }
            }
            System.out.println("for done." + System.nanoTime());
        }).start();
        System.in.read();
    }
}
