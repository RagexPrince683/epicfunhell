package ca.ragexprince.epicfunhell.client.render;

import com.mojang.blaze3d.shaders.FogShape;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.event.entity.player.PlayerWakeUpEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class MorningFog {
    private static long wakeTime = -1;
    private static int originalRenderDistance = -1; // Store the original render distance
    public static final Minecraft mc = Minecraft.getInstance();

    public static boolean achievementdone = false;

    @SubscribeEvent
    public static void onPlayerWakeUp(PlayerWakeUpEvent event) {
        // This block must run server-side because advancements are server-only
        if (event.getEntity().level.isClientSide) return;

        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        // Check if the player has the advancement
        boolean hasAdvancement = player.getAdvancements().getOrStartProgress(
                player.getServer().getAdvancements().getAdvancement(new ResourceLocation("minecraft:story/iron_tools"))
        ).isDone();

        if (hasAdvancement) {
            // Sync this information to the client by using a custom packet
            player.getLevel().getServer().getPlayerList().sendPacket(
                    new ClientboundCustomPayloadPacket(
                            new ResourceLocation("morningfog:achievement_done"),
                            new FriendlyByteBuf(Unpooled.buffer()).writeBoolean(true)
                    )
            );
        }
    }

    @SubscribeEvent
    public static void onRenderFog(EntityViewRenderEvent.RenderFogEvent event) {
        if (wakeTime == -1 || !achievementdone) return;

        long currentTime = Minecraft.getInstance().level.getGameTime();
        long elapsed = currentTime - wakeTime;

        if (elapsed < 600) { // Fog lasts for 600 ticks (30 seconds)
            float progress = elapsed / 600f;
            float fogStart = 0.5f * (1 - progress); // Starts closer to the player
            float fogEnd = fogStart + 5.0f * (1 - progress); // Slightly farther end point as fog fades

            // Apply very dense green fog
            RenderSystem.setShaderFogStart(fogStart);
            RenderSystem.setShaderFogEnd(fogEnd);
            RenderSystem.setShaderFogColor(0.1f, 0.4f, 0.1f); // Strong green color

            // Reduce render distance to simulate dense fog
            OptionInstance<Integer> renderDistanceOption = mc.options.renderDistance();
            renderDistanceOption.set(2);
            mc.options.save();

            event.setCanceled(true); // Apply custom fog effect
        } else {
            // Restore original render distance after fog effect ends
            if (originalRenderDistance > 0) {
                OptionInstance<Integer> renderDistanceOption = mc.options.renderDistance();
                renderDistanceOption.set(originalRenderDistance);
                mc.options.save();
            }

            wakeTime = -1; // Reset after fog effect ends
            achievementdone = false; // Reset the achievement trigger
        }
    }
}