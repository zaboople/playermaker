package org.tmotte.pm;

public class SpreadTest {
    public static void main(String[] args) {
        spreadTest(4, 2);
        spreadTest(5, 2);
        spreadTest(10, 1);
        spreadTest(10, 3);
        spreadTest(20, 1);
        spreadTest(20, 2);
        spreadTest(20, 3);
        spreadTest(20, 4);
        spreadTest(20, 5);
        spreadTest(20, 6);
        spreadTest(21, 6);
        spreadTest(100, 14);
        spreadTest(100, 33);
        spreadTest(100, 37);
        spreadTest(100, 72);
        spreadTest(100, 99);
        spreadTest(100, 10);
    }

    private static void spreadTest(int time, int remainder) {
        System.out.println("\nspreadin "+time+" "+remainder);
        boolean[] array=Spreader.array(time, remainder);
        for (int i=0; i<array.length; i++){
            if (array[i]) remainder--;
            System.out.print(array[i] ?"+" :"_");
        }
        System.out.println();
        if (remainder!=0) throw new RuntimeException("Remainder: "+remainder);
    }
}