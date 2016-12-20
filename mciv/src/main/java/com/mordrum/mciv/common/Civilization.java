package com.mordrum.mciv.common;

import java.util.List;
import java.util.UUID;

public class Civilization {
	private final long id;
	private final String name;
	private final List<UUID> players;
	private boolean inviteOnly;
	private final String bannerId;

	public Civilization(long id, String name, String bannerId, boolean inviteOnly, List<UUID> players) {
		this.id = id;
		this.name = name;
		this.players = players;
		this.inviteOnly = inviteOnly;
		this.bannerId = bannerId;
	}

	public long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public List<UUID> getPlayers() {
		return players;
	}

	public boolean isInviteOnly() {
		return inviteOnly;
	}

	public void setInviteOnly(boolean inviteOnly) {
		this.inviteOnly = inviteOnly;
	}

	public String getBanner() {
		return bannerId;
	}
}
