package net.chiisana.builddit.helper;

import com.sk89q.worldedit.LocalPlayer;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.bukkit.WorldEditAPI;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.masks.CombinedMask;
import com.sk89q.worldedit.masks.RegionMask;
import com.sk89q.worldedit.regions.CuboidRegion;
import net.chiisana.builddit.Builddit;
import net.chiisana.builddit.controller.Plot;
import org.bukkit.entity.Player;

public class WorldEditHelper {
	public static void setMask(Player player, Plot plot) {
		// Adds relevant plots to the current EditSession's mask
		WorldEditPlugin wePlugin = Builddit.getInstance().wePlugin;
		WorldEditAPI weAPI = Builddit.getInstance().weAPI;

		LocalPlayer localPlayer = wePlugin.wrapPlayer(player);
		LocalSession session = weAPI.getSession(player);

		CombinedMask mask = new CombinedMask();

		// Retain their current mask settings (i.e.: for blocks)
		if (session.getMask() != null)
		{
			mask.add(session.getMask());
		}

		// Go through each of the connected plots, and add a region mask for them
		for (Plot connectedPlot : plot.getConnectedPlots())
		{
			Vector v1 = new Vector(connectedPlot.getBottom().getX(), connectedPlot.getBottom().getY(), connectedPlot.getBottom().getZ());
			Vector v2 = new Vector(connectedPlot.getTop().getX(), connectedPlot.getTop().getY(), connectedPlot.getTop().getZ());

			CuboidRegion cuboidRegion = new CuboidRegion(localPlayer.getWorld(), v1, v2);
			RegionMask regionMask = new RegionMask(cuboidRegion);
			mask.add(regionMask);
		}

		// Finally, set the mask back to their edit session
		session.setMask(mask);
	}
}
