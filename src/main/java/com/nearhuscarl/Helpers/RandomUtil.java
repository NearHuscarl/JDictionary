package com.nearhuscarl.Helpers;

import java.util.List;
import java.util.Random;

public class RandomUtil {
    private static Random random = new Random();

    public static <T> T getRandomItem(List<T> list) {
        return list.get(random.nextInt(list.size()));
    }
}
