package net.chiisana.builddit.controller;

import com.sk89q.worldedit.*;
import com.sk89q.worldedit.bukkit.WorldEditAPI;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.data.DataException;
import com.sk89q.worldedit.masks.Mask;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.regions.RegionSelector;
import com.sk89q.worldedit.schematic.SchematicFormat;
import net.chiisana.builddit.Builddit;
import net.chiisana.builddit.helper.WorldEditHelper;
import net.chiisana.builddit.model.Direction;
import net.chiisana.builddit.model.PlotConfiguration;
import net.chiisana.builddit.model.PlotModel;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.logging.Level;

public class Plot {
	private PlotModel model;

	private Plot plotWest;
	private Plot plotEast;
	private Plot plotNorth;
	private Plot plotSouth;

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

	public void setWorld(World world) {
		this.model.setWorld(world);
	}

	public World getWorld() {
		return this.model.getWorld();
	}

	public Location getSelectionBottom() {
		// Note: Returned location may NOT be actually in the plot, be careful and use masks on WE!
		Plot plotLowestPX = this;
		Plot plotLowestPZ = this;
		int lowestPX = this.getPlotX();
		int lowestPZ = this.getPlotZ();
		for (Plot plot : this.getConnectedPlots())
		{
			if (plot.getPlotX() < lowestPX)
			{
				plotLowestPX = plot;
			}

			if (plot.getPlotZ() < lowestPZ)
			{
				plotLowestPZ = plot;
			}
		}
		return (new Location(
				this.getWorld(),
				plotLowestPX.getBottom().getX(),
				0,
				plotLowestPZ.getBottom().getZ()
		));
	}

	public Location getSelectionTop() {
		// Note: Returned location may NOT be actually in the plot, be careful and use masks on WE!
		Plot plotHighestPX = this;
		Plot plotHighestPZ = this;
		int highestPX = this.getPlotX();
		int highestPZ = this.getPlotZ();
		for (Plot plot : this.getConnectedPlots())
		{
			if (plot.getPlotX() > highestPX)
			{
				plotHighestPX = plot;
			}

			if (plot.getPlotZ() > highestPZ)
			{
				plotHighestPZ = plot;
			}
		}
		return (new Location(
				this.getWorld(),
				plotHighestPX.getTop().getX(),
				0,
				plotHighestPZ.getTop().getZ()
		));
	}

	public Location getBottom() {
		return(new Location(
					this.getWorld(),
					this.getPlotX() * PlotConfiguration.intPlotCalculatedSize,
					0,
					this.getPlotZ() * PlotConfiguration.intPlotCalculatedSize
		));
	}

	public Location getTop() {
		return (new Location(
					this.getWorld(),
					((this.getPlotX()+1) * PlotConfiguration.intPlotCalculatedSize) - 1,
					this.getWorld().getMaxHeight(),
					((this.getPlotZ()+1) * PlotConfiguration.intPlotCalculatedSize) - 1
		));
	}

	public int claim(Player claimant) {
		if (!this.isOwned() || claimant.hasPermission("builddit.admin"))
		{
			Boolean dbsuccess = true;
			if (!this.isOwned())
			{
				// No owner, INSERT into database table;
				String querySavePlot = "INSERT INTO builddit_plot " +
						"SET " +
						"   world = \"" + this.getWorld().getName() + "\", " +
						"   plotx = " + this.getPlotX() + ", " +
						"   plotz = " + this.getPlotZ() + ", " +
						"   owner = \"" + claimant.getName() + "\" " +
						"ON DUPLICATE KEY UPDATE owner=\"" + claimant.getName() + "\";";
				if (Builddit.getInstance().database.runUpdateQuery(querySavePlot) == -1)
				{
					dbsuccess = false;
				}

				// Fetch builddit_plot.id (pid) for model
				String queryPID = "SELECT id FROM builddit_plot " +
						"WHERE " +
						"   world = \"" + this.getWorld().getName() + "\" " +
						"   AND plotx = " + this.getPlotX() + " " +
						"   AND plotz = " + this.getPlotZ() + " " +
						"   AND owner = \"" + claimant.getName() + "\" " +
						"LIMIT 1;";
				try {
					ResultSet rs = Builddit.getInstance().database.runSelectQuery(queryPID);
					while (rs.next()) {
						int pid = rs.getInt("id");
						this.model.setPid(pid);
					}
				} catch (SQLException e) {
					// Unable to get plot ID, utoh, database down?
					dbsuccess = false;
				} catch (NullPointerException e) {
					// No result from database, bad transaction, not safe to continue
					dbsuccess = false;
				}
			} else {
				// Previously owned, admin override, UPDATE record from database table;
				String querySavePlot = "UPDATE builddit_plot " +
						"SET " +
						"   world = \"" + this.getWorld().getName() + "\", " +
						"   plotx = " + this.getPlotX() + ", " +
						"   plotz = " + this.getPlotZ() + ", " +
						"   owner = \"" + claimant.getName() + "\" " +
						"WHERE " +
						"   id = " + this.model.getPid();
				if (Builddit.getInstance().database.runUpdateQuery(querySavePlot) == -1)
				{
					dbsuccess = false;
				}
			}
			if (dbsuccess) {
				this.setOwner(claimant.getName());
				this.updateNeighbours();

				if (this.isNeighbourNorth())
				{
					// Remove road on North part of plot
					this.removeRoadNorth();
				}
				if (this.isNeighbourSouth())
				{
					// Remove road on North part of South plot
					this.getPlotSouth().removeRoadNorth();
				}
				if (this.isNeighbourEast())
				{
					// Remove road on East part of plot
					this.removeRoadEast();
				}
				if (this.isNeighbourWest())
				{
					// Remove road on East part of West plot
					this.getPlotWest().removeRoadEast();
				}
				return 1;
			} else {
				return -1;
			}
		}
		return 0;
	}

	public int unclaim(Player unclaimant) {
		if (this.getOwner().equals(unclaimant.getName()) || unclaimant.hasPermission("builddit.admin"))
		{
			// DELETE from database table
			String querySavePlot = "DELETE FROM builddit_plot " +
					"WHERE " +
					"   id = " + this.model.getPid();
			if (Builddit.getInstance().database.runUpdateQuery(querySavePlot) == -1)
			{
				return -1;
			}
			this.setOwner("");
			this.unauthorizeAll();
			if (this.isNeighbourNorth())
			{
				// Remove road on North part of plot
				this.placeRoadNorth(unclaimant);
			}
			if (this.isNeighbourSouth())
			{
				// Remove road on North part of South plot
				this.getPlotSouth().placeRoadNorth(unclaimant);
			}
			if (this.isNeighbourEast())
			{
				// Remove road on East part of plot
				this.placeRoadEast(unclaimant);
			}
			if (this.isNeighbourWest())
			{
				// Remove road on East part of West plot
				this.getPlotWest().placeRoadEast(unclaimant);
			}
			this.updateNeighbours();
			return 1;
		}
		return 0;
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
			this.authorize(user);
			return true;
		} else {
			return false;
		}
	}

	private void authorize(String user) {
		String queryAddAuth = "" +
				"INSERT INTO builddit_authorization " +
				"SET " +
				"   pid = " + this.model.getPid() + ", " +
				"   player = \"" + user + "\" " +
				"ON DUPLICATE KEY UPDATE pid=pid";
		Builddit.getInstance().database.runUpdateQuery(queryAddAuth);
		this.model.authorize(user);
	}

	public boolean unauthorize(String user, Player requester) {
		if (this.getOwner().equals(requester.getName()) || requester.hasPermission("builddit.admin"))
		{
			this.unauthorize(user);
			return true;
		} else {
			return false;
		}
	}

	private void unauthorize(String user) {
		String queryDeleteAuth = "" +
				"DELETE FROM builddit_authorization " +
				"WHERE " +
				"   pid = " + this.model.getPid() + "" +
				"   AND player = \"" + user + "\";";
		Builddit.getInstance().database.runUpdateQuery(queryDeleteAuth);
		this.model.unauthorize(user);
	}

	public String getSchematicLink(Player requester) {
		WorldEditPlugin wePlugin = Builddit.getInstance().wePlugin;
		WorldEditAPI weAPI = Builddit.getInstance().weAPI;

		LocalPlayer player = wePlugin.wrapPlayer(requester);

		Location location1 = this.getSelectionBottom();
		Location location2 = this.getSelectionTop();

		Vector vPos1 = new Vector(location1.getX(), location1.getY(), location1.getZ());
		Vector vPos2 = new Vector(location2.getX(), location2.getY(), location2.getZ());

		LocalSession session = weAPI.getSession(requester);
		EditSession editSession = session.createEditSession(player);
		RegionSelector regionSelector = session.getRegionSelector(editSession.getWorld());

		regionSelector.selectPrimary(vPos1);
		regionSelector.explainPrimarySelection(player, session, vPos1);
		regionSelector.selectSecondary(vPos2);
		regionSelector.explainSecondarySelection(player, session, vPos2);

		// Don't give them stuff from neighbour plots
		WorldEditHelper.setMask(requester, this);

		String schematicFileName = this.getOwner() + "." + this.getWorld() + "." + this.getPlotX() + "." + this.getPlotZ() + ".schematic";
		File schematicFile = new File("/tmp/" + schematicFileName);

		try {
			SchematicFormat format = SchematicFormat.getFormats().iterator().next();
			format.save(session.getClipboard(), schematicFile);
		} catch (IOException e) {
			Builddit.getInstance().getLogger().log(Level.SEVERE, "IOException cannot create schematic file.");
			return "Failed to write schematic file. Please contact server administrator.";
		} catch (DataException e) {
			Builddit.getInstance().getLogger().log(Level.SEVERE, "DataException invalid schematic content.");
			return "Invalid schematic content. Please contact server administrator.";
		} catch (EmptyClipboardException e) {
			Builddit.getInstance().getLogger().log(Level.SEVERE, "EmptyClipboardException no region selected.");
			return "You should never see this, blame developer.";
		}

		// TODO: Add information about this schematic file into database.
		// TODO: Generate the actual link and send to user.


		return "";
	}

	public boolean clear(Player clearer) {
		if (this.getOwner().equals(clearer.getName()) || clearer.hasPermission("builddit.admin"))
		{
			Location location1 = this.getBottom();
			Location location2 = this.getTop();

			this._regenerateArea(clearer, (int)location1.getX(), (int)location2.getX(), (int)location1.getZ(), (int)location2.getZ());
			return true;
		}
		return false;
	}

	public HashSet<Plot> getConnectedPlots() {
		HashSet<Plot> connectedPlots = new HashSet<Plot>();

		if (this.isOwned())
		{
			connectedPlots.addAll(getConnectedPlots(this, connectedPlots));
		}

		return connectedPlots;
	}

	private HashSet<Plot> getConnectedPlots(Plot rootPlot, HashSet<Plot> currentSet) {
		// Almost "flood fill" like algorithm to find connected plots

		// Note: String comparison is lighter than set.contains lookup for a large set, so we check owner first.
		if (!this.getOwner().equals(rootPlot.getOwner()))
		{
			// This plot is not the same owner as the one we are checking for, we don't need to go further anymore.
			return currentSet;
		}

		// If the set already know about this node, we don't need to scan again.
		if (currentSet.contains(this)) {
			return currentSet;
		}

		// This plot is the same owner as the one we are checking for, and not already in set, add itself to the set
		currentSet.add(this);

		// Look at our west, east, north, and south neighbour...
		Plot west = BuildditPlot.getInstance().getPlotAt(this.getWorld(), this.getPlotX()-1, this.getPlotZ());
		Plot east = BuildditPlot.getInstance().getPlotAt(this.getWorld(), this.getPlotX()+1, this.getPlotZ());
		Plot north = BuildditPlot.getInstance().getPlotAt(this.getWorld(), this.getPlotX(), this.getPlotZ()+1);
		Plot south = BuildditPlot.getInstance().getPlotAt(this.getWorld(), this.getPlotX(), this.getPlotZ()-1);

		// ...and add their connected plots recursively as needed.
		currentSet.addAll(west.getConnectedPlots(rootPlot,currentSet));
		currentSet.addAll(east.getConnectedPlots(rootPlot,currentSet));
		currentSet.addAll(north.getConnectedPlots(rootPlot,currentSet));
		currentSet.addAll(south.getConnectedPlots(rootPlot,currentSet));

		return currentSet;
	}

	public String toString() {
		return "BuildditPlot{world=" + this.getWorld().getName() + ";plotX=" + this.getPlotX() + ";plotZ=" + this.getPlotZ() + ";owner='" + this.getOwner() + "'}";
	}

	public int load() {
		// Attempt to load this Plot from MySQL
		try {
			String queryPlotInfo = "SELECT id, owner FROM builddit_plot " +
					"WHERE " +
					"   world = \"" + this.getWorld().getName() + "\"" +
					"   AND plotx = " + this.getPlotX() + " " +
					"   AND plotz = " + this.getPlotZ() + " " +
					"LIMIT 1;";
			ResultSet rs = Builddit.getInstance().database.runSelectQuery(queryPlotInfo);
			while (rs.next())
			{
				this.model.setPid(rs.getInt("id"));
				this.setOwner(rs.getString("owner"));
			}
		} catch (SQLException e) {
			// Unable to access database, database server down?
			return -1;
		} catch (NullPointerException e) {
			// Plot not in database, this was not previously claimed.
		}
		try {
			String queryGetAuth = "SELECT player FROM builddit_authorization " +
					"WHERE " +
					"   pid = " + this.model.getPid() + ";";
			ResultSet rs = Builddit.getInstance().database.runSelectQuery(queryGetAuth);
			while (rs.next())
			{
				this.authorize(rs.getString("player"));
			}
		} catch (SQLException e) {
			// Unable to access database, database server down?
			return -1;
		} catch (NullPointerException e) {
			// Permission not in database; this is fine, new plots and owner-only plots will not have permissions
		}

		if (this.isOwned())
		{
			Builddit.getInstance().getLogger().log(Level.INFO, this.toString());
			// On loading, only update neighbours if it is owned, so we don't check into infinity.
			this.updateNeighbours();
		}
		return 1;
	}

	public HashSet<String> getAuthorized() {
		return this.model.getAuthorized();
	}

	public void copyAuthFrom(Plot plot) {
		for (String authorized : plot.getAuthorized())
		{
			this.authorize(authorized);
		}
	}

	public void unauthorizeAll() {
		String queryDeleteAuth = "" +
				"DELETE FROM builddit_authorization " +
				"WHERE " +
				"   pid = " + this.model.getPid() + ";";
		Builddit.getInstance().database.runUpdateQuery(queryDeleteAuth);

		for (String authorized : (HashSet<String>)this.model.getAuthorized().clone())
		{
			this.model.unauthorize(authorized);
		}
	}

	public Plot getPlotWest() {
		if (this.plotWest != null)
		{
			return this.plotWest;
		}
		this.plotWest = BuildditPlot.getInstance().getPlotAt(this.getWorld(), this.getPlotX() - 1, this.getPlotZ());
		return this.plotWest;
	}

	public Plot getPlotEast() {
		if (this.plotEast != null)
		{
			return this.plotEast;
		}
		this.plotEast = BuildditPlot.getInstance().getPlotAt(this.getWorld(), this.getPlotX() + 1, this.getPlotZ());
		return this.plotEast;
	}

	public Plot getPlotNorth() {
		if (this.plotNorth != null)
		{
			return this.plotNorth;
		}
		this.plotNorth = BuildditPlot.getInstance().getPlotAt(this.getWorld(), this.getPlotX(), this.getPlotZ() + 1);
		return this.plotNorth;
	}

	public Plot getPlotSouth() {
		if (this.plotSouth != null)
		{
			return this.plotSouth;
		}
		this.plotSouth = BuildditPlot.getInstance().getPlotAt(this.getWorld(), this.getPlotX(), this.getPlotZ() - 1);
		return this.plotSouth;
	}

	public boolean isNeighbourWest() {
		return this.model.isNeighbourWest();
	}

	public boolean isNeighbourEast() {
		return this.model.isNeighbourEast();
	}

	public boolean isNeighbourNorth() {
		return this.model.isNeighbourNorth();
	}

	public boolean isNeighbourSouth() {
		return this.model.isNeighbourSouth();
	}

	public void setNeighbourWest(boolean neighbourWest) {
		this.model.setNeighbourWest(neighbourWest);
	}

	public void setNeighbourEast(boolean neighbourEast) {
		this.model.setNeighbourEast(neighbourEast);
	}

	public void setNeighbourNorth(boolean neighbourNorth) {
		this.model.setNeighbourNorth(neighbourNorth);
	}

	public void setNeighbourSouth(boolean neighbourSouth) {
		this.model.setNeighbourSouth(neighbourSouth);
	}

	private void updateNeighbours() {
		this.setNeighbourWest(this.getPlotWest().getOwner().equals(this.getOwner()));
		this.getPlotWest().updateNeighbour(Direction.EAST); // Tell the neighbour to update itself.
		this.setNeighbourEast(this.getPlotEast().getOwner().equals(this.getOwner()));
		this.getPlotEast().updateNeighbour(Direction.WEST);
		this.setNeighbourNorth(this.getPlotNorth().getOwner().equals(this.getOwner()));
		this.getPlotNorth().updateNeighbour(Direction.SOUTH);
		this.setNeighbourSouth(this.getPlotSouth().getOwner().equals(this.getOwner()));
		this.getPlotSouth().updateNeighbour(Direction.NORTH);
	}

	public void updateNeighbour(Direction direction) {
		switch(direction) {
			case WEST:
				this.setNeighbourWest(this.getPlotWest().getOwner().equals(this.getOwner()));
				break;
			case EAST:
				this.setNeighbourEast(this.getPlotEast().getOwner().equals(this.getOwner()));
				break;
			case NORTH:
				this.setNeighbourNorth(this.getPlotNorth().getOwner().equals(this.getOwner()));
				break;
			case SOUTH:
				this.setNeighbourSouth(this.getPlotSouth().getOwner().equals(this.getOwner()));
				break;
			default:
				Builddit.getInstance().getLogger().log(Level.INFO, "You should never see this. Something went wrong, blame developer.");
		}
	}

	private void _removeRoadAt(int x, int y, int z)
	{
		Location cursor = new Location(this.getWorld(), x, y, z);

		if (y == PlotConfiguration.intPlotHeight)
		{
			// Road/Surface layer
			if (cursor.getBlock().getType().equals(PlotConfiguration.materialRoadA))
			{
				cursor.getBlock().setType(PlotConfiguration.materialPlotSurface);
			}
		}
		if (y > PlotConfiguration.intPlotHeight)
		{
			// Wall layer
			if (cursor.getBlock().getType().equals(PlotConfiguration.materialWall))
			{
				cursor.getBlock().setType(Material.AIR);
			}
		}
	}

	public void removeRoadEast()
	{
		// Removes road on east side of this plot
		// pX = 0,0 => x = 32 ~ 38, z = 0 ~ 31
		// pX = 1,0 => x = 71 ~ 77, z = 0 ~ 31
		// pX = 0,1 => x = 32 ~ 38, z = 39 ~ 71;
		// xStart = 7PX + 32*(PX+1)
		// xEnd   = Xstart + 7
		// zStart = PZ*(39)
		// zEnd   = PZ*(39) + 32

		int xStart = ((PlotConfiguration.intRoadWidth + 2) * this.getPlotX()) + (PlotConfiguration.intPlotSize * (this.getPlotX()+1));
		int xEnd   = xStart + PlotConfiguration.intRoadWidth + 2;
		int zStart = this.getPlotZ() * PlotConfiguration.intPlotCalculatedSize;
		int zEnd   = this.getPlotZ() * PlotConfiguration.intPlotCalculatedSize + PlotConfiguration.intPlotSize;

		for (int x = xStart; x < xEnd; x++)
		{
			for (int z = zStart; z < zEnd; z++)
			{
				for (int y = PlotConfiguration.intPlotHeight; y < PlotConfiguration.intPlotHeight + 2; y++)
				{
					this._removeRoadAt(x, y, z);
				}
			}
		}

		this.model.setEastRoadRemoved(true);
		if (this.model.isNorthRoadRemoved())
		{
			// If other road was removed, we'd also need to remove center
			this.removeRoadCenter();
		}
	}

	public void removeRoadNorth()
	{
		// Removes road on North side of this plot
		// Same as removeRoadEast, except swapped X Z
		int zStart = ((PlotConfiguration.intRoadWidth + 2) * this.getPlotZ()) + (PlotConfiguration.intPlotSize * (this.getPlotZ()+1));
		int zEnd   = zStart + PlotConfiguration.intRoadWidth + 2;
		int xStart = this.getPlotX() * PlotConfiguration.intPlotCalculatedSize;
		int xEnd   = this.getPlotX() * PlotConfiguration.intPlotCalculatedSize + PlotConfiguration.intPlotSize;

		for (int x = xStart; x < xEnd; x++)
		{
			for (int z = zStart; z < zEnd; z++)
			{
				for (int y = PlotConfiguration.intPlotHeight; y < PlotConfiguration.intPlotHeight + 2; y++)
				{
					this._removeRoadAt(x, y, z);
				}
			}
		}

		this.model.setNorthRoadRemoved(true);
		if (this.model.isEastRoadRemoved())
		{
			// If other road was removed, we'd also need to remove center
			this.removeRoadCenter();
		}
	}

	public void removeRoadCenter()
	{
		// Remove the square from the center of road
		int xStart = this.getPlotX() * PlotConfiguration.intPlotCalculatedSize;
		int xEnd   = this.getPlotX() * PlotConfiguration.intPlotCalculatedSize + PlotConfiguration.intPlotSize;
		int zStart = this.getPlotZ() * PlotConfiguration.intPlotCalculatedSize;
		int zEnd   = this.getPlotZ() * PlotConfiguration.intPlotCalculatedSize + PlotConfiguration.intPlotSize;

		for (int x = xStart; x < xEnd; x++)
		{
			for (int z = zStart; z < zEnd; z++)
			{
				for (int y = PlotConfiguration.intPlotHeight; y < PlotConfiguration.intPlotHeight + 2; y++)
				{
					this._removeRoadAt(x, y, z);
				}
			}
		}
	}

	private void _regenerateArea(Player requester, int xStart, int xEnd, int zStart, int zEnd)
	{
		WorldEditPlugin wePlugin = Builddit.getInstance().wePlugin;
		WorldEditAPI weAPI = Builddit.getInstance().weAPI;

		LocalPlayer player = wePlugin.wrapPlayer(requester);

		Location location1 = new Location(this.getWorld(), xStart, 0, zStart);
		Location location2 = new Location(this.getWorld(), xEnd, 256, zEnd);

		Vector vPos1 = new Vector(location1.getX(), location1.getY(), location1.getZ());
		Vector vPos2 = new Vector(location2.getX(), location2.getY(), location2.getZ());

		LocalSession session = weAPI.getSession(requester);
		EditSession editSession = session.createEditSession(player);
		RegionSelector regionSelector = session.getRegionSelector(editSession.getWorld());

		regionSelector.selectPrimary(vPos1);
		regionSelector.explainPrimarySelection(player, session, vPos1);
		regionSelector.selectSecondary(vPos2);
		regionSelector.explainSecondarySelection(player, session, vPos2);

		try {
			// Note: Lifted code from WorldEdit as there is no API for regenerating a selection
			Region region = session.getSelection(player.getWorld());
			Mask mask = session.getMask();
			session.setMask(null);
			player.getWorld().regenerate(region, editSession);
			session.setMask(mask);
		} catch (IncompleteRegionException e) {
			// This should never happen as we've set vPos1 and vPos2 automatically in code.
		}
	}

	public void placeRoadEast(Player player)
	{
		int xStart = ((PlotConfiguration.intRoadWidth + 2) * this.getPlotX()) + (PlotConfiguration.intPlotSize * (this.getPlotX()+1));
		int xEnd   = xStart + PlotConfiguration.intRoadWidth + 2;
		int zStart = this.getPlotZ() * PlotConfiguration.intPlotCalculatedSize;
		int zEnd   = this.getPlotZ() * PlotConfiguration.intPlotCalculatedSize + PlotConfiguration.intPlotSize;

		this._regenerateArea(player, xStart, xEnd, zStart, zEnd);
		this.model.setEastRoadRemoved(false);
		if (this.model.isNorthRoadRemoved())
		{
			// If the other road was removed, we need to regenerate center
			this.placeRoadCenter(player);
		}
	}

	public void placeRoadNorth(Player player)
	{
		int zStart = ((PlotConfiguration.intRoadWidth + 2) * this.getPlotZ()) + (PlotConfiguration.intPlotSize * (this.getPlotZ()+1));
		int zEnd   = zStart + PlotConfiguration.intRoadWidth + 2;
		int xStart = this.getPlotX() * PlotConfiguration.intPlotCalculatedSize;
		int xEnd   = this.getPlotX() * PlotConfiguration.intPlotCalculatedSize + PlotConfiguration.intPlotSize;

		this._regenerateArea(player, xStart, xEnd, zStart, zEnd);
		this.model.setNorthRoadRemoved(false);
		if (this.model.isEastRoadRemoved())
		{
			// If the other road was removed, we need to regenerate center
			this.placeRoadCenter(player);
		}
	}

	public void placeRoadCenter(Player player)
	{
		// Regenerate the center square of the road
		int xStart = this.getPlotX() * PlotConfiguration.intPlotCalculatedSize;
		int xEnd   = this.getPlotX() * PlotConfiguration.intPlotCalculatedSize + PlotConfiguration.intPlotSize;
		int zStart = this.getPlotZ() * PlotConfiguration.intPlotCalculatedSize;
		int zEnd   = this.getPlotZ() * PlotConfiguration.intPlotCalculatedSize + PlotConfiguration.intPlotSize;

		this._regenerateArea(player, xStart, xEnd, zStart, zEnd);
	}
}
