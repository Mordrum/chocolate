package com.mordrum.mmetallurgy.common.generation;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.mordrum.mmetallurgy.MMetallurgy;
import com.mordrum.mmetallurgy.common.generation.util.Vector2i;
import com.mordrum.mmetallurgy.common.generation.util.Vector3i;
import io.netty.util.internal.ConcurrentSet;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.fml.common.IWorldGenerator;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class OreGenerator implements IWorldGenerator {
	private static final OreGenerator ourInstance = new OreGenerator();
	public static OreGenerator getInstance() {
		return ourInstance;
	}

	private final ConcurrentHashMap<Integer, ConcurrentHashMap<Vector2i, ConcurrentHashMap<Integer, ConcurrentSet<Vector3i>>>> cache;
	private final Set<IBlockState> blocksThatCanBeReplaced;

	private OreGenerator() {
		cache = new ConcurrentHashMap<>();
		try {
			populateCache();
		} catch (IOException e) {
			e.printStackTrace();
		}
		blocksThatCanBeReplaced = ImmutableSet.of(
				Blocks.STONE.getDefaultState(),
				Blocks.GRASS.getDefaultState(),
				Blocks.DIRT.getDefaultState(),
				Blocks.SANDSTONE.getDefaultState(),
				Blocks.SAND.getDefaultState()
		);
	}

	@Override
	public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) {
		// This world is not in the cache, exit
		if (!cache.containsKey(world.provider.getDimension())) return;
		ConcurrentHashMap<Vector2i, ConcurrentHashMap<Integer, ConcurrentSet<Vector3i>>> worldChunks = cache.get(world.provider.getDimension());

		// This chunk is not in the cache, exit
		if (!worldChunks.containsKey(new Vector2i(chunkX, chunkZ))) return;
		ConcurrentHashMap<Integer, ConcurrentSet<Vector3i>> chunkOres = worldChunks.get(new Vector2i(chunkX, chunkZ));

		// Iterate over the different ore types cached for this chunk
		chunkOres.keySet().forEach((oreID) -> {
			// Get the blockstate representation of the ore
			IBlockState defaultState = Block.getBlockById(oreID).getDefaultState();
			// Iterate over all positions where we CAN place this ore
			chunkOres.get(oreID).forEach((position) -> {
				// Get the existing block at this location and check if it's okay to replace it
				BlockPos blockPos = new BlockPos(position.getX(), position.getY(), position.getZ());
				IBlockState existingState = world.getBlockState(blockPos);
				if (blocksThatCanBeReplaced.contains(existingState)) world.setBlockState(blockPos, defaultState);
			});
		});

		// Flush this chunk from the cache
		worldChunks.remove(new Vector2i(chunkX, chunkZ));
	}

	void cacheOres(Block oreBlock, World world, HashSet<BlockPos> positions) {
		// Ensure the cache contains this world / dimension, then grab the chunks for the world
		if (!cache.containsKey(world.provider.getDimension())) cache.put(world.provider.getDimension(), new ConcurrentHashMap<>());
		final ConcurrentHashMap<Vector2i, ConcurrentHashMap<Integer, ConcurrentSet<Vector3i>>> chunks = cache.get(world.provider.getDimension());

		// Prefetch the oreblock's ID
		final int oreID = Block.getIdFromBlock(oreBlock);

		// Iterate over all ore positions to cache
		positions.parallelStream().forEach((blockPos -> {
			// Ensure that the current block position falls within the height boundaries of the world
			if (blockPos.getY() >= 0 && blockPos.getY() < world.getHeight()) {
				// Figure out which chunk this block is located in, then grab the
				final Vector2i chunkPos = new Vector2i(blockPos.getX() >> 4, blockPos.getZ() >> 4);
				if (!chunks.containsKey(chunkPos)) chunks.put(chunkPos, new ConcurrentHashMap<>());
				final ConcurrentHashMap<Integer, ConcurrentSet<Vector3i>> metalSetMap = chunks.get(chunkPos);

				if (metalSetMap == null) {
					throw new RuntimeException("Somehow metalSetMap was null, this should never happen!");
				} else {
					// Grab the cached positions for this ore (in the current chunk), then cache the current position
					if (!metalSetMap.containsKey(oreID)) metalSetMap.put(oreID, new ConcurrentSet<>());
					ConcurrentSet<Vector3i> cachedPositions = metalSetMap.get(oreID);
					cachedPositions.add(new Vector3i(blockPos.getX(), blockPos.getY(), blockPos.getZ()));
				}
			}
		}));
	}

	private void populateCache() throws IOException {
		// Get the cache file and exit if it doesn't exist
		File file = new File("orecache.json");
		if (!file.exists()) return;

		String rawJSON = FileUtils.readFileToString(file);
		JsonParser parser = new JsonParser();
		JsonArray worlds = parser.parse(rawJSON).getAsJsonArray();
		long startTime = System.currentTimeMillis();

		// new hotness
		worlds.forEach((worldElement) -> {
			final JsonArray worldAsArray = worldElement.getAsJsonArray();
			final int worldID = worldAsArray.get(0).getAsInt();

			if (!cache.containsKey(worldID)) cache.put(worldID, new ConcurrentHashMap<>());
			final ConcurrentHashMap<Vector2i, ConcurrentHashMap<Integer, ConcurrentSet<Vector3i>>> worldMap = cache.get(worldID);

			worldAsArray.get(1).getAsJsonArray().forEach((chunkXElement) -> {
				final JsonArray chunkXAsArray = chunkXElement.getAsJsonArray();
				final int chunkX = chunkXAsArray.get(0).getAsInt();

				chunkXAsArray.get(1).getAsJsonArray().forEach((chunkYElement) -> {
					final JsonArray chunkYAsArray = chunkYElement.getAsJsonArray();
					final int chunkY = chunkYAsArray.get(0).getAsInt();

					final Vector2i chunkPos = new Vector2i(chunkX, chunkY);
					if (!worldMap.containsKey(chunkPos)) worldMap.put(chunkPos, new ConcurrentHashMap<>());
					final ConcurrentHashMap<Integer, ConcurrentSet<Vector3i>> chunkMap = worldMap.get(chunkPos);

					chunkYAsArray.get(1).getAsJsonArray().forEach((oreIdElement) -> {
						final JsonArray oreIdAsArray = oreIdElement.getAsJsonArray();
						final int oreId = oreIdAsArray.get(0).getAsInt();

						if (!chunkMap.containsKey(oreId)) chunkMap.put(oreId, new ConcurrentSet<>());
						final ConcurrentSet<Vector3i> oreSet = chunkMap.get(oreId);

						oreIdAsArray.get(1).getAsJsonArray().forEach((blockXElement) -> {
							final JsonArray blockXAsArray = blockXElement.getAsJsonArray();
							final int blockX = blockXAsArray.get(0).getAsInt();

							blockXAsArray.get(1).getAsJsonArray().forEach((blockYElement) -> {
								final JsonArray blockYAsArray = blockYElement.getAsJsonArray();
								final int blockY = blockYAsArray.get(0).getAsInt();

								blockYAsArray.get(1).getAsJsonArray().forEach((blockZElement) -> {
									final int blockZ = blockZElement.getAsInt();
									final Vector3i blockPos = new Vector3i(blockX, blockY, blockZ);
									oreSet.add(blockPos);
								});
							});
						});
					});
				});
			});
		});
		LogManager.getLogger().info("Populated cache in %ims", System.currentTimeMillis() - startTime);
	}

	public void saveCachedOres() throws IOException {
		File file = new File("orecache.json");
		if (file.exists()) file.delete();

		JsonArray enclosingArray = new JsonArray();
		cache.keySet().forEach((dimensionId) -> {
			ConcurrentHashMap<Vector2i, ConcurrentHashMap<Integer, ConcurrentSet<Vector3i>>> chunkMap = cache.get(dimensionId);
			chunkMap.keySet().forEach((chunkPos) -> {
				ConcurrentHashMap<Integer, ConcurrentSet<Vector3i>> oreMap = chunkMap.get(chunkPos);
				oreMap.keySet().forEach((oreId) -> {
					ConcurrentSet<Vector3i> positions = oreMap.get(oreId);
					positions.forEach((position) -> {
						int chunkX = chunkPos.getX();
						int chunkY = chunkPos.getY();
						int x = position.getX();
						int y = position.getY();
						int z = position.getZ();

						JsonArray dimensionArray = godDammit(dimensionId, enclosingArray);
						JsonArray chunkXArray = godDammit(chunkX, dimensionArray);
						JsonArray chunkYArray = godDammit(chunkY, chunkXArray);
						JsonArray oreIdArray = godDammit(oreId, chunkYArray);
						JsonArray xArray = godDammit(x, oreIdArray);
						JsonArray yArray = godDammit(y, xArray);
						yArray.add(new JsonPrimitive(z));
					});
				});
			});

		});

		FileUtils.writeStringToFile(file, MMetallurgy.gson.toJson(enclosingArray));
	}

	private static JsonArray godDammit(int value, JsonArray array) {
		for (JsonElement jsonElement : array) {
			JsonArray asJsonArray = jsonElement.getAsJsonArray();
			if (asJsonArray.get(0).getAsInt() == value) return asJsonArray.get(1).getAsJsonArray();
		}
		// the value wasn't already stored
		JsonArray unit = new JsonArray();
		unit.add(new JsonPrimitive(value));
		JsonArray jsonElements = new JsonArray();
		unit.add(jsonElements);
		array.add(unit);
		return jsonElements;
	}
}
