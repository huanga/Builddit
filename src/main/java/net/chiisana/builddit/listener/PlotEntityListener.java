package net.chiisana.builddit.listener;

import net.chiisana.builddit.controller.BuildditPlot;
import net.chiisana.builddit.controller.Plot;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;

public class PlotEntityListener implements Listener {
	@EventHandler(priority = EventPriority.NORMAL)
	public void onEntityCombustByEntityEvent(EntityCombustByEntityEvent event) {
		// Check if someone is setting another person's animal on fire
		if (event.getCombuster() instanceof Player)
		{
			Player player = (Player)event.getCombuster();
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
	public void onEntityDamageByEntityEvent(EntityDamageByEntityEvent event) {
		// Check if someone is hurting another person's animal
		if (event.getDamager() instanceof Player)
		{
			Player player = (Player)event.getDamager();
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
	public void onEntityTameEvent(EntityTameEvent event) {
		// Check if someone is taming another person's animal
		if (event.getOwner() instanceof Player)
		{
			Player player = (Player) event.getOwner();
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
	public void onEntityTargetEvent(EntityTargetEvent event) {
		// Pretty much disable aggro and targeting for unauthorized people so they don't
		// lure skele to shoot at creeper, and make a mess etc.
		if (event.getTarget() instanceof Player)
		{
			Player player = (Player) event.getTarget();
			Plot plot = BuildditPlot.getInstance().getPlotAt(event.getEntity().getLocation());

			if (plot.isAuthorizedFor(player.getName())) {
				return;
			} else {
				// Not Authorized
				event.setCancelled(true);
				// No need to notify for this.
				return;
			}
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPotionSplashEvent(PotionSplashEvent event) {
		// Check if someone is throwing potions (or using dispensers to throw potions) at
		// another person's plot

		Plot plotFrom = BuildditPlot.getInstance().getPlotAt(event.getEntity().getLocation());
		Plot plotTo;

		Player player = null;
		if (event.getEntity() instanceof Player)
		{
			player = (Player) event.getEntity();
		}

		for (LivingEntity target : event.getAffectedEntities())
		{
			plotTo = BuildditPlot.getInstance().getPlotAt(target.getLocation());
			if (plotFrom != plotTo)
			{
				// Cross plot throwing is not allowed (i.e.: thrown from a dispenser in another plot or
				// standing on another plot to throw in, mainly dispenser, since next scope will catch
				// players throwing when they're not supposed to, even if on same plot)
				event.setCancelled(true);
				return;
			}

			if ((player != null) && (!plotTo.isAuthorizedFor(player.getName())))
			{
				// Potion is thrown by a player, who is not authorized
				event.setCancelled(true);
				return;
			}
		}


	}
}
