package ca.ragexprince.epicfunhell.client.render;

import com.mojang.blaze3d.shaders.FogShape;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.event.entity.player.PlayerWakeUpEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import org.apache.commons.lang3.tuple.Pair;


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

            if (elapsed < 600) { // Fog lasts for 600 ticks (30 seconds)
                //float density = 0.5f * (1 - (elapsed / 600f)); // Increase density for thicker fog
                float fogStart = 100.5f; // Set fog start extremely close to the player
                float fogEnd = 1.5f; // Set fog end slightly further to simulate mist

                // Apply very close fog distances
                RenderSystem.setShaderFogStart(fogStart);
                RenderSystem.setShaderFogEnd(fogEnd);

                // Set fog color to green
                RenderSystem.setShaderFogColor(0.1f, 0.6f, 0.1f); // Dark green

                event.setCanceled(true); // Apply custom fog effect
            } else {
                wakeTime = -1; // Reset after fog effect ends
            }
        }
    }
}