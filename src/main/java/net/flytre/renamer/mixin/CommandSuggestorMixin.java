package net.flytre.renamer.mixin;


import net.flytre.renamer.core.NameProtect;
import net.minecraft.client.gui.screen.CommandSuggestor;
import net.minecraft.client.network.ClientCommandSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Collection;
import java.util.stream.Collectors;

@Mixin(CommandSuggestor.class)
public class CommandSuggestorMixin {

    @Redirect(method = "refresh", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientCommandSource;getPlayerNames()Ljava/util/Collection;"))
    public Collection<String> renamer$protectChatSuggestions(ClientCommandSource clientCommandSource) {
        return clientCommandSource.getPlayerNames().stream().map(i -> NameProtect.protect(i, false)).collect(Collectors.toList());
    }
}
