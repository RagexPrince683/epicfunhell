package ca.ragexprince.epicfunhell.client.render;

import com.mojang.blaze3d.shaders.FogShape;
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

            // Set fog density based on time elapsed since waking
            if (elapsed < 600) { // Fog lasts for 600 ticks (30 seconds)
                float density = 0.05f * (1 - (elapsed / 600f)); // Density decreases over time
                event.getRenderer().setupFog(event.getCamera(), FogShape.SPHERE, density, 0.0f, 1.0f);
            } else {
                wakeTime = -1; // Reset after fog effect ends
            }
        }
    }
}

