package net.mehvahdjukaar.supplementaries.common.block.tiles;


import net.mehvahdjukaar.moonlight.api.block.ItemDisplayTile;
import net.mehvahdjukaar.moonlight.api.misc.ForgeOverride;
import net.mehvahdjukaar.supplementaries.common.block.ModBlockProperties;
import net.mehvahdjukaar.supplementaries.common.block.blocks.BuntingBlock;
import net.mehvahdjukaar.supplementaries.common.items.BuntingItem;
import net.mehvahdjukaar.supplementaries.reg.ModRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import java.util.EnumMap;
import java.util.Map;


public class BuntingBlockTile extends ItemDisplayTile { //implements IExtraModelDataProvider

    // client model cache
    private final Map<Direction, DyeColor> buntings = new EnumMap<>(Direction.class);

    public BuntingBlockTile(BlockPos pos, BlockState state) {
        super(ModRegistry.BUNTING_TILE.get(), pos, state, 4);
    }

    @ForgeOverride
    public AABB getRenderBoundingBox() {
        return new AABB(worldPosition);
    }


    @Override
    protected Component getDefaultName() {
        return Component.literal("Bunting");
    }

    @Override
    public void updateClientVisualsOnLoad() {
        super.updateClientVisualsOnLoad();
        buntings.clear();
        for (Direction d : Direction.Plane.HORIZONTAL) {
            ItemStack stack = this.getItem(d.get2DDataValue());
            if (stack.getItem() instanceof BuntingItem) {
                DyeColor color = BuntingItem.getColor(stack);
                if (color != null) {
                    this.buntings.put(d, color);
                }
            }
        }
    }

    @Override
    public void updateTileOnInventoryChanged() {
        BlockState state = getBlockState();
        if (this.isEmpty()) {
            level.setBlockAndUpdate(worldPosition, BuntingBlock.toRope(state));
        } else {
            BlockState state2 = state;
            for (Direction dir : Direction.Plane.HORIZONTAL) {
                var prop = BuntingBlock.HORIZONTAL_FACING_TO_PROPERTY_MAP.get(dir);
                var old = state2.getValue(prop);
                boolean isEmpty = this.getItem(dir.get2DDataValue()).isEmpty();
                state2 = state2.setValue(prop, isEmpty ? (old == ModBlockProperties.Bunting.NONE ?  ModBlockProperties.Bunting.NONE : ModBlockProperties.Bunting.ROPE) :
                        ModBlockProperties.Bunting.BUNTING);

            }
            if(state != state2){
                level.setBlockAndUpdate(worldPosition, state2);
            }
        }
    }

    public Map<Direction, DyeColor> getBuntings() {
        return buntings;
    }

    @Override
    public boolean canPlaceItem(int index, ItemStack stack) {
        return stack.getItem() instanceof BuntingItem && getItem(index).isEmpty() &&
                BuntingBlock.canSupportBunting(getBlockState(), index);
    }


}