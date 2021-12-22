package net.flytre.renamer.core;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.mojang.authlib.GameProfile;
import net.flytre.renamer.Renamer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.entity.player.PlayerEntity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

public class NameHelper {


    public static final List<String> NAMES = new ArrayList<>();
    public static final Map<UUID, String> UUID_TO_NAME = new HashMap<>();
    public static final Set<String> USED_NAMES = new HashSet<>();
    public static final BiMap<String, String> ACTUAL_TO_DISPLAY_NAME = HashBiMap.create();

    static {
        try {
            ClassLoader classloader = Thread.currentThread().getContextClassLoader();
            InputStream inputStream = classloader.getResourceAsStream("names.txt");
            assert inputStream != null;
            InputStreamReader streamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
            BufferedReader reader = new BufferedReader(streamReader);
            for (String line; (line = reader.readLine()) != null; ) {
                NAMES.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String useRandomName(UUID uuid, String actual) {
        long unique = uuid.getMostSignificantBits() * Renamer.CONFIG.getConfig().seed + uuid.getLeastSignificantBits();
        Random generator = new Random(unique);

        if (USED_NAMES.size() > 250)
            clearUnusedNames();

        String str;
        do {
            str = NAMES.get(generator.nextInt(NAMES.size()));
        } while (USED_NAMES.contains(str));
        USED_NAMES.add(str);
        ACTUAL_TO_DISPLAY_NAME.put(actual, str);
        return str;
    }

    private static String getName(UUID uuid, String actual) {
        if (!UUID_TO_NAME.containsKey(uuid))
            UUID_TO_NAME.put(uuid, useRandomName(uuid, actual));

        return UUID_TO_NAME.get(uuid);
    }

    public static String getName(PlayerListEntry entry, String actual) {
        return getName(entry.getProfile().getId(), actual);
    }

    public static String getName(AbstractClientPlayerEntity player, String actual) {
        return getName(player.getGameProfile().getId(), actual);
    }

    public static void clearUnusedNames() {
        MinecraftClient client = MinecraftClient.getInstance();
        assert client.player != null;
        Set<UUID> uuids = client.player.networkHandler.getPlayerList()
                .stream()
                .map(PlayerListEntry::getProfile)
                .map(GameProfile::getId)
                .collect(Collectors.toSet());

        assert client.world != null;
        uuids.addAll(client.world.getPlayers()
                .stream()
                .map(PlayerEntity::getGameProfile)
                .map(GameProfile::getId)
                .collect(Collectors.toSet()));


        var iter = UUID_TO_NAME.entrySet().iterator();
        while (iter.hasNext()) {
            var entry = iter.next();
            if (!uuids.contains(entry.getKey())) {
                USED_NAMES.remove(entry.getValue());
                ACTUAL_TO_DISPLAY_NAME.inverse().remove(entry.getValue());
                iter.remove();
            }
        }
    }
}
