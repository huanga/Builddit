package net.chiisana.builddit.listener;

import net.chiisana.builddit.controller.BuildditPlot;
import net.chiisana.builddit.controller.Plot;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.hanging.HangingPlaceEvent;

public class PlotHangingListener implements Listener {
	@EventHandler(priority = EventPriority.NORMAL)
	public void onHangingBreakByEntityEvent(HangingBreakByEntityEvent event) {
		// Check if player is allowed to break the hanging object
		if (event.getRemover() instanceof Player) {
			Player player = (Player) event.getRemover();
			Plot plot = BuildditPlot.getInstance().getPlotAt(event.getEntity().getLocation());
			if (plot.isAuthorizedFor(player.getName()))
			{
				return;
			} else {
				// Not Authorized
				event.setCancelled(true);
				BuildditPlot.getInstance().tellNotAuthorizedForPlot(player, plot);
				return;
			}
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onHangingPlaceEvent(HangingPlaceEvent event) {
		// Check if player is allowed to hang something
		Plot plot = BuildditPlot.getInstance().getPlotAt(event.getEntity().getLocation());
		Player player = event.getPlayer();
		if (plot.isAuthorizedFor(player.getName())) {
			return;
		} else {
			// Not Authorized
			event.setCancelled(true);
			BuildditPlot.getInstance().tellNotAuthorizedForPlot(player, plot);
		}
	}
}
