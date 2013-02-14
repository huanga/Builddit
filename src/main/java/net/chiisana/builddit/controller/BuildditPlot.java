package net.chiisana.builddit.controller;

import net.chiisana.builddit.Builddit;
import net.chiisana.builddit.command.PlotCommand;
import net.chiisana.builddit.helper.PlotHelper;
import net.chiisana.builddit.listener.PlotBlockListener;
import net.chiisana.builddit.listener.PlotEntityListener;
import net.chiisana.builddit.listener.PlotHangingListener;
import net.chiisana.builddit.listener.PlotPlayerListener;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

import java.util.HashMap;

public class BuildditPlot {
	private static BuildditPlot instance;
	private HashMap<String, Plot> plotHashMap;

	private PlotBlockListener blockListener;
	private PlotEntityListener entityListener;
	private PlotHangingListener hangingListener;
	private PlotPlayerListener playerListener;

	public static BuildditPlot getInstance() {
		if (instance == null) { instance = new BuildditPlot(); }
		return instance;
	}

	protected BuildditPlot() {
		// No clean way to unregister commands, so it will always be registered, for now.
		Builddit.getInstance().getCommand("plot").setExecutor(new PlotCommand());
	}

	public void onEnable() {
		this.blockListener = new PlotBlockListener();
		this.entityListener = new PlotEntityListener();
		this.hangingListener = new PlotHangingListener();
		this.playerListener = new PlotPlayerListener();

		Builddit.getInstance().getServer().getPluginManager().registerEvents(this.blockListener, Builddit.getInstance());
		Builddit.getInstance().getServer().getPluginManager().registerEvents(this.entityListener, Builddit.getInstance());
		Builddit.getInstance().getServer().getPluginManager().registerEvents(this.hangingListener, Builddit.getInstance());
		Builddit.getInstance().getServer().getPluginManager().registerEvents(this.playerListener, Builddit.getInstance());
	}

	public void onDisable() {
		HandlerList.unregisterAll(this.blockListener);
		HandlerList.unregisterAll(this.entityListener);
		HandlerList.unregisterAll(this.hangingListener);
		HandlerList.unregisterAll(this.playerListener);

		this.blockListener = null;
		this.entityListener = null;
		this.hangingListener = null;
		this.playerListener = null;
	}

	public Plot getPlotAt(Location location) {
		return (getPlotAt(PlotHelper.getPX(location), PlotHelper.getPZ(location)));
	}

	public Plot getPlotAt(int px, int pz) {
		return (getPlotAt(px + "." + pz));
	}

	public Plot getPlotAt(String plotID) {
		try {
			return plotHashMap.get(plotID);
		} catch (NullPointerException npe) {
			// We don't know about this plot yet, create it
			Plot plot = new Plot();
			plotHashMap.put(plotID, plot);
			return plot;
		}
	}

	public void tellNotAuthorizedForPlot(Player player, Plot plot) {
		if (plot.isOwned()) {
			player.sendMessage("[Builddit Plot] You are not authorized to work on this plot. Please contact " + plot.getOwner() + " for permission.");
		} else {
			player.sendMessage("[Builddit Plot] This plot is unclaimed. Claim it first before working on it.");
		}
	}

	public void tellNotAuthorizedForRoad(Player player) {
		player.sendMessage("[Builddit Plot] You are not authorized to edit roads or walls.");
	}

	public void tellCannotPushPiston(Player player) {
		player.sendMessage("[Builddit Plot] You cannot activate a piston that will push into another plot.");
	}
}
