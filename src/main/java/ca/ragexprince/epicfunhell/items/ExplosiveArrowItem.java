package ca.ragexprince.epicfunhell.items;

import ca.ragexprince.epicfunhell.entity.ExplosiveArrowEntity;
import ca.ragexprince.epicfunhell.init.EntityInit;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class ExplosiveArrowItem extends ArrowItem {
    public ExplosiveArrowItem(Properties props) {
        super(props);
    }

    @Override
    public AbstractArrow createArrow(Level world, ItemStack ammoStack, LivingEntity shooter) {
        return new ExplosiveArrowEntity(EntityInit.EXPLOSIVE_ARROW.get(), shooter, world);
    }
}
