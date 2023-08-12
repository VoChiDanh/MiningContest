package net.danh.miningcontest.Manager;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ChatManager {

    public static String colorize(String message) {
        return ColorManager.colorize(FileManager.getConfig().getString("prefix") + " " + message);
    }

    public static String colorizewp(String message) {
        return ColorManager.colorize(message);
    }

    public static List<String> colorize(String... message) {
        return Arrays.stream(message).map(ChatManager::colorize).collect(Collectors.toList());
    }

    public static List<String> colorize(List<String> message) {
        return message.stream().map(ChatManager::colorize).collect(Collectors.toList());
    }
}
