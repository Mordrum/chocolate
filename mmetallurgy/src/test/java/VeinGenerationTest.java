import com.mordrum.mmetallurgy.common.generation.VeinGenerator;
import com.mordrum.mmetallurgy.common.generation.util.Vector2i;
import com.mordrum.mmetallurgy.common.generation.util.Vein;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import net.minecraft.util.math.BlockPos;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class VeinGenerationTest {
	private final static int WORLD_SIZE = 200;
	private final static long WORLD_VOLUME = (long) Math.floor(Math.pow(WORLD_SIZE * 16, 2) * 64);

	@Test
	public void generateWeightedVeins() {
		final Logger logger = LogManager.getLogger();
		List<? extends Config> configs = ConfigFactory.parseResources(this.getClass(), "default_config.conf")
				.getConfigList("veins");
		VeinGenerator generator = new VeinGenerator(configs);
		Map<String, Vector2i> totals = new HashMap<>();
		// Do this loop here so we retain a clean order in our logs
		for (Config config : configs) totals.put(config.getString("ore"), new Vector2i(0, 0));
		int veinsSum = 0;
		int oreSum = 0;

		long seed = 1232456789;
		Random random = new Random(seed);

		for (int x = 0; x < WORLD_SIZE; x++) {
			for (int z = 0; z < WORLD_SIZE; z++) {
				// Reset the random
				random.setSeed(seed);

				// Calculate the seed for this chunk
				long xSeed = random.nextLong() >> 2 + 1L;
				long zSeed = random.nextLong() >> 2 + 1L;
				long chunkSeed = (xSeed*x + zSeed*z) ^ seed;
				random.setSeed(chunkSeed);

				double r = random.nextDouble();
				double totalWeight = 0.0;
				for (Config generationConfig : configs) {
					double currentWeight = generationConfig.getDouble("generationChance");
					if (r >= totalWeight && r < (totalWeight + currentWeight)) {
						int seedX = x*16 + random.nextInt(16);
						int seedY = 64;
						int seedZ = z*16 + random.nextInt(16);
						BlockPos seedPos = new BlockPos(seedX, seedY, seedZ);
						Vein vein = generator.generateVein(random, generationConfig, seedPos);
						if (!totals.containsKey(generationConfig.getString("ore"))) {
							totals.put(generationConfig.getString("ore"), new Vector2i(0, 0));
						}
						Vector2i total = totals.get(generationConfig.getString("ore"));
						total.x++;
						total.y += vein.getVein(true).size();
						veinsSum++;
						oreSum += vein.getVein(true).size();
						break;
					} else {
						totalWeight += currentWeight;
					}
				}
			}
		}
		for (Map.Entry<String, Vector2i> entry : totals.entrySet()) {
			logger.info(String.format("[%s]Generated %,d veins, totalling %,d ore (%f%% density)", entry.getKey(), entry.getValue().x, entry.getValue().y, entry.getValue().y * 100.0 / WORLD_VOLUME));
		}
		logger.info(String.format("Generated %,d total veins, totalling %,d ore (%f%% density)", veinsSum, oreSum, oreSum * 100.0 / WORLD_VOLUME));
	}
}
