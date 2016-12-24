package com.mordrum.mmetallurgy.common.generation;

import com.google.common.primitives.Ints;
import com.mordrum.mmetallurgy.common.generation.util.Vein;
import com.typesafe.config.Config;
import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkGenerator;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraftforge.fml.common.IWorldGenerator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class VeinGenerator implements IWorldGenerator {
	private final Map<Block, Config> oreBlocks;
	private final Logger logger;

	public VeinGenerator(List<? extends Config> veins) {
		this.oreBlocks = new HashMap<>();
		this.logger = LogManager.getLogger();
		for (Config oreConfig : veins) {
			Block ore = Block.getBlockFromName(oreConfig.getString("ore").toLowerCase());
			if (ore == null) {
				throw new RuntimeException("Found non-existent ore '" + oreConfig.getString("ore") + "' in configuration file");
			} else {
				oreBlocks.put(ore, oreConfig);
			}
		}
	}

	@Override
	public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) {
		int dimensionId = world.provider.getDimension();

		double r = random.nextDouble();
		double totalWeight = 0.0;
		for (Block oreBlock : oreBlocks.keySet()) {
			Config generationConfig = oreBlocks.get(oreBlock);
			// If this particular ore cannot spawn in this dimension, don't run the weight calculations
			if (!generationConfig.getIntList("dimensions").contains(dimensionId)) continue;

			// Weight calculations
			double currentWeight = generationConfig.getDouble("generationChance");
			if (r >= totalWeight && r < (totalWeight + currentWeight)) {
				int seedX = chunkX * 16 + random.nextInt(16);
				int seedY = Ints.max(world.getChunkFromChunkCoords(chunkX, chunkZ).getHeightMap());; // Choose the highest block
				int seedZ = chunkZ * 16 + random.nextInt(16);
				BlockPos seedPos = new BlockPos(seedX, seedY, seedZ);
				Vein vein = this.generateVein(random, generationConfig, seedPos);
				OreGenerator.getInstance().cacheOres(oreBlock, world, vein.getVein(true));
				int oreTotal = vein.getVein(true).size();
				int fillerTotal = vein.getVein(false).size();
				int volume = oreTotal + fillerTotal;
				int percentOre = Math.floorDiv(oreTotal * 100, volume);
				String debugText = String.format("Placing vein %s at %s, total volume is %d blocks, vein is %d%% (%d) ore", oreBlock
						.getLocalizedName(), seedPos, volume, percentOre, oreTotal);
				logger.info(debugText);
				return;
			} else {
				totalWeight += currentWeight;
			}
		}
	}

	public Vein generateVein(Random random, Config generationConfig, BlockPos seedPos) {
		Vein vein = new Vein(random, seedPos.getY(), generationConfig);
		vein.startBranching(seedPos);
		return vein;
	}
}
