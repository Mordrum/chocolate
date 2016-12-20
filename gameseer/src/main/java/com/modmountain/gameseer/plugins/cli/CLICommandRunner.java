package com.modmountain.gameseer.plugins.cli;

import com.modmountain.gameseer.CommandNotSupportedException;
import com.modmountain.gameseer.CommandRunner;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Arrays;
import java.util.Optional;

public class CLICommandRunner implements CommandRunner {
	private Process process;
	private final ProcessBuilder processBuilder;
	private BufferedWriter bufferedWriter;

	public CLICommandRunner(ProcessBuilder processBuilder, Process process) {
		this.process = process;
		this.processBuilder = processBuilder;
		bufferedWriter = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
	}

	@Override
	public Optional<String> start(String... args) throws CommandNotSupportedException {
		if (process.isAlive()) {
			return Optional.of("Server already started");
		} else {
			try {
				this.process = processBuilder.start();
				this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
				return Optional.of("Server started");
			} catch (IOException e) {
				e.printStackTrace();
				return Optional.of("Error: " + e.getMessage());

			}
		}
	}

	@Override
	public Optional<String> stop(String... args) throws CommandNotSupportedException {
		if (!process.isAlive()) return Optional.of("Server not started");

		try {
			bufferedWriter.write("/stop\n");
			bufferedWriter.flush();
			return Optional.of("Stopping server");
		} catch (IOException e) {
			e.printStackTrace();
			return Optional.of("Error: " + e.getMessage());
		}
	}

	@Override
	public Optional<String> execute(String... args) throws CommandNotSupportedException {
		if (!process.isAlive()) return Optional.of("Server not started");

		StringBuilder stringBuilder = new StringBuilder(args.length * 2 + 1);
		for (String arg : args) {
			stringBuilder.append(arg);
			stringBuilder.append(" ");
		}
		stringBuilder.append("\n");

		try {
			bufferedWriter.write(stringBuilder.toString());
			bufferedWriter.flush();
			return Optional.of("Executed command '" + stringBuilder.toString() + "'");
		} catch (IOException e) {
			e.printStackTrace();
			return Optional.of("Error: " + e.getMessage());
		}
	}
}
