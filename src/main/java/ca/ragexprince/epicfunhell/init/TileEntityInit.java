package ca.ragexprince.epicfunhell.init;

import ca.ragexprince.epicfunhell.FirstModMain;
import ca.ragexprince.epicfunhell.tiles.MobSlayerTile;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class TileEntityInit {
    public static final DeferredRegister<BlockEntityType<?>> TILE_ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITIES, FirstModMain.MOD_ID);

    public static final RegistryObject<BlockEntityType<MobSlayerTile>> MOB_SLAYER
            = TILE_ENTITY_TYPES.register("mob_slayer",
            () -> BlockEntityType.Builder.of(MobSlayerTile::new, BlockInit.MOB_SLAYER.get()).build(null));

}

