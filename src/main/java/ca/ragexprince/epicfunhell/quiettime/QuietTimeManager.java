package ca.ragexprince.epicfunhell.quiettime;

import ca.ragexprince.epicfunhell.util.ModSounds;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.GameRules;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Mod.EventBusSubscriber
public class QuietTimeManager {

    private static final Logger LOGGER = LogManager.getLogger();
    private static final long QUIET_PERIOD_DURATION = 60 * 60;  // Duration of the quiet period in seconds (1 hour)
    private static final long QUIET_PERIOD_INTERVAL = 1;   // Interval in seconds between quiet periods (5 minutes)
    private static boolean quietPeriodActive = false;
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public static void init() {
        LOGGER.info("QuietTimeManager: Initializing quiet period cycle.");
        startQuietPeriodCycle();
    }

    private static void startQuietPeriodCycle() {
        LOGGER.info("QuietTimeManager: Starting quiet period scheduler.");
        // Schedule the quiet period toggle at the specified interval
        scheduler.scheduleAtFixedRate(() -> {
            LOGGER.info("QuietTimeManager: Attempting to toggle quiet period.");
            toggleQuietPeriod();
        }, 0, QUIET_PERIOD_INTERVAL, TimeUnit.SECONDS);
    }

    private static void toggleQuietPeriod() {
        if (quietPeriodActive) {
            endQuietPeriod();
        } else {
            startQuietPeriod();
        }
    }

    private static void startQuietPeriod() {
        quietPeriodActive = true;
        LOGGER.info("QuietTimeManager: Entering quiet period.");
        disableHostileMobSpawning();

        // Schedule end of the quiet period after QUIET_PERIOD_DURATION
        scheduler.schedule(QuietTimeManager::endQuietPeriod, QUIET_PERIOD_DURATION, TimeUnit.SECONDS);
    }

    private static void endQuietPeriod() {
        if (quietPeriodActive) {
            quietPeriodActive = false;
            LOGGER.info("QuietTimeManager: Ending quiet period.");
            enableHostileMobSpawning();
        }
    }

    private static void disableHostileMobSpawning() {
        LOGGER.info("QuietTimeManager: Disabling hostile mob spawning.");
        for (ServerLevel world : ServerLifecycleHooks.getCurrentServer().getAllLevels()) {
            world.getGameRules().getRule(GameRules.RULE_DOMOBSPAWNING).set(false, world.getServer());
        }
    }

    private static void enableHostileMobSpawning() {
        LOGGER.info("QuietTimeManager: Enabling hostile mob spawning.");
        for (ServerLevel world : ServerLifecycleHooks.getCurrentServer().getAllLevels()) {
            world.getGameRules().getRule(GameRules.RULE_DOMOBSPAWNING).set(true, world.getServer());
        }
    }

    @Mod.EventBusSubscriber
    private static class QuietTimeTickHandler {
        @SubscribeEvent
        public static void onWorldTick(TickEvent.WorldTickEvent event) {
            if (event.phase == TickEvent.Phase.END && quietPeriodActive && event.world instanceof ServerLevel serverWorld) {
                LOGGER.info("QuietTimeManager: Actively despawning hostile mobs.");
                activelyDespawnHostileMobs(serverWorld);
            }
        }

        public static void activelyDespawnHostileMobs(ServerLevel serverWorld) {
            for (Entity entity : serverWorld.getEntities().getAll()) {
                if (entity instanceof Monster) {
                    entity.remove(Entity.RemovalReason.DISCARDED);
                    LOGGER.debug("QuietTimeManager: Despawned hostile mob - " + entity.getName().getString());
                }
            }
        }

        private void triggerEnvironmentalSounds(ServerLevel serverWorld) {
            serverWorld.players().forEach(player -> {
                player.playSound(ModSounds.CREAKING.get(), 1.0F, 1.0F);
                player.playSound(ModSounds.WHISPERS.get(), 1.0F, 0.9F);
            });
            LOGGER.info("QuietTimeManager: Played environmental sounds for players.");
        }
    }
}