package com.dimeh.cassebrique.lwjgl3;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.dimeh.cassebrique.CasseBriqueMain;
import com.dimeh.cassebrique.config.GameConfig;

/** Launches the desktop (LWJGL3) application. */
public class Lwjgl3Launcher {
    public static void main(String[] args) {
        // StartupHelper causes sealing violation in IntelliJ on Windows.
        // Since user path is standard ASCII, we can skip it.
        // if (StartupHelper.startNewJvmIfRequired()) return;
        createApplication();
    }

    private static Lwjgl3Application createApplication() {
        return new Lwjgl3Application(new CasseBriqueMain(), getDefaultConfiguration());
    }

    private static Lwjgl3ApplicationConfiguration getDefaultConfiguration() {
        Lwjgl3ApplicationConfiguration configuration = new Lwjgl3ApplicationConfiguration();
        configuration.setTitle("Casse Brique");
        configuration.useVsync(true);
        configuration.setForegroundFPS(60);
        configuration.setWindowedMode((int) GameConfig.WORLD_WIDTH, (int) GameConfig.WORLD_HEIGHT);
        // Window icon removed - files do not exist in assets
        return configuration;
    }
}
