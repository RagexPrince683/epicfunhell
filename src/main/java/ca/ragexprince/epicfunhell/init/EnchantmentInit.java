package ca.ragexprince.epicfunhell.init;

import ca.ragexprince.epicfunhell.FirstModMain;
import ca.ragexprince.epicfunhell.enchants.BridgeEnchantment;
import ca.ragexprince.epicfunhell.enchants.DistanceEnchantment;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class EnchantmentInit {
    public static final DeferredRegister<Enchantment> ENCHANTMENTS = DeferredRegister.create(ForgeRegistries.ENCHANTMENTS, FirstModMain.MOD_ID);

    public static final RegistryObject<Enchantment> BRIDGE = ENCHANTMENTS.register("bridge", BridgeEnchantment::new);

    public static final RegistryObject<Enchantment> DISTANCE = ENCHANTMENTS.register("distance", DistanceEnchantment::new);
}


