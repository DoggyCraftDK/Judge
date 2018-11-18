package com.dogonfire.judge;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import net.milkbowl.vault.economy.Economy;


public class Judge extends JavaPlugin
{	
	private boolean debug = false;
	
	public int minMinutesBetweenVote = 2;
	private FileConfiguration config = null;	
	private ConsoleCommandSender console;
	private String serverName = "Your Server"; 
	private Commands commands	= null;	
	private PlayerManager playerManager;	
	private Economy economy	= null;
	
	public PlayerManager getPlayerManager()
	{
		return playerManager;		
	}
	
	public Economy getEconomy()
	{
		return economy;
	}
	
	// This gets triggered once when the server starts
	@Override
	public void onEnable()
	{
		this.console = this.getServer().getConsoleSender();

		// Make a command class 
		this.commands = new Commands(this);		
		this.playerManager = new PlayerManager(this);
		this.playerManager.load();
		
		PluginManager pm = getServer().getPluginManager();
		
		if (pm.getPlugin("Vault") != null)
		{
			log("Vault detected.");

			RegisteredServiceProvider<Economy> economyProvider = this.getServer().getServicesManager().getRegistration(Economy.class);
			if (economyProvider != null)
			{
				economy = economyProvider.getProvider();
			}
			else
			{
				log("Vault not found.");
			}
		}
		else
		{
			log("Vault not found.");
		}
		
		this.getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
		
		this.getServer().getScheduler().runTaskTimerAsynchronously(this, new Runnable()
		{
			public void run()
			{
				getPlayerManager().announceOnlinePlayerPersonality();
			}
		}, 20L, 20000L);
		
		log(ChatColor.DARK_AQUA + "Plugin enabled!");		
	}
		
	public void onDisabled()
	{
	}
	
	public void log(String message)
	{
		console.sendMessage("[" + getDescription().getFullName() + "] " + message);
	}

	public void logDebug(String message)
	{
		if (this.debug)
		{
			console.sendMessage("[" + getDescription().getFullName() + "] " + message);
		}
	}

	public void reloadSettings()
	{
		reloadConfig();

		loadSettings();
	}

	public void loadSettings()
	{
		this.serverName = config.getString("Settings.ServerName", "Your Server");		
	}

	public void saveSettings()
	{
		config.set("Settings.ServerName", this.serverName);

		saveConfig();
	}

	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
		return this.commands.onCommand(sender, cmd, label, args);
	}		
}

