package net.chiisana.builddit.generator;

import net.chiisana.builddit.Builddit;
import net.chiisana.builddit.model.PlotConfiguration;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.generator.BlockPopulator;

import java.util.Random;
import java.util.logging.Level;

public class PlotPopulator extends BlockPopulator {
	@Override
	public void populate(World world, Random random, Chunk chunk) {
		Builddit.getInstance().getLogger().log(Level.INFO, "Entering populate");
		int cxx = chunk.getX() << 4;
		int czz = chunk.getZ() << 4;

		for (int x = 0; x < 16; x++) {
			Builddit.getInstance().getLogger().log(Level.INFO, "...for x: " + x);
			for (int z = 0; z < 16; x++) {
				Builddit.getInstance().getLogger().log(Level.INFO, "...for z: " + z);
				world.setBiome(x + cxx, z + czz, Biome.PLAINS);
				for (int y = 0; y < PlotConfiguration.intPlotHeight + 2; y++) {
					Builddit.getInstance().getLogger().log(Level.INFO, "...for y: " + y);
					if (y == 0) {
						// Base Layer
						setBlockData(world, x, y, z, PlotConfiguration.byteMaterialBaseData);
					} else if (y <= PlotConfiguration.intPlotHeight - 1) {
						// Foundation Layer
						setBlockData(world, x, y, z, PlotConfiguration.byteMaterialPlotFoundationData);
					} else if (y == PlotConfiguration.intPlotHeight) {
						// Surface|Road Layer
						if (PlotHelper.isRoad(cxx, czz, x, z)) {
							setBlockData(world, x, y, z, PlotConfiguration.byteMaterialRoadAData);
						} else {
							setBlockData(world, x, y, z, PlotConfiguration.byteMaterialPlotSurfaceData);
						}
					} else if (y == PlotConfiguration.intPlotHeight + 1) {
						// Wall Layer
						if (PlotHelper.isWall(cxx, czz, x, z)) {
							setBlockData(world, x, y, z, PlotConfiguration.byteMaterialWallData);
						}
					}
				}
			}
		}
	}

	private void setBlockData(World w, int x, int y, int z, byte blkData)
	{
		if (blkData != 0) {
			w.getBlockAt(x, y, z).setData(blkData);
		}
	}
}
