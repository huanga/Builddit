package net.chiisana.builddit.model;

import java.util.HashSet;

public class PlotModel {

	private String owner;

	private HashSet<String> authorized = new HashSet<String>();

	private int plotX;
	private int plotZ;

	public PlotModel() {
		this.owner = "";
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public boolean isOwned() {
		return (this.owner.equals(""));
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
