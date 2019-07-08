package com.nearhuscarl.Helpers;

import java.util.List;
import java.util.stream.Collectors;

public class ListUtil {
    public static <T> List<T> distinct(List<T> list) {
        return list.stream().distinct().collect(Collectors.toList());
    }
}
