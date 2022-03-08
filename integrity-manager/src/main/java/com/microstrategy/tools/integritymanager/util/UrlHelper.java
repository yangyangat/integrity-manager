package com.microstrategy.tools.integritymanager.util;

public class UrlHelper {
    private static final String PATH_SEPARATOR = "/";

    public enum PathNames {
        PROJECT_ID("projectId"),
        REPORT_ID("reportId"),
        REPORT_INSTANCE_ID("instanceId"),
        PROMPT_ID("promptId"),
        ;

        private final String name;

        PathNames(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public String getPattern() {
            return "{{" + name + "}}";
        }
    }

    public static String joinUrl(String... paths) {
        StringBuilder urlBuilder = new StringBuilder();
        if (paths.length > 0) {
            for (String path : paths) {
                urlBuilder.append(path.startsWith(PATH_SEPARATOR) ? path.substring(1) : path);
                if (!path.endsWith(PATH_SEPARATOR)) {
                    urlBuilder.append(PATH_SEPARATOR);
                }
            }
        }
        return urlBuilder.deleteCharAt(urlBuilder.length() - 1).toString();
    }

    public static String replaceUrl(String url, PathNames pathName, String objectId) {
        return url.replace(pathName.getPattern(), objectId);
    }
}
