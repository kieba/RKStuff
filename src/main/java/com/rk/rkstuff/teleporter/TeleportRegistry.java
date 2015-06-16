package com.rk.rkstuff.teleporter;

import com.rk.rkstuff.teleporter.tile.TileTeleporter;
import com.rk.rkstuff.util.Pair;
import com.rk.rkstuff.util.Pos;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.UUID;

public class TeleportRegistry {

    private static HashMap<UUID, LinkEntry> playerRegistry = new HashMap<UUID, LinkEntry>();

    public static boolean register(TileTeleporter tile) {
        LinkEntry entry = getEntry(getRegistry(tile), tile);
        boolean registered = false;
        if (entry.pos[0].equals(Pos.UNDEFINED)) {
            entry.pos[0] = tile.getPosition();
            entry.dimension[0] = tile.getWorldObj();
            registered = true;
        } else if (entry.pos[1].equals(Pos.UNDEFINED)) {
            entry.pos[1] = tile.getPosition();
            entry.dimension[1] = tile.getWorldObj();
            registered = true;
        }
        return registered;
    }

    public static void unregister(TileTeleporter tile) {
        HashMap<UUID, LinkEntry> registry = getRegistry(tile);
        LinkEntry entry = getEntry(registry, tile);
        if (entry.pos[0].equals(tile.getPosition())) {
            entry.pos[0] = Pos.UNDEFINED;
            entry.dimension[0] = null;
        } else if (entry.pos[1].equals(tile.getPosition())) {
            entry.pos[1] = Pos.UNDEFINED;
            entry.dimension[1] = null;
        }
        if (entry.pos[0].equals(Pos.UNDEFINED) && entry.pos[1].equals(Pos.UNDEFINED)) {
            registry.remove(tile.getUuid());
        }
    }

    public static Pair<Pos, World> getDestination(TileTeleporter tile) {
        LinkEntry entry = getEntry(getRegistry(tile), tile);
        if (entry.pos[0].equals(tile.getPosition())) {
            return new Pair<Pos, World>(entry.pos[1], entry.dimension[1]);
        } else {
            return new Pair<Pos, World>(entry.pos[0], entry.dimension[0]);
        }
    }

    private static LinkEntry getEntry(HashMap<UUID, LinkEntry> registry, TileTeleporter tile) {
        LinkEntry entry = registry.get(tile.getUuid());
        if (entry == null) {
            entry = new LinkEntry();
            registry.put(tile.getUuid(), entry);
        }
        return entry;
    }

    private static HashMap<UUID, LinkEntry> getRegistry(TileTeleporter tile) {
        return playerRegistry;
    }

    public static void clear() {
        playerRegistry.clear();
    }

    private static class LinkEntry {

        private Pos[] pos = new Pos[]{
                Pos.UNDEFINED,
                Pos.UNDEFINED
        };

        private World[] dimension = new World[]{null, null};
    }
}
