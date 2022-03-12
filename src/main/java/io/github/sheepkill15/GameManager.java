package io.github.sheepkill15;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;

public class GameManager {

    private final int PlayersToStart;
    private final int TimeToStart;
    private final int TimeInBetween;
    public final int RoundDuration;

    public final BuildSomething mainPlugin;

    public final ArrayList<GamePlayer> joinedPlayers = new ArrayList<>();
    public final Scoreboard scoreboard = new Scoreboard(this);

    private boolean starting = false;

    private final ArrayList<GamePlayer> eligible = new ArrayList<>();

    private GameRound currentRound;

    public static final ArrayList<String> words = new ArrayList<>();
    public GameManager(BuildSomething main, ArrayList<String> newWords, int requiredPlayers, int start, int inbetween, int duration)
    {
        mainPlugin = main;
        if(newWords == null) {
             words.addAll(Arrays.asList(
                    "door",
                    "bunk bed",
                    "bowl",
                    "swing",
                    "heart",
                    "nail",
                    "camera",
                    "bike",
                    "shirt",
                    "butterfly",
                    "sea turtle",
                    "kite",
                    "starfish",
                    "person",
                    "corn",
                    "motorcycle",
                    "eyes",
                    "sea",
                    "house",
                    "car",
                    "basketball",
                    "carrot",
                    "eye",
                    "backpack",
                    "feet",
                    "water",
                    "cookie",
                    "purse",
                    "ice cream cone",
                    "beak",
                    "glasses",
                    "dream",
                    "neck",
                    "bus",
                    "diamond",
                    "giraffe",
                    "dinosaur",
                    "boat",
                    "woman",
                    "grapes",
                    "hamburger",
                    "ghost",
                    "pencil",
                    "mountain",
                    "baseball",
                    "Earth",
                    "jellyfish",
                    "button",
                    "swimming pool",
                    "blocks",
                    "sister",
                    "hot",
                    "dictionary",
                    "swimming pool",
                    "restaurant",
                    "lullaby",
                    "worm",
                    "lemon",
                    "bridge",
                    "niece",
                    "eye",
                    "cheerleader",
                    "day",
                    "triangle",
                    "television",
                    "ballet",
                    "spy",
                    "drums",
                    "queen",
                    "fox",
                    "bug",
                    "fan",
                    "spoon",
                    "study",
                    "grandpa",
                    "soap",
                    "crib",
                    "laundry",
                    "tree house",
                    "square",
                    "book",
                    "zoo",
                    "window",
                    "antlers",
                    "dirt",
                    "nightmare",
                    "puppy",
                    "kite",
                    "talk",
                    "elbow",
                    "ride",
                    "smoke",
                    "alarm clock",
                    "car",
                    "bed",
                    "tree",
                    "winter",
                    "candy",
                    "bat",
                    "cake",
                    "breakfast",
                    "drive",
                    "airplane",
                    "jog",
                    "row",
                    "finger",
                    "grab",
                    "push",
                    "karate",
                    "awake",
                    "mad",
                    "baseball",
                    "juggle",
                    "tail",
                    "alligator",
                    "count",
                    "quiet",
                    "spider",
                    "toes",
                    "nose",
                    "photograph",
                    "snap",
                    "eat",
                    "penguin",
                    "swimming",
                    "blow",
                    "hat",
                    "slow",
                    "mouth",
                    "break",
                    "top",
                    "ear",
                    "shoulder",
                    "point",
                    "phone",
                    "dig",
                    "type",
                    "hand",
                    "dinner",
                    "touch",
                    "shrug",
                    "hit",
                    "jump rope",
                    "bite",
                    "beg",
                    "bird",
                    "frog",
                    "scared",
                    "crawl",
                    "ice cream cone",
                    "tape",
                    "state",
                    "t-shirt",
                    "rat",
                    "merry-go-round",
                    "mini blinds",
                    "shopping cart",
                    "table",
                    "porch",
                    "class",
                    "cake",
                    "gift",
                    "beach",
                    "knee",
                    "crown",
                    "lobster",
                    "alarm clock",
                    "outer space",
                    "mug",
                    "knot",
                    "smoke",
                    "cage",
                    "bell pepper",
                    "strap",
                    "throne",
                    "muscle",
                    "banana split",
                    "homeless",
                    "railroad",
                    "television",
                    "dirt",
                    "melt",
                    "goblin",
                    "deer",
                    "stocking",
                    "ski",
                    "penguin",
                    "germ",
                    "plant",
                    "ceiling fan",
                    "boot",
                    "maze",
                    "coconut",
                    "bushes",
                    "cracker",
                    "cotton candy",
                    "parachute",
                    "bicycle",
                    "tank",
                    "pogo stick",
                    "drill bit",
                    "handle",
                    "lie",
                    "propose",
                    "thief",
                    "knight",
                    "boa constrictor",
                    "electrical outlet",
                    "thunder",
                    "jungle",
                    "engaged",
                    "obey",
                    "art gallery",
                    "darkness",
                    "hermit crab",
                    "tugboat",
                    "front",
                    "sweater",
                    "surround",
                    "competition",
                    "great-grandfather",
                    "Quidditch",
                    "kneel",
                    "economics",
                    "back flip",
                    "lace",
                    "zoom",
                    "chariot racing",
                    "roller coaster",
                    "ratchet",
                    "clown",
                    "husband",
                    "drain",
                    "landlord",
                    "florist",
                    "pharmacist",
                    "swamp",
                    "diver",
                    "idea",
                    "nightmare",
                    "wrap",
                    "dance",
                    "download",
                    "jeans",
                    "quit",
                    "printer ink",
                    "retail",
                    "comfy",
                    "diagonal",
                    "recycle"
            ));
        }
        else words.addAll(newWords);

        PlayersToStart = requiredPlayers;
        TimeToStart = start;
        TimeInBetween = inbetween;
        RoundDuration = duration;
    }

