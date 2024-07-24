package com.dangerye;

import java.util.concurrent.TimeUnit;

public class TestMain {
    public static void main(String[] args) throws Exception {
        final Number number = new Number();
        for (int i = 0; i < 10; i++) {
            new Thread(() -> {
                for (int j = 0; j < 1000; j++) {
                    number.addNum();
                }
            }).start();
        }
        TimeUnit.SECONDS.sleep(1);
        System.out.println("num: " + number.num);
    }

    private static class Number {
        int num;

        public synchronized void addNum() {
            num++;
        }
    }
}
