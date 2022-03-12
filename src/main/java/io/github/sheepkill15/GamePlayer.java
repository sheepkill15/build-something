package io.github.sheepkill15;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Score;

public class GamePlayer {

    public final Player player;
    public final Location originalLocation;
    public final GameMode originalGameMode;
    public final Player.Spigot spigot;

    public Score scoreDisplay;
    public int score = 0;
    public boolean guessed = false;

    public GamePlayer(Player pl) {
        player = pl;
        originalLocation = pl.getLocation();
        originalGameMode = pl.getGameMode();
        spigot = pl.spigot();
    }

    public void UpdateScore(int amount) {
        score += amount;
        scoreDisplay.setScore(score);
    }

}
