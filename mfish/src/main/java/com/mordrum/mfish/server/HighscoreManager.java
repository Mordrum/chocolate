package com.mordrum.mfish.server;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.async.Callback;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mordrum.mcore.MCore;
import com.mordrum.mfish.common.Fish;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.json.JSONObject;

import java.text.DecimalFormat;

class HighscoreManager {
    private static final StringBuilder sb = new StringBuilder();
    private static final DecimalFormat decimalFormat = new DecimalFormat("#,###.##");

    static void checkHighscore(EntityPlayer player, Fish fish, double weight) {
        if (player.getEntityWorld().isRemote) return;

        Unirest.post(MCore.API_URL + "/fishing")
                .header("accept", "application/json")
                .header("Content-Type", "application/json")
                .body(new JSONObject().put("player", player.getUniqueID()).put("fish", fish.getMetadata()).put("weight", weight))
                .asJsonAsync(new Callback<JsonNode>() {
                    @Override
                    public void completed(HttpResponse<JsonNode> response) {
                        if (response.getBody().getObject().getBoolean("isHighscore")) {
                            sb.setLength(0);
                            sb.append(player.getName())
                                    .append(" just caught a record breaking ")
                                    .append(decimalFormat.format(weight))
                                    .append("lb ")
                                    .append(fish.getName())
                                    .append("!");
                            TextComponentString component = new TextComponentString(sb.toString());
                            for (EntityPlayerMP entityPlayerMP : player.getServer().getPlayerList().getPlayers()) {
                                entityPlayerMP.sendMessage(component);
                            }
                        } else {
                            player.sendMessage(new TextComponentString("You caught a " + decimalFormat.format(weight) + "lb " + fish.getName()));
                        }
                    }

                    @Override
                    public void failed(UnirestException e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void cancelled() {

                    }
                });
    }
}
