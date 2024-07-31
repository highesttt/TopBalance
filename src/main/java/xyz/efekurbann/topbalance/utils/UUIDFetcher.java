package xyz.efekurbann.topbalance.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

import xyz.efekurbann.topbalance.utils.UUIDRes.UUIDResponse;
import com.mojang.util.UUIDTypeAdapter;
import org.bukkit.Bukkit;

public class UUIDFetcher {

	/**
	 * Date when name changes were introduced
	 * @see UUIDFetcher#getUUIDAt(String, long)
	 */
	public static final long FEBRUARY_2015 = 1422748800000L;


	private static com.google.gson.Gson gson = new com.google.gson.GsonBuilder().registerTypeAdapter(UUID.class, new UUIDTypeAdapter()).create();

	private static final String UUID_URL = "https://playerdb.co/api/player/minecraft/%s?at=%d";
	private static final String NAME_URL = "https://playerdb.co/api/player/minecraft/%s";

	private static Map<String, UUID> uuidCache = new HashMap<String, UUID>();
	private static Map<UUID, String> nameCache = new HashMap<UUID, String>();

	private static ExecutorService pool = Executors.newCachedThreadPool();

	/**
	 * Fetches the uuid asynchronously and passes it to the consumer
	 *
	 * @param name The name
	 * @param action Do what you want to do with the uuid her
	 */
	public static void getUUID(String name, Consumer<UUID> action) {
		pool.execute(() -> action.accept(getUUID(name)));
	}

	/**
	 * Fetches the uuid synchronously and returns it
	 *
	 * @param name The name
	 * @return The uuid
	 */
	public static UUID getUUID(String name) {
		if (Bukkit.getOfflinePlayer(name).hasPlayedBefore()) {
			return Bukkit.getOfflinePlayer(name).getUniqueId();
		}
		return getUUIDAt(name, System.currentTimeMillis());
	}

	/**
	 * Fetches the uuid synchronously for a specified name and time and passes the result to the consumer
	 *
	 * @param name The name
	 * @param timestamp Time when the player had this name in milliseconds
	 * @param action Do what you want to do with the uuid her
	 */
	public static void getUUIDAt(String name, long timestamp, Consumer<UUID> action) {
		pool.execute(() -> action.accept(getUUIDAt(name, timestamp)));
	}

	/**
	 * Fetches the uuid synchronously for a specified name and time
	 *
	 * @param name The name
	 * @param timestamp Time when the player had this name in milliseconds
	 * @see UUIDFetcher#FEBRUARY_2015
	 */
	public static UUID getUUIDAt(String name, long timestamp) {
		name = name.toLowerCase();
		if (uuidCache.containsKey(name)) {
			return uuidCache.get(name);
		}
		try {
			HttpURLConnection connection = (HttpURLConnection) new URL(String.format(UUID_URL, name, timestamp/1000)).openConnection();
			connection.setReadTimeout(5000);
			UUIDResponse data = gson.fromJson(new BufferedReader(new InputStreamReader(connection.getInputStream())), UUIDResponse.class);

			uuidCache.put(name, data.data.player.raw_id);
			nameCache.put(data.data.player.raw_id, data.data.player.username);

			return data.data.player.raw_id;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * Fetches the name asynchronously and passes it to the consumer
	 *
	 * @param uuid The uuid
	 * @param action Do what you want to do with the name her
	 */
	public static void getName(UUID uuid, Consumer<String> action) {
		pool.execute(() -> action.accept(getName(uuid)));
	}

	/**
	 * Fetches the name synchronously and returns it
	 *
	 * @param uuid The uuid
	 * @return The name
	 */
	public static String getName(UUID uuid) {
		if (nameCache.containsKey(uuid)) {
			return nameCache.get(uuid);
		}
		try {
			HttpURLConnection connection = (HttpURLConnection) new URL(String.format(NAME_URL, Bukkit.getOfflinePlayer(uuid).getName())).openConnection();
			connection.setReadTimeout(5000);
			UUIDResponse currentNameData = gson.fromJson(new BufferedReader(new InputStreamReader(connection.getInputStream())), UUIDResponse.class);

			uuidCache.put(currentNameData.data.player.username.toLowerCase(), uuid);
			nameCache.put(uuid, currentNameData.data.player.username);

			return currentNameData.data.player.username;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}
}