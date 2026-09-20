package io.github.abduznik.foreman.client.fishing;

import io.github.abduznik.foreman.client.ForemanClient;
import io.github.abduznik.foreman.client.config.ForemanConfig;
import io.github.abduznik.foreman.client.mixin.FishingHookAccessor;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

/**
 * Draws a bite marker above the local player's bobber in screen space. Screen space rather
 * than a 3D world glow because {@link FishingHook} has a hand-written renderer that bypasses
 * vanilla's entity-outline pipeline entirely, so there's no reliable hook to make an actual
 * glow effect render through walls; a HUD marker sidesteps that and is guaranteed visible in
 * the dark or through terrain since it draws after the 3D scene.
 */
public final class BobberGlowFeature {
	private static final int WAITING_COLOR = ARGB.color(255, 255, 224, 102);
	private static final int BITING_COLOR = ARGB.color(255, 255, 64, 64);

	private BobberGlowFeature() {
	}

	public static void init() {
		HudElementRegistry.attachElementBefore(
				VanillaHudElements.CHAT,
				Identifier.fromNamespaceAndPath(ForemanClient.MOD_ID, "bobber_bite_marker"),
				BobberGlowFeature::extract);
	}

	private static void extract(GuiGraphicsExtractor graphics, DeltaTracker tickCounter) {
		if (!ForemanConfig.data.fishingGlowEnabled) {
			return;
		}

		Minecraft client = Minecraft.getInstance();
		Player player = client.player;
		if (player == null || client.level == null) {
			return;
		}

		FishingHook hook = player.fishing;
		if (hook == null) {
			return;
		}

		float partialTick = tickCounter.getGameTimeDeltaPartialTick(true);
		Vec3 hookPos = hook.getPosition(partialTick).add(0.0, 0.35, 0.0);
		Vector3f screen = projectToScreen(client, hookPos);
		if (screen == null) {
			return;
		}

		boolean biting = hook.getEntityData().get(FishingHookAccessor.foreman$dataBiting());
		int color = biting ? BITING_COLOR : WAITING_COLOR;

		graphics.centeredText(client.font, "!", Math.round(screen.x()), Math.round(screen.y()), color);
	}

	/** Returns null when the point is behind the camera or outside the visible frustum. */
	private static Vector3f projectToScreen(Minecraft client, Vec3 worldPos) {
		Vec3 projected = client.gameRenderer.projectPointToScreen(worldPos);
		if (projected.z() < -1.0 || projected.z() >= 1.0 || Math.abs(projected.x()) > 1.0 || Math.abs(projected.y()) > 1.0) {
			return null;
		}

		int scaledWidth = client.getWindow().getGuiScaledWidth();
		int scaledHeight = client.getWindow().getGuiScaledHeight();

		float screenX = (float) ((projected.x() + 1.0) / 2.0 * scaledWidth);
		float screenY = (float) ((1.0 - projected.y()) / 2.0 * scaledHeight);

		return new Vector3f(screenX, screenY, (float) projected.z());
	}
}
