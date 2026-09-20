package io.github.abduznik.foreman.client.mixin;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.world.entity.projectile.FishingHook;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * Read-only accessor for the bobber's synced "biting" entity data. This is the same flag
 * vanilla uses to trigger the splash-particle/dip animation, so it is the ground truth for
 * the bite window rather than an approximation. Foreman only ever reads it.
 */
@Mixin(FishingHook.class)
public interface FishingHookAccessor {
	@Accessor("DATA_BITING")
	static EntityDataAccessor<Boolean> foreman$dataBiting() {
		throw new AssertionError();
	}
}
