package com.mordrum.mcore.client;

import com.google.gson.Gson;
import com.mordrum.mcore.client.character.CharacterBackground;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CharacterManager {
    private static CharacterManager ourInstance = new CharacterManager();

    public static CharacterManager getInstance() {
        return ourInstance;
    }

    private final Map<Integer, CharacterBackground> characterBackgroundMap;

    private CharacterManager() {
        characterBackgroundMap = new HashMap<>();

        try {
            loadCharacterBackgrounds();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //TODO load from code
    private void loadCharacterBackgrounds() throws IOException {
        IResourceManager resourceManager = Minecraft.getMinecraft().getResourceManager();
//        List<IResource> backgroundResources = resourceManager.getAllResources(new ResourceLocation("mcore", "character/backgrounds/"));
        Gson gson = new Gson();

//        for (IResource backgroundResource : backgroundResources) {
//            JsonObject jsonElement = gson.fromJson(new BufferedReader(new InputStreamReader(backgroundResource.getInputStream())), JsonObject.class);
//            CharacterBackground characterBackground = new CharacterBackground(
//                    jsonElement.get("id").getAsInt(),
//                    jsonElement.get("title").getAsString(),
//                    jsonElement.get("description").getAsString(),
//                    jsonElement.get("perkDescription").getAsString(),
//                    jsonElement.get("heirloomDescription").getAsString()
//            );
//            characterBackgroundMap.put(characterBackground.getId(), characterBackground);
//        }

        characterBackgroundMap.put(0, new CharacterBackground(0, "Brewmaster", "You were a brewmaster in the Old World, spending most of your time creating drinks, potions, and elixirs.", "Anything you brew is 10%% stronger", "A brewing stand"));
        characterBackgroundMap.put(1, new CharacterBackground(1, "Farmer", "You were a farmer in the Old World, responsible for tending to crops and feeding civilization.", "Your crops grow 10%% faster", "An iron hoe"));
        characterBackgroundMap.put(2, new CharacterBackground(2, "Fisherman", "You were a fisherman in the Old World.", "+3 weight bonus to all caught fish", "An iron fishing rod"));
        characterBackgroundMap.put(3, new CharacterBackground(3, "Lumberjack", "You were a lumberjack in the Old World, tasked with collecting vast amounts of wood to fuel the ever expanding cities.", "3%% chance to instantly fell a tree", "An iron axe"));
        characterBackgroundMap.put(4, new CharacterBackground(4, "Miner", "You were a miner in the Old World, collecting coal and ore to fuel the war machine.", "You are 10%% more efficient with a pickaxe", "An iron pickaxe"));
        characterBackgroundMap.put(5, new CharacterBackground(5, "Scholar", "", "", ""));
        characterBackgroundMap.put(6, new CharacterBackground(6, "Soldier", "", "", ""));
    }

    public Collection<CharacterBackground> getCharacterBackgrounds() {
        return characterBackgroundMap.values();
    }

    public CharacterBackground getCharacterBackground(int id) {
        return characterBackgroundMap.get(id);
    }
}
