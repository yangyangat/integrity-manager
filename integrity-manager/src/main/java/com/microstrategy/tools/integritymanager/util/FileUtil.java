package com.microstrategy.tools.integritymanager.util;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;

public class FileUtil {
    /**
     * Convert the json object to file.
     * @param jsonObject The json object.
     * @param absoluteFileName The output file name.
     * @throws IOException When fail to save the object.
     */
    public static void jsonObjectToFile(Object jsonObject, String absoluteFileName) throws IOException {
        if (jsonObject == null) {
            return;
        }
        File file = new File(absoluteFileName);
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.writeValue(file, jsonObject);
    }

    public static Object jsonFileToObject(File jsonFile, Class<? extends Object> objectType) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(jsonFile, objectType);
    }
}
