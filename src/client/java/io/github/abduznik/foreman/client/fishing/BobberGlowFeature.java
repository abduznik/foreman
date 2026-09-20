package io.github.abduznik.foreman.client.fishing;

/**
 * The actual glow is applied by {@link io.github.abduznik.foreman.client.mixin.FishingHookGlowMixin},
 * which overrides the bobber's client-side glow check whenever vanilla's own bite flag is set.
 * This class only exists as the feature's init hook so {@code ForemanClient} has one obvious
 * place per feature, matching {@code TerraformFeature}.
 */
public final class BobberGlowFeature {
	private BobberGlowFeature() {
	}

	public static void init() {
		// No render-event registration needed: the mixin reacts to the entity's own state.
	}
}
