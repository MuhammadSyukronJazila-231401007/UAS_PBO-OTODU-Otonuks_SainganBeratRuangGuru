package com.example.otodu.Utils;

import java.util.Random;

public class IDGenerator {
    public static int generateIntID() {
        int timePart = (int) (System.currentTimeMillis() % 10000); // 4 digit terakhir milidetik
        int randomPart = new Random().nextInt(90000) + 10000; // 5 digit random
        String idStr = String.valueOf(timePart) + randomPart; // gabung jadi string

        return Integer.parseInt(idStr); // ubah ke int
    }
}
