package com.mordrum.mmetallurgy.common.generation.util;

import net.minecraft.util.math.BlockPos;

// TODO: Auto-generated Javadoc

/**
 * The Class Shape.
 */
class Shape {
	/** The points. */
	BlockPos[] points;

	/**
	 * Align to points.
	 *
	 * @param start the start
	 * @param end the end
	 */
	void alignToPoints(BlockPos start, BlockPos end) {
		double vx = end.getX() - start.getX();
		double vy = end.getY() - start.getY();
		double vz = end.getZ() - start.getZ();
		double r = Math.sqrt(vx*vx + vy*vy + vz*vz);
		double dotProd = vz/r;
		if (dotProd < .95) {
			double cvx, cvy;
			cvx = vy/r;
			cvy = -vx/r;
			double h = (1 - dotProd)/(1 - (dotProd*dotProd));
			double a1 = dotProd + h*cvx*cvx;
			double a2 = h*cvy*cvx;
			double a3 = cvy;
			double b1 = h*cvy*cvx;
			double b2 = dotProd + h*cvy*cvy;
			double b3 = -cvx;
			double c1 = -cvy;
			double c2 = cvx;
			int xrot, yrot, zrot;
			for (int i = 0; i < this.points.length; i++) {
				BlockPos point = this.points[i];
				xrot = (int) Math
						.round((point.getX()*a1 + point.getY()*a2 + point.getZ()*a3));
				yrot = (int) Math
						.round((point.getX()*b1 + point.getY()*b2 + point.getZ()*b3));
				zrot = (int) Math
						.round((point.getX()*c1 + point.getY()*c2 + point.getZ()*dotProd));
				this.points[i] = new BlockPos(xrot, yrot, zrot);
			}
		}
	}

	/**
	 * concatenate.
	 *
	 * @param temp the temp
	 */
	private void concatenate(BlockPos[] temp) {
		int count = 0;
		for (BlockPos aTemp1 : temp) {
			if (aTemp1 != null) {
				count++;
			}
		}
		this.points = new BlockPos[count];
		count = 0;
		for (BlockPos aTemp : temp) {
			if (aTemp != null) {
				this.points[count] = aTemp;
				count++;
			}
		}
	}

	/**
	 * Make ellipse.
	 *
	 * @param a the a
	 * @param b the b
	 */
	void makeEllipse(int a, int b) {
		int i = 1;
		int a2 = a*a;
		int b2 = b*b;
		int fa2 = 4*a2, fb2 = 4*b2;
		int x0, y0, x, y, sigma, xtemp = 1;
		BlockPos[] temp = new BlockPos[4*(a + 1)*(b + 1)];
		temp[0] = new BlockPos(0, 0, 0);
		/* first half, fill in by scanning up to y */
		for (x = 0, y = b, sigma = 2*b2 + a2*(1 - 2*b); b2*x <= a2
				*y; x++) {
			if (x == 0) {
				for (y0 = 1; y0 <= y; y0++) {
					temp[i] = new BlockPos(0, y0, 0);
					temp[i + 1] = new BlockPos(0, -y0, 0);
					i = i + 2;
				}
			} else {
				//TODO deduplicate this code
				for (y0 = 0; y0 <= y; y0++) {
					temp[i] = new BlockPos(x, y0, 0);
					temp[i + 1] = new BlockPos(x, -y0, 0);
					temp[i + 2] = new BlockPos(-x, y0, 0);
					temp[i + 3] = new BlockPos(-x, -y0, 0);
					i = i + 4;
				}
			}
			if (sigma >= 0) {
				sigma += fa2*(1 - y);
				y--;
			}
			sigma += b2*((4*x) + 6);
			xtemp = x;
		}
		xtemp++;
		/*
		 * second half, fill up by scanning from x. Start point is 1 more than
		 * the x value provided before
		 */
		for (x = a, y = 0, sigma = 2*a2 + b2*(1 - 2*a); a2*y <= b2
				*x; y++) {
			if (y == 0) {
				for (x0 = xtemp; x0 <= x; x0++) {
					temp[i] = new BlockPos(x0, 0, 0);
					temp[i + 1] = new BlockPos(-x0, 0, 0);
					i = i + 2;
				}
			} else {
				for (x0 = xtemp; x0 <= x; x0++) {
					temp[i] = new BlockPos(x0, y, 0);
					temp[i + 1] = new BlockPos(x0, -y, 0);
					temp[i + 2] = new BlockPos(-x0, y, 0);
					temp[i + 3] = new BlockPos(-x0, -y, 0);
					i = i + 4;
				}
			}
			if (sigma >= 0) {
				sigma += fb2*(1 - x);
				x--;
			}
			sigma += a2*((4*y) + 6);
		}
		concatenate(temp);
	}

}
