package top.redstonemusic;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.server.ServerCommandEvent;

public class KillListener implements Listener {
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onKill (ServerCommandEvent event) {
		String[] command = event.getCommand().split(" ");
		if (command[0].toLowerCase().equals("/kill") || command[0].toLowerCase().equals("kill")) {
			if (command.length > 1 && command[1].toLowerCase().equals("@e")) {
				event.setCancelled(true);
				event.getSender().sendMessage("You can't use /kill @e command withot a selector");
			}
		}
	}
	
	@EventHandler
	public void onKill (PlayerCommandPreprocessEvent event) {
		String[] command = event.getMessage().split(" ");
		if (command[0].toLowerCase().equals("/kill") || command[0].toLowerCase().equals("kill")) {
			if (command.length == 1)
				event.setMessage("/kill @s");
			else if (command[1].toLowerCase().equals("@e")) {
				event.setCancelled(true);
				event.getPlayer().sendMessage(ChatColor.RED + "You can't use /kill @e command withot a selector");
			}
		}
	}
	
}
