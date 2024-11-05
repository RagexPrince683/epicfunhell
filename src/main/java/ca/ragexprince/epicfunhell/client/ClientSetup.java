package ca.ragexprince.epicfunhell.client;

import ca.ragexprince.epicfunhell.FirstModMain;
import ca.ragexprince.epicfunhell.client.render.ExplosiveArrowRenderer;
import ca.ragexprince.epicfunhell.init.EntityInit;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = FirstModMain.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientSetup {
    @SubscribeEvent
    public static void doSetup(FMLClientSetupEvent event) {
        EntityRenderers.register(EntityInit.EXPLOSIVE_ARROW.get(), ExplosiveArrowRenderer::new);
    }
}