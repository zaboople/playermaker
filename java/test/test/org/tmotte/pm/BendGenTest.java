package org.tmotte.pm;
import java.util.Arrays;
import org.tmotte.common.text.Log;

public class BendGenTest {
    // Note to self: Bend(delay, duration, toVolume)

    public static void main(String[] args) {
        Log.add("BendGen", "MyMidi3", "MidiTracker");
        test1();
    }
    private static void print(int channel, int amount, long tick) {
        if (amount > 16383 || amount <0)
            throw new RuntimeException("Illegal: "+amount);
        System.out.append("[test] Amount ").append(String.valueOf(amount)).append(" tick ").append(String.valueOf(tick)).append("\n");
    }

    private static void test1() {
        makeGen(100).handle(
            0, 0,
            Arrays.asList(new Bend(0, 100, 1))
        );
    }
    private static void test2() {
        makeGen(6).handle(
            0, 63,
            Arrays.asList(new Bend(5, 100, 0))
        );
    }

    private static void test3() {
        makeGen(6).handle(
            0, 127,
            Arrays.asList(new Bend(5, 100, 0))
        );
    }
    private static void test4() {
        makeGen(6).handle(
            0, 63,
            Arrays.asList(new Bend(5, 100, 127), new Bend(0, 100, 0))
        );
    }

    private static void test5() {
        makeGen(6).handle(
            0, 63,
            Arrays.asList(new Bend(5, 100, 65), new Bend(0, 100, 68))
        );
    }

    private static BendGen makeGen(int tickX) {
        return new BendGen(()->tickX, BendGenTest::print);
    }

}