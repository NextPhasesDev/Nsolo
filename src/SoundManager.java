import javax.sound.sampled.*;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SoundManager {
    private static SoundManager instance;
    private Map<String, Clip> soundClips;
    private Clip backgroundMusic;
    private boolean isMuted;
    private float volume;
    private String currentMusicType;

    // Music intensity levels for dynamic music
    public enum MusicIntensity { CALM, NORMAL, TENSE, INTENSE }
    private MusicIntensity currentIntensity = MusicIntensity.NORMAL;

    private SoundManager() {
        soundClips = new HashMap<>();
        isMuted = false;
        volume = 0.7f;
        loadSounds();
    }

    public static SoundManager getInstance() {
        if (instance == null) {
            instance = new SoundManager();
        }
        return instance;
    }

    private void loadSounds() {
        try {
            // === SOUND EFFECTS ===
            // Stone drop - warm wooden thud
            soundClips.put("drop", generateWoodenDrop());

            // Stone pickup - soft lift sound
            soundClips.put("pickup", generatePickupSound());

            // Capture sound - satisfying victory chime
            soundClips.put("capture", generateCaptureSound());

            // Move complete - gentle confirmation
            soundClips.put("complete", generateCompleteSound());

            // Invalid move - soft warning
            soundClips.put("invalid", generateInvalidSound());

            // Button click - crisp UI feedback
            soundClips.put("click", generateClickSound());

            // Win fanfare
            soundClips.put("win", generateWinFanfare());

            // Lose sound
            soundClips.put("lose", generateLoseSound());

            // === BACKGROUND MUSIC ===
            // Menu music - calm, welcoming African-inspired melody
            soundClips.put("menu_music", generateMenuMusic());

            // PvP music - rhythmic, balanced energy
            soundClips.put("pvp_music", generatePvPMusic());

            // AI Easy music - relaxed, peaceful
            soundClips.put("ai_easy_music", generateRelaxedMusic());

            // AI Hard music - tense, strategic
            soundClips.put("ai_hard_music", generateTenseMusic());

            // Intense music - for close games
            soundClips.put("intense_music", generateIntenseMusic());

        } catch (Exception e) {
            System.err.println("Error loading sounds: " + e.getMessage());
        }
    }

    // ==================== SOUND EFFECTS ====================

    private Clip generateWoodenDrop() throws LineUnavailableException, IOException {
        float sampleRate = 44100;
        int duration = 120;
        int samples = (int) (sampleRate * duration / 1000);
        byte[] buffer = new byte[samples * 2];

        for (int i = 0; i < samples; i++) {
            double t = i / sampleRate;
            double envelope = Math.exp(-t * 40); // Quick decay

            // Layered frequencies for wooden sound
            double sample = 0;
            sample += Math.sin(2 * Math.PI * 180 * t) * 0.5; // Low thump
            sample += Math.sin(2 * Math.PI * 320 * t) * 0.3; // Mid body
            sample += Math.sin(2 * Math.PI * 520 * t) * 0.15; // High click
            sample += (Math.random() - 0.5) * 0.1 * Math.exp(-t * 80); // Initial noise

            short s = (short) (sample * envelope * 32767 * 0.4);
            buffer[i * 2] = (byte) (s & 0xFF);
            buffer[i * 2 + 1] = (byte) ((s >> 8) & 0xFF);
        }
        return createClipFromBuffer(buffer, sampleRate, samples);
    }

    private Clip generatePickupSound() throws LineUnavailableException, IOException {
        float sampleRate = 44100;
        int duration = 80;
        int samples = (int) (sampleRate * duration / 1000);
        byte[] buffer = new byte[samples * 2];

        for (int i = 0; i < samples; i++) {
            double t = i / sampleRate;
            double envelope = Math.exp(-t * 35);
            double freqSlide = 400 + (600 * t * 10); // Upward pitch slide

            double sample = Math.sin(2 * Math.PI * freqSlide * t) * 0.6;
            sample += Math.sin(2 * Math.PI * freqSlide * 1.5 * t) * 0.2;

            short s = (short) (sample * envelope * 32767 * 0.3);
            buffer[i * 2] = (byte) (s & 0xFF);
            buffer[i * 2 + 1] = (byte) ((s >> 8) & 0xFF);
        }
        return createClipFromBuffer(buffer, sampleRate, samples);
    }

    private Clip generateCaptureSound() throws LineUnavailableException, IOException {
        float sampleRate = 44100;
        int duration = 400;
        int samples = (int) (sampleRate * duration / 1000);
        byte[] buffer = new byte[samples * 2];

        // Victory arpeggio: C5 - E5 - G5
        int[] notes = {523, 659, 784};
        int noteLength = samples / 3;

        for (int i = 0; i < samples; i++) {
            double t = i / sampleRate;
            int noteIndex = Math.min(i / noteLength, 2);
            double localT = (i % noteLength) / sampleRate;
            double envelope = Math.exp(-localT * 8) * Math.min(localT * 200, 1.0);

            double freq = notes[noteIndex];
            double sample = 0;
            sample += Math.sin(2 * Math.PI * freq * t) * 0.5;
            sample += Math.sin(2 * Math.PI * freq * 2 * t) * 0.2; // Octave harmonic
            sample += Math.sin(2 * Math.PI * freq * 3 * t) * 0.1; // Fifth harmonic

            short s = (short) (sample * envelope * 32767 * 0.35);
            buffer[i * 2] = (byte) (s & 0xFF);
            buffer[i * 2 + 1] = (byte) ((s >> 8) & 0xFF);
        }
        return createClipFromBuffer(buffer, sampleRate, samples);
    }

    private Clip generateCompleteSound() throws LineUnavailableException, IOException {
        float sampleRate = 44100;
        int duration = 150;
        int samples = (int) (sampleRate * duration / 1000);
        byte[] buffer = new byte[samples * 2];

        for (int i = 0; i < samples; i++) {
            double t = i / sampleRate;
            double envelope = Math.exp(-t * 15) * Math.min(t * 300, 1.0);

            // Pleasant major third
            double sample = Math.sin(2 * Math.PI * 440 * t) * 0.4;
            sample += Math.sin(2 * Math.PI * 554 * t) * 0.3; // E5

            short s = (short) (sample * envelope * 32767 * 0.3);
            buffer[i * 2] = (byte) (s & 0xFF);
            buffer[i * 2 + 1] = (byte) ((s >> 8) & 0xFF);
        }
        return createClipFromBuffer(buffer, sampleRate, samples);
    }

    private Clip generateInvalidSound() throws LineUnavailableException, IOException {
        float sampleRate = 44100;
        int duration = 200;
        int samples = (int) (sampleRate * duration / 1000);
        byte[] buffer = new byte[samples * 2];

        for (int i = 0; i < samples; i++) {
            double t = i / sampleRate;
            double envelope = Math.exp(-t * 12);

            // Dissonant minor second - sounds "wrong" but not harsh
            double sample = Math.sin(2 * Math.PI * 220 * t) * 0.4;
            sample += Math.sin(2 * Math.PI * 233 * t) * 0.3; // Dissonant

            short s = (short) (sample * envelope * 32767 * 0.25);
            buffer[i * 2] = (byte) (s & 0xFF);
            buffer[i * 2 + 1] = (byte) ((s >> 8) & 0xFF);
        }
        return createClipFromBuffer(buffer, sampleRate, samples);
    }

    private Clip generateClickSound() throws LineUnavailableException, IOException {
        float sampleRate = 44100;
        int duration = 40;
        int samples = (int) (sampleRate * duration / 1000);
        byte[] buffer = new byte[samples * 2];

        for (int i = 0; i < samples; i++) {
            double t = i / sampleRate;
            double envelope = Math.exp(-t * 100);
            double sample = Math.sin(2 * Math.PI * 1200 * t) * 0.5 + (Math.random() - 0.5) * 0.3 * envelope;

            short s = (short) (sample * envelope * 32767 * 0.2);
            buffer[i * 2] = (byte) (s & 0xFF);
            buffer[i * 2 + 1] = (byte) ((s >> 8) & 0xFF);
        }
        return createClipFromBuffer(buffer, sampleRate, samples);
    }

    private Clip generateWinFanfare() throws LineUnavailableException, IOException {
        float sampleRate = 44100;
        int duration = 1200;
        int samples = (int) (sampleRate * duration / 1000);
        byte[] buffer = new byte[samples * 2];

        // Triumphant fanfare: C-E-G-C (octave up)
        int[] notes = {523, 659, 784, 1047};
        int[] durations = {200, 200, 200, 400};

        int sampleIndex = 0;
        for (int n = 0; n < notes.length; n++) {
            int noteSamples = (int)(sampleRate * durations[n] / 1000);
            for (int i = 0; i < noteSamples && sampleIndex < samples; i++) {
                double t = i / sampleRate;
                double envelope = Math.exp(-t * 4) * Math.min(t * 200, 1.0);

                double sample = 0;
                sample += Math.sin(2 * Math.PI * notes[n] * t) * 0.4;
                sample += Math.sin(2 * Math.PI * notes[n] * 2 * t) * 0.2;
                sample += Math.sin(2 * Math.PI * notes[n] * 0.5 * t) * 0.2; // Sub octave

                short s = (short) (sample * envelope * 32767 * 0.4);
                buffer[sampleIndex * 2] = (byte) (s & 0xFF);
                buffer[sampleIndex * 2 + 1] = (byte) ((s >> 8) & 0xFF);
                sampleIndex++;
            }
        }
        return createClipFromBuffer(buffer, sampleRate, samples);
    }

    private Clip generateLoseSound() throws LineUnavailableException, IOException {
        float sampleRate = 44100;
        int duration = 800;
        int samples = (int) (sampleRate * duration / 1000);
        byte[] buffer = new byte[samples * 2];

        // Descending minor progression
        int[] notes = {392, 349, 330, 294}; // G4-F4-E4-D4 descending
        int noteLength = samples / 4;

        for (int i = 0; i < samples; i++) {
            double t = i / sampleRate;
            int noteIndex = Math.min(i / noteLength, 3);
            double localT = (i % noteLength) / sampleRate;
            double envelope = Math.exp(-localT * 6) * Math.min(localT * 150, 1.0);

            double freq = notes[noteIndex];
            double sample = Math.sin(2 * Math.PI * freq * t) * 0.5;
            sample += Math.sin(2 * Math.PI * freq * 0.5 * t) * 0.2; // Lower octave

            short s = (short) (sample * envelope * 32767 * 0.3);
            buffer[i * 2] = (byte) (s & 0xFF);
            buffer[i * 2 + 1] = (byte) ((s >> 8) & 0xFF);
        }
        return createClipFromBuffer(buffer, sampleRate, samples);
    }

    // ==================== BACKGROUND MUSIC ====================

    private Clip generateMenuMusic() throws LineUnavailableException, IOException {
        float sampleRate = 44100;
        int totalDuration = 16000; // 16 seconds loop
        int samples = (int) (sampleRate * totalDuration / 1000);
        byte[] buffer = new byte[samples * 2];

        // African-inspired pentatonic melody (A minor pentatonic: A C D E G)
        // Calm, welcoming feel
        int[] melody = {440, 523, 587, 659, 784, 659, 587, 523, 440, 392, 440, 523, 587, 523, 440, 392};
        int[] bass = {220, 220, 262, 262, 294, 294, 330, 330, 220, 220, 262, 262, 294, 294, 196, 196};
        int noteDuration = totalDuration / melody.length;

        int bufferIndex = 0;
        for (int noteIndex = 0; noteIndex < melody.length; noteIndex++) {
            int noteSamples = (int) (sampleRate * noteDuration / 1000);

            for (int i = 0; i < noteSamples && bufferIndex < samples; i++) {
                double t = i / sampleRate;
                double noteT = (double)i / noteSamples;

                // Smooth envelope
                double envelope = Math.sin(Math.PI * noteT) * 0.8 + 0.2;
                envelope *= Math.min(noteT * 20, 1.0); // Attack

                // Melody with soft timbre (sine + subtle harmonics)
                double melodyFreq = melody[noteIndex];
                double sample = Math.sin(2 * Math.PI * melodyFreq * t) * 0.25;
                sample += Math.sin(2 * Math.PI * melodyFreq * 2 * t) * 0.08;
                sample += Math.sin(2 * Math.PI * melodyFreq * 3 * t) * 0.03;

                // Soft bass pad
                double bassFreq = bass[noteIndex];
                sample += Math.sin(2 * Math.PI * bassFreq * t) * 0.15;
                sample += Math.sin(2 * Math.PI * bassFreq * 0.5 * t) * 0.08;

                // Subtle ambient pad (fifth harmony)
                sample += Math.sin(2 * Math.PI * melodyFreq * 1.5 * t) * 0.05;

                short s = (short) (sample * envelope * 32767 * 0.5);
                buffer[bufferIndex * 2] = (byte) (s & 0xFF);
                buffer[bufferIndex * 2 + 1] = (byte) ((s >> 8) & 0xFF);
                bufferIndex++;
            }
        }
        return createClipFromBuffer(buffer, sampleRate, samples);
    }

    private Clip generatePvPMusic() throws LineUnavailableException, IOException {
        float sampleRate = 44100;
        int totalDuration = 12000;
        int samples = (int) (sampleRate * totalDuration / 1000);
        byte[] buffer = new byte[samples * 2];

        // More energetic but balanced - suitable for competitive play
        int[] melody = {523, 587, 659, 784, 880, 784, 659, 587, 523, 659, 784, 659};
        int[] rhythm = {1, 1, 1, 2, 1, 1, 1, 2, 1, 1, 2, 2}; // Variable note lengths
        int baseNoteDuration = totalDuration / 16;

        int bufferIndex = 0;
        for (int noteIndex = 0; noteIndex < melody.length; noteIndex++) {
            int noteSamples = (int) (sampleRate * baseNoteDuration * rhythm[noteIndex] / 1000);

            for (int i = 0; i < noteSamples && bufferIndex < samples; i++) {
                double t = i / sampleRate;
                double noteT = (double)i / noteSamples;

                double envelope = Math.sin(Math.PI * noteT);
                envelope *= Math.min(noteT * 30, 1.0);

                double freq = melody[noteIndex];
                double sample = 0;

                // Brighter timbre for energy
                sample += Math.sin(2 * Math.PI * freq * t) * 0.3;
                sample += Math.sin(2 * Math.PI * freq * 2 * t) * 0.12;
                sample += Math.sin(2 * Math.PI * freq * 3 * t) * 0.05;

                // Rhythmic bass pulse
                double bassFreq = freq / 2;
                double bassEnv = Math.abs(Math.sin(2 * Math.PI * 2 * t));
                sample += Math.sin(2 * Math.PI * bassFreq * t) * 0.18 * bassEnv;

                short s = (short) (sample * envelope * 32767 * 0.45);
                buffer[bufferIndex * 2] = (byte) (s & 0xFF);
                buffer[bufferIndex * 2 + 1] = (byte) ((s >> 8) & 0xFF);
                bufferIndex++;
            }
        }
        return createClipFromBuffer(buffer, sampleRate, samples);
    }

    private Clip generateRelaxedMusic() throws LineUnavailableException, IOException {
        float sampleRate = 44100;
        int totalDuration = 20000; // Longer, slower loop
        int samples = (int) (sampleRate * totalDuration / 1000);
        byte[] buffer = new byte[samples * 2];

        // Very calm - perfect for easy AI (learning/casual play)
        // Uses slower tempo, softer dynamics, major key
        int[] melody = {392, 440, 494, 523, 494, 440, 392, 349, 392, 440, 494, 523, 587, 523, 494, 440};
        int noteDuration = totalDuration / melody.length;

        int bufferIndex = 0;
        for (int noteIndex = 0; noteIndex < melody.length; noteIndex++) {
            int noteSamples = (int) (sampleRate * noteDuration / 1000);

            for (int i = 0; i < noteSamples && bufferIndex < samples; i++) {
                double t = i / sampleRate;
                double noteT = (double)i / noteSamples;

                // Very smooth envelope
                double envelope = Math.sin(Math.PI * noteT) * 0.7 + 0.3;
                envelope *= Math.min(noteT * 15, 1.0);
                envelope *= (1.0 - noteT * 0.3); // Gentle fade

                double freq = melody[noteIndex];
                double sample = 0;

                // Pure, soft sine waves
                sample += Math.sin(2 * Math.PI * freq * t) * 0.25;
                sample += Math.sin(2 * Math.PI * freq * 0.5 * t) * 0.1; // Sub octave warmth

                // Ambient pad
                sample += Math.sin(2 * Math.PI * freq * 1.5 * t) * 0.06;
                sample += Math.sin(2 * Math.PI * 220 * t) * 0.08; // Drone

                short s = (short) (sample * envelope * 32767 * 0.4);
                buffer[bufferIndex * 2] = (byte) (s & 0xFF);
                buffer[bufferIndex * 2 + 1] = (byte) ((s >> 8) & 0xFF);
                bufferIndex++;
            }
        }
        return createClipFromBuffer(buffer, sampleRate, samples);
    }

    private Clip generateTenseMusic() throws LineUnavailableException, IOException {
        float sampleRate = 44100;
        int totalDuration = 10000;
        int samples = (int) (sampleRate * totalDuration / 1000);
        byte[] buffer = new byte[samples * 2];

        // Strategic tension - minor key, slightly unsettling but not overwhelming
        // D minor scale patterns
        int[] melody = {587, 659, 698, 784, 698, 659, 587, 523, 587, 698, 784, 880, 784, 698, 587, 523};
        int noteDuration = totalDuration / melody.length;

        int bufferIndex = 0;
        for (int noteIndex = 0; noteIndex < melody.length; noteIndex++) {
            int noteSamples = (int) (sampleRate * noteDuration / 1000);

            for (int i = 0; i < noteSamples && bufferIndex < samples; i++) {
                double t = i / sampleRate;
                double noteT = (double)i / noteSamples;
                double globalT = (double)bufferIndex / samples;

                double envelope = Math.sin(Math.PI * noteT) * 0.85 + 0.15;
                envelope *= Math.min(noteT * 25, 1.0);

                double freq = melody[noteIndex];
                double sample = 0;

                // Slightly edgier timbre
                sample += Math.sin(2 * Math.PI * freq * t) * 0.28;
                sample += Math.sin(2 * Math.PI * freq * 2 * t) * 0.1;
                sample += Math.sin(2 * Math.PI * freq * 3 * t) * 0.06;

                // Pulsing bass for tension
                double bassFreq = 147; // D2
                double pulse = 0.6 + 0.4 * Math.sin(2 * Math.PI * 1.5 * globalT * totalDuration / 1000);
                sample += Math.sin(2 * Math.PI * bassFreq * t) * 0.15 * pulse;

                // Subtle dissonance
                sample += Math.sin(2 * Math.PI * (freq * 1.06) * t) * 0.03;

                short s = (short) (sample * envelope * 32767 * 0.5);
                buffer[bufferIndex * 2] = (byte) (s & 0xFF);
                buffer[bufferIndex * 2 + 1] = (byte) ((s >> 8) & 0xFF);
                bufferIndex++;
            }
        }
        return createClipFromBuffer(buffer, sampleRate, samples);
    }

    private Clip generateIntenseMusic() throws LineUnavailableException, IOException {
        float sampleRate = 44100;
        int totalDuration = 8000; // Faster loop for urgency
        int samples = (int) (sampleRate * totalDuration / 1000);
        byte[] buffer = new byte[samples * 2];

        // High intensity for close games - faster, more dramatic
        int[] melody = {784, 880, 988, 1047, 988, 880, 784, 698, 784, 880, 988, 1047, 1175, 1047, 988, 880};
        int noteDuration = totalDuration / melody.length;

        int bufferIndex = 0;
        for (int noteIndex = 0; noteIndex < melody.length; noteIndex++) {
            int noteSamples = (int) (sampleRate * noteDuration / 1000);

            for (int i = 0; i < noteSamples && bufferIndex < samples; i++) {
                double t = i / sampleRate;
                double noteT = (double)i / noteSamples;

                // Punchy envelope
                double envelope = Math.pow(Math.sin(Math.PI * noteT), 0.7);
                envelope *= Math.min(noteT * 50, 1.0);

                double freq = melody[noteIndex];
                double sample = 0;

                // Bright, cutting timbre
                sample += Math.sin(2 * Math.PI * freq * t) * 0.25;
                sample += Math.sin(2 * Math.PI * freq * 2 * t) * 0.12;
                sample += Math.sin(2 * Math.PI * freq * 3 * t) * 0.08;
                sample += Math.sin(2 * Math.PI * freq * 4 * t) * 0.04;

                // Driving bass
                double bassFreq = 196; // G2
                double bassRhythm = Math.abs(Math.sin(2 * Math.PI * 4 * t));
                sample += Math.sin(2 * Math.PI * bassFreq * t) * 0.2 * bassRhythm;

                // Octave doubling for power
                sample += Math.sin(2 * Math.PI * freq * 0.5 * t) * 0.1;

                short s = (short) (sample * envelope * 32767 * 0.55);
                buffer[bufferIndex * 2] = (byte) (s & 0xFF);
                buffer[bufferIndex * 2 + 1] = (byte) ((s >> 8) & 0xFF);
                bufferIndex++;
            }
        }
        return createClipFromBuffer(buffer, sampleRate, samples);
    }

    // ==================== UTILITY METHODS ====================

    private Clip createClipFromBuffer(byte[] buffer, float sampleRate, int samples) throws LineUnavailableException, IOException {
        AudioFormat format = new AudioFormat(sampleRate, 16, 1, true, false);
        ByteArrayInputStream bais = new ByteArrayInputStream(buffer);
        AudioInputStream ais = new AudioInputStream(bais, format, samples);

        Clip clip = AudioSystem.getClip();
        clip.open(ais);
        return clip;
    }

    public void playSound(String soundName) {
        if (isMuted) return;

        Clip clip = soundClips.get(soundName);
        if (clip != null) {
            clip.setFramePosition(0);
            clip.start();
        }
    }

    public void startBackgroundMusic(String musicType) {
        stopBackgroundMusic();
        if (isMuted) return;

        currentMusicType = musicType;
        Clip music = soundClips.get(musicType);
        if (music != null) {
            backgroundMusic = music;
            backgroundMusic.setFramePosition(0);
            backgroundMusic.loop(Clip.LOOP_CONTINUOUSLY);
        }
    }

    public void stopBackgroundMusic() {
        if (backgroundMusic != null && backgroundMusic.isRunning()) {
            backgroundMusic.stop();
        }
    }

    /**
     * Switch to intense music when game is close
     * Call this when score difference is small near end of game
     */
    public void setMusicIntensity(MusicIntensity intensity) {
        if (intensity == currentIntensity) return;
        currentIntensity = intensity;

        if (intensity == MusicIntensity.INTENSE) {
            startBackgroundMusic("intense_music");
        }
        // Can extend this for more dynamic music switching
    }

    public void toggleMute() {
        isMuted = !isMuted;
        if (isMuted) {
            stopBackgroundMusic();
        } else if (currentMusicType != null) {
            startBackgroundMusic(currentMusicType);
        }
    }

    public boolean isMuted() {
        return isMuted;
    }

    public void setMuted(boolean muted) {
        this.isMuted = muted;
        if (muted) {
            stopBackgroundMusic();
        } else if (currentMusicType != null) {
            startBackgroundMusic(currentMusicType);
        }
    }

    public String getCurrentMusicType() {
        return currentMusicType;
    }
}

