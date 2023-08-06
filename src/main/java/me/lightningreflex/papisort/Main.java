package me.lightningreflex.papisort;

import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main extends PlaceholderExpansion {

    @Override
    public @NotNull String getAuthor() {
        return "LightningReflex";
    }

    @Override
    public @NotNull String getIdentifier() {
        return "sort";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0.0";
    }

    @Override
    public String onRequest(OfflinePlayer player, @NotNull String params) {
        // (\{.*})_(asc|desc)_([0-9]+)_(name|value)
        // Group 1: Placeholder to sort
        // Group 2: Sort direction
        // Group 3: Place in the sorted list to return

//        System.out.println("Params: " + params);

        Pattern pattern = Pattern.compile("(.*)_(asc|desc)_([0-9]+)_(name|value)");
        Matcher matcher = pattern.matcher(params);

        if (matcher.find()) {
//            System.out.println("A match was found!");

            String placeholder = matcher.group(1);
            String direction = matcher.group(2);
            int index = Integer.parseInt(matcher.group(3));
            if (index < 1) {
                index = 1;
            }
            String type = matcher.group(4);
//            System.out.println("Placeholder: " + placeholder);
//            System.out.println("Direction: " + direction);
//            System.out.println("Index: " + index);
//            System.out.println("Type: " + type);

            // PlaceholderAPI.setPlaceholders(player, placeholder);
            LinkedHashMap<String, Double> values = new LinkedHashMap<>();
            for (OfflinePlayer p : Bukkit.getOfflinePlayers()) {
                Object value = PlaceholderAPI.setBracketPlaceholders(p, "{" + placeholder + "}");
                Double doubleValue;
                try {
                    doubleValue = Double.parseDouble(value.toString());
                } catch (NumberFormatException e) {
                    doubleValue = null;
                }
                if (doubleValue != null) {
                    values.put(p.getName(), doubleValue);
                }
            }

            // System.out.println("Values: " + values);

            ArrayList<String> sortedKeys = new ArrayList<>(values.keySet());
            sortedKeys.sort((a, b) -> {
                if (direction.equals("asc")) {
                    return values.get(a).compareTo(values.get(b));
                } else {
                    return values.get(b).compareTo(values.get(a));
                }
            });

//             System.out.println("Sorted Keys: " + sortedKeys);

            if (index <= sortedKeys.size()) {
                String key = sortedKeys.get(index - 1);
                if (type.equals("name")) {
//                     System.out.println("Returning name: " + key);
                    return key;
                } else if (type.equals("value")) {
//                     System.out.println("Returning value: " + values.get(key));
                    return values.get(key).toString();
                }
            }

            // Out of bounds
//            return null;
            if (type.equals("name")) {
                return "N/A";
            } else if (type.equals("value")) {
                return "0";
            }
        }

        return null; // Placeholder is unknown by the Expansion
    }
}