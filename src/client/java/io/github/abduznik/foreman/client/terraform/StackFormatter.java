package io.github.abduznik.foreman.client.terraform;

/** Converts a raw block count into stacks/shulkers-of-stacks, vanilla stack size of 64 assumed. */
public final class StackFormatter {
	private static final int STACK_SIZE = 64;
	private static final int STACKS_PER_SHULKER = 27;

	private StackFormatter() {
	}

	public static String format(long count) {
		long shulkers = count / (STACK_SIZE * STACKS_PER_SHULKER);
		long remainderAfterShulkers = count % (STACK_SIZE * STACKS_PER_SHULKER);
		long stacks = remainderAfterShulkers / STACK_SIZE;
		long items = remainderAfterShulkers % STACK_SIZE;

		StringBuilder sb = new StringBuilder();
		if (shulkers > 0) {
			sb.append(shulkers).append(" shulker").append(shulkers == 1 ? "" : "s");
		}
		if (stacks > 0) {
			if (!sb.isEmpty()) {
				sb.append(", ");
			}
			sb.append(stacks).append(" stack").append(stacks == 1 ? "" : "s");
		}
		if (items > 0 || sb.isEmpty()) {
			if (!sb.isEmpty()) {
				sb.append(", ");
			}
			sb.append(items).append(" item").append(items == 1 ? "" : "s");
		}
		return sb.toString();
	}
}
