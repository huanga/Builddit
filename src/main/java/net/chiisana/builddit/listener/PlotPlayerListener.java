package net.chiisana.builddit.listener;

import net.chiisana.builddit.controller.BuildditPlot;
import net.chiisana.builddit.controller.Plot;
import net.chiisana.builddit.helper.WorldEditHelper;
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
		Plot plot = BuildditPlot.getInstance().getPlotAt(event.getEntity().getLocation());
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

		// Quick way to check if player actually moved far enough for us to care
		if (
				(event.getFrom().getBlockX() == event.getTo().getBlockX())
				&&
				(event.getFrom().getBlockZ() == event.getTo().getBlockZ())
				&&
				(event.getFrom().getWorld() == event.getTo().getWorld())
			)
		{
			// Player didn't really move enough for us to care
			return;
		}

		Plot from = BuildditPlot.getInstance().getPlotAt(event.getFrom());
		Plot to = BuildditPlot.getInstance().getPlotAt(event.getTo());

		// Player is actually moving between plots, check if we need to update mask
		if (!from.toString().equals(to.toString()))
		{
			Player player = event.getPlayer();

			if (from.isAuthorizedFor(player.getName()))
			{
				// Only update if they are authorized in the destination
				WorldEditHelper.removeMask(player, from);
				WorldEditHelper.setMask(player, to);
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
