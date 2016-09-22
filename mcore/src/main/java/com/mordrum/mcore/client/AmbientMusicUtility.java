package com.mordrum.mcore.client;

import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.ambience.Ambience;
import vazkii.ambience.PlayerThread;
import vazkii.ambience.SongLoader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.function.Consumer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@SideOnly(Side.CLIENT)
public class AmbientMusicUtility {
    private static Path musicPath = Paths.get(System.getProperty("user.dir") + "/ambience_music/ambience.properties");
    private static URL musicURL;

    static {
        try {
            musicURL = new URL("http://solder.mordrum.com/mods/ambient-music/ambient-music-pokemon.zip");
        } catch (MalformedURLException ignored) {
        }
    }

    public static boolean isMusicPresent() {
        try {
            Properties props = new Properties();
            if (!Files.exists(musicPath)) return false;

            props.load(Files.newInputStream(musicPath));
            return Boolean.valueOf(props.getProperty("enabled"));
        } catch (IOException e) {
            e.printStackTrace();
            return true;
        }
    }

    public static Thread downloadMusic(Consumer<String> progressCallback, Runnable onComplete) {
        Runnable downloadThread = () -> {
            try {
                HttpURLConnection urlConnection = (HttpURLConnection) musicURL.openConnection();

                ZipInputStream zipInputStream = new ZipInputStream(urlConnection.getInputStream());
                ZipEntry nextEntry;
                while ((nextEntry = zipInputStream.getNextEntry()) != null) {
                    if (nextEntry.isDirectory()) {
                        new File(System.getProperty("user.dir") + "/" + nextEntry.getName()).mkdirs();
                        continue;
                    }
                    progressCallback.accept(nextEntry.getName());
                    FileOutputStream fos = new FileOutputStream(System.getProperty("user.dir") + "/" + nextEntry.getName());
                    final byte[] buffer = new byte[102400]; //TODO this can probably be optimized, take a look at MM codebase

                    int len;
                    while((len = (zipInputStream.read(buffer))) > 0) {
                        fos.write(buffer, 0, len);
                    }
                    fos.close();
                }
                zipInputStream.close();

                Minecraft.getMinecraft().addScheduledTask(() -> {
                    SongLoader.loadFrom(musicPath.toFile().getParentFile());
                    Ambience.thread = new PlayerThread();
                    onComplete.run();
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        };
        Thread thread = new Thread(downloadThread);
        thread.setName("Music Download Thread");
        thread.start();
        return thread;
    }
}
