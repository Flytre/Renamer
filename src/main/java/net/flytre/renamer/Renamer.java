package net.flytre.renamer;

import net.fabricmc.api.ClientModInitializer;
import net.flytre.flytre_lib.api.config.ConfigHandler;
import net.flytre.flytre_lib.api.config.ConfigRegistry;
import net.flytre.renamer.config.Config;

public class Renamer implements ClientModInitializer {


    public static final ConfigHandler<Config> CONFIG = new ConfigHandler<>(new Config(), "renamer");

    @Override
    public void onInitializeClient() {
        ConfigRegistry.registerClientConfig(CONFIG);
    }
}
