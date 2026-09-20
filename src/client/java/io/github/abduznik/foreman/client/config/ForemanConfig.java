package io.github.abduznik.foreman.client.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.github.abduznik.foreman.client.ForemanClient;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Plain JSON settings file under config/foreman.json. Deliberately not using Cloth Config
 * to keep the mod's only hard dependency on Fabric API, per the project's lightweight-deps rule.
 */
public final class ForemanConfig {
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	private static final Path PATH = FabricLoader.getInstance().getConfigDir().resolve("foreman.json");

	public static Data data = new Data();

	private ForemanConfig() {
	}

	public static class Data {
		// Fishing
		public boolean fishingGlowEnabled = true;

		// Terraform
		public boolean terraformIgnoreAir = true;
		public boolean terraformIgnoreWater = false;
		public boolean terraformIgnoreBedrock = true;
	}

	public static void load() {
		if (!Files.exists(PATH)) {
			save();
			return;
		}
		try (Reader reader = Files.newBufferedReader(PATH, StandardCharsets.UTF_8)) {
			Data loaded = GSON.fromJson(reader, Data.class);
			if (loaded != null) {
				data = loaded;
			}
		} catch (IOException e) {
			ForemanClient.LOGGER.warn("Failed to read config/foreman.json, using defaults", e);
		}
	}

	public static void save() {
		try {
			Files.createDirectories(PATH.getParent());
			try (Writer writer = Files.newBufferedWriter(PATH, StandardCharsets.UTF_8)) {
				GSON.toJson(data, writer);
			}
		} catch (IOException e) {
			ForemanClient.LOGGER.warn("Failed to write config/foreman.json", e);
		}
	}
}
