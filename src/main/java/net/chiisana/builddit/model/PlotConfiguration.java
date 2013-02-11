package net.chiisana.builddit.model;

import org.bukkit.Material;

public class PlotConfiguration {
	/* Eventually, these should be done as configuration variables... for now, hardcode */
	public static int intPlotSize = 32;
	public static int intPlotHeight = 64;
	public static int intRoadWidth = 5;

	public static Material materialRoadA = Material.WOOD;
	public static byte byteMaterialRoadAData = 0;

	public static Material materialRoadB = Material.WOOD;
	public static byte byteMaterialRoadBData = 0;

	public static Material materialWall = Material.STEP;
	public static byte byteMaterialWallData = 0;

	public static Material materialPlotSurface = Material.GRASS;
	public static byte byteMaterialPlotSurfaceData = 0;

	public static Material materialPlotFoundation = Material.DIRT;
	public static byte byteMaterialPlotFoundationData = 0;

	public static Material materialBase = Material.BEDROCK;
	public static byte byteMaterialBaseData = 0;
}
