package com.audtream.desktop.manager;

import com.audtream.desktop.model.dto.PlaylistDTO;
import com.audtream.desktop.model.dto.TrackDTO;
import com.audtream.desktop.model.event.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PlayerStateManager {
    private static PlayerStateManager instance;
    private TrackDTO currentTrack;
    private List<TrackDTO> queue;
    private int currentQueueIndex;
    private boolean isPlaying;
    private boolean isShuffle;
    private LoopMode loopMode;
    private double volume;
    private double currentTime;
    private PlaylistDTO currentPlaylist;

    public enum LoopMode {
        OFF, ONE, ALL
    }

    private PlayerStateManager() {
        this.queue = new ArrayList<>();
        this.currentQueueIndex = -1;
        this.isPlaying = false;
        this.isShuffle = false;
        this.loopMode = LoopMode.OFF;
        this.volume = 0.5;
        this.currentTime = 0.0;
    }

    public static PlayerStateManager getInstance() {
        if (instance == null) {
            instance = new PlayerStateManager();
        }
        return instance;
    }

    public void playTrack(TrackDTO track) {
        boolean wasPlaying = isPlaying;
        TrackDTO previousTrack = currentTrack;

        currentTrack = track;
        isPlaying = true;
        currentTime = 0.0;

        EventBus.getInstance().publish(new TrackChangedEvent(track, previousTrack));
        if (!wasPlaying) {
            EventBus.getInstance().publish(new PlaybackStateChangedEvent(true));
        }
    }

    public void playTrackFromQueue(int index) {
        if (index >= 0 && index < queue.size()) {
            currentQueueIndex = index;
            playTrack(queue.get(index));
        }
    }

    public void setQueue(List<TrackDTO> tracks, int startIndex) {
        queue.clear();
        queue.addAll(tracks);
        currentQueueIndex = startIndex;
        if (startIndex >= 0 && startIndex < tracks.size()) {
            playTrack(tracks.get(startIndex));
        }
    }

    public void setQueueFromPlaylist(PlaylistDTO playlist, int startIndex) {
        currentPlaylist = playlist;
        setQueue(playlist.getTracks(), startIndex);
    }

    public void addToQueue(TrackDTO track) {
        queue.add(track);
        EventBus.getInstance().publish(new QueueChangedEvent(new ArrayList<>(queue)));
    }

    public void addToQueueNext(TrackDTO track) {
        if (currentQueueIndex >= 0 && currentQueueIndex < queue.size() - 1) {
            queue.add(currentQueueIndex + 1, track);
        } else {
            queue.add(track);
        }
        EventBus.getInstance().publish(new QueueChangedEvent(new ArrayList<>(queue)));
    }

    public void removeFromQueue(int index) {
        if (index >= 0 && index < queue.size()) {
            queue.remove(index);
            if (index < currentQueueIndex) {
                currentQueueIndex--;
            }
            EventBus.getInstance().publish(new QueueChangedEvent(new ArrayList<>(queue)));
        }
    }

    public void clearQueue() {
        queue.clear();
        currentQueueIndex = -1;
        EventBus.getInstance().publish(new QueueChangedEvent(new ArrayList<>()));
    }

    public void playNext() {
        if (queue.isEmpty()) return;

        if (loopMode == LoopMode.ONE) {
            playTrack(currentTrack);
            return;
        }

        int nextIndex = currentQueueIndex + 1;
        if (nextIndex >= queue.size()) {
            if (loopMode == LoopMode.ALL) {
                nextIndex = 0;
            } else {
                stop();
                return;
            }
        }
        playTrackFromQueue(nextIndex);
    }

    public void playPrevious() {
        if (queue.isEmpty()) return;

        if (currentTime > 3.0) {
            currentTime = 0.0;
            EventBus.getInstance().publish(new PlaybackTimeChangedEvent(0.0));
            return;
        }

        int prevIndex = currentQueueIndex - 1;
        if (prevIndex < 0) {
            if (loopMode == LoopMode.ALL) {
                prevIndex = queue.size() - 1;
            } else {
                prevIndex = 0;
            }
        }
        playTrackFromQueue(prevIndex);
    }

    public void togglePlayPause() {
        isPlaying = !isPlaying;
        if (isPlaying) {
            com.audtream.desktop.service.AudioPlayerService.getInstance().play();
        } else {
            com.audtream.desktop.service.AudioPlayerService.getInstance().pause();
        }
        EventBus.getInstance().publish(new PlaybackStateChangedEvent(isPlaying));
    }

    public void play() {
        if (!isPlaying) {
            isPlaying = true;
            com.audtream.desktop.service.AudioPlayerService.getInstance().play();
            EventBus.getInstance().publish(new PlaybackStateChangedEvent(true));
        }
    }

    public void pause() {
        if (isPlaying) {
            isPlaying = false;
            com.audtream.desktop.service.AudioPlayerService.getInstance().pause();
            EventBus.getInstance().publish(new PlaybackStateChangedEvent(false));
        }
    }

    public void stop() {
        isPlaying = false;
        currentTime = 0.0;
        com.audtream.desktop.service.AudioPlayerService.getInstance().stop();
        EventBus.getInstance().publish(new PlaybackStateChangedEvent(false));
        EventBus.getInstance().publish(new PlaybackTimeChangedEvent(0.0));
    }

    public void seek(double time) {
        currentTime = time;
        com.audtream.desktop.service.AudioPlayerService.getInstance().seek(time);
        EventBus.getInstance().publish(new PlaybackTimeChangedEvent(time));
    }

    public void setVolume(double volume) {
        this.volume = Math.max(0.0, Math.min(1.0, volume));
        com.audtream.desktop.service.AudioPlayerService.getInstance().setVolume(this.volume);
        EventBus.getInstance().publish(new VolumeChangedEvent(this.volume));
    }

    public void toggleShuffle() {
        isShuffle = !isShuffle;
        if (isShuffle && !queue.isEmpty()) {
            TrackDTO current = currentTrack;
            Collections.shuffle(queue);
            if (current != null) {
                currentQueueIndex = queue.indexOf(current);
            }
        }
        EventBus.getInstance().publish(new ShuffleChangedEvent(isShuffle));
    }

    public void cycleLoopMode() {
        loopMode = switch (loopMode) {
            case OFF -> LoopMode.ALL;
            case ALL -> LoopMode.ONE;
            case ONE -> LoopMode.OFF;
        };
        EventBus.getInstance().publish(new LoopModeChangedEvent(loopMode));
    }

    public TrackDTO getCurrentTrack() { return currentTrack; }
    public List<TrackDTO> getQueue() { return new ArrayList<>(queue); }
    public int getCurrentQueueIndex() { return currentQueueIndex; }
    public boolean isPlaying() { return isPlaying; }
    public boolean isShuffle() { return isShuffle; }
    public LoopMode getLoopMode() { return loopMode; }
    public double getVolume() { return volume; }
    public double getCurrentTime() { return currentTime; }
    public PlaylistDTO getCurrentPlaylist() { return currentPlaylist; }

    public void updateCurrentTime(double time) {
        this.currentTime = time;
    }
}