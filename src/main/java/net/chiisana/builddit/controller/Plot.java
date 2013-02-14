package net.chiisana.builddit.controller;

import com.sk89q.worldedit.*;
import com.sk89q.worldedit.bukkit.WorldEditAPI;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.masks.Mask;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.regions.RegionSelector;
import net.chiisana.builddit.Builddit;
import net.chiisana.builddit.model.PlotConfiguration;
import net.chiisana.builddit.model.PlotModel;
import org.bukkit.Location;
import org.bukkit.entity.Player;

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

	public Location getBottom() {
		return(new Location(
					this.model.getWorld(),
					this.getPlotX() * PlotConfiguration.intPlotCalculatedSize,
					0,
					this.getPlotZ() * PlotConfiguration.intPlotCalculatedSize
		));
	}

	public Location getTop() {
		return(new Location(
					this.model.getWorld(),
					((this.getPlotX()+1) * PlotConfiguration.intPlotCalculatedSize) - 1,
					this.model.getWorld().getMaxHeight(),
					((this.getPlotZ()+1) * PlotConfiguration.intPlotCalculatedSize) - 1
		));
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

	public boolean unclaim(Player unclaimant) {
		if (this.getOwner().equals(unclaimant.getName()) || unclaimant.hasPermission("builddit.admin"))
		{
			this.setOwner("");
			return true;
		}
		return false;
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

	public boolean authorize(String user, Player requester) {
		if (this.getOwner().equals(requester.getName()) || requester.hasPermission("builddit.admin"))
		{
			this.model.authorize(user);
			return true;
		} else {
			return false;
		}
	}

	public boolean unauthorize(String user, Player requester) {
		if (this.getOwner().equals(requester.getName()) || requester.hasPermission("builddit.admin"))
		{
			this.model.unauthorize(user);
			return true;
		} else {
			return false;
		}
	}

	public boolean clear(Player clearer) {
		if (this.getOwner().equals(clearer.getName()) || clearer.hasPermission("builddit.admin"))
		{
			Location location1 = this.getBottom();
			Location location2 = this.getTop();

			Vector vPos1 = new Vector(location1.getX(), location1.getY(), location1.getZ());
			Vector vPos2 = new Vector(location2.getX(), location2.getY(), location2.getZ());

			WorldEditPlugin wePlugin = Builddit.getInstance().wePlugin;
			WorldEditAPI weAPI = Builddit.getInstance().weAPI;

			EditSession editSession = wePlugin.createEditSession(clearer);
			LocalSession session = weAPI.getSession(clearer);

			RegionSelector regionSelector = session.getRegionSelector(editSession.getWorld());
			regionSelector.selectPrimary(vPos1);
			regionSelector.selectSecondary(vPos2);

			try {
				// Note: Lifted code from WorldEdit as there is no API for regenerating a selection
				LocalPlayer player = wePlugin.wrapPlayer(clearer);
				Region region = regionSelector.getRegion();
				Mask mask = session.getMask();
				session.setMask(null);
				player.getWorld().regenerate(region, editSession);
				session.setMask(mask);
				return true;
			} catch (IncompleteRegionException e) {
				// This should never happen.
				return false;
			}
		}
		return false;
	}
}
