package me.aleiv.modeltool.utilities;

public class RandomUtils {

    public static int generateInt(int min, int max) {
        return (int) (Math.random() * (max - min) + min);
    }

}
