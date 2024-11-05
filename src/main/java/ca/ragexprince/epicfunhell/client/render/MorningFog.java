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
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerWakeUpEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import java.util.function.Consumer;


public class MorningFog {

    static boolean fogActive = false;
    static final float[] fogColors = new float[3];
    static float fogColorIntensity = 0.0F;
    static float fogDensity = 1.0F;

    public MorningFog() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        bus.addListener(this::setupClient);

        IEventBus forgeBus = net.minecraftforge.common.MinecraftForge.EVENT_BUS;
        forgeBus.addListener((Consumer<EntityViewRenderEvent.RenderFogEvent>) this::onRenderFog);
        //forgeBus.addListener((Consumer<EntityViewRenderEvent.ComputeFogColor>) this::onComputeFogColor);
        forgeBus.addListener((Consumer<TickEvent.PlayerTickEvent>) this::onPlayerTick);
        forgeBus.addListener(this::onPlayerWakeUp);
    }

    private void setupClient(FMLClientSetupEvent event) {
        // Client-side setup if needed
    }

    @SubscribeEvent
    public void onPlayerWakeUp(PlayerWakeUpEvent event) {
        if (event.getEntity().level.isClientSide && event.getEntity() == Minecraft.getInstance().player) {
            fogActive = true;
        }
    }

    private void onRenderFog(EntityViewRenderEvent.RenderFogEvent event) {
        if (fogActive || fogDensity < 1F) {
            float maxFogDistance = 6.0F; // Close-range fog effect
            maxFogDistance = Math.min(maxFogDistance, event.getFarPlaneDistance());
            float fogTransitionSpeed = (float) (0.01F * event.getPartialTick());

            if (fogActive) {
                fogDensity -= fogTransitionSpeed;
            } else {
                fogDensity += fogTransitionSpeed;
            }
            fogDensity = Mth.clamp(fogDensity, 0F, 1F);

            RenderSystem.setShaderFogStart(0.0F);
            RenderSystem.setShaderFogEnd(maxFogDistance * fogDensity);
        }
    }

    @SubscribeEvent
    public void onRenderFogColor(EntityViewRenderEvent.FogColors event) {
        if (fogActive || fogColorIntensity > 0F) {
            // Define your target fog color (greenish)
            final float baseRed = 0.2F;
            final float baseGreen = 0.6F;
            final float baseBlue = 0.2F;

            // Calculate the transition speed for the fog color
            float colorTransitionSpeed = 0.02F * Minecraft.getInstance().getFrameTime();

            // Update fog color intensity based on whether fog is active
            if (fogActive) {
                fogColorIntensity += colorTransitionSpeed;
            } else {
                fogColorIntensity -= colorTransitionSpeed;
            }
            fogColorIntensity = Mth.clamp(fogColorIntensity, 0F, 1F);

            // Smoothly interpolate from the current fog color to the base color
            float red = Mth.lerp(fogColorIntensity, event.getRed(), baseRed);
            float green = Mth.lerp(fogColorIntensity, event.getGreen(), baseGreen);
            float blue = Mth.lerp(fogColorIntensity, event.getBlue(), baseBlue);

            // Apply the modified color values to the fog
            event.setRed(event.getRed() * 0);
            event.setGreen(event.getGreen() * 2);
            event.setBlue(event.getBlue() * 0);
        }
    }

    private void onPlayerTick(TickEvent.PlayerTickEvent event) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null || player != event.player) return;

        if (fogActive) {
            // Optional particle effect for added misty atmosphere
            for (int i = 0; i < 10; i++) {
                double offsetX = player.getX() + (player.level.random.nextDouble() - 0.5) * 4.0;
                double offsetY = player.getY() + (player.level.random.nextDouble() * 2.0);
                double offsetZ = player.getZ() + (player.level.random.nextDouble() - 0.5) * 4.0;
                player.level.addParticle(ParticleTypes.MYCELIUM, offsetX, offsetY, offsetZ, 0, 0, 0);
            }
        } else {
            fogActive = false;
        }
    }
}