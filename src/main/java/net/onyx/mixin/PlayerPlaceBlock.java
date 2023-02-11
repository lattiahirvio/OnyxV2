package net.onyx.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemPlacementContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

// Credit: https://www.github.com/Devan-Kerman/OneEvents/tree/master/LOOK%20IN%20HERE%2FPlayerPlaceBlock.java
@Mixin(BlockItem.class)
public class PlayerPlaceBlock {
	@Inject(method = "place(Lnet/minecraft/item/ItemPlacementContext;Lnet/minecraft/block/BlockState;)Z", at = @At("HEAD"), cancellable = true)
	private void restrict(ItemPlacementContext context, BlockState state, CallbackInfoReturnable<Boolean> cir) {
		if(this.restrict(context, state)) {
			PlayerEntity entity = context.getPlayer();

			cir.setReturnValue(false);
		}
	}

	/**
	 * @param context the context of the placement
	 * @param state the state to place
	 * @return return true if the player cannot place a block there
	 */
	@Unique
	public boolean restrict(ItemPlacementContext context, BlockState state) {
		return false;
	}
}