package net.chiisana.builddit.command;

import com.sk89q.worldedit.*;
import com.sk89q.worldedit.data.DataException;
import com.sk89q.worldedit.regions.RegionSelector;
import com.sk89q.worldedit.schematic.SchematicFormat;
import com.worldcretornica.plotme.PlotManager;
import net.chiisana.builddit.Builddit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

public class BuildditCommand implements CommandExecutor {

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("builddit")) {
			if (!(sender instanceof Player)) {
				sender.sendMessage(ChatColor.DARK_RED + "[Builddit] " + ChatColor.RESET + "Command can only be used by player in game");
				return false;
			}

			// TODO: NOT trust third party developers with their shit, and code your own plot system...
			/*
			Professional coder, hard at work:
            ~/temp/PlotMe/src/com/worldcretornica/plotme $ grep -r AutoLink *
				PlotManager.java:				/*if(pmi.AutoLinkPlots)
				PlotMapInfo.java:	public boolean AutoLinkPlots;
				PlotMe.java:			plotworld.set("AutoLinkPlots", true);
				PlotMe.java:			tempPlotInfo.AutoLinkPlots = currworld.getBoolean("AutoLinkPlots", true);
				PlotMe.java:			currworld.set("AutoLinkPlots", tempPlotInfo.AutoLinkPlots);
			*/

			/*
			// For now, just one action: getlink
			Player player = (Player)sender;

			String plotID = PlotManager.getPlotId(player);
			Location pos1 = PlotManager.getPlotTopLoc(player.getWorld(), plotID);
			Location pos2 = PlotManager.getPlotBottomLoc(player.getWorld(), plotID);

			Vector vPos1 = new Vector(pos1.getX(),pos1.getY(),pos1.getZ());
			Vector vPos2 = new Vector(pos2.getX(),pos2.getY(),pos2.getZ());

			EditSession editSession = Builddit.getInstance().wePlugin.createEditSession(player);
			LocalSession session = Builddit.getInstance().weAPI.getSession(player);
			RegionSelector regionSelector = session.getRegionSelector(editSession.getWorld());
			regionSelector.selectPrimary(vPos1);
			regionSelector.selectSecondary(vPos2);

			SchematicFormat format = SchematicFormat.getFormats().iterator().next();    // just the default format, kplsthxbye.
			String schematicFileName = player.getName() + plotID + ".schematic";
			File schematicFile = new File("/tmp/" + schematicFileName);
			try {
				format.save(session.getClipboard(), schematicFile);
			} catch (IOException e) {
				Builddit.getInstance().getLogger().log(Level.SEVERE, "IOException cannot create schematic file.");
			} catch (DataException e) {
				Builddit.getInstance().getLogger().log(Level.SEVERE, "DataException invalid schematic content.");
			} catch (EmptyClipboardException e) {
				Builddit.getInstance().getLogger().log(Level.SEVERE, "EmptyClipboardException no region selected.");
			}
			*/
			// TODO: Add information about this schematic file into database.
			// TODO: Generate the actual link and send to user.
			return true;
		}
		return false;
	}
}
