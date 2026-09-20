package io.github.abduznik.foreman.client.terraform;

import com.mojang.blaze3d.platform.InputConstants;
import io.github.abduznik.foreman.client.ForemanClient;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

import java.util.Map;

/**
 * WorldEdit-style two-corner selection plus a material-list scan of the real blocks inside it.
 * Selection and reporting only: nothing here ever calls a block-set method.
 */
public final class TerraformFeature {
	private static final Selection SELECTION = new Selection();

	private static KeyMapping setPosAKey;
	private static KeyMapping setPosBKey;
	private static KeyMapping scanKey;
	private static KeyMapping clearKey;

	private TerraformFeature() {
	}

	public static void init() {
		KeyMapping.Category category = KeyMapping.Category.register(Identifier.fromNamespaceAndPath(ForemanClient.MOD_ID, "terraform"));

		setPosAKey = KeyMappingHelper.registerKeyMapping(new KeyMapping(
				"key.foreman.set_pos_a", InputConstants.Type.KEYSYM, InputConstants.KEY_LBRACKET, category));
		setPosBKey = KeyMappingHelper.registerKeyMapping(new KeyMapping(
				"key.foreman.set_pos_b", InputConstants.Type.KEYSYM, InputConstants.KEY_RBRACKET, category));
		scanKey = KeyMappingHelper.registerKeyMapping(new KeyMapping(
				"key.foreman.scan", InputConstants.Type.KEYSYM, InputConstants.KEY_BACKSLASH, category));
		clearKey = KeyMappingHelper.registerKeyMapping(new KeyMapping(
				"key.foreman.clear_selection", InputConstants.Type.KEYSYM, -1, category));

		ClientTickEvents.END_CLIENT_TICK.register(TerraformFeature::onClientTick);
	}

	private static void onClientTick(Minecraft client) {
		while (setPosAKey.consumeClick()) {
			targetedBlockPos(client).ifPresentOrElse(
					pos -> {
						SELECTION.setPosA(pos);
						reportSelection(client, "Position 1 set", pos);
					},
					() -> warn(client, "Look at a block first")
			);
		}

		while (setPosBKey.consumeClick()) {
			targetedBlockPos(client).ifPresentOrElse(
					pos -> {
						SELECTION.setPosB(pos);
						reportSelection(client, "Position 2 set", pos);
					},
					() -> warn(client, "Look at a block first")
			);
		}

		while (clearKey.consumeClick()) {
			SELECTION.clear();
			if (client.player != null) {
				client.player.sendSystemMessage(Component.literal("[Foreman] Selection cleared"));
			}
		}

		while (scanKey.consumeClick()) {
			runScan(client);
		}
	}

	private static java.util.Optional<BlockPos> targetedBlockPos(Minecraft client) {
		HitResult hit = client.hitResult;
		if (hit instanceof BlockHitResult blockHit && hit.getType() == HitResult.Type.BLOCK) {
			return java.util.Optional.of(blockHit.getBlockPos());
		}
		return java.util.Optional.empty();
	}

	private static void reportSelection(Minecraft client, String label, BlockPos pos) {
		if (client.player == null) {
			return;
		}
		String suffix = SELECTION.isComplete()
				? " — selection complete, " + SELECTION.volume() + " blocks. Press \\ to scan."
				: "";
		client.player.sendSystemMessage(Component.literal(
				"[Foreman] " + label + " (" + pos.getX() + ", " + pos.getY() + ", " + pos.getZ() + ")" + suffix));
	}

	private static void warn(Minecraft client, String message) {
		if (client.player != null) {
			client.player.sendSystemMessage(Component.literal("[Foreman] " + message));
		}
	}

	private static void runScan(Minecraft client) {
		if (client.player == null || client.level == null) {
			return;
		}
		if (!SELECTION.isComplete()) {
			warn(client, "No selection yet — set both corners first ([ and ])");
			return;
		}

		TerraformScanner.Result result = TerraformScanner.scan(client.level, SELECTION);
		Map<Identifier, Long> byId = result.byId();

		client.player.sendSystemMessage(Component.literal(
				"[Foreman] Scanned " + result.blocksScanned() + " blocks in selection (" + result.selectionVolume() + " volume):"));

		if (byId.isEmpty()) {
			client.player.sendSystemMessage(Component.literal("[Foreman]   (nothing matched current filters)"));
			return;
		}

		byId.entrySet().stream()
				.sorted(Map.Entry.<Identifier, Long>comparingByValue().reversed())
				.forEach(entry -> client.player.sendSystemMessage(Component.literal(
						"[Foreman]   " + entry.getKey().getPath() + ": " + entry.getValue()
								+ " (" + StackFormatter.format(entry.getValue()) + ")")));
	}
}
