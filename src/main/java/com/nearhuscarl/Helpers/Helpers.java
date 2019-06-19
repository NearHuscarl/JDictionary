package com.nearhuscarl.Helpers;

//import com.google.gson.Gson;
//import org.apache.commons.io.FilenameUtils;

import com.google.gson.Gson;
import com.nearhuscarl.Models.Word;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;
import java.util.ArrayList;

public class Helpers {
//
//    public static void Execute(String path) throws IOException {
//        var folder = new File(path);
//        var listOfFiles = folder.listFiles();
//        var index = 0; // TODO: remove
//
//        if (listOfFiles == null) {
//            return;
//        }
////        var gson = new Gson();
//
//        for (var file: listOfFiles) {
//            if (index < 3 && file.isFile() && FilenameUtils.getExtension(file.getName()).equals("json")) {
//                var filePath = Paths.get(path, file.getName());
//                var content = Files.readAllLines(filePath);
//
//                System.out.println(FilenameUtils.getBaseName(file.getName()));
//                System.out.println(content);
//                index++;
////                gson.fromJson(content);
//            }
//        }
//    }



    public static String List2String(ArrayList<?> list) {
        return List2String(list, '\n');
    }

    public static String List2String(ArrayList<?> list, char seperator) {
        if (list == null || list.size() == 0) {
            return "[empty]";
        }

        var stringBuilder = new StringBuilder();
        var index = 0;
        stringBuilder.append('\n');

        for (var item: list) {
            if (index == list.size() - 1) {
                stringBuilder.append('\t').append(item.toString());
            } else {
                stringBuilder.append('\t').append(item.toString()).append(seperator);
            }
            index++;
        }

        return stringBuilder.toString();
    }
}
