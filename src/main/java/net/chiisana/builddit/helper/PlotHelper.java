package net.chiisana.builddit.helper;

import net.chiisana.builddit.model.PlotConfiguration;
import org.bukkit.Location;

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
		if (
				((modulus((cxx + lx), PlotConfiguration.intPlotCalculatedSize) < PlotConfiguration.intPlotCalculatedSize - 1) && (modulus((cxx + lx), PlotConfiguration.intPlotCalculatedSize) > PlotConfiguration.intPlotSize))
				||
				((modulus((czz + lz), PlotConfiguration.intPlotCalculatedSize) < PlotConfiguration.intPlotCalculatedSize - 1) && (modulus((czz + lz), PlotConfiguration.intPlotCalculatedSize) > PlotConfiguration.intPlotSize))
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
		if (
				((modulus((cxx + lx), PlotConfiguration.intPlotCalculatedSize) == PlotConfiguration.intPlotCalculatedSize - 1) || (modulus((cxx + lx), PlotConfiguration.intPlotCalculatedSize) == PlotConfiguration.intPlotSize))
				||
				((modulus((czz + lz), PlotConfiguration.intPlotCalculatedSize) == PlotConfiguration.intPlotCalculatedSize - 1) || (modulus((czz + lz), PlotConfiguration.intPlotCalculatedSize) == PlotConfiguration.intPlotSize))
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
		int px;
		px = ((int)StrictMath.floor(location.getX()/PlotConfiguration.intPlotCalculatedSize));
		return px;
	}

	public static int getPZ(Location location) {
		int pz;
		pz = ((int)StrictMath.floor(location.getZ()/PlotConfiguration.intPlotCalculatedSize));
		return pz;
	}
}
