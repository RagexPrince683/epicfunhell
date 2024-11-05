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
//import net.minecraftforge.fml.server.ServerLifecycleHooks;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Mod.EventBusSubscriber
public class QuietTimeManager {

    private static final long QUIET_PERIOD_DURATION = 60 * 60;  // Set the quiet period duration in seconds (1 hour for testing)
    private static final long QUIET_PERIOD_INTERVAL = 5;   // Set the interval between quiet periods in seconds (5 minutes for testing)
    private static boolean quietPeriodActive = false;
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public static void init() {
        System.out.println("QuietTimeManager: Starting quiet period cycle.");
        startQuietPeriodCycle();
    }

    private static void startQuietPeriodCycle() {
        // Schedule to start a quiet period at regular intervals
        scheduler.scheduleAtFixedRate(() -> {
            if (!quietPeriodActive) {
                startQuietPeriod();
            }
        }, 0, QUIET_PERIOD_INTERVAL, TimeUnit.SECONDS);
    }

    private static void startQuietPeriod() {
        if (!quietPeriodActive) {
            quietPeriodActive = true;
            System.out.println("Entering quiet period...");
            disableHostileMobSpawning();

            // Schedule the end of the quiet period after QUIET_PERIOD_DURATION
            scheduler.schedule(QuietTimeManager::endQuietPeriod, QUIET_PERIOD_DURATION, TimeUnit.SECONDS);
        }
    }

    private static void endQuietPeriod() {
        if (quietPeriodActive) {
            quietPeriodActive = false;
            System.out.println("Ending quiet period...");
            enableHostileMobSpawning();
        }
    }

    private static void disableHostileMobSpawning() {
        for (ServerLevel world : ServerLifecycleHooks.getCurrentServer().getAllLevels()) {
            world.getGameRules().getRule(GameRules.RULE_DOMOBSPAWNING).set(false, world.getServer());
        }
    }

    private static void enableHostileMobSpawning() {
        for (ServerLevel world : ServerLifecycleHooks.getCurrentServer().getAllLevels()) {
            world.getGameRules().getRule(GameRules.RULE_DOMOBSPAWNING).set(true, world.getServer());
        }
    }

    @Mod.EventBusSubscriber
    private static class QuietTimeTickHandler {
        @SubscribeEvent
        public static void onWorldTick(TickEvent.WorldTickEvent event) {
            if (event.phase == TickEvent.Phase.END && quietPeriodActive && event.world instanceof ServerLevel serverWorld) {
                activelyDespawnHostileMobs(serverWorld);
            }
        }

        public static void activelyDespawnHostileMobs(ServerLevel serverWorld) {
            // Iterate over all loaded entities in the server world
            for (Entity entity : serverWorld.getEntities().getAll()) {
                // Check if the entity is an instance of Monster (hostile mob)
                if (entity instanceof Monster) {
                    // Remove the entity with the specified reason
                    entity.remove(Entity.RemovalReason.DISCARDED);
                }
            }
        }

        private void triggerEnvironmentalSounds(ServerLevel serverWorld) {
            serverWorld.players().forEach(player -> {
                player.playSound(ModSounds.CREAKING.get(), 1.0F, 1.0F);
                player.playSound(ModSounds.WHISPERS.get(), 1.0F, 0.9F);
            });
        }

    }
}