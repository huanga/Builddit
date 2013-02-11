package net.chiisana.builddit.model;

import java.util.HashSet;

public class PlotModel {
	public String owner;

	public int plotX;
	public int plotZ;

	private HashSet<String> authorized = new HashSet<String>();

	public void addAuthorized(String user) {
		this.authorized.add(user);
	}

	public void removeAuthorized(String user) {
		this.authorized.remove(user);
	}

	public boolean isAuthorized(String user) {
		return this.authorized.contains(user);
	}
}
