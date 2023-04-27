package me.bobthe28th.birthday.music;

import me.bobthe28th.birthday.games.GamePlayer;
import me.bobthe28th.birthday.Main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.SoundCategory;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MusicController {

    Main plugin;
    Music currentlyPlaying;
    boolean controlledByGame = false;

    BukkitTask musicLength;
    MusicQueue queue;

    List<Music> musicList = Arrays.asList(
            new Music("battle1",3380L),
            new Music("battle2",3280L),
            new Music("battle3",2480L),
            new Music("battle4",3240L),
            new Music("battle5",3000L),
            new Music("battle6",4980L),
            new Music("battle7",2560L),
            new Music("battle8",3300L),
            new Music("battle9",3100L),
            new Music("battle10",2100L),
            new Music("bonusround1",2320L),
            new Music("bonusround2",3140L),
            new Music("bonusround3",3360L),
            new Music("bonusround4",4120L),
            new Music("bonusround5",2460L),
            new Music("bonusround6",2460L),
            new Music("zombiefun",1640L)
    );

    public MusicController(Main plugin) {
        this.plugin = plugin;
        queue = new MusicQueue();
    }

    public void start() {
        if (currentlyPlaying == null) {
            currentlyPlaying = queue.nextInQueue();
            queue.advanceQueue();
        }
        if (musicLength == null || musicLength.isCancelled()) {
            playCurrent();
        }
    }

    public void playCurrent() {
        if (currentlyPlaying != null) {
            Bukkit.broadcastMessage(ChatColor.YELLOW + "Now playing: " + ChatColor.RED + currentlyPlaying.getName());
            for (GamePlayer p : plugin.getGamePlayers().values()) {
                p.getPlayer().playSound(p.getPlayer().getLocation(), currentlyPlaying.getName(), SoundCategory.MUSIC, 1f, 1);
            }
            musicLength = new BukkitRunnable() {
                @Override
                public void run() {
                    if (!this.isCancelled()) {
                        playNext();
                    }
                }
            }.runTaskLater(plugin,currentlyPlaying.getLength() + 20L);
        }
    }

    public void playNext() {
//        stopCurrent();
        currentlyPlaying = queue.nextInQueue();
        playCurrent();
        queue.advanceQueue();
    }

    public void stopCurrent() {
        if (musicLength != null) {
            musicLength.cancel();
        }
        if (currentlyPlaying != null) {
            for (GamePlayer p : plugin.getGamePlayers().values()) {
                p.getPlayer().stopSound(currentlyPlaying.getName(),SoundCategory.MUSIC);
                musicLength.cancel();
            }
        }
    }

    public boolean isControlledByGame() {
        return controlledByGame;
    }

    public List<Music> getMusicList() {
        return musicList;
    }

    public List<String> getMusicNameList() {
        List<String> nameList = new ArrayList<>();
        for (Music m : musicList) {
            nameList.add(m.getName());
        }
        return nameList;
    }

    public Music getCurrentlyPlaying() {
        return currentlyPlaying;
    }

    public String getCurrentlyPlayingName() {
        if (currentlyPlaying == null) {
            return null;
        }
        return currentlyPlaying.getName();
    }

    public Music getMusicByName(String name) {
        for (Music m : musicList) {
            if (m.getName().equals(name)) {
                return m;
            }
        }
        return null;
    }

    public MusicQueue getQueue() {
        return queue;
    }

    public void disable() {
        stopCurrent();
    }
}
