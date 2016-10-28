/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bl;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

/**
 *
 * @author tommi
 */
public class MultiPingConfig {

    private static final Properties DEFAULT;
    private static Properties loadedConfig;

    static {
        DEFAULT = new Properties();
        DEFAULT.setProperty("interval", "5000");
    }

    public static Properties getConfig() throws IOException {
        if (loadedConfig == null) {
            File f = new File(System.getProperty("user.dir") + File.separator + "config.cfg");
            if (!f.exists()) {
                f.createNewFile();
                try (BufferedWriter bw = new BufferedWriter(new FileWriter(f))) {
                    DEFAULT.store(bw, "The config for multiping");
                }
                return loadedConfig = new Properties(DEFAULT);
            } else {
                try (FileInputStream fis = new FileInputStream(f)) {
                    loadedConfig = new Properties();
                    loadedConfig.load(fis);
                }
                return loadedConfig;
            }
        } else {
            return loadedConfig;
        }
    }
}
