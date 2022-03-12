package io.github.sheepkill15;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.HashSet;
import java.util.concurrent.ThreadLocalRandom;

public class GameRound implements Listener, OnCountdownEnd {

    public final String word;
    private final GameManager manager;

    private final GamePlayer builder;

    private final int TimeToEnd;
    private int score = 350;
    private int builderScore = 50;

    private String actionBar = "";
    private final HashSet<Integer> positionsRevealed = new HashSet<>();

    private int correctGuesses = 0;

    private int lengthToHint;

    public GameRound(String word, GamePlayer drawerPlayer, GameManager manager) {
        this.word = word;
        this.manager = manager;
        this.builder = drawerPlayer;
        TimeToEnd = manager.RoundDuration;
        Start();
    }

    public void Start() {
        builder.player.sendTitle("You are the builder!", "Your word is: " + word, 10, 60, 10);
        manager.mainPlugin.getServer().getPluginManager().registerEvents(this, manager.mainPlugin);
        lengthToHint = TimeToEnd / 4;
        for(int i = 0; i < word.length(); i++) {
            if(word.charAt(i) == ' ' || word.charAt(i) == '-') {
                positionsRevealed.add(i);
            }
        }

        for(GamePlayer player : manager.joinedPlayers) {
            player.guessed = false;
            manager.scoreboard.RemovePlayer(player);
            if(player != builder)
            {
                player.player.sendTitle(builder.player.getDisplayName(),"Is the builder", 10, 60, 10);
                manager.scoreboard.AddPlayer(player, ChatColor.WHITE);
            }
            else {
                manager.scoreboard.AddPlayer(player, ChatColor.YELLOW);
            }
        }

        manager.scoreboard.StartCountdown(TimeToEnd, 80, this);
    }

    @EventHandler
    public void wordGuess(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        int index = manager.GetPlayerIndex(player);
        if(index == -1) return;
        GamePlayer gamePlayer = manager.joinedPlayers.get(index);
        int goodLetterCount = 0;
        if(player != builder.player) {
            String playerWord = event.getMessage();
            if(playerWord.length() != word.length()) return;
            for(int i = 0; i < playerWord.length(); i++) {
                if(word.charAt(i) == playerWord.charAt(i)) {
                    goodLetterCount++;
                }
            }
            if(goodLetterCount == word.length()) {
                if(!gamePlayer.guessed) CorrectGuess(gamePlayer);
                event.setCancelled(true);
            }
            else if(goodLetterCount == word.length() - 1 && !gamePlayer.guessed) {
                player.sendMessage(BuildSomething.CommandChatPrefix + ChatColor.YELLOW + '\'' + playerWord + '\'' + " is really close!");
            }
        }
    }

    private void CorrectGuess(final GamePlayer player) {
        player.guessed = true;
        player.UpdateScore(score);

        manager.mainPlugin.getServer().getScheduler().runTask(manager.mainPlugin, new Runnable() {
            @Override
            public void run() {
                manager.scoreboard.RemovePlayer(player);
                manager.scoreboard.AddPlayer(player, ChatColor.GREEN);
            }
        });

        score *= 0.75;
        builder.UpdateScore(builderScore);
        correctGuesses++;

        for(GamePlayer gamePlayer : manager.joinedPlayers) {
            gamePlayer.player.sendMessage(BuildSomething.CommandChatPrefix + ChatColor.GREEN + player.player.getDisplayName() + " has guessed the word right!");
        }

        if(correctGuesses >= manager.joinedPlayers.size() - 1) {
            manager.scoreboard.Cancel(this);
        }
    }


    private void EndRound() {
        AsyncPlayerChatEvent.getHandlerList().unregister(this);
        manager.GameRoundEnded();
    }

    private void UpdateRound(int timeSoFar) {
        StringBuilder builder = new StringBuilder(ChatColor.BOLD + "" + ChatColor.RED + "");

        if(timeSoFar == TimeToEnd - lengthToHint) {
            BuildWord(builder);
        }
        else if(timeSoFar == TimeToEnd - lengthToHint * 2) {
            int index = ThreadLocalRandom.current().nextInt(word.length());
            for(int i = 0; i < word.length(); i++) {
                if(i == index) {
                    builder.append(word.charAt(i)).append(' ');
                }
                else
                    builder.append("_ ");
            }
            builder.deleteCharAt(builder.length() - 1);
            actionBar = builder.toString();
            positionsRevealed.add(index);
        }
        else if(timeSoFar == TimeToEnd - lengthToHint * 3) {

            int index = ThreadLocalRandom.current().nextInt(word.length());
            while(positionsRevealed.contains(index)) {
                index = ThreadLocalRandom.current().nextInt(word.length());
            }
            positionsRevealed.add(index);
            BuildWord(builder);
        }
        for(GamePlayer player : manager.joinedPlayers) {
            if(!player.guessed && player != this.builder) {
                player.spigot.sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(actionBar));
            }
            else {
                player.spigot.sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(word));
            }
        }
    }

    private void BuildWord(StringBuilder builder) {
        for(int i = 0; i < word.length(); i++) {
            if(positionsRevealed.contains(i)) {
                builder.append(word.charAt(i)).append(' ');
            }
            else
                builder.append("_ ");
        }
        builder.deleteCharAt(builder.length() - 1);
        actionBar = builder.toString();
    }

    @Override
    public void onEnd() {
        EndRound();
    }

    @Override
    public void onUpdate(int currTime) {
        UpdateRound(currTime);
    }
}
