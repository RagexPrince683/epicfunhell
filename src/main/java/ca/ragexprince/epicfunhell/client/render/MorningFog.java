package ca.ragexprince.epicfunhell.client.render;

import com.mojang.blaze3d.shaders.FogShape;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.event.entity.player.PlayerWakeUpEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

public class MorningFog {
    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
    public class FogHandler {

        private static long wakeTime = -1;

        @SubscribeEvent
        public static void onPlayerWakeUp(PlayerWakeUpEvent event) {
            if (!event.getEntity().level.isClientSide) return;

            wakeTime = event.getEntity().level.getGameTime();
        }

        @SubscribeEvent
        public static void onRenderFog(EntityViewRenderEvent.RenderFogEvent event) {
            if (wakeTime == -1) return;

            long currentTime = Minecraft.getInstance().level.getGameTime();
            long elapsed = currentTime - wakeTime;

            // Fog effect lasts for 600 ticks (30 seconds)
            if (elapsed < 600) {
                float density = 5.2f * (1 - (elapsed / 600f)); // Increase base density
                float fogEnd = 90.0f * density; // Make the fog end closer to increase intensity

                // Set fog start closer to make it denser and harder to see
                RenderSystem.setShaderFogStart(0.0f);
                RenderSystem.setShaderFogEnd(fogEnd);

                // Set fog color to green (adjust RGB values as needed for desired color)
                RenderSystem.setShaderFogColor(0.1f, 0.6f, 0.1f); // Dark green color

                event.setCanceled(true); // Apply custom fog
            } else {
                wakeTime = -1; // Reset after fog effect ends
            }
        }
    }
}