package io.github.abduznik.foreman.client.mixin;

import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.storage.LevelStorageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * Read-only accessor for the integrated server's save-folder handle, used to key persisted
 * terraform selections by the world's actual folder name (stable across renames) instead of
 * its mutable display name.
 */
@Mixin(MinecraftServer.class)
public interface MinecraftServerAccessor {
	@Accessor("storageSource")
	LevelStorageSource.LevelStorageAccess foreman$storageSource();
}
