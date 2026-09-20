package io.github.abduznik.foreman.client.terraform;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.github.abduznik.foreman.client.ForemanClient;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.core.BlockPos;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/**
 * Remembers each world/server's terraform selection across sessions, keyed by server address
 * (multiplayer) or save name (singleplayer), so re-joining a world doesn't force a re-select.
 * Pure client-side bookkeeping of coordinates the player already chose — nothing here reads
 * or writes any block.
 */
public final class SelectionStorage {
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	private static final Path PATH = FabricLoader.getInstance().getConfigDir().resolve("foreman").resolve("selections.json");

	private static Map<String, Entry> entries = new HashMap<>();

	private SelectionStorage() {
	}

	private record Entry(int ax, int ay, int az, int bx, int by, int bz) {
		static Entry of(Selection selection) {
			BlockPos a = selection.getPosA();
			BlockPos b = selection.getPosB();
			return new Entry(a.getX(), a.getY(), a.getZ(), b.getX(), b.getY(), b.getZ());
		}

		void applyTo(Selection selection) {
			selection.setPosA(new BlockPos(ax, ay, az));
			selection.setPosB(new BlockPos(bx, by, bz));
		}
	}

	public static void load() {
		if (!Files.exists(PATH)) {
			return;
		}
		try (Reader reader = Files.newBufferedReader(PATH, StandardCharsets.UTF_8)) {
			Map<String, Entry> loaded = GSON.fromJson(reader, MAP_TYPE);
			if (loaded != null) {
				entries = loaded;
			}
		} catch (IOException e) {
			ForemanClient.LOGGER.warn("Failed to read config/foreman/selections.json, starting empty", e);
		}
	}

	private static void save() {
		try {
			Files.createDirectories(PATH.getParent());
			try (Writer writer = Files.newBufferedWriter(PATH, StandardCharsets.UTF_8)) {
				GSON.toJson(entries, MAP_TYPE, writer);
			}
		} catch (IOException e) {
			ForemanClient.LOGGER.warn("Failed to write config/foreman/selections.json", e);
		}
	}

	private static final java.lang.reflect.Type MAP_TYPE =
			com.google.gson.reflect.TypeToken.getParameterized(Map.class, String.class, Entry.class).getType();

	/** Loads the saved selection for the current world/server into {@code selection}, if any. */
	public static void restore(Selection selection) {
		String key = currentKey();
		if (key == null) {
			return;
		}
		Entry entry = entries.get(key);
		if (entry != null) {
			entry.applyTo(selection);
		}
	}

	/** Persists {@code selection} under the current world/server's key, or clears it if incomplete. */
	public static void persist(Selection selection) {
		String key = currentKey();
		if (key == null) {
			return;
		}
		if (selection.isComplete()) {
			entries.put(key, Entry.of(selection));
		} else {
			entries.remove(key);
		}
		save();
	}

	private static String currentKey() {
		Minecraft client = Minecraft.getInstance();

		ServerData server = client.getCurrentServer();
		if (server != null) {
			return "server:" + server.ip;
		}

		if (client.getSingleplayerServer() != null) {
			return "world:" + client.getSingleplayerServer().getWorldData().getLevelName();
		}

		return null;
	}
}
