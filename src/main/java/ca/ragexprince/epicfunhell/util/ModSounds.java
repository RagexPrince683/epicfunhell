package ca.ragexprince.epicfunhell.util;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
public class ModSounds {
    public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, "epicfunhell");

    public static final RegistryObject<SoundEvent> CREAKING = SOUNDS.register("environment.creaking",
            () -> new SoundEvent(new ResourceLocation("epicfunhell", "environment.creaking")));

    public static final RegistryObject<SoundEvent> WHISPERS = SOUNDS.register("environment.whispers",
            () -> new SoundEvent(new ResourceLocation("epicfunhell", "environment.whispers")));

    public static void register(IEventBus eventBus) {
        SOUNDS.register(eventBus);
    }

}
