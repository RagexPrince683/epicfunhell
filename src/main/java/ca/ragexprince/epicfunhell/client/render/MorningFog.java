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
    public static Minecraft mc = Minecraft.getInstance();
    OptionInstance<Integer> renderDistanceOption = mc.options.renderDistance();
    //originalRenderDistance = 16;
    public static int originalRenderDistance = mc.options.renderDistance().get();


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
        Minecraft mc = Minecraft.getInstance();

        if (elapsed < 600) { // Fog lasts for 600 ticks (30 seconds)
            float fogDensity = 0.5f * (1 - (elapsed / 600f)); // Increase density over time
            float fogStart = 0.2f + fogDensity * 0.8f; // Start fog very close to the player
            float fogEnd = fogStart + 1.0f; // End fog slightly further

            // Apply very close fog distances
            RenderSystem.setShaderFogStart(fogStart);
            RenderSystem.setShaderFogEnd(fogEnd);

            // Set fog color to dark green
            RenderSystem.setShaderFogColor(0.1f, 0.2f, 0.1f); // Very dark green

            // Adjust render distance
            //LevelRenderer levelRenderer = mc.gameRenderer.renderLevel(); pain
            //levelRenderer.setFogRenderRange(Math.max(levelRenderer.getFogRenderRange(), 32));
            //levelRenderer.MIN_FOG_DISTANCE

            // Add particle effects for extra density
            BlockPos playerPos = mc.player.blockPosition();
            Vec3 playerVec = mc.player.position();

            //for (int i = 0; i < 10; i++) {
            //    double x = playerVec.x() + (Math.random() - 0.5) * 2.0;
            //    double y = playerVec.y() + (Math.random() - 0.5) * 2.0;
            //    double z = playerVec.z() + (Math.random() - 0.5) * 2.0;
//
            //    //mc.level.addParticle(ParticleTypes.SMOKE, x, y, z, 0.0D, 0.01D, 0.0D);
            //}

            OptionInstance<Integer> renderDistanceOption = mc.options.renderDistance();
            renderDistanceOption.set(2);
            mc.options.save();


            event.setCanceled(true); // Apply custom fog effect
        } else {

            OptionInstance<Integer> renderDistanceOption = mc.options.renderDistance();
            renderDistanceOption.set(originalRenderDistance); // Restore original value
            mc.options.save();

            wakeTime = -1; // Reset after fog effect ends
        }
    }
}