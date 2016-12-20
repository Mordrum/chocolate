package com.modmountain.gameseer;

import com.modmountain.gameseer.networking.CloudLink;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.URISyntaxException;
import java.util.Scanner;

public class Gameseer {
	/**
	 * Initialize the connection
	 * @param inputStream
	 * @param errorStream
	 * @param outputStream
	 * @param commandRunner
	 */
	public static void initialize(InputStream inputStream, InputStream errorStream, OutputStream outputStream, CommandRunner commandRunner) {
		inheritIO(inputStream, System.out);
		inheritIO(errorStream, System.err);

		try {
			new CloudLink(commandRunner);
		} catch (URISyntaxException | IOException e) {
			e.printStackTrace();
		}
	}

	private static void inheritIO(final InputStream src, final PrintStream dest) {
		new Thread(() -> {
			Scanner sc = new Scanner(src);
			while (sc.hasNextLine()) {
				dest.println(sc.nextLine());
			}
		}).start();
	}

	enum Commands {
		START, STOP, EXECUTE
	}
}
