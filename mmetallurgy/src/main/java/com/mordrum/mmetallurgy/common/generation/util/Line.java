package com.mordrum.mmetallurgy.common.generation.util;

import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.Random;

class Line {
	public static ArrayList<BlockPos> bezierCurve(BlockPos start, BlockPos end, Random rand) {
		int vx = end.getX() - start.getX();
		int vy = end.getY() - start.getY();
		int vz = end.getZ() - start.getZ();
		int dist = (int) Math.sqrt(vx*vx + vy*vy + vz*vz); // Distance between start and end points
		int x = start.getX();
		int y = start.getY();
		int z = start.getZ();
		double step;
		BlockPos[] offsets;
		// We figure out how many random points to disperse in the middle of the line, based off of the distance between the endpoints.
		if (dist < 15) {
			return bresenham(start, end);
		} else if (dist >= 15 && dist < 60) {
			offsets = new BlockPos[3];
			BlockPos mid = new BlockPos(x + vx/2, y + vy/2, z + vz/2);
			offsets[1] = getEndPoint(mid, dist/2, rand);
			step = 1.0/4;
		} else if (dist >= 60 && dist < 120) {
			offsets = new BlockPos[4];
			BlockPos midone = new BlockPos(x + vx/3, y + vy/3, z + vz/3);
			offsets[1] = getEndPoint(midone, dist/3, rand);
			BlockPos midtwo = new BlockPos(x + (2*vx/3), y + (2*vy/3), z + (2*vz/3));
			offsets[2] = getEndPoint(midtwo, dist/3, rand);
			step = 1.0/6;
		} else {
			offsets = new BlockPos[5];
			BlockPos midone = new BlockPos(x + (vx/4), y + (vy/4), z + (vz/4));
			offsets[1] = getEndPoint(midone, dist/4, rand);
			BlockPos midtwo = new BlockPos(x + (2*vx/4), y + (2*vy/4), z + (2*vz/4));
			offsets[2] = getEndPoint(midtwo, dist/4, rand);
			BlockPos midthree = new BlockPos(x + (3*vx/4), y + (3*vy/4), z + (3*vz/4));
			offsets[3] = getEndPoint(midthree, dist/4, rand);
			step = 1.0/10;
		}
		int n = offsets.length - 1;
		offsets[0] = start;
		offsets[n] = end;
		double t = step;
		BlockPos prev = start;
		BlockPos next;
		ArrayList<BlockPos> veinPoints = new ArrayList<>();
		while (t < 1.0 + step) {
			x = 0;
			y = 0;
			z = 0;
			// Perform the summation needed for a Bezier curve
			for (int i = 0; i <= n; i++) {
				x += (int) (doubleOps(i, n, t)*offsets[i].getX());
				y += (int) (doubleOps(i, n, t)*offsets[i].getY());
				z += (int) (doubleOps(i, n, t)*offsets[i].getZ());
			}
			next = new BlockPos(x, y, z);
			veinPoints.addAll(bresenham(prev, next));
			prev = next;
			t = t + step;
		}
		return veinPoints;
	}

	private static int binomialCoefficient(int n, int k)
	{
		int coefficient = 1;
		for (int i = n - k + 1; i <= n; i++) {
			coefficient *= i;
		}
		for (int i = 1; i <= k; i++) {
			coefficient /= i;
		}
		return coefficient;
	}

	private static ArrayList<BlockPos> bresenham(BlockPos start, BlockPos end) {
		ArrayList<BlockPos> thePoints = new ArrayList<>();
		thePoints.add(start);
		int xMult = 1, yMult = 1, zMult = 1, x, y, z;
		int Dz = end.getZ() - start.getZ();
		int Dy = end.getY() - start.getY();
		int Dx = end.getX() - start.getX();
		x = start.getX();
		y = start.getY();
		z = start.getZ();
		if (Dz < 0) {
			Dz = -Dz;
			zMult = -1;
		}
		if (Dy < 0) {
			Dy = -Dy;
			yMult = -1;
		}
		if (Dx < 0) {
			Dx = -Dx;
			xMult = -1;
		}
		int dx2 = Dx*2;
		int dy2 = Dy*2;
		int dz2 = Dz*2;

		//TODO fix this mess
		if (Dx >= Dy && Dx >= Dz) {
			int errA = dy2 - Dx;
			int errB = dz2 - Dx;
			for (int i = 0; i < Dx; i++) {
				if (errA > 0) {
					y += yMult;
					errA -= dx2;
				}
				if (errB > 0) {
					z += zMult;
					errB -= dx2;
				}
				errA += dy2;
				errB += dz2;
				x += xMult;
				BlockPos point = new BlockPos(x, y, z);
				thePoints.add(point);
			}
		} else if (Dy > Dx && Dy >= Dz) {
			int errA = dx2 - Dy;
			int errB = dz2 - Dy;
			for (int i = 0; i < Dy; i++) {
				if (errA > 0) {
					x += xMult;
					errA -= dy2;
				}
				if (errB > 0) {
					z += zMult;
					errB -= dy2;
				}
				errA += dx2;
				errB += dz2;
				y += yMult;
				BlockPos point = new BlockPos(x, y, z);
				thePoints.add(point);
			}
		} else {
			int errA = dy2 - Dz;
			int errB = dx2 - Dz;
			for (int i = 0; i < Dz; i++) {
				if (errA > 0) {
					y += yMult;
					errA -= dz2;
				}
				if (errB > 0) {
					x += xMult;
					errB -= dz2;
				}
				errA += dy2;
				errB += dx2;
				z += zMult;
				BlockPos point = new BlockPos(x, y, z);
				thePoints.add(point);
			}
		}
		return thePoints;
	}

	private static double doubleOps(int i, int n, double t) {
		return binomialCoefficient(n, i)*Math.pow(1 - t, n - i)*Math.pow(t, i);
	}

	private static BlockPos getEndPoint(BlockPos start, int strike, Random rand) {
		if (strike == 0)
			return start;
		int rx = strike/2 - returnGaussian((strike), strike, rand);
		int ry = strike/2 - returnGaussian((int) (strike*.5), strike, rand);
		int rz = strike/2 - returnGaussian((strike), strike, rand);
		BlockPos endpoint = new BlockPos(rx, ry, rz);
		return new BlockPos(start.getX() + endpoint.getX(), start.getY() + endpoint.getY(), start.getZ() + endpoint.getZ());
	}

	static BlockPos getEndPoint(BlockPos start, int strike, Random rand, int maxHeight) {
		if (strike == 0)
			return start;
		int rx = strike/2 - returnGaussian((int) (strike*.75), strike, rand);
		int ry = strike/2 - returnGaussian((int) (strike*.5), strike, rand);
		int rz = strike/2 - returnGaussian((int) (strike*.75), strike, rand);
		while (ry > maxHeight) {
			ry = strike/2 - returnGaussian((int) (strike*.5), strike, rand);
		}
		BlockPos endpoint = new BlockPos(rx, ry, rz);
		return new BlockPos(start.getX() + endpoint.getX(), start.getY() + endpoint.getY(), start.getZ() + endpoint.getZ());
	}

	private static int returnGaussian(int deviance, int mean, Random rand) {
		return (int) (deviance*rand.nextGaussian()) + mean;
	}

}