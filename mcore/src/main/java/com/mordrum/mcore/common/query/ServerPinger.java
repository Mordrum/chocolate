package com.mordrum.mcore.common.query;

import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerAddress;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.network.EnumConnectionState;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.ServerStatusResponse;
import net.minecraft.network.handshake.client.C00Handshake;
import net.minecraft.network.status.INetHandlerStatusClient;
import net.minecraft.network.status.client.CPacketPing;
import net.minecraft.network.status.client.CPacketServerQuery;
import net.minecraft.network.status.server.SPacketPong;
import net.minecraft.network.status.server.SPacketServerInfo;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.List;

@SideOnly(Side.CLIENT)
public class ServerPinger {
    private static final Logger LOGGER = LogManager.getLogger();
    private final List<NetworkManager> pingDestinations = Collections.synchronizedList(Lists.newArrayList());

    public void ping(final ServerData server) throws UnknownHostException {
        ServerAddress serveraddress = ServerAddress.fromString(server.serverIP);
        final NetworkManager networkmanager = NetworkManager.createNetworkManagerAndConnect(InetAddress.getByName(serveraddress.getIP()), serveraddress.getPort(), false);
        this.pingDestinations.add(networkmanager);
        server.serverMOTD = "Pinging...";
        server.pingToServer = -1L;
        server.playerList = null;
        networkmanager.setNetHandler(new INetHandlerStatusClient() {
            private boolean successful;
            private boolean receivedStatus;
            private long pingSentAt;

            public void handleServerInfo(SPacketServerInfo packetIn) {
                if (this.receivedStatus) {
                    networkmanager.closeChannel(new TextComponentString("Received unrequested status"));
                } else {
                    this.receivedStatus = true;
                    ServerStatusResponse serverstatusresponse = packetIn.getResponse();

                    if (serverstatusresponse.getServerDescription() != null) {
                        server.serverMOTD = serverstatusresponse.getServerDescription().getFormattedText();
                    } else {
                        server.serverMOTD = "";
                    }

                    if (serverstatusresponse.getVersion() != null) {
                        server.gameVersion = serverstatusresponse.getVersion().getName();
                        server.version = serverstatusresponse.getVersion().getProtocol();
                    } else {
                        server.gameVersion = "Old";
                        server.version = 0;
                    }

                    if (serverstatusresponse.getPlayers() != null) {
                        server.populationInfo = serverstatusresponse.getPlayers().getOnlinePlayerCount() + "/" + serverstatusresponse.getPlayers().getMaxPlayers();

                        if (ArrayUtils.isNotEmpty(serverstatusresponse.getPlayers().getPlayers())) {
                            StringBuilder stringbuilder = new StringBuilder();

                            for (GameProfile gameprofile : serverstatusresponse.getPlayers().getPlayers()) {
                                if (stringbuilder.length() > 0) {
                                    stringbuilder.append("\n");
                                }

                                stringbuilder.append(gameprofile.getName());
                            }

                            if (serverstatusresponse.getPlayers().getPlayers().length < serverstatusresponse.getPlayers().getOnlinePlayerCount()) {
                                if (stringbuilder.length() > 0) {
                                    stringbuilder.append("\n");
                                }

                                stringbuilder.append("... and ").append(serverstatusresponse.getPlayers().getOnlinePlayerCount() - serverstatusresponse.getPlayers().getPlayers().length).append(" more ...");
                            }

                            server.playerList = stringbuilder.toString();
                        }
                    } else {
                        server.populationInfo = TextFormatting.DARK_GRAY + "???";
                    }

                    if (serverstatusresponse.getFavicon() != null) {
                        String s = serverstatusresponse.getFavicon();

                        if (s.startsWith("data:image/png;base64,")) {
                            server.setBase64EncodedIconData(s.substring("data:image/png;base64,".length()));
                        } else {
                            ServerPinger.LOGGER.error("Invalid server icon (unknown format)");
                        }
                    } else {
                        server.setBase64EncodedIconData(null);
                    }

                    this.pingSentAt = Minecraft.getSystemTime();
                    networkmanager.sendPacket(new CPacketPing(this.pingSentAt));
                    this.successful = true;
                }
            }

            public void handlePong(SPacketPong packetIn) {
                long i = this.pingSentAt;
                long j = Minecraft.getSystemTime();
                server.pingToServer = j - i;
                networkmanager.closeChannel(new TextComponentString("Finished"));
            }

            /**
             * Invoked when disconnecting, the parameter is a ChatComponent describing the reason for termination
             */
            public void onDisconnect(ITextComponent reason) {
                if (!this.successful) {
                    ServerPinger.LOGGER.error("Can\'t ping {}: {}", server.serverIP, reason.getUnformattedText());
                    server.serverMOTD = TextFormatting.DARK_RED + "Can\'t connect to server.";
                    server.populationInfo = "";
                }
            }
        });

        try {
            networkmanager.sendPacket(new C00Handshake(210, serveraddress.getIP(), serveraddress.getPort(), EnumConnectionState.STATUS, true));
            networkmanager.sendPacket(new CPacketServerQuery());
        } catch (Throwable throwable) {
            LOGGER.error(throwable);
        }
    }

}