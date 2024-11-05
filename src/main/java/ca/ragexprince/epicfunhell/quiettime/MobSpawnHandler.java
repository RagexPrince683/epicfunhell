package ca.ragexprince.epicfunhell.quiettime;

import net.minecraft.world.entity.monster.Monster;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class MobSpawnHandler {

    @SubscribeEvent
    public void onMobSpawn(LivingSpawnEvent.CheckSpawn event) {
        if (event.getEntity() instanceof Monster && QuietTimeManager.isQuietPeriodActive()) {
            event.setCanceled(true); // Prevent hostile mob spawning
        }
    }
}
