package ca.ragexprince.epicfunhell;

import ca.ragexprince.epicfunhell.events.AnvilHandler;
import ca.ragexprince.epicfunhell.init.*;
import ca.ragexprince.epicfunhell.quiettime.QuietTimeManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ca.ragexprince.epicfunhell.util.ModSounds;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(FirstModMain.MOD_ID)
public class FirstModMain {
    public static final Logger LOGGER = LogManager.getLogger();
    public static final String MOD_ID = "epicfunhell";

    public FirstModMain() {
        QuietTimeManager.init();
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        ModSounds.register(FMLJavaModLoadingContext.get().getModEventBus());

        modEventBus.addListener(this::setup);
        ItemInit.ITEMS.register(modEventBus);
        BlockInit.BLOCKS.register(modEventBus);
        TileEntityInit.TILE_ENTITY_TYPES.register(modEventBus);
        EnchantmentInit.ENCHANTMENTS.register(modEventBus);
        EntityInit.ENTITY_TYPES.register(modEventBus);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        QuietTimeManager.init();
        // Additional initialization, if needed
    }

    private void setup(final FMLCommonSetupEvent event) {
        AnvilHandler.initAnvilRecipes();
    }
}
