package com.oort.virgo.util;


import java.util.UUID;

public class StrUtil {
    private StrUtil() {
    }

    public static String[] chars = new String[] { "a", "b", "c", "d", "e", "f",
            "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s",
            "t", "u", "v", "w", "x", "y", "z", "0", "1", "2", "3", "4", "5",
            "6", "7", "8", "9", "A", "B", "C", "D", "E", "F", "G", "H", "I",
            "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V",
            "W", "X", "Y", "Z" };

    public static String[] chars2 = new String[] { "1", "2", "3", "4", "5", "6",
            "7", "8", "9", "1", "1", "2", "3", "4", "5", "6", "7", "8", "9",
            "2", "1", "2", "3", "4", "5", "6", "3", "1", "2", "3", "4", "5",
            "6", "7", "8", "9", "1", "2", "3", "4", "5", "6", "7", "8", "9",
            "1", "2", "3", "9", "8", "7", "6", "5", "4", "3", "2", "1", "4",
            "4", "5", "6", "7" };

    public static String generateShortUuid() {

        StringBuilder shortBuffer = new StringBuilder();
        String uuid = UUID.randomUUID().toString().replace("-", "");
        for (int i = 0; i < 8; i++) {
            String str = uuid.substring(i * 4, i * 4 + 4);
            int x = Integer.parseInt(str, 16);
            shortBuffer.append(chars[x % 0x3E]);
        }
        return shortBuffer.toString() + System.currentTimeMillis();

    }

    public static String trim(String str) {

        String result = str;
        if(str != null) {
            result = str.trim();
        }
        return result;
    }

    public static void main(String[] args) {
        System.out.println(generateShortUuid());
    }
}
