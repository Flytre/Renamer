package net.flytre.renamer.core;

import com.google.common.collect.BiMap;
import net.flytre.renamer.Renamer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.PlayerListEntry;

import java.util.function.Function;

public class NameProtect {
    public static String protect(String string, boolean format) {

        if(!Renamer.CONFIG.getConfig().enabled)
            return string;

        if (string.contains(" "))
            return protectSpace(string, format);
        return protectNoSpace(string, format);

    }

    public static String displayToActual(String string) {

        if(!Renamer.CONFIG.getConfig().enabled)
            return string;

        BiMap<String, String> displayToActual = NameHelper.ACTUAL_TO_DISPLAY_NAME.inverse();
        MinecraftClient client = MinecraftClient.getInstance();

        string = string.replaceAll("Me", client.getSession().getUsername());

        for (var entry : displayToActual.entrySet()) {
            string = string.replaceAll(entry.getKey(), entry.getValue());
        }
        return string;
    }


    private static String protectSpace(String string, boolean format) {
        MinecraftClient client = MinecraftClient.getInstance();

        Function<String, String> formatter = format ? str -> "§c" + str + "§r" : str -> str;

        if (client == null || client.player == null || client.world == null)
            return string;

        String me = client.getSession().getUsername();
        if (string.contains(me))
            string = string.replace(me, format ? "§6Me§r" : "Me");

        for (PlayerListEntry info : client.player.networkHandler.getPlayerList()) {
            String name = info.getProfile().getName().replace("§", "");
            if (name.length() > 1 && string.contains(name))
                string = string.replace(name, formatter.apply(NameHelper.getName(info, name)));
        }

        for (AbstractClientPlayerEntity player : client.world.getPlayers()) {
            String name = player.getName().getString();

            if (name.length() > 1 && string.contains(name))
                string = string.replace(name, formatter.apply(NameHelper.getName(player, name)));
        }

        return string;
    }

    private static String protectNoSpace(String string, boolean format) {
        MinecraftClient client = MinecraftClient.getInstance();

        Function<String, String> formatter = format ? str -> "§c" + str + "§r" : str -> str;

        if (client == null || client.player == null || client.world == null)
            return string;


        String me = client.getSession().getUsername();
        if (string.contains(me))
            return string.replace(me, format ? "§6Me§r" : "Me");


        String name;
        for (PlayerListEntry info : client.player.networkHandler.getPlayerList()) {
            name = info.getProfile().getName().replaceAll("§(?:\\w|\\d)", "");
            if (name.length() > 1 && string.contains(name)) {
                return string.replace(name, formatter.apply(NameHelper.getName(info, name)));
            }

        }

        for (AbstractClientPlayerEntity player : client.world.getPlayers()) {
            name = player.getName().getString();
            if (name.length() > 1 && string.contains(name)) {
                return string.replace(name, formatter.apply(NameHelper.getName(player, name)));
            }
        }

        return string;
    }
}
