package net.chiisana.builddit.command;

import net.chiisana.builddit.controller.BuildditPlot;
import net.chiisana.builddit.controller.Plot;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PlotCommand implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("plot"))
		{
			if (!(sender instanceof Player))
			{
				sender.sendMessage("Builddit Plot only have in game commands.");
				return true;
			}

			Player player = (Player)sender;

			String subCmd;
			if (args.length == 0)
			{
				// No length, output help
				subCmd = "help";
			} else {
				subCmd = args[0];
			}

			if (subCmd.equalsIgnoreCase("help"))
			{
				/*
				int page;
				if (args.length < 2)
				{
					page = 1;
				} else {
					page = Integer.parseInt(args[1]);
					page = page > 0 ? page : 1;         // No page 0, not a number from user.
				}
				sender.sendMessage("Builddit Plot Commands (Page " + page + " of Y)"); // For future
				*/
				player.sendMessage("Builddit Plot Commands");
				player.sendMessage("====================================");
				player.sendMessage(" claim   - attempt to claim the plot.");
				player.sendMessage(" unclaim - unclaim the plot.");
				player.sendMessage(" clear   - clear (regenerate) the plot (warning: no undo).");
				player.sendMessage(" reset   - clear + unclaim the plot (warning: no undo).");
				player.sendMessage(" auth <name>   - authorizes <name> to work on the plot.");
				player.sendMessage(" unauth <name> - unauthorizes <name> to work on the plot.");
				return true;
			}

			Plot currentPlot = BuildditPlot.getInstance().getPlotAt(player.getLocation());
			if (subCmd.equalsIgnoreCase("claim"))
			{
				// Claiming a plot is pretty straight forward: try to claim it, and let player know result
				if (currentPlot.claim(player.getName()))
				{
					player.sendMessage("You have successfully claimed the plot.");
				} else {
					player.sendMessage("Plot is already owned by " + currentPlot.getOwner() + ".");
				}
				return true;
			}
			else if (subCmd.equalsIgnoreCase("unclaim"))
			{
				String result = this._unclaim(currentPlot, player);
				player.sendMessage(result);
				return true;
			}
			else if (subCmd.equalsIgnoreCase("clear"))
			{
				String result = this._clear(currentPlot, player);
				player.sendMessage(result);
				return true;
			}
			else if (subCmd.equalsIgnoreCase("reset"))
			{
				String result = this._clear(currentPlot, player);
				player.sendMessage(result);
				result = this._unclaim(currentPlot, player);
				player.sendMessage(result);
				return true;
			}
			else if (subCmd.equalsIgnoreCase("auth"))
			{
				if (args.length < 2)
				{
					player.sendMessage("You must specify who you are authorizing. Example usage: ");
					player.sendMessage("/plot auth huang_a  -- this authorizes huang_a to work on the plot.");
					return true;
				}

				String target = args[1];
				if (currentPlot.authorize(target, player))
				{
					player.sendMessage(target + " has been added to the authorized users list.");
					return true;
				} else {
					player.sendMessage("You do not own the plot, so you cannot modify the authorized users list.");
					return true;
				}
			}
			else if (subCmd.equalsIgnoreCase("unauth"))
			{
				if (args.length < 2)
				{
					player.sendMessage("You must specify who you are unauthorizing. Example usage: ");
					player.sendMessage("/plot unauth huang_a  -- this unauthorizes huang_a to work on the plot.");
				}

				String target = args[1];
				if (currentPlot.unauthorize(target, player))
				{
					player.sendMessage(target + " has been removed from the authorized users list.");
					return true;
				} else {
					player.sendMessage("You do not own the plot, so you cannot modify the authorized users list.");
					return true;
				}
			}
		}
		return false;
	}

	private String _unclaim(Plot plot, Player player) {
		// Unclaiming is a bit less straight forward: only allow if player owns it or is admin
		if (plot.unclaim(player))
		{
			return "You have successfully unclaimed the plot.";
		} else {
			return "You do not own the plot, so you cannot unclaim it.";
		}
	}

	private String _clear(Plot plot, Player player) {
		// Clearing the plot: only allow if player owns it or is admin
		if (plot.clear(player))
		{
			return "Plot content have been cleared.";
		} else {
			return "You do not own the plot, so you cannot clear it.";
		}
	}
}
