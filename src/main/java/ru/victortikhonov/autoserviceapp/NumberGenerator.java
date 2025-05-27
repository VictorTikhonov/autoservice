package ru.victortikhonov.autoserviceapp;


import org.springframework.stereotype.Component;

import java.time.LocalTime;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class NumberGenerator {

    // Перевод английских букв в русские для отображения
    private static final Map<Character, Character> engToRus = Map.of(
            'A', 'А',
            'B', 'Б',
            'C', 'В',
            'D', 'Г',
            'E', 'Д',
            'F', 'Е'
    );

    // Обратный перевод для поиска
    private static final Map<Character, Character> rusToEng = engToRus.entrySet().stream()
            .collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));


     public static String generateNumber()
     {
         long time = System.currentTimeMillis() / 1000;
         String hexTime = Long.toHexString(time).toUpperCase();

         return hexTime;
     }


    public static String toRussian(String engCode) {
        StringBuilder sb = new StringBuilder();
        for (char c : engCode.toCharArray()) {
            sb.append(engToRus.getOrDefault(c, c));
        }
        return sb.toString();
    }


    public static String toEnglish(String rusCode) {
        StringBuilder sb = new StringBuilder();
        for (char c : rusCode.toCharArray()) {
            sb.append(rusToEng.getOrDefault(c, c));
        }
        return sb.toString();
    }
}
