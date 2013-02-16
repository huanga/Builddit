package net.chiisana.builddit.model;

import org.bukkit.World;

import java.util.HashSet;

public class PlotModel {

	private int pid;
	private String owner = "";

	private HashSet<String> authorized = new HashSet<String>();

	private World world;
	private int plotX;
	private int plotZ;

	public int getPid() {
		return pid;
	}

	public void setPid(int pid) {
		this.pid = pid;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public boolean isOwned() {
		return (!this.owner.equals(""));
	}

	public int getPlotX() {
		return plotX;
	}

	public void setPlotX(int plotX) {
		this.plotX = plotX;
	}

	public int getPlotZ() {
		return plotZ;
	}

	public void setPlotZ(int plotZ) {
		this.plotZ = plotZ;
	}

	public void setWorld(World world) {
		this.world = world;
	}

	public World getWorld() {
		return this.world;
	}

	public void authorize(String user) {
		this.authorized.add(user);
	}

	public void unauthorize(String user) {
		this.authorized.remove(user);
	}

	public boolean isAuthorizedFor(String user) {
		return (this.authorized.contains(user) || this.owner.equals(user));
	}
}
