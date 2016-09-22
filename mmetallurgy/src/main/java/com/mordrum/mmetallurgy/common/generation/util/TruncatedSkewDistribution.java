package com.mordrum.mmetallurgy.common.generation.util;

import java.util.Random;

class TruncatedSkewDistribution {
	private final Random rand;

	TruncatedSkewDistribution(Random rand) {
		this.rand = rand;
	}

	double getRVar(double max, double min, double skew, double bias) {
		double range = max - min;
		double mid = min + (range/2.0);
		double unitGaussian = rand.nextGaussian();
		double biasFactor = Math.exp(bias);
		return mid + (range*(biasFactor/(biasFactor + Math.exp(-unitGaussian/skew)) - 0.5));
	}
}
