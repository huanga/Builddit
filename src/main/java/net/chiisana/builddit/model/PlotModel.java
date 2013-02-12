package net.chiisana.builddit.model;

import java.util.HashSet;

public class PlotModel {

	private String owner;

	private HashSet<String> authorized = new HashSet<String>();

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
