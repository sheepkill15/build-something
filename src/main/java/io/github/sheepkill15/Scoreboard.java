package io.github.sheepkill15;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scoreboard.*;

import java.util.Objects;

public class Scoreboard {

    private final GameManager manager;

    private int remainingTime;
    private int countdownTask;

    private final Score countDownScore;

    public org.bukkit.scoreboard.Scoreboard scoreboard;
    private final Objective objective;

    public Scoreboard(GameManager gameManager) {
        manager = gameManager;
        ScoreboardManager scoreboardManager = Bukkit.getScoreboardManager();

        scoreboard = scoreboardManager.getNewScoreboard();
        objective = scoreboard.registerNewObjective("BuildSomething", "", ChatColor.YELLOW + "" + ChatColor.BOLD + "Build Something", RenderType.INTEGER);
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        countDownScore = objective.getScore(ChatColor.ITALIC + "Remaining time");
    }

    public void AddPlayer(GamePlayer player, ChatColor additional) {
        player.scoreDisplay = objective.getScore(additional + player.player.getDisplayName());
        player.scoreDisplay.setScore(player.score);
        player.player.setScoreboard(scoreboard);
    }

    public void RemovePlayer(GamePlayer player) {
        Objects.requireNonNull(objective.getScoreboard()).resetScores(player.scoreDisplay.getEntry());
        player.player.setScoreboard(Objects.requireNonNull(Bukkit.getScoreboardManager()).getNewScoreboard());
    }


    public void StartCountdown(int time, int startDelay, final OnCountdownEnd end) {
        remainingTime = time;
        countdownTask = Bukkit.getScheduler().scheduleSyncRepeatingTask(manager.mainPlugin, new Runnable() {
            @Override
            public void run() {
                CountDown(end);
            }
        }, startDelay, 20);
    }

    public void Update() {
        countDownScore.setScore(-remainingTime);
    }

    private void CountDown(OnCountdownEnd endTask) {
        Update();
        if(remainingTime <= 0) {
            remainingTime = 1;
            Cancel(endTask);
            return;
        }
        remainingTime--;
        if(endTask != null) {
            endTask.onUpdate(remainingTime);
        }
    }

    public void Cancel(OnCountdownEnd endTask) {
        Bukkit.getScheduler().cancelTask(countdownTask);
        if(endTask != null) {
            endTask.onEnd();
        }
    }

}
