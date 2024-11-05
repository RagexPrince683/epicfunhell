package ca.ragexprince.epicfunhell.quiettime;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import ca.ragexprince.epicfunhell.util.ModSounds;

@Mod.EventBusSubscriber
public class QuietTimeManager {
    public static boolean isQuietPeriodActive() {
        return quietPeriodActive;
    }

    private static boolean quietPeriodActive = false;
    private static final long QUIET_PERIOD_DURATION = 3600; // 1 hour in seconds 3600
    private static final long QUIET_PERIOD_INTERVAL = 7200; // 2 hours in seconds 7200
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public static void init() {
        startQuietPeriodCycle(); // Start the scheduled quiet period cycle
    }
    private static void startQuietPeriodCycle() {
        scheduler.scheduleAtFixedRate(() -> startQuietPeriod(), 0, QUIET_PERIOD_INTERVAL, TimeUnit.SECONDS);
    }

    private static void startQuietPeriod() {
        if (!quietPeriodActive) {
            quietPeriodActive = true;
            System.out.println("Entering quiet period...");
            MinecraftForge.EVENT_BUS.register(new MobSpawnHandler()); // Disable mob spawning

            // Schedule the end of the quiet period after QUIET_PERIOD_DURATION
            scheduler.schedule(QuietTimeManager::endQuietPeriod, QUIET_PERIOD_DURATION, TimeUnit.SECONDS);
        }
    }

    private static void endQuietPeriod() {
        if (quietPeriodActive) {
            quietPeriodActive = false;
            System.out.println("Ending quiet period...");
            MinecraftForge.EVENT_BUS.unregister(MobSpawnHandler.class); // Enable mob spawning
        }
    }

    @SubscribeEvent
    public static void onServerTick(TickEvent.WorldTickEvent event) {
        if (event.phase == TickEvent.Phase.END && event.world instanceof ServerLevel serverWorld) {
            if (quietPeriodActive) {
                activelyDespawnMobs(serverWorld);
                triggerEnvironmentalSounds(serverWorld);
            }
        }
    }

    private static void activelyDespawnMobs(ServerLevel serverWorld) {
        for (Entity entity : serverWorld.getEntities().getAll()) {
            if (entity instanceof Monster) {
                entity.remove(Entity.RemovalReason.DISCARDED);
            }
        }
    }

    private static void triggerEnvironmentalSounds(ServerLevel serverWorld) {
        serverWorld.players().forEach(player -> {
            player.playSound(ModSounds.CREAKING.get(), 1.0F, 1.0F);
            player.playSound(ModSounds.WHISPERS.get(), 1.0F, 0.9F);
            // Add more environmental sounds here as desired
        });
    }
}
