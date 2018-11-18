package com.dogonfire.judge;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class PlayerManager
{
	private Judge plugin;
    List<String> traits = new ArrayList<String>();
	private FileConfiguration playersConfig = null;
	private File playersConfigFile = null;
	private String datePattern = "HH:mm:ss dd-MM-yyyy";
	private Random random = new Random();
	
	public PlayerManager(Judge p)
	{
		this.plugin = p;
	}

	public void load()
	{
		traits.add("Funny");
		traits.add("Nazi");
		traits.add("Cold");
		traits.add("Sweet");
		traits.add("Awesome");
		traits.add("Criminal");
		traits.add("Friendly");
		traits.add("Shy");
		traits.add("Rude");
		traits.add("Hostile");
		traits.add("Nasty");
		traits.add("Pervert");
		traits.add("Freak");
		traits.add("Emo");
		traits.add("Annoying");
		traits.add("Cool");
		traits.add("Intelligent");
		traits.add("Random");
		traits.add("Badass");
		traits.add("Rebel");
		traits.add("Nice");
		traits.add("Honest");
		traits.add("Cute");
		traits.add("Brutal");
		traits.add("Weird");
		traits.add("Hot");
		traits.add("Creative");
		traits.add("Evil");
		traits.add("Crazy");
		traits.add("Unstable");
		traits.add("Lazy");
		traits.add("Gamer");
		traits.add("Nerd");
		traits.add("Naive");
		traits.add("Scary");
		traits.add("Fabulous");
		traits.add("Peasant");
		traits.add("Hipster");
		traits.add("Romantic");
		traits.add("Royal");		
		traits.add("Drama");
		traits.add("Charming");
		traits.add("Otaku");
		traits.add("Sexy");
		traits.add("Sunshine");
		traits.add("Hippie");
		traits.add("Rockstar");
		traits.add("Strong");
		traits.add("Hero");
		traits.add("Chicken");
		traits.add("Beast");
		traits.add("Winner");
		traits.add("Loser");
		traits.add("BadBoy");
		traits.add("Skank");
		traits.add("Fancy");
		traits.add("Magnificent");
		traits.add("Clever");
		traits.add("Helpful");
		traits.add("Chill");
		traits.add("Bully");
		traits.add("Childish");
		traits.add("Broken");
		traits.add("Handsome");
		traits.add("Zombie");
		traits.add("Pirate");
		traits.add("Short");
		traits.add("Tall");
		traits.add("Epic");
		traits.add("Legendary");

		if (this.playersConfigFile == null)
		{
			this.playersConfigFile = new File(plugin.getDataFolder(), "players.yml");
		}

		this.playersConfig = YamlConfiguration.loadConfiguration(playersConfigFile);

		this.plugin.log("Loaded " + playersConfig.getKeys(false).size() + " players.");
	}

	public void save()
	{
		if ((this.playersConfig == null) || (playersConfigFile == null))
		{
			return;
		}

		try
		{
			this.playersConfig.save(playersConfigFile);
		}
		catch (Exception ex)
		{
			this.plugin.log("Could not save config to " + playersConfigFile + ": " + ex.getMessage());
		}
	}
		
	public void setLastVoteTime(Player player)
	{
		DateFormat formatter = new SimpleDateFormat(datePattern);
		Date thisDate = new Date();

		this.playersConfig.set(player.getUniqueId() + ".LastVoteTime", formatter.format(thisDate));

		save();
	}
	
	public int getTimeUntilCanVote(Player player)
	{
		String lastVoteString = this.playersConfig.getString(player.getUniqueId() + ".LastVoteTime");

		DateFormat formatter = new SimpleDateFormat(datePattern);
		Date lastVoteDate = null;
		Date thisDate = new Date();
		try
		{
			lastVoteDate = formatter.parse(lastVoteString);
		}
		catch (Exception ex)
		{
			lastVoteDate = new Date();
			lastVoteDate.setTime(0L);
		}
		long diff = this.plugin.minMinutesBetweenVote * 60 * 1000 + lastVoteDate.getTime() - thisDate.getTime();
		long diffSeconds = diff / 1000L;
		int diffMinutes = (int) (diffSeconds / 60L);

		return diffMinutes;
	}
	
	public void clearQuestions(Player player)
	{
		playersConfig.set(player.getUniqueId() + ".Questions", null);		

		save();
	}

	public List<String> getQuestionOptionsForPlayer(Player player)
	{
		return playersConfig.getStringList(player.getUniqueId() + ".Questions");
	}

	public void setQuestionsForPlayer(Player player, List<String> questions)
	{
		playersConfig.set(player.getUniqueId() + ".Questions", questions);

		save();
	}
	
	public UUID getTargetPlayerForPlayer(Player player)
	{
		String playerId = playersConfig.getString(player.getUniqueId() + ".TargetPlayer");
		
		return UUID.fromString(playerId);
	}
	
	public void setTargetPlayerForPlayer(Player player, UUID targetPlayerId)
	{
		playersConfig.set(player.getUniqueId() + ".TargetPlayer", targetPlayerId.toString());

		save();
	}
	
	public UUID getTargetPlayerForQuestions(Player exceptPlayer)
	{
		Date thisDate = new Date();
		Date lastLoginDate = null;
		List<UUID> targetPlayers = new ArrayList<UUID>();

		// Choose someone who has been online recently
		Set<String> players = this.playersConfig.getKeys(false);

		for(String playerIdString : players)
		{
			UUID playerId = null;
			
			try
			{
				playerId = UUID.fromString(playerIdString);
			}
			catch(Exception ex)
			{
				continue;
			}
			
			if(exceptPlayer.getUniqueId().equals(playerId))
			{
				continue;
			}
			
			String lastLoginString = this.playersConfig.getString(playerId + ".LastLoginTime");			
					
			DateFormat formatter = new SimpleDateFormat(datePattern);
			try
			{
				lastLoginDate = formatter.parse(lastLoginString);
			}
			catch (Exception ex)
			{
				lastLoginDate = new Date();
				lastLoginDate.setTime(0L);
			}

			long diffHours = (thisDate.getTime() - lastLoginDate.getTime()) / (60 * 60 * 1000);

			if(diffHours < 48)
			{
				targetPlayers.add(playerId);
			}
		}
		
		if(targetPlayers.size() == 0)
		{
			return null;
		}
		

		
		return targetPlayers.get(random.nextInt(targetPlayers.size()));
	}
	
	public PlayerPersonality getPersonalityForPlayer(UUID playerId)
	{
		List<String> words = this.playersConfig.getStringList(playerId + ".Words");
		List<String> votes = this.playersConfig.getStringList(playerId + ".Votes");
		HashMap<String, Integer> wordStats = new HashMap<String, Integer>();
		
		int n = 0;
		
		for(String word : words)
		{
			wordStats.put(word, Integer.parseInt(votes.get(n)));
			n++;
		}
		
		Collections.sort(words, new WordComparator(wordStats));

		int total = 0;
		
		for(int i=0; i<4; i++)
		{
			if(words.size() > i)
			{
				total += wordStats.get(words.get(i));
			}
		}
		
		PlayerPersonality personality = new PlayerPersonality();
		
		for(int i=0; i<4; i++)
		{
			if(words.size() > i)
			{
				float s = wordStats.get(words.get(i));
				s = s / (float)total;
				personality.words.add(words.get(i));
				personality.wordStats.put(words.get(i), s);
			}
		}

		return personality;		
	}

	// 
	public List<String> generatePersonalityQuestionsForPlayer(Player judgingPlayer, UUID targetPlayerId)
	{
		List<String> options = new ArrayList<String>();

		for(int i=0; i<5; i++)
		{
			String newWord = traits.get(random.nextInt(traits.size()));
			
			while(options.contains(newWord))
			{			
				newWord = traits.get(random.nextInt(traits.size()));
			}
			
			options.add(newWord);
		}
		
		options.add("None of these");
		
		setQuestionsForPlayer(judgingPlayer, options);
		setTargetPlayerForPlayer(judgingPlayer, targetPlayerId);
		
		return options;
	}

	public void addWordForPlayer(UUID playerId, String voteWord)
	{		
		List<String> words = this.playersConfig.getStringList(playerId + ".Words");
		List<String> votes = this.playersConfig.getStringList(playerId + ".Votes");
		
		if (words == null)
		{
			words = new ArrayList<String>();
		}

		if(votes==null)
		{
			votes = new ArrayList<String>(); 
		}

		int n = 0;
		int index = -1;
				
		for(String word : words)
		{
			if(word.equals(voteWord))
			{
				index = n;				
			}
			
			n++;
		}
		
		if(index==-1)
		{
			index = n;
			words.add(voteWord);
			votes.add("0");
			this.playersConfig.set(playerId + ".Words", words);
			this.playersConfig.set(playerId + ".Votes", votes);
		}
		
		int currentVotes = 0;
		
		try
		{
			currentVotes = Integer.parseInt(votes.get(index));
		}
		catch(Exception ex)
		{
		}
		
		currentVotes++;
		
		votes.set(index, String.valueOf(currentVotes));
		
		this.playersConfig.set(playerId + ".Votes", votes);
		
		save();
	}
	
	public void registerPlayerLogin(Player player)
	{
		DateFormat formatter = new SimpleDateFormat(datePattern);
		Date thisDate = new Date();

		playersConfig.set(player.getUniqueId() + ".LastLoginTime", formatter.format(thisDate));		
		playersConfig.set(player.getUniqueId() + ".Name", player.getName());

		save();		
	}
	
	public void announceOnlinePlayerPersonality()
	{
		int numberOfPlayerOnline = plugin.getServer().getOnlinePlayers().size();
		
		if(numberOfPlayerOnline==0)
		{
			return;
		}
		
		Player player = (Player)(plugin.getServer().getOnlinePlayers().toArray()[random.nextInt(numberOfPlayerOnline)]);
		
		PlayerPersonality personality = getPersonalityForPlayer(player.getUniqueId());
		
		if(personality.words.size() == 0)
		{
			return;
		}

		int index = random.nextInt(personality.words.size());
		int percent = (int)(100 * personality.wordStats.get(personality.words.get(index)));
			
		plugin.getServer().broadcastMessage(ChatColor.AQUA + "People on this server thinks that " + ChatColor.GOLD + player.getName() + ChatColor.AQUA + " is " + ChatColor.GOLD + percent + "% " + ChatColor.AQUA + personality.words.get(index));					
	}
	
	public class WordComparator implements Comparator
	{
		private HashMap<String, Integer> wordStats;
		
		public WordComparator(HashMap<String, Integer> wordStats)
		{
			this.wordStats = wordStats;
		}

		public int compare(Object object1, Object object2)
		{
			String word1 = (String) object1;
			String word2 = (String) object2;

			float votes1 = wordStats.get(word1);
			float votes2 = wordStats.get(word2);

			return (int) (votes2 - votes1);
		}
	}
}