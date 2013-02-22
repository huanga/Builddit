package net.chiisana.builddit.listener;

import net.chiisana.builddit.controller.BuildditPlot;
import net.chiisana.builddit.controller.Plot;
import net.chiisana.builddit.helper.PlotHelper;
import net.chiisana.builddit.helper.WorldEditHelper;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerShearEntityEvent;

public class PlotPlayerListener implements Listener {
	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerShearEntityEvent(PlayerShearEntityEvent event) {
		Location entityLocation = event.getEntity().getLocation();
		Plot plot = BuildditPlot.getInstance().getPlotAt(entityLocation);
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

	@EventHandler(priority = EventPriority.LOW)
	public void onPlayerMove(PlayerMoveEvent event) {
		// Love it or hate it, we need to track player's location to update the mask automatically :/
		Location from = event.getFrom();
		Location to = event.getTo();

		boolean updateMask = false;

		if (!from.getWorld().equals(to.getWorld()))
		{
			// Changing world, definitely update WE mask
			updateMask = true;
		}

		Plot plotFrom = BuildditPlot.getInstance().getPlotAt(from);
		Plot plotTo = BuildditPlot.getInstance().getPlotAt(to);
		if (!from.getBlock().equals(to.getBlock()))
		{
			// Player is actually moving, check if we need to update mask
			if (!plotFrom.toString().equals(plotTo.toString()))
			{
				updateMask = true;
			}
		}

		Player player = event.getPlayer();
		if (updateMask)
		{
			if (plotTo.isAuthorizedFor(player.getName()))
			{
				// Only update if they are authorized in the destination
				WorldEditHelper.removeMask(player, plotFrom);
				WorldEditHelper.setMask(player, plotTo);
			}
		}
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onPlayerJoin(PlayerJoinEvent event)	{
		Player player = event.getPlayer();
		Plot plot = BuildditPlot.getInstance().getPlotAt(player.getLocation());
		if (plot.isAuthorizedFor(player.getName()))
		{
			// Set the initial mask, nothing to remove for now.
			WorldEditHelper.setMask(player, plot);
		}
	}

}
