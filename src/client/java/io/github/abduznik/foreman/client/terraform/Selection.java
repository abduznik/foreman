package io.github.abduznik.foreman.client.terraform;

import net.minecraft.core.BlockPos;

/**
 * A two-corner, axis-aligned region the player has explicitly marked, WorldEdit-style.
 * Selection only — Foreman never edits blocks inside it.
 */
public final class Selection {
	private BlockPos posA;
	private BlockPos posB;

	public void setPosA(BlockPos pos) {
		this.posA = pos;
	}

	public void setPosB(BlockPos pos) {
		this.posB = pos;
	}

	public BlockPos getPosA() {
		return posA;
	}

	public BlockPos getPosB() {
		return posB;
	}

	public boolean isComplete() {
		return posA != null && posB != null;
	}

	public void clear() {
		posA = null;
		posB = null;
	}

	public BlockPos min() {
		return new BlockPos(
				Math.min(posA.getX(), posB.getX()),
				Math.min(posA.getY(), posB.getY()),
				Math.min(posA.getZ(), posB.getZ())
		);
	}

	public BlockPos max() {
		return new BlockPos(
				Math.max(posA.getX(), posB.getX()),
				Math.max(posA.getY(), posB.getY()),
				Math.max(posA.getZ(), posB.getZ())
		);
	}

	public long volume() {
		if (!isComplete()) {
			return 0;
		}
		BlockPos min = min();
		BlockPos max = max();
		long dx = (long) (max.getX() - min.getX() + 1);
		long dy = (long) (max.getY() - min.getY() + 1);
		long dz = (long) (max.getZ() - min.getZ() + 1);
		return dx * dy * dz;
	}
}
