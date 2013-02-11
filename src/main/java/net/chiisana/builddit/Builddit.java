package net.chiisana.builddit;

import com.sk89q.worldedit.bukkit.WorldEditAPI;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import net.chiisana.builddit.command.BuildditCommand;
import net.chiisana.builddit.generator.PlotGenerator;
import org.bukkit.event.Listener;
import org.bukkit.generator.ChunkGenerator;
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

	public ChunkGenerator getDefaultWorldGenerator(String worldname, String id) {
		return (new PlotGenerator());
	}
}

