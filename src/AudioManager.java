import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * AudioManager - high-level persistent audio controller.
 * Wraps the legacy SoundManager and provides crossfade/fade, clear API for music vs SFX,
 * global volume, mute toggle and resource lifecycle.
 */
public class AudioManager {
    private static AudioManager instance;
    private final SoundManager soundManager;
    private float targetMusicVolume;
    private float targetSfxVolume;
    private javax.swing.Timer fadeTimer;
    private final Object fadeLock = new Object();

    private AudioManager() {
        this.soundManager = SoundManager.getInstance();
        this.targetMusicVolume = soundManager.getMusicVolume();
        this.targetSfxVolume = soundManager.getSfxVolume();
    }

    public static synchronized AudioManager getInstance() {
        if (instance == null) instance = new AudioManager();
        return instance;
    }

    // ---------------------- Music control ----------------------
    public float getMusicVolume() {
        return targetMusicVolume;
    }

    public void setMusicVolume(float v) {
        targetMusicVolume = Math.max(0f, Math.min(1f, v));
        soundManager.setMusicVolume(targetMusicVolume);
    }

    public void previewMusicVolume(float v) {
        float vv = Math.max(0f, Math.min(1f, v));
        soundManager.previewMusicVolume(vv);
    }

    public float getSfxVolume() {
        return targetSfxVolume;
    }

    public void setSfxVolume(float v) {
        targetSfxVolume = Math.max(0f, Math.min(1f, v));
        soundManager.setSfxVolume(targetSfxVolume);
    }

    public void previewSfxVolume(float v) {
        float vv = Math.max(0f, Math.min(1f, v));
        soundManager.previewSfxVolume(vv);
    }

    public void playSfx(String name) {
        // SFX should obey mute state
        if (soundManager.isMuted()) return;
        soundManager.playSound(name);
    }

    public void playSfxPreview() {
        soundManager.playSfxPreview();
    }

    public boolean isMuted() {
        return soundManager.isMuted();
    }

    public String getCurrentMusicType() {
        return soundManager.getCurrentMusicType();
    }

    public void toggleMute() {
        soundManager.toggleMute();
    }

    public void setMuted(boolean muted) {
        soundManager.setMuted(muted);
    }

    // start music immediately (no fade)
    public void startMusic(String musicType) {
        if (soundManager.isMuted()) return;
        String current = soundManager.getCurrentMusicType();
        if (musicType != null && musicType.equals(current) && soundManager.isMusicPlaying()) {
            soundManager.setMusicVolume(targetMusicVolume);
            return;
        }
        if (soundManager.isMusicPlaying() && current != null && !current.equals(musicType)) {
            crossfadeTo(musicType, 520);
            return;
        }
        soundManager.startBackgroundMusic(musicType);
        soundManager.setMusicVolume(targetMusicVolume);
    }

    public void fadeToMusic(String musicType, int durationMs) {
        crossfadeTo(musicType, durationMs);
    }

    // stop music immediately
    public void stopMusic() {
        soundManager.stopBackgroundMusic();
    }

    // Crossfade to a new music track over durationMs (simple two-stage fade)
    public void crossfadeTo(String musicType, int durationMs) {
        if (soundManager.isMuted()) {
            // if muted, simply switch without audible transition
            soundManager.startBackgroundMusic(musicType);
            soundManager.setMusicVolume(targetMusicVolume);
            return;
        }

        synchronized (fadeLock) {
            if (fadeTimer != null && fadeTimer.isRunning()) {
                fadeTimer.stop();
            }

            final int tickMs = 40;
            final int steps = Math.max(1, durationMs / tickMs);
            // Fade out current -> switch -> fade in
            final int half = Math.max(1, steps / 2);
            final float startVol = soundManager.getMusicVolume();
            final float midVol = 0f;

            final int[] step = {0};
            fadeTimer = new javax.swing.Timer(tickMs, null);
            fadeTimer.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    step[0]++;
                    if (step[0] <= half) {
                        float t = 1f - (step[0] / (float) half);
                        soundManager.setMusicVolume(startVol * t);
                    } else if (step[0] == half + 1) {
                        // switch track with zero volume
                        soundManager.startBackgroundMusic(musicType);
                        soundManager.setMusicVolume(0f);
                    } else if (step[0] <= steps) {
                        float t = (step[0] - half) / (float) (steps - half);
                        soundManager.setMusicVolume(targetMusicVolume * t);
                    }

                    if (step[0] >= steps) {
                        fadeTimer.stop();
                    }
                }
            });
            fadeTimer.start();
        }
    }

    // Convenience: short crossfade
    public void crossfadeTo(String musicType) {
        crossfadeTo(musicType, 700);
    }

    // Proxy to dynamic intensity control in existing manager
    public void setMusicIntensity(SoundManager.MusicIntensity intensity) {
        soundManager.setMusicIntensity(intensity);
    }

    // Clean up resources. After calling dispose the AudioManager should not be used.
    public void dispose() {
        synchronized (fadeLock) {
            if (fadeTimer != null && fadeTimer.isRunning()) fadeTimer.stop();
        }
        soundManager.stopAllSounds();
        try {
            soundManager.release();
        } catch (Exception ignored) {}
    }
}

