package top.redstonemusic;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonWriter;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class CommandLandmark implements TabExecutor {
	
	public boolean loaded = false;
	public File dataFile;
	Map<String, Landmark> landmarks;
	
	public CommandLandmark() {
		// TODO Auto-generated constructor stub
		dataFile = new File (TeahousePlugin.instance.getDataFolder(), "landmarks.json");
		loadFile();
	}
	
	public boolean loadFile () {
		broadcastMsg("[Landmark] Loading data", ChatColor.YELLOW);
		if (landmarks == null)
			landmarks = new HashMap <String, Landmark> ();
		else
			landmarks.clear();
		//load
		if (!dataFile.exists())
			createFile();
		try {
			JsonArray arr = (JsonArray) new JsonParser().parse(new FileReader(dataFile));
			for (JsonElement e : arr) {
				JsonObject o = (JsonObject) e;
				Landmark lm = new Landmark(o.get("name").getAsString(), o.get("author").getAsString(), o.get("description").getAsString(), o.get("x").getAsDouble(), o.get("y").getAsDouble(), o.get("z").getAsDouble(), o.get("time").getAsLong());
				landmarks.put(o.get("name").getAsString(), lm);
			}
			loaded = true;
			broadcastMsg("[Landmark] Landmark data loaded, command enabled!", ChatColor.AQUA);
			return true;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			loaded = false;
			System.err.println("Fail to load landmark data file, landmark command disabled");
			e.printStackTrace();
			broadcastMsg("Fail to load data, \"/landmark\" command is not disabled, see server log for detail.", ChatColor.RED);
			return false;
		}
	}
	
	public boolean saveToFile () {
		boolean flag = loaded;
		loaded = false;
		broadcastMsg("[Landmark] Saving data", ChatColor.YELLOW);
		try {
			JsonWriter writer = new JsonWriter(new FileWriter(dataFile));
			writer.beginArray();
			for (Landmark lm : landmarks.values()) {
				lm.save(writer);
			}
			writer.endArray();
			writer.flush();
			writer.close();
			loaded = flag;
			return true;
		} catch (Exception e) {
			System.err.println("Fail to save data");
			e.printStackTrace();
			broadcastMsg("Fail to save data, see server log for detail", ChatColor.RED);
			loaded = flag;
			return false;
		}
	}
	
	public void createFile () {
		try {
			if (!dataFile.exists()) {
				if (!dataFile.getParentFile().exists())
					dataFile.getParentFile().mkdirs();
				dataFile.createNewFile();
			}
		} catch (Exception e) {
			System.err.println("Fail to create landmark data file, command will be disabled");
			e.printStackTrace();
		}
	}
	
	
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		// TODO Auto-generated method stub
		if (sender instanceof Player) {
			Player player = (Player) sender;
			if (args.length == 0) {
//				BaseComponent[] msg = {new TextComponent ("[book]"), new TextComponent (", "), new TextComponent ("[list]")};
//				BaseComponent[] hoverBook = {new TextComponent ("Get a landmark book.")};
//				BaseComponent[] hoverList = {new TextComponent ("Show landmark list in chat window.")};
				err (player, "/landmark action ...");
				return false;
			} else {
				//create
				if (args[0].equals("create")) return create (player, args);
				//list
				else if (args[0].equals("list")) return list (player);
				//book
				else if (args[0].equals("book")) return book (player);
				//go, tp, teleport
				else if (args[0].equals("go") || args[0].equals("tp") || args[0].equals("teleport")) return tp (player, args);
				//delet, del, remove, rm
				else if (args[0].equals("delet") || args[0].equals("del") || args[0].equals("remove") || args[0].equals("rm")) return del (player, args);
				//save
				else if (args[0].equals("-save")) return saveToFile ();
				//reload
				else if (args[0].equals("-reload")) return loadFile ();
			}
			return true;
		}
		return false;
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		// TODO Auto-generated method stub
		if (sender instanceof Player ) {
			Player player = (Player) sender;
			
			if (args.length == 1) {
				if (args[0].startsWith("-"))
					return Arrays.<String>asList(new String[] {"-save", "-reload"});
				return Arrays.<String>asList(new String[] {"list", "create", "remove", "go"});
			}
			if (args.length > 1) {
				//create
				if (args[0].equals("create")) {
					if (args.length == 2)
						return Arrays.<String>asList(new String[] {"[name]"});
					else if (args.length == 3)
						return Arrays.<String>asList(new String[] {"~", String.format("%.2f", player.getLocation().getX())});
					else if (args.length == 4)
						return Arrays.<String>asList(new String[] {"~", String.format("%.2f", player.getLocation().getY())});
					else if (args.length == 5)
						return Arrays.<String>asList(new String[] {"~", String.format("%.2f", player.getLocation().getZ())});
				}
				//remove, go
				else if (args[0].equals("go") || args[0].equals("tp") || args[0].equals("teleport") || args[0].equals("delet") || args[0].equals("del") || args[0].equals("remove") || args[0].equals("rm")) {
					if (args.length == 2)
						return new ArrayList<>(landmarks.keySet());
				}
			}
		}
		return new ArrayList<>();
	}
	
	private boolean create (Player player, String[] args) {
		if (args.length < 2) {
			err (player, "/landmark create [name] <x> <y> <z> description...");
			return false;
		}
		if (landmarks.containsKey(args[1])) {
			err (player, "Name Duplicated");
			return false;
		}
		if (args.length == 2) {
			landmarks.put(args[1], new Landmark(args[1], player.getName(), "", player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getZ()));
			return true;
		} else if (2 < args.length && args.length < 5) {
			err (player, "/landmark create [name] <x> <y> <z> description...");
			return false;
		} else if (args.length == 5) {
			try {
				landmarks.put(args[1], new Landmark(args[1], player.getName(), "", 
						args[2].equals("~") ? player.getLocation().getX() : Double.parseDouble(args[2]), 
						args[3].equals("~") ? player.getLocation().getY() : Double.parseDouble(args[3]), 
						args[4].equals("~") ? player.getLocation().getZ() : Double.parseDouble(args[4])));
				return true;
			} catch (Exception e) {
				err (player, "/landmark create [name] <x> <y> <z> description...");
				return false;
			}
		} else if (args.length > 5) {
			StringBuilder sb = new StringBuilder(args[5]);
			if (args.length > 6) for (String str : Arrays.copyOfRange(args, 6, args.length)) {
				sb.append(" " + str);
			}
			landmarks.put(args[1], new Landmark(args[1], player.getName(), sb.toString(), player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getZ()));
			return true;
		}
		err (player, "/landmark create [name] <x> <y> <z> description...");
		return false;
	}
	
	private boolean del (Player player, String[] args) {
		if (args.length == 2) {
			landmarks.remove(args[1]);
			return true;
		}
		err (player, String.format("/landmark %s [name]", args[0]));
		return false;
	}
	
	private boolean list (Player player) {
		Iterator<Landmark> I = landmarks.values().iterator();
		List<BaseComponent> components = new ArrayList<>();
		while (I.hasNext()) {
			components.add(((Landmark) I.next()).displayText());
			if (I.hasNext())
				components.add(new TextComponent(" "));
		}
		BaseComponent[] cs = new BaseComponent[components.size()];
		components.toArray(cs);
		player.spigot().sendMessage(cs);
		return true;
	}
	
	private boolean book (Player player) {
		err (player, "Not implemented");
		return false;
	}
	
	private boolean tp (Player player, String[] args) {
		if (args.length == 2) {
			Landmark lm = landmarks.get(args[1]);
			player.teleport(new Location(player.getWorld(), lm.x, lm.y, lm.z));
			return true;
		}
		err (player, String.format("/landmark %s [name]", args[0]));
		return false;
	}
	
	private void broadcastMsg (String msg, ChatColor color) {
		for (Player p : TeahousePlugin.instance.getServer().getOnlinePlayers()) {
			msg (p, msg, color);
		}
	}
	
	private void msg (Player player, String msg, ChatColor color) {
		BaseComponent m = new TextComponent(msg);
		m.setColor(color);
		player.spigot().sendMessage(m);
	}
	
	private void err (Player player, String msg) {
		BaseComponent m = new TextComponent(msg);
		m.setColor(ChatColor.RED);
		player.spigot().sendMessage(m);
	}
}

