package io.github.abduznik.foreman.client.terraform;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;

/**
 * Draws a low-opacity wireframe box around the terraform selection while it's active. Purely a
 * visual guide over blocks the player already selected — occluded by terrain the same way
 * vanilla's own block-outline is, so it never reveals anything through walls.
 */
public final class SelectionRenderer {
	private static final float LINE_WIDTH = 2.0F;
	private static final int COLOR = 0x66FFE066; // translucent, matches the fishing waiting color family

	private SelectionRenderer() {
	}

	public static void init(Selection selection) {
		LevelRenderEvents.COLLECT_SUBMITS.register(context -> render(context, selection));
	}

	private static void render(LevelRenderContext context, Selection selection) {
		if (!selection.isComplete()) {
			return;
		}

		Vec3 camPos = Minecraft.getInstance().gameRenderer.mainCamera().position();
		BlockPos min = selection.min();
		BlockPos max = selection.max();

		double x0 = min.getX() - camPos.x();
		double y0 = min.getY() - camPos.y();
		double z0 = min.getZ() - camPos.z();
		double x1 = max.getX() + 1.0 - camPos.x();
		double y1 = max.getY() + 1.0 - camPos.y();
		double z1 = max.getZ() + 1.0 - camPos.z();

		PoseStack poseStack = context.poseStack();
		poseStack.pushPose();

		context.submitNodeCollector().submitCustomGeometry(poseStack, RenderTypes.lines(), (pose, buffer) -> {
			// bottom face
			edge(buffer, pose, x0, y0, z0, x1, y0, z0);
			edge(buffer, pose, x1, y0, z0, x1, y0, z1);
			edge(buffer, pose, x1, y0, z1, x0, y0, z1);
			edge(buffer, pose, x0, y0, z1, x0, y0, z0);
			// top face
			edge(buffer, pose, x0, y1, z0, x1, y1, z0);
			edge(buffer, pose, x1, y1, z0, x1, y1, z1);
			edge(buffer, pose, x1, y1, z1, x0, y1, z1);
			edge(buffer, pose, x0, y1, z1, x0, y1, z0);
			// verticals
			edge(buffer, pose, x0, y0, z0, x0, y1, z0);
			edge(buffer, pose, x1, y0, z0, x1, y1, z0);
			edge(buffer, pose, x1, y0, z1, x1, y1, z1);
			edge(buffer, pose, x0, y0, z1, x0, y1, z1);
		});

		poseStack.popPose();
	}

	private static void edge(
			VertexConsumer buffer, PoseStack.Pose pose,
			double ax, double ay, double az, double bx, double by, double bz) {
		float nx = (float) (bx - ax);
		float ny = (float) (by - ay);
		float nz = (float) (bz - az);
		float length = (float) Math.sqrt(nx * nx + ny * ny + nz * nz);
		if (length > 1.0e-4F) {
			nx /= length;
			ny /= length;
			nz /= length;
		}

		buffer.addVertex(pose, (float) ax, (float) ay, (float) az).setColor(COLOR).setNormal(pose, nx, ny, nz).setLineWidth(LINE_WIDTH);
		buffer.addVertex(pose, (float) bx, (float) by, (float) bz).setColor(COLOR).setNormal(pose, nx, ny, nz).setLineWidth(LINE_WIDTH);
	}
}
