package com.nearhuscarl.Helpers;

import com.google.gson.Gson;
import java.lang.reflect.Type;

public class JsonUtil {
    private static Gson gson = new Gson();

    public static String toJson(Object src) {
        return gson.toJson(src);
    }

    public static <T> T fromJson(String json,  Type typeOfT) {
        return gson.fromJson(json, typeOfT);
    }
}
