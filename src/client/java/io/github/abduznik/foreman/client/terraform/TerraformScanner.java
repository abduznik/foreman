package io.github.abduznik.foreman.client.terraform;

import io.github.abduznik.foreman.client.config.ForemanConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * Reads real block data out of an already-selected, already-loaded region and tallies it by
 * block type. Read-only: never places or removes a single block. Filters are player-chosen
 * (see {@link ForemanConfig.Data}), not a built-in x-ray toggle.
 */
public final class TerraformScanner {
	private TerraformScanner() {
	}

	public static Result scan(LevelAccessor level, Selection selection) {
		if (!selection.isComplete()) {
			throw new IllegalStateException("Selection is not complete");
		}

		BlockPos min = selection.min();
		BlockPos max = selection.max();

		Map<Block, Long> counts = new LinkedHashMap<>();
		long scanned = 0;

		for (int x = min.getX(); x <= max.getX(); x++) {
			for (int y = min.getY(); y <= max.getY(); y++) {
				for (int z = min.getZ(); z <= max.getZ(); z++) {
					BlockPos pos = new BlockPos(x, y, z);
					BlockState state = level.getBlockState(pos);
					scanned++;

					if (ForemanConfig.data.terraformIgnoreAir && state.isAir()) {
						continue;
					}
					if (ForemanConfig.data.terraformIgnoreBedrock && state.is(Blocks.BEDROCK)) {
						continue;
					}
					if (ForemanConfig.data.terraformIgnoreWater && state.getFluidState().getType() == Fluids.WATER) {
						continue;
					}

					Block block = state.getBlock();
					counts.merge(block, 1L, Long::sum);
				}
			}
		}

		return new Result(scanned, selection.volume(), counts);
	}

	public record Result(long blocksScanned, long selectionVolume, Map<Block, Long> counts) {
		/** Sorted by descending count for a readable shopping list. */
		public Map<Identifier, Long> byId() {
			Map<Identifier, Long> byId = new TreeMap<>();
			for (Map.Entry<Block, Long> entry : counts.entrySet()) {
				byId.put(BuiltInRegistries.BLOCK.getKey(entry.getKey()), entry.getValue());
			}
			return byId;
		}

		public long totalCountedBlocks() {
			return counts.values().stream().mapToLong(Long::longValue).sum();
		}
	}
}
