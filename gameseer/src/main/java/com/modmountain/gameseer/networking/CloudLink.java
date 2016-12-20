package com.modmountain.gameseer.networking;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.modmountain.gameseer.CommandRunner;
import org.apache.commons.io.IOUtils;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_17;
import org.java_websocket.handshake.ServerHandshake;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

public class CloudLink {
	public CloudLink(CommandRunner commandRunner) throws URISyntaxException, IOException {
		Map<String, String> headers = new HashMap<>();
		String apiKey = IOUtils.toString(this.getClass().getResourceAsStream("/config.txt"));

		headers.put("Authorization", "Basic: " + apiKey);
		WebSocketClient socketClient = new WebSocketClient(new URI("ws://localhost:8080"), new Draft_17(), headers, 0) {
			@Override
			public void onOpen(ServerHandshake handshakedata) {
				System.out.println("Opened connection");
//				try {
//					JsonObject jsonObject = new JsonObject();
//					jsonObject.addProperty("event", "authenticate");
//					jsonObject.addProperty("api_key", apiKey);
//					this.send(jsonObject.toString());
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
			}

			@Override
			public void onMessage(String message) {
				System.out.println(message);
				JsonObject json = new JsonParser().parse(message).getAsJsonObject();
				if (json.get("event").getAsString().equalsIgnoreCase("authenticate")) {

				}
			}

			@Override
			public void onClose(int code, String reason, boolean remote) {
				System.out.println("Closed: " + reason);
			}

			@Override
			public void onError(Exception ex) {
				ex.printStackTrace();
			}
		};
		socketClient.connect();


//		socket.on("start", args -> {
//			try {
//				commandRunner.start();
//			} catch (CommandNotSupportedException e) {
//				socket.emit("error", "The start command is not supported");
//			}
//		});
//		socket.on("stop", args -> {
//			try {
//				commandRunner.stop();
//			} catch (CommandNotSupportedException e) {
//				socket.emit("error", "The stop command is not supported");
//			}
//		});
//		socket.on("execute", args -> {
//			try {
//				commandRunner.execute();
//			} catch (CommandNotSupportedException e) {
//				socket.emit("error", "The execute command is not supported");
//			}
//		});
//		socket.connect();
	}

	private static String convertStreamToString(java.io.InputStream is) {
		java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
		return s.hasNext() ? s.next() : "";
	}

}
