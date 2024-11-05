package ca.ragexprince.epicfunhell.util;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
@Mod.EventBusSubscriber(modid = "epicfunhell", bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModSounds {
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, "epicfunhell");

    public static final RegistryObject<SoundEvent> CREAKING = SOUND_EVENTS.register("environment.creak",
            () -> new SoundEvent(new ResourceLocation("epicfunhell", "creak")));
    public static final RegistryObject<SoundEvent> WHISPERS = SOUND_EVENTS.register("environment.whisper",
            () -> new SoundEvent(new ResourceLocation("epicfunhell", "whisper")));

    public static void register(IEventBus eventBus) {
        SOUND_EVENTS.register(eventBus);
    }
}
