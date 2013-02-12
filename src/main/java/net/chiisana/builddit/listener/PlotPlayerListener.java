package net.chiisana.builddit.listener;

import net.chiisana.builddit.controller.BuildditPlot;
import net.chiisana.builddit.controller.Plot;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
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
}
