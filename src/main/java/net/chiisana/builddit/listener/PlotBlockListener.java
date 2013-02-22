package net.chiisana.builddit.listener;

import net.chiisana.builddit.controller.BuildditPlot;
import net.chiisana.builddit.controller.Plot;
import net.chiisana.builddit.helper.PlotHelper;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;

public class PlotBlockListener implements Listener {
	@EventHandler(priority = EventPriority.NORMAL)
	public void onBlockDamageEvent(BlockDamageEvent event) {
		// Debug function really.
		Location blockLocation = event.getBlock().getLocation();
		if (event.getPlayer().getItemInHand().getType().equals(Material.YELLOW_FLOWER))
		{
			// Yellow flower = Plot Reader
			event.getPlayer().sendMessage(BuildditPlot.getInstance().getPlotAt(blockLocation).toString());
		}
		else if (event.getPlayer().getItemInHand().getType().equals(Material.RED_ROSE))
		{
			// Red flower = Get connected plots
			for (Plot plot : BuildditPlot.getInstance().getPlotAt(blockLocation).getConnectedPlots())
			{
				event.getPlayer().sendMessage(plot.toString());
			}
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onBlockBreakEvent(BlockBreakEvent event) {
		// Check if the player is allowed to break things here.
		Location blockLocation = event.getBlock().getLocation();

		// Not allowed to edit roads and walls
		if (PlotHelper.isRoad(event.getBlock().getX(),event.getBlock().getZ()) || PlotHelper.isWall(event.getBlock().getX(),  event.getBlock().getZ()))
		{
			event.setCancelled(true);
			BuildditPlot.getInstance().tellNotAuthorizedForRoad(event.getPlayer());
			return;
		}

		Plot plot = BuildditPlot.getInstance().getPlotAt(blockLocation);
		if (plot.isAuthorizedFor(event.getPlayer().getName()))
		{
			return;
		} else {
			// Not Authorized!
			event.setCancelled(true);
			BuildditPlot.getInstance().tellNotAuthorizedForPlot(event.getPlayer(), plot);
			return;
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onBlockFromToEvent(BlockFromToEvent event) {
		// Prevent lava/water griefing
		Plot plotFrom = BuildditPlot.getInstance().getPlotAt(event.getBlock().getLocation());
		Plot plotTo = BuildditPlot.getInstance().getPlotAt(event.getToBlock().getLocation());
		if (plotFrom != plotTo)
		{
			event.setCancelled(true);
			return;
		}

		// Prevent dragon eggs from teleporting
		if (event.getBlock().getType().equals(Material.DRAGON_EGG))
		{
			event.setCancelled(true);
			return;
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onBlockPistonExtendEvent(BlockPistonExtendEvent event) {
		// Disallow piston pushing blocks into other people's plot
		Plot plotFrom = BuildditPlot.getInstance().getPlotAt(event.getBlock().getLocation());
		Plot plotTo;
		for (Block block : event.getBlocks())
		{
			plotTo = BuildditPlot.getInstance().getPlotAt(block.getLocation());
			if (plotFrom != plotTo)
			{
				// Note: No way to trace who invoked the redstone, so there would be no announcement
				event.setCancelled(true);
				return;
			}
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onBlockPlaceEvent(BlockPlaceEvent event) {
		// Check if the player is allowed to place things here.
		Location blockLocation = event.getBlock().getLocation();

		// Not allowed to edit roads and walls
		if (PlotHelper.isRoad(event.getBlock().getX(),event.getBlock().getZ()) || PlotHelper.isWall(event.getBlock().getX(),  event.getBlock().getZ()))
		{
			event.setCancelled(true);
			BuildditPlot.getInstance().tellNotAuthorizedForRoad(event.getPlayer());
			return;
		}

		Plot plot = BuildditPlot.getInstance().getPlotAt(blockLocation);
		if (plot.isAuthorizedFor(event.getPlayer().getName()))
		{
			return;
		} else {
			// Not Authorized!
			event.setCancelled(true);
			BuildditPlot.getInstance().tellNotAuthorizedForPlot(event.getPlayer(), plot);
			return;
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onSignChangeEvent(SignChangeEvent event) {
		// This should theoretically never happen, as there is no
		// way for the to place signs in place they can't access
		// but we're including it just on the safe side.

		Plot plot = BuildditPlot.getInstance().getPlotAt(event.getBlock().getLocation());
		if (plot.isAuthorizedFor(event.getPlayer().getName()))
		{
			return;
		} else {
			// Not Authorized!
			event.setCancelled(true);
			BuildditPlot.getInstance().tellNotAuthorizedForPlot(event.getPlayer(), plot);
		}
	}
}
