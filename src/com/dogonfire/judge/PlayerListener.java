package com.dogonfire.judge;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerListener implements Listener
{
	private Judge	plugin	= null;

	public PlayerListener(Judge plugin)
	{
		this.plugin = plugin;
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onPlayerJoin(PlayerJoinEvent event)
	{
		plugin.getPlayerManager().registerPlayerLogin(event.getPlayer());
	}
}
