package org.example;

import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        int value = 0b1100; // Binary literal
        System.out.println("Value in decimal: " + value); // Outputs: 12


        int[][] array = new int[][] {
                {0, 0, 0, 0, 0, 0, 0, 0},
                {50, 50, 50, 50, 50, 50, 50, 50},
                {10, 10, 20, 30, 30, 20, 10, 10},
                {5, 5, 10, 25, 25, 10, 5, 5},
                {0, 0, 0, 20, 20, 0, 0, 0},
                {5, -5, -10, 0, 0, -10, -5, 5},
                {5, 10, 10, -20, -20, 10, 10, 5},
                {0, 0, 0, 0, 0, 0, 0, 0}
        };
        System.out.println(array[6][1]);

        Move[][] array2 = new Move[6][2];
        System.out.println(array2[5][0]);
    }
}