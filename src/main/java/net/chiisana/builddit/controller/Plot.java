package net.chiisana.builddit.controller;

import net.chiisana.builddit.model.PlotModel;

public class Plot {
	private PlotModel model;

	public Plot() {
		this.model = new PlotModel();
	}

	public String getOwner() {
		return this.model.getOwner();
	}

	public void setOwner(String owner) {
		this.model.setOwner(owner);
	}

	public boolean isOwned() {
		return this.model.isOwned();
	}

	public boolean isAuthorizedFor(String user) {
		return this.model.isAuthorizedFor(user);
	}

	public void authorize(String user) {
		this.model.authorize(user);
	}

	public void unauthorize(String user) {
		this.model.unauthorize(user);
	}
}
