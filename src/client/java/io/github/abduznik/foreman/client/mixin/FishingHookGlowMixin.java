package io.github.abduznik.foreman.client.mixin;

import io.github.abduznik.foreman.client.config.ForemanConfig;
import net.minecraft.world.entity.projectile.FishingHook;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Makes the bobber render with the vanilla glowing-outline effect while it's in the bite
 * window. This only overrides the client-side render check ({@code isCurrentlyGlowing}); it
 * never writes the networked glowing flag, so nothing about the entity's real state changes.
 */
@Mixin(FishingHook.class)
public abstract class FishingHookGlowMixin {

	@Inject(method = "isCurrentlyGlowing", at = @At("HEAD"), cancellable = true)
	private void foreman$forceGlowWhenBiting(CallbackInfoReturnable<Boolean> cir) {
		if (!ForemanConfig.data.fishingGlowEnabled || !ForemanConfig.data.fishingGlowThroughWalls) {
			return;
		}

		FishingHook self = (FishingHook) (Object) this;
		if (self.getEntityData().get(FishingHookAccessor.foreman$dataBiting())) {
			cir.setReturnValue(true);
		}
	}
}
