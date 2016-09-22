package com.mordrum.mmetallurgy.common.generation.util;

import com.typesafe.config.Config;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

public class Vein {
	private final TruncatedSkewDistribution skew;
	private boolean main;
	private final int depth;
	private final Random rand;
	private final HashSet<BlockPos> ore;
	private final HashSet<BlockPos> fill;
	private final double maxGrade;
	private final double minGrade;
	private final int maxBranches;
	private final int minBranches;
	private final int maxStrike;
	private final int minStrike;
	private final int maxWidth;
	private final int minWidth;
	private final int ceiling;

	public Vein(Random rand, int ceiling, Config config) {
		this.minGrade = config.getDouble("minGrade");
		this.maxGrade = config.getDouble("maxGrade");
		this.minBranches = config.getInt("minBranches");
		this.maxBranches = config.getInt("maxBranches");
		this.minStrike = config.getInt("minStrike");
		this.maxStrike = config.getInt("maxStrike");
		this.minWidth = config.getInt("minWidth");
		this.maxWidth = config.getInt("maxWidth");
		this.depth = config.getInt("depth");

		this.rand = rand;
		this.skew = new TruncatedSkewDistribution(rand);
		this.main = true;
		this.ore = new HashSet<>();
		this.fill = new HashSet<>();
		this.ceiling = ceiling;
	}

	/**
	 * Starts the main large vein and spawns a series of child veins
	 */
	private void branch(int d, BlockPos start) {
		if (d < this.depth) {
			ArrayList<BlockPos> bezier;
			Vector2i gradeBranch;
			Ellipse ellipse;
			if (main) {
				ellipse = new Ellipse(3 + rand.nextInt(minWidth), 5 + rand.nextInt(maxWidth));
				bezier = Line.bezierCurve(start, Line.getEndPoint(start, 30 + rand.nextInt(maxStrike), rand, ceiling), rand);
				ellipse.alignToPoints(bezier.get(0), bezier.get(bezier.size() - 1));
				gradeBranch = setVeinParams(bezier.size(), ellipse.points.length);
				main = false;
			} else {
				ellipse = new Ellipse(1 + rand.nextInt(3), 1 + rand.nextInt(4));
				int length = (int) (this.skew.getRVar((double) maxStrike, (double) minStrike, -1.0, -0.5));
				bezier = Line.bezierCurve(start, Line.getEndPoint(start, length, rand, ceiling), rand);
				ellipse.alignToPoints(bezier.get(0), bezier.get(bezier.size() - 1));
				gradeBranch = setVeinParams(bezier.size(), ellipse.points.length);
			}

			makeBranch(gradeBranch, bezier, ellipse, d);
		}
	}

	public HashSet<BlockPos> getVein(boolean ore) {
		if (ore)
			return this.ore;
		else
			return this.fill;
	}

	private void makeBranch(Vector2i gradeBranch, ArrayList<BlockPos> bezier, Ellipse ellipse, int d) {
		ArrayList<BlockPos> nuclei = new ArrayList<>();
		bezier.forEach((center) -> {
			for (int i = 0; i < ellipse.points.length; i++) {
				BlockPos point = new BlockPos(
						ellipse.points[i].getX() + center.getX(),
						ellipse.points[i].getY() + center.getY(), ellipse.points[i].getZ() + center.getZ());
				if (point.getY() > 0 && point.getY() < 256) {
					if (rand.nextInt(gradeBranch.y) == 0) {
						ore.add(point);
					} else {
						fill.add(point);
					}
					if (0 == rand.nextInt(gradeBranch.x)) {
						nuclei.add(point);
					}
				}
			}
		});
		nucleate(nuclei, d);
	}

	private void nucleate(ArrayList<BlockPos> nuclei, int d) {
		d++;
		int volume = fill.size() + ore.size();
		if (d < depth) {
			int finalD = d;
			nuclei.forEach((point) -> {
				if (volume < 60000) {
					branch(finalD, point);
				}
			});
		}
	}

	private Vector2i setVeinParams(int bezierLength, int ellipseArea) {
		int volume = bezierLength*ellipseArea;
		Vector2i params;
		if (main) {
			/* ***** Grade****** */
			// define base values by gaussian skew
			double percent = this.skew.getRVar(maxGrade, minGrade, -1.0, 1.5);
			int branchNum = (int) this.skew.getRVar((double) maxBranches, (double) 1, 0.5, 1.0);
			// modify vein params if wanted
			// make param obj
			if (branchNum < 1)
				branchNum = volume*2;
			else
				branchNum = volume/(branchNum*3);
			params = new Vector2i(branchNum, (int) (1/percent) + 1);
		} else {
			// define base values by gaussian skew
			double percent = this.skew.getRVar(maxGrade, minGrade, 0.5, 0.5);
			int branchNum = (int) this.skew.getRVar((double) 4, (double) minBranches, 0.5, 0.2);
			// modify vein params if wanted
			percent = percent*(Math.exp(ellipseArea*-0.005) - 0.5);

			if (bezierLength < branchNum + 1)
				branchNum = volume*2;
			else {
				branchNum = volume/(branchNum);
			}
			// make param obj
			params = new Vector2i(branchNum, (int) (1/percent) + 1);
		}
		return params;
	}

	public void startBranching(BlockPos start) {
		/*
		 * starts the master vein
		 */
		branch(0, start);
	}
}