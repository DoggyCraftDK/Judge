package com.dogonfire.judge;

import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class Commands 
{
	private Judge  plugin;
	private Random random = new Random();

	Commands(Judge p)
	{
		this.plugin = p;
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
		Player player = null;
		
		if (sender instanceof Player)
		{
			player = (Player) sender;			
		}
			
		if (cmd.getName().equalsIgnoreCase("judge")) 
		{			
			if (args.length == 0)
			{
				CommandQuestionPersonality(player, args);
				//CommandInfo(player);
				return true;
			}

			if (args.length == 1)
			{				
				if (args[0].equalsIgnoreCase("a") || args[0].equalsIgnoreCase("b") || args[0].equalsIgnoreCase("c") || args[0].equalsIgnoreCase("d") || args[0].equalsIgnoreCase("e") || args[0].equalsIgnoreCase("f"))
				{					
					CommandVotePersonality(player, args);
				}
				else
				{
					CommandViewPersonality(player, args);
				}
			}
			
			
			return true;					
		}	

		return true;
	}

	public void CommandInfo(Player player)
	{
		player.sendMessage(ChatColor.YELLOW + "---------- " + this.plugin.getDescription().getFullName() + " ----------");
		player.sendMessage(ChatColor.AQUA + "By DogOnFire");
		player.sendMessage("");
		player.sendMessage(ChatColor.AQUA + "Use " + ChatColor.WHITE + "/judge" + ChatColor.AQUA + " to judge another players personality!");				
	}	
	
	public void CommandViewPersonality(Player player, String[] args)
	{
		OfflinePlayer targetPlayer = plugin.getServer().getOfflinePlayer(args[0]);	
		
		if(targetPlayer==null)
		{
			player.sendMessage(ChatColor.RED + "No such player found.");			
			return;
		}
		
		UUID playerId = targetPlayer.getUniqueId();
				
		player.sendMessage(ChatColor.AQUA + "People on this server thinks that " + ChatColor.GOLD + targetPlayer.getName() + ChatColor.AQUA + " is:");	

		PlayerPersonality personality = plugin.getPlayerManager().getPersonalityForPlayer(playerId);
		
		for(int i=0; i<personality.words.size(); i++)
		{
			int percent = (int)(100 * personality.wordStats.get(personality.words.get(i)));
			
			player.sendMessage("  " + ChatColor.GOLD + percent + "% " + ChatColor.AQUA + personality.words.get(i));					
		}
		
		//player.sendMessage(ChatColor.AQUA + "Check back later and see how it changes!");		
	}
	
	public void CommandQuestionPersonality(Player player, String[] args)
	{
		int time = plugin.getPlayerManager().getTimeUntilCanVote(player);
		
		if(time > 0 )
		{
			//player.sendMessage(ChatColor.RED + "Wait " + ChatColor.YELLOW + time + ChatColor.RED + " minutes before judging again!");
			//return;
		}
		
		UUID targetplayerId = plugin.getPlayerManager().getTargetPlayerForQuestions(player);
		
		OfflinePlayer targetPlayer = plugin.getServer().getOfflinePlayer(targetplayerId);
		
		if(targetPlayer==null)
		{
			player.sendMessage(ChatColor.RED + "No player found for judging. Please try again later.");			
			return;
		}
		
		List<String> questions = plugin.getPlayerManager().generatePersonalityQuestionsForPlayer(player, targetPlayer.getUniqueId());

		player.sendMessage(ChatColor.AQUA + "");		
		player.sendMessage(ChatColor.AQUA + "Which word do you think best describes " + ChatColor.GOLD + targetPlayer.getName() + ChatColor.AQUA + "?");		
		
		player.sendMessage(ChatColor.WHITE + " A - " + ChatColor.GOLD + questions.get(0));		
		player.sendMessage(ChatColor.WHITE + " B - " + ChatColor.GOLD + questions.get(1));		
		player.sendMessage(ChatColor.WHITE + " C - " + ChatColor.GOLD + questions.get(2));		
		player.sendMessage(ChatColor.WHITE + " D - " + ChatColor.GOLD + questions.get(3));		
		player.sendMessage(ChatColor.WHITE + " E - " + ChatColor.GOLD + questions.get(4));		
		player.sendMessage(ChatColor.WHITE + " F - " + ChatColor.GOLD + questions.get(5));		
		
		player.sendMessage(ChatColor.AQUA + "Use " + ChatColor.WHITE + "/judge a /judge b /judge c " + ChatColor.AQUA + " to answer the question");				
		player.sendMessage(ChatColor.AQUA + "Don't know that player? Just use " + ChatColor.WHITE + "/judge" + ChatColor.AQUA + " again!");				
	}

	public void CommandVotePersonality(Player player, String[] args)
	{		
		int time = plugin.getPlayerManager().getTimeUntilCanVote(player);
		
		if(time > 0 )
		{
			//player.sendMessage(ChatColor.RED + "Wait " + ChatColor.YELLOW + time + ChatColor.RED + " minutes before judging again!");
			//return;
		}

		String choice = args[0].toLowerCase();
		int index = -1;
		
		switch(choice)
		{
			case "a" : index = 0; break;
			case "b" : index = 1; break;
			case "c" : index = 2; break;
			case "d" : index = 3; break;
			case "e" : index = 4; break;
			case "f" : index = 5; break;
		}

		if(index==-1)
		{
			player.sendMessage(ChatColor.RED + "That's not an answer!");
			return;
		}
		
		if (index != 5)
		{
			List<String> options = plugin.getPlayerManager().getQuestionOptionsForPlayer(player);

			if (options == null)
			{
				player.sendMessage(ChatColor.RED + "There is nothing to answer!");
				return;
			}

			String word = options.get(index);

			UUID targetPlayerId = plugin.getPlayerManager().getTargetPlayerForPlayer(player);
			plugin.getPlayerManager().addWordForPlayer(targetPlayerId, word);

			plugin.log(player.getName() +" judged " + plugin.getServer().getOfflinePlayer(targetPlayerId).getName() + " as " + word);
		}
			
		plugin.getPlayerManager().setLastVoteTime(player);
		plugin.getPlayerManager().clearQuestions(player);
		
		if(random.nextInt(5) == 0)
		{
			plugin.getEconomy().depositPlayer(player, 10);
			player.sendMessage(ChatColor.GREEN + "You received " + ChatColor.GOLD + " 10 wanks for judging!");				
		}
		
		player.sendMessage(ChatColor.GREEN + "Thanks for judging!");				
		player.sendMessage(ChatColor.AQUA + "Remember that you can check judgement of any player using " + ChatColor.WHITE + "/judge <playername>");				
	}

	public void CommandReload(Player player)
	{
		this.plugin.reloadSettings();
		
		if (player == null)
		{
			this.plugin.log(this.plugin.getDescription().getFullName() + ": Reloaded configuration.");
		}
		else
		{
			player.sendMessage(ChatColor.YELLOW + this.plugin.getDescription().getFullName() + ": " + ChatColor.WHITE + "Reloaded configuration.");
		}
	}
}