package com.microstrategy.tools.integritymanager.comparator;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.MapDifference;
import com.google.common.collect.Maps;
import com.microstrategy.tools.integritymanager.util.FlatMapUtil;

import java.util.HashMap;
import java.util.Map;

public class ReportJsonComparator {
    static public MapDifference<String, Object> difference(String leftJson, String rightJson) {
        ObjectMapper mapper = new ObjectMapper();
        TypeReference<HashMap<String, Object>> type =
                new TypeReference<>() {
                };

        Map<String, Object> leftMap;
        Map<String, Object> rightMap;
        try {
            leftMap = mapper.readValue(leftJson, type);
            rightMap = mapper.readValue(rightJson, type);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }

        Map<String, Object> leftFlatMap = FlatMapUtil.flatten(leftMap);
        Map<String, Object> rightFlatMap = FlatMapUtil.flatten(rightMap);

        return Maps.difference(leftFlatMap, rightFlatMap);
    }

    static public void printDifference(MapDifference<String, Object> difference) {

        System.out.println("Entries only on the left\n--------------------------");
        difference.entriesOnlyOnLeft()
                .forEach((key, value) -> System.out.println(key + ": " + value));

        System.out.println("\n\nEntries only on the right\n--------------------------");
        difference.entriesOnlyOnRight()
                .forEach((key, value) -> System.out.println(key + ": " + value));

        System.out.println("\n\nEntries differing\n--------------------------");
        difference.entriesDiffering()
                .forEach((key, value) -> System.out.println(key + ": " + value));
    }

    public static void main(String[] args) {
        String leftJson = "{\n" +
                "  \"name\": {\n" +
                "    \"first\": \"John\",\n" +
                "    \"last\": \"Doe\"\n" +
                "  },\n" +
                "  \"address\": null,\n" +
                "  \"birthday\": \"1980-01-01\",\n" +
                "  \"company\": \"Acme\",\n" +
                "  \"occupation\": \"Software engineer\",\n" +
                "  \"phones\": [\n" +
                "    {\n" +
                "      \"number\": \"000000000\",\n" +
                "      \"type\": \"home\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"number\": \"999999999\",\n" +
                "      \"type\": \"mobile\"\n" +
                "    }\n" +
                "  ]\n" +
                "}";
        String rightJson = "{\n" +
                "  \"name\": {\n" +
                "    \"first\": \"Jane\",\n" +
                "    \"last\": \"Doe\",\n" +
                "    \"nickname\": \"Jenny\"\n" +
                "  },\n" +
                "  \"birthday\": \"1990-01-01\",\n" +
                "  \"occupation\": null,\n" +
                "  \"phones\": [\n" +
                "    {\n" +
                "      \"number\": \"111111111\",\n" +
                "      \"type\": \"mobile\"\n" +
                "    }\n" +
                "  ],\n" +
                "  \"favorite\": true,\n" +
                "  \"groups\": [\n" +
                "    \"close-friends\",\n" +
                "    \"gym\"\n" +
                "  ]\n" +
                "}";

        MapDifference<String, Object> difference = ReportJsonComparator.difference(leftJson, rightJson);

        System.out.println("Entries only on the left\n--------------------------");
        difference.entriesOnlyOnLeft()
                .forEach((key, value) -> System.out.println(key + ": " + value));

        System.out.println("\n\nEntries only on the right\n--------------------------");
        difference.entriesOnlyOnRight()
                .forEach((key, value) -> System.out.println(key + ": " + value));

        System.out.println("\n\nEntries differing\n--------------------------");
        difference.entriesDiffering()
                .forEach((key, value) -> System.out.println(key + ": " + value));
    }
}
