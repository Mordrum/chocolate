package com.modmountain.gameseer;

import java.util.Optional;

public interface CommandRunner {
	public Optional<String> start(String... args) throws CommandNotSupportedException;
	public Optional<String> stop(String... args) throws CommandNotSupportedException;
	public Optional<String> execute(String... args) throws CommandNotSupportedException;
}
