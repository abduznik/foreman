package io.github.abduznik.foreman.client;

import io.github.abduznik.foreman.client.config.ForemanConfig;
import io.github.abduznik.foreman.client.fishing.BobberGlowFeature;
import io.github.abduznik.foreman.client.terraform.TerraformFeature;
import net.fabricmc.api.ClientModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ForemanClient implements ClientModInitializer {
	public static final String MOD_ID = "foreman";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitializeClient() {
		ForemanConfig.load();

		BobberGlowFeature.init();
		TerraformFeature.init();

		LOGGER.info("Foreman loaded: it plans, it calculates, it highlights. It never touches a block for you.");
	}
}
