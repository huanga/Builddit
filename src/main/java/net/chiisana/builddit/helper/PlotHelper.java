package net.chiisana.builddit.helper;

import net.chiisana.builddit.model.PlotConfiguration;
import org.bukkit.Location;

/**
 * Created with IntelliJ IDEA.
 * User: andy
 * Date: 2013-02-10
 * Time: 5:01 PM
 */
public class PlotHelper {
	/*
	boolean isRoad(int cxx, int czz, int lx, int lz)
		input:  cxx - chunk x << 4
				czz - chunk z << 4
				lx - local x
				lz - local z
		output:
				returns true if tile is supposed to be road
				returns false for all other tiles
	*/
	public static boolean isRoad(int cxx, int czz, int lx, int lz) {
		int plotSize = PlotConfiguration.intPlotSize + PlotConfiguration.intRoadWidth + 2;  // +2 for wall
		if (
				((modulus((cxx + lx), plotSize) < plotSize - 1) && (modulus((cxx + lx), plotSize) > plotSize - 2 - PlotConfiguration.intRoadWidth))
						||
						((modulus((czz + lz), plotSize) < plotSize - 1) && (modulus((czz + lz), plotSize) > plotSize - 2 - PlotConfiguration.intRoadWidth))
				) {
			return true;
		}
		return false;
	}

	public static boolean isRoad(int x, int z) {
		return isRoad(0,0,x,z);
	}

	/*
	boolean isWall(int cxx, int czz, int lx, int lz)
		input:  cxx - chunk x << 4
				czz - chunk z << 4
				lx - local x
				lz - local z
		output:
				returns true if tile is supposed to be wall
				returns false for all other tiles
	*/
	public static boolean isWall(int cxx, int czz, int lx, int lz) {
		int plotSize = PlotConfiguration.intPlotSize + PlotConfiguration.intRoadWidth + 2;  // +2 for wall
		if (
				((modulus((cxx + lx), plotSize) == plotSize - 1) || (modulus((cxx + lx), plotSize) == plotSize - 2 - PlotConfiguration.intRoadWidth))
						||
						((modulus((czz + lz), plotSize) == plotSize - 1) || (modulus((czz + lz), plotSize) == plotSize - 2 - PlotConfiguration.intRoadWidth))
				) {
			// Wall also should not run on top of road
			if (!isRoad(cxx, czz, lx, lz)) {
				return true;
			}
		}
		return false;
	}

	public static boolean isWall(int x, int z) {
		return isWall(0,0,x,z);
	}

	public static int modulus(int n, int m) {
		return (n < 0) ? (m - (StrictMath.abs(n) % m)) % m : (n % m);
	}

	public static int getPX(Location location) {
		if (location.getX() >= 0) {
			return ((int)StrictMath.ceil(location.getX()/(PlotConfiguration.intPlotSize+PlotConfiguration.intRoadWidth+2)));
		} else {
			return ((int)StrictMath.floor(location.getX()/(PlotConfiguration.intPlotSize+PlotConfiguration.intRoadWidth+2)));
		}
	}

	public static int getPZ(Location location) {
		if (location.getZ() >= 0) {
			return ((int)StrictMath.ceil(location.getZ()/(PlotConfiguration.intPlotSize+PlotConfiguration.intRoadWidth+2)));
		} else {
			return ((int)StrictMath.floor(location.getZ()/(PlotConfiguration.intPlotSize+PlotConfiguration.intRoadWidth+2)));
		}
	}
}
