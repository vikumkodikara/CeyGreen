package com.ceygreen.treatment_service.util;

public final class DiseaseNameUtil {

    private DiseaseNameUtil() {
    }

    public static String normalize(String rawName) {
        if (rawName == null)
            return "";
        return rawName
                .toLowerCase()
                .replace("___", " ") // PlantVillage triple-underscore separator
                .replace("_", " ") // single underscores
                .replace("-", " ")
                .replaceAll("[^a-z0-9 ]", "") // strip any other punctuation
                .replaceAll("\\s+", " ") // collapse multiple spaces
                .trim();
    }
}
