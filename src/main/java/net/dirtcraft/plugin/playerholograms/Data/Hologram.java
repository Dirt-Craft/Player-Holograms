package net.dirtcraft.plugin.playerholograms.Data;

import net.dirtcraft.plugin.playerholograms.Utility;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.value.mutable.Value;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.living.ArmorStand;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.util.TypeTokens;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.Optional;
import java.util.UUID;

public class Hologram {

    public static final Key<Value<String>> HOLOGRAM_NAME = Key.builder().type(TypeTokens.STRING_VALUE_TOKEN).name("Hologram Name").id("name").query(SpongeHologramData.NAME).build();
    public static final Key<Value<Boolean>> IS_HOLOGRAM = Key.builder().type(TypeTokens.BOOLEAN_VALUE_TOKEN).name("Is Active").id("active").query(SpongeHologramData.ACTIVE).build();
    public static final Key<Value<UUID>> HOLOGRAM_OWNER = Key.builder().type(TypeTokens.UUID_VALUE_TOKEN).name("Hologram Owner").id("owner").query(SpongeHologramData.OWNER).build();

    private final ArmorStand stand;

    public Hologram(Entity entity) {
        this.stand = (ArmorStand) entity;
    }

    public Hologram(UUID owner, String name, Location<World> location, Text text) throws CommandException {
        this.stand = (ArmorStand) location.add(0, 1, 0).createEntity(EntityTypes.ARMOR_STAND);
        if (!location.getExtent().spawnEntity(stand)) throw new CommandException(Utility.format("&cCould not create a hologram! Contact an administrator."));
        stand.offer(Keys.DISPLAY_NAME, text);
        stand.offer(Keys.CUSTOM_NAME_VISIBLE, true);
        stand.offer(Keys.HAS_GRAVITY, false);
        stand.offer(Keys.ARMOR_STAND_MARKER, true);
        stand.offer(Keys.INVISIBLE, true);

        HologramData data = Sponge.getDataManager()
                .getManipulatorBuilder(HologramData.class).get()
                .create()
                .set(HOLOGRAM_NAME, name)
                .set(IS_HOLOGRAM, true)
                .set(HOLOGRAM_OWNER, owner);
        stand.offer(data);
    }

    public String getName() {
        return stand.get(HOLOGRAM_NAME).orElse("N/A");
    }

    public boolean isHologram() {
        return stand.get(IS_HOLOGRAM).orElse(false);
    }

    public Optional<UUID> getOwnerUUID() {
        return stand.get(HOLOGRAM_OWNER);
    }

    public Optional<User> getOwner() {
        return getOwnerUUID().flatMap(Utility::getUserFromUUID);
    }

    public Location<World> getLocation() {
        return stand.getLocation();
    }

    public void removeHologram() {
        stand.remove();
    }

}
