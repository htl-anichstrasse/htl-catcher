package tirol.htlanichstrasse.htlcatcher.game.logos;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import lombok.Getter;

public class LogoModeManager {
    private static LogoModeManager INSTANCE;
    private Map<Mode, Long> currentModes = new ConcurrentHashMap<>();

    public static LogoModeManager getInstance() {
        if (LogoModeManager.INSTANCE == null) {
            LogoModeManager.INSTANCE = new LogoModeManager();
        }
        return LogoModeManager.INSTANCE;
    }

    private LogoModeManager() {
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(this::updateCurrentModes, 0, 1, TimeUnit.SECONDS);
    }

    public void reset() {
        currentModes.clear();
    }

    public void enableMode(Mode mode) {
        if (!currentModes.containsKey(mode)) {
            currentModes.remove(mode);
        }
        currentModes.put(mode, System.currentTimeMillis());
    }

    public void disableMode(Mode mode) {
        currentModes.remove(mode);
    }

    public long getStartForMode(Mode mode) {
        if (currentModes.containsKey(mode)) {
            return Objects.requireNonNull(currentModes.get(mode));
        }
        return 0L;
    }

    public Collection<Mode> getCurrentModes() {
        updateCurrentModes();
        return currentModes.keySet();
    }

    private void updateCurrentModes() {
        for (Map.Entry<Mode, Long> entry : currentModes.entrySet()) {
            if (entry.getValue() + entry.getKey().getDuration() < System.currentTimeMillis()) {
                currentModes.remove(entry.getKey());
            }
        }
    }

    @Getter
    public enum Mode {
        SHIELD(TimeUnit.SECONDS.toMillis(30)),
        INVERT(TimeUnit.SECONDS.toMillis(3)),
        MULTIPLIER(TimeUnit.SECONDS.toMillis(40));
        /**
         * The duration in milliseconds
         */
        private long duration;

        Mode(long duration) {
            this.duration = duration;
        }
    }
}
