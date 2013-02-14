package net.chiisana.builddit.controller;

import net.chiisana.builddit.model.PlotModel;

public class Plot {
	private PlotModel model;

	public Plot() {
		this.model = new PlotModel();
	}

	public int getPlotX() {
		return this.model.getPlotX();
	}

	public int getPlotZ() {
		return this.model.getPlotZ();
	}

	public void setPlotXZ(int plotX, int plotZ) {
		this.model.setPlotX(plotX);
		this.model.setPlotZ(plotZ);
	}

	public boolean claim(String owner) {
		if (this.isOwned())
		{
			return false;
		} else {
			this.setOwner(owner);
			return true;
		}
	}

	public boolean unclaim(String unclaimant) {
		if (this.getOwner().equals(unclaimant))
		{
			this.unclaim();
			return true;
		}
		return false;
	}

	public void unclaim() {
		this.setOwner("");
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

	public boolean authorize(String user, String requester) {
		if (this.getOwner().equals(requester))
		{
			this.model.authorize(user);
			return true;
		} else {
			return false;
		}
	}

	public void authorize(String user, boolean forced) {
		this.model.authorize(user);
	}

	public boolean unauthorize(String user, String requester) {
		if (this.getOwner().equals(requester))
		{
			this.model.unauthorize(user);
			return true;
		} else {
			return false;
		}
	}

	public void unauthorize(String user, boolean forced) {
		this.model.unauthorize(user);
	}


	public boolean clear(String clearer) {
		if (this.getOwner().equals(clearer))
		{
			this.clear();
			return true;
		}
		return false;
	}

	public void clear() {
		// TODO: Do something to wipe the contents.
	}
}