class Landmark {
	
	String name, author, description;
	double x, y, z;
	long time;
	
	public Landmark (String name, String author, String description, double x, double y, double z, long time) {
		this.name = name;
		this.author = author;
		this.description = description;
		this.x = x;
		this.y = y;
		this.z = z;
		this.time = time;
	}
	
	public Landmark (String name, String author, String description, double x, double y, double z) {
		this (name, author, description, x, y, z, System.currentTimeMillis());
	}
	
	public TextComponent displayText () {
		TextComponent out = new TextComponent(String.format("[%s]", name));
		out.setColor(ChatColor.GREEN);
		TextComponent[] hoverText = {
				new TextComponent ("Name: "),
				new TextComponent (name + "\n"),
				new TextComponent ("  By: "),
				new TextComponent (author + "\n"),
				new TextComponent (description.equals("") ? "No discription" : description + "\n"),
				new TextComponent ("Click to teleport")
			};
		hoverText[0].setColor(ChatColor.BLUE);
		hoverText[0].setBold(true);
		hoverText[1].setColor(ChatColor.GOLD);
		hoverText[1].setBold(false);
		hoverText[2].setColor(ChatColor.BLUE);
		hoverText[2].setBold(true);
		hoverText[3].setColor(ChatColor.YELLOW);
		hoverText[3].setBold(false);
		hoverText[4].setColor(ChatColor.GREEN);
		hoverText[4].setBold(false);
		hoverText[5].setColor(ChatColor.YELLOW);
		hoverText[5].setBold(false);
		hoverText[5].setItalic(true);
		
		out.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hoverText));
		out.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, String.format("/tp @s %f %f %f", x, y, z)));
		return out;
	}
	
	public void save (JsonWriter writer) throws IOException {
		writer.beginObject();
		writer.name("name").value(name);
		writer.name("author").value(author);
		writer.name("description").value(description);
		writer.name("x").value(x);
		writer.name("y").value(y);
		writer.name("z").value(z);
		writer.name("time").value(time);
		writer.endObject();
	}
}
