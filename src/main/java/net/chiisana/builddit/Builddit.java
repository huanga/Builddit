package net.chiisana.builddit;

import com.sk89q.worldedit.bukkit.WorldEditAPI;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import net.chiisana.builddit.command.BuildditCommand;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class Builddit extends JavaPlugin implements Listener {
	public WorldEditPlugin wePlugin;
	public WorldEditAPI weAPI;

	public static Builddit instance;

    public void onDisable() { }

    public void onEnable() {
	    instance = this;

	    wePlugin = (WorldEditPlugin)getServer().getPluginManager().getPlugin("WorldEdit");
	    weAPI = new WorldEditAPI(wePlugin);

	    getCommand("builddit").setExecutor(new BuildditCommand());
    }

	public static Builddit getInstance() {
		if (instance == null) { instance = new Builddit(); }
		return instance;
	}
}