    public void JoinPlayer(Player player) {
        GamePlayer newPlayer = new GamePlayer(player);
        joinedPlayers.add(newPlayer);
        eligible.add(newPlayer);

        if(joinedPlayers.size() >= PlayersToStart && !starting) {
            StartCountdown();
        }

        scoreboard.AddPlayer(newPlayer, ChatColor.WHITE);
    }

    public void LeavePlayer(Player player) {
        int index = GetPlayerIndex(player);
        if(index < 0) {
            mainPlugin.getLogger().info("Something went wrong on removing player! Index is: " + index);
            return;
        }
        GamePlayer gamePlayer = joinedPlayers.get(index);
        player.setGameMode(gamePlayer.originalGameMode);
        player.teleport(gamePlayer.originalLocation);

        joinedPlayers.remove(index);
        eligible.remove(gamePlayer);

        if(joinedPlayers.size() < PlayersToStart) {
            Bukkit.getScheduler().cancelTasks(mainPlugin);
            starting = false;
        }

        scoreboard.RemovePlayer(gamePlayer);
    }

    public void GameRoundEnded() {
        if(eligible.size() == 0) {

        GamePlayer winner = joinedPlayers.get(0);

        for(GamePlayer player : joinedPlayers) {
            if(player.score > winner.score) {
                winner = player;
            }

        }
        eligible.addAll(joinedPlayers);

        for(GamePlayer player : joinedPlayers) {
            player.player.sendTitle(winner.player.getDisplayName() + " is the winner!", "His score was: " + winner.score, 10, 60, 10);
        }

        for(GamePlayer player : joinedPlayers) {
            player.UpdateScore(-player.score);
        }
    }
        if(currentRound != null) {
            for (GamePlayer player : joinedPlayers) {
                player.player.sendMessage(BuildSomething.CommandChatPrefix + "The word was: " + currentRound.word);
            }
        }
        Bukkit.getScheduler().scheduleSyncDelayedTask(mainPlugin, new Runnable() {
            @Override
            public void run() {
                NewGameRound();
            }
        }, TimeInBetween);
    }

    private void NewGameRound() {

        GamePlayer drawer = eligible.get(ThreadLocalRandom.current().nextInt(eligible.size()));
        for(GamePlayer player : joinedPlayers) {
            if(player != drawer)
                player.player.setGameMode(GameMode.SPECTATOR);
        }
        drawer.player.setGameMode(GameMode.CREATIVE);
        eligible.remove(drawer);
        currentRound = new GameRound(words.get(ThreadLocalRandom.current().nextInt(words.size())), drawer, this);
    }

    public int GetPlayerIndex(Player player) {
        for(int i = 0; i < joinedPlayers.size(); i++) {
            if(joinedPlayers.get(i).player == player) return i;
        }
        return -1;
    }

    private void StartCountdown() {
        starting = true;

        scoreboard.StartCountdown(TimeToStart, 20, new OnCountdownEnd() {
            @Override
            public void onEnd() {
                NewGameRound();
            }

            @Override
            public void onUpdate(int currTime) {

            }
        });
    }
}
