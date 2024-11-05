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
import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber
public class QuietTimeManager {

    private static final Logger LOGGER = LogManager.getLogger();
    private static final long QUIET_PERIOD_DURATION = 360000000;  // Duration of quiet period in seconds (e.g., 5 for testing)
    private static final long QUIET_PERIOD_INTERVAL = 10; // Interval in seconds between quiet periods (e.g., 10 for testing)
    private static boolean quietPeriodActive = false;
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public static void init() {
        LOGGER.info("QuietTimeManager: Initializing quiet period cycle.");
        startQuietPeriodCycle();
        MinecraftForge.EVENT_BUS.register(new QuietTimeTickHandler());
    }

    private static void startQuietPeriodCycle() {
        LOGGER.info("QuietTimeManager: Starting quiet period scheduler.");
        // Schedule toggling at intervals; handle quiet period duration within toggleQuietPeriod
        scheduler.scheduleAtFixedRate(QuietTimeManager::toggleQuietPeriod, 0, QUIET_PERIOD_INTERVAL, TimeUnit.SECONDS);
    }

    private static void toggleQuietPeriod() {
        if (quietPeriodActive) {
            endQuietPeriod();  // End quiet period if active
        } else {
            startQuietPeriod();  // Start new quiet period if inactive
        }
    }

    private static void startQuietPeriod() {
        if (!quietPeriodActive) {
            quietPeriodActive = true;
            LOGGER.info("QuietTimeManager: Entering quiet period.");
            disableHostileMobSpawning();

            // Schedule end of quiet period strictly after QUIET_PERIOD_DURATION
            scheduler.schedule(QuietTimeManager::endQuietPeriod, QUIET_PERIOD_DURATION, TimeUnit.SECONDS);
        }
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
            // Create a temporary list to store entities to be removed
            List<Entity> entitiesToRemove = new ArrayList<>();

            for (Entity entity : serverWorld.getEntities().getAll()) {
                if (entity instanceof Monster) {
                    entitiesToRemove.add(entity);
                    LOGGER.debug("QuietTimeManager: Queued hostile mob for despawn - " + entity.getName().getString());
                }
            }

            // Remove all hostile mobs from the temporary list
            for (Entity entity : entitiesToRemove) {
                entity.remove(Entity.RemovalReason.DISCARDED);
                LOGGER.debug("QuietTimeManager: Despawned hostile mob - " + entity.getName().getString());
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