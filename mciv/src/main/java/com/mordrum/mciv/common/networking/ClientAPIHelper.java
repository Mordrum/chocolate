package com.mordrum.mciv.common.networking;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.async.Callback;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mordrum.mciv.common.Civilization;
import com.mordrum.mcore.MCore;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import scala.util.parsing.json.JSON;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.logging.Logger;

public class ClientAPIHelper {
	private static final Logger logger;

	static {
		Unirest.setDefaultHeader("accept", "application/json");
		Unirest.setDefaultHeader("content-type", "application/json");
		logger = Logger.getLogger("mciv.apihelper");
	}

	public static void findCivilizations(List<String> params, Consumer<List<Civilization>> consumer) {
		Unirest.get(MCore.API_URL + "/civilizations")
				.queryString(queryStringHelper(params))
				.asJsonAsync(new Callback<JsonNode>() {
					@Override
					public void completed(HttpResponse<JsonNode> response) {
						try {
							if (response.getStatus() == 200) {
								JSONArray data = response.getBody().getArray();
								List<Civilization> civs = new ArrayList<>();
								for (Object rawCiv : data) {
									JSONObject civJson = (JSONObject) rawCiv;
									long id = civJson.getLong("id");
									String name = civJson.getString("name");
									String banner = civJson.getString("banner");
									boolean inviteOnly = civJson.getBoolean("invite_only");

									List<UUID> players = new ArrayList<>();
									for (Object rawPlayer : civJson.getJSONArray("players")) {
										JSONObject playerJson = (JSONObject) rawPlayer;
										players.add(UUID.fromString(playerJson.getString("uuid")));
									}
									civs.add(new Civilization(id, name, banner, inviteOnly, players));
								}
								consumer.accept(civs);
							} else {
								logError("GET", "/civilizations", response);
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}

					@Override
					public void failed(UnirestException e) {
						e.printStackTrace();
					}

					@Override
					public void cancelled() {
						consumer.accept(Lists.newArrayList());
					}
				});
	}

	public static void getPlayerCivilization(UUID player, Consumer<Optional<Civilization>> consumer) {
		Unirest.get(MCore.API_URL + "/players/" + player.toString())
				.asJsonAsync(new Callback<JsonNode>() {
					@Override
					public void completed(HttpResponse<JsonNode> response) {
						try {
							if (response.getStatus() == 200) {
								JSONObject body = response.getBody().getObject();
								if (body.has("civilization")) {
									JSONObject civJson = body.getJSONObject("civilization");
									Civilization civilization = new Civilization(
											civJson.getLong("id"),
											civJson.getString("name"),
											civJson.getString("banner"),
											civJson.getBoolean("invite_only"),
											null);
									consumer.accept(Optional.of(civilization));
								} else {
									consumer.accept(Optional.empty());
								}
							} else if (response.getStatus() == 404) {
								consumer.accept(Optional.empty());
							} else {
								logError("GET", "/players/" + player.toString(), response);
								consumer.accept(Optional.empty());
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}

					@Override
					public void failed(UnirestException e) {
						e.printStackTrace();
					}

					@Override
					public void cancelled() {
						consumer.accept(Optional.empty());
					}
				});
	}

	public static void getChunksForCivilization(long civilizationId, Consumer<JSONArray> consumer) {
		Unirest.get(MCore.API_URL + "/chunks")
				.queryString("civilization.id", civilizationId)
				.asJsonAsync(new Callback<JsonNode>() {
					@Override
					public void completed(HttpResponse<JsonNode> response) {
						if (response.getStatus() == 200) {
							consumer.accept(response.getBody().getArray());
						} else {
							logError("GET", "/chunks", response);
						}
					}

					@Override
					public void failed(UnirestException e) {

					}

					@Override
					public void cancelled() {

					}
				});
	}

	private static Map<String, Object> queryStringHelper(List<String> params) {
		if (params.size()%2 != 0) throw new RuntimeException("Query string parameter length must be an even number");

		HashMap<String, Object> queryMap = new HashMap<>(params.size()/2);
		for (int i = 0; i < params.size(); i += 2) {
			queryMap.put(params.get(i), params.get(i + 1));
		}
		return queryMap;
	}

	private static void logError(String method, String route, HttpResponse<JsonNode> response) {
		JSONObject object = response.getBody().getObject();
		logger.severe(String.format("Failed API call to '%s %s': %s", "GET", "/civilizations", object
				.getString("message")));
		logger.severe(object.getString("description"));
	}
}
