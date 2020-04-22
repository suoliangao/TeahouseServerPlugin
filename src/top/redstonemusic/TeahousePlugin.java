package top.redstonemusic;

import org.bukkit.command.TabExecutor;
import org.bukkit.plugin.java.JavaPlugin;

public class TeahousePlugin extends JavaPlugin {
	
	public static TeahousePlugin instance;
	
	public static TabExecutor commandCB;
	public static TabExecutor commandLandmark;
	
	@Override
	public void onEnable() {
		// TODO Auto-generated method stub
		super.onEnable();
		instance = this;
		
		commandCB = new CommandCB();
		commandLandmark = new CommandLandmark();
		
		this.getServer().getPluginManager().registerEvents(new KillListener(), this);
		this.getCommand("cb").setExecutor(commandCB);
		this.getCommand("landmark").setExecutor(commandLandmark);
		System.out.println("Teahouse server plugin loaded!!!");
	}
	
	@Override
	public void onDisable() {
		// TODO Auto-generated method stub
		super.onDisable();
		((CommandLandmark) commandLandmark).saveToFile();
	}
	
}
