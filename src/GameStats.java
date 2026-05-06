import java.io.*;
import java.util.Properties;

public class GameStats {
    private int wins;
    private int losses;

    private final String path = System.getProperty("user.home") + "\\nsolo_stats.properties";

    public void load() {
        Properties p = new Properties();
        try (FileInputStream in = new FileInputStream(path)) {
            p.load(in);
            wins = Integer.parseInt(p.getProperty("wins", "0"));
            losses = Integer.parseInt(p.getProperty("losses", "0"));
        } catch (Exception ignored) {}
    }

    public void save() {
        Properties p = new Properties();
        p.setProperty("wins", String.valueOf(wins));
        p.setProperty("losses", String.valueOf(losses));

        try (FileOutputStream out = new FileOutputStream(path)) {
            p.store(out, "Stats");
        } catch (Exception ignored) {}
    }

    public void addWin() { wins++; save(); }
    public void addLoss() { losses++; save(); }

    public int getWins() { return wins; }
    public int getLosses() { return losses; }
}