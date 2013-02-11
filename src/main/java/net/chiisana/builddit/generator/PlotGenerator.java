package net.chiisana.builddit.generator;

import net.chiisana.builddit.Builddit;
import net.chiisana.builddit.model.PlotConfiguration;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.ChunkGenerator;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;

public class PlotGenerator extends ChunkGenerator {
	@Override
	public List<BlockPopulator> getDefaultPopulators(World world) {
		return Arrays.asList((BlockPopulator) new PlotPopulator());
	}

	@Override
	public boolean canSpawn(World world, int x, int z) {
		return true;
	}

	@Override
	public Location getFixedSpawnLocation(World world, Random random) {
		return new Location(world, 0, PlotConfiguration.intPlotHeight + 2, 0);
	}

	@Override
	public short[][] generateExtBlockSections(World world, Random random, int cx, int cz, BiomeGrid biomes) {
		Builddit.getInstance().getLogger().log(Level.INFO, "Entering generateExtBlockSections");

		int height = PlotConfiguration.intPlotHeight + 2;
		int cxx = cx << 4;
		int czz = cz << 4;

		short[][] result = new short[world.getMaxHeight()/16][];
		for (int x = 0; x < 16; x++) {
			Builddit.getInstance().getLogger().log(Level.INFO, "...for x: " + x);
			for (int z = 0; z < 16; z++) {
				Builddit.getInstance().getLogger().log(Level.INFO, "...for z: " + z);
				for (int y = 0; y < height; y++) {
					Builddit.getInstance().getLogger().log(Level.INFO, "...for y: " + y);
					if (y == 0) {
						// Base Layer
						setBlock(result, x, y, z, (short)PlotConfiguration.materialBase.getId());
					} else if (y <= PlotConfiguration.intPlotHeight - 1) {
						// Foundation Layer
						setBlock(result, x, y, z, (short)PlotConfiguration.materialPlotFoundation.getId());
					} else if (y == PlotConfiguration.intPlotHeight) {
						// Surface|Road Layer
						if (PlotHelper.isRoad(cxx, czz, x, z)) {
							setBlock(result, x, y, z, (short)PlotConfiguration.materialRoadA.getId());
						} else {
							setBlock(result, x, y, z, (short)PlotConfiguration.materialPlotSurface.getId());
						}
					} else if (y == PlotConfiguration.intPlotHeight + 1) {
						// Wall Layer
						if (PlotHelper.isWall(cxx, czz, x, z)) {
							setBlock(result, x, y, z, (short)PlotConfiguration.materialWall.getId());
						}
					}
				}
			}
		}
		return result;
	}

	private void setBlock(short[][] result, int x, int y, int z, Short blkid) {
		if (result[y >> 4] == null) {
			result[y >> 4] = new short[4096];
		}
		result[y >> 4][((y & 0xF) << 8) | (z << 4) | x] = blkid;
	}
}
