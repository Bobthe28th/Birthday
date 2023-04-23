package me.bobthe28th.birthday.music;

import me.bobthe28th.birthday.Main;

import java.util.ArrayList;
import java.util.List;

public class MusicQueue {

    List<Music> queue = new ArrayList<>();
    List<Music> loopQueue = new ArrayList<>();
    int loopIndex = 0;

    public void addQueue(Music music) {
        queue.add(music);
    }

    public void addQueueStart(Music music) {
        queue.add(0,music);
    }

    public void addLoopQueue(Music music) {
        loopQueue.add(music);
    }

    public void addQueue(List<Music> music) {
        queue.addAll(music);
    }

    public void addQueueStart(List<Music> music) {
        queue.addAll(0,music);
    }

    public void addLoopQueue(List<Music> music) {
        loopQueue.addAll(music);
    }

    public Music nextInQueue() {
        if (queue.size() > 0) {
            return queue.get(0);
        } else if (loopQueue.size() > 0) {
            return loopQueue.get(loopIndex);
        } else {
            return null;
        }
    }

    public void advanceQueue() {
        if (queue.size() > 0) {
            queue.remove(0);
        } else if (loopQueue.size() > 0) {
            loopIndex ++;
            loopIndex %= loopQueue.size();
        }
    }

    public void clearQueue() {
        Main.musicController.stopCurrent();
        queue.clear();
        loopIndex = 0; //todo clear loop queue
        loopQueue.clear();
    }

    public List<Music> getQueue() {
        return queue;
    }

    public List<Music> getLoopQueue() {
        return loopQueue;
    }

    public List<String> getQueueName() {
        List<String> nameList = new ArrayList<>();
        for (Music m : queue) {
            nameList.add(m.getName());
        }
        return nameList;
    }

    public List<String> getLoopQueueName() {
        List<String> nameList = new ArrayList<>();
        for (Music m : loopQueue) {
            nameList.add(m.getName());
        }
        return nameList;
    }
}
