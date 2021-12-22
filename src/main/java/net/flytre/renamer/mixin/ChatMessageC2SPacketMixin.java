package net.flytre.renamer.mixin;

import net.flytre.renamer.core.NameProtect;
import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChatMessageC2SPacket.class)
public class ChatMessageC2SPacketMixin {

    @Mutable
    @Shadow
    @Final
    private String chatMessage;

    @Inject(method = "<init>(Ljava/lang/String;)V", at = @At("TAIL"))
    public void renamer$patchOnSendMessage(String chatMessage, CallbackInfo ci) {
        this.chatMessage = NameProtect.displayToActual(chatMessage);
    }
}

