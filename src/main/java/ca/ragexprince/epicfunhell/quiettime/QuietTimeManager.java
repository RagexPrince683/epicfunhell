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

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Mod.EventBusSubscriber
public class QuietTimeManager {

    private static boolean quietPeriodActive = false;
    private static final long QUIET_PERIOD_DURATION = 3600; // 1 hour in seconds 3600
    private static final long QUIET_PERIOD_INTERVAL = 7200; // 2 hours in seconds 7200
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public static void init() {
        System.out.println("QuietTimeManager: Starting quiet period cycle.");
        startQuietPeriodCycle();
    }

    private static void startQuietPeriodCycle() {
        scheduler.scheduleAtFixedRate(() -> {
            System.out.println("QuietTimeManager: Scheduled quiet period check.");
            startQuietPeriod();
        }, 0, QUIET_PERIOD_INTERVAL, TimeUnit.SECONDS);
    }

    private static void startQuietPeriod() {
        if (!quietPeriodActive) {
            quietPeriodActive = true;
            System.out.println("Entering quiet period...");
            // Disable mob spawning
            for (ServerLevel world : ServerLifecycleHooks.getCurrentServer().getAllLevels()) {
                world.getGameRules().getRule(GameRules.RULE_DOMOBSPAWNING).set(false, world.getServer());
            }
            MinecraftForge.EVENT_BUS.register(new QuietTimeTickHandler());

            // Schedule the end of the quiet period after QUIET_PERIOD_DURATION
            scheduler.schedule(QuietTimeManager::endQuietPeriod, QUIET_PERIOD_DURATION, TimeUnit.SECONDS);
        }
    }

    private static void endQuietPeriod() {
        if (quietPeriodActive) {
            quietPeriodActive = false;
            System.out.println("Ending quiet period...");
            // Re-enable mob spawning
            for (ServerLevel world : ServerLifecycleHooks.getCurrentServer().getAllLevels()) {
                world.getGameRules().getRule(GameRules.RULE_DOMOBSPAWNING).set(true, world.getServer());
            }
            MinecraftForge.EVENT_BUS.unregister(QuietTimeTickHandler.class);
        }
    }

    private static class QuietTimeTickHandler {
        @SubscribeEvent
        public void onWorldTick(TickEvent.WorldTickEvent event) {
            if (event.phase == TickEvent.Phase.END && event.world instanceof ServerLevel serverWorld) {
                if (quietPeriodActive) {
                    activelyDespawnMobs(serverWorld);
                    triggerEnvironmentalSounds(serverWorld);
                }
            }
        }

        private void activelyDespawnMobs(ServerLevel serverWorld) {
            for (Entity entity : serverWorld.getEntities().getAll()) {
                if (entity instanceof Monster) {
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
