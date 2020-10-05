package net.dirtcraft.plugin.playerholograms.Data;

import net.dirtcraft.plugin.playerholograms.Utility;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataHolder;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.manipulator.DataManipulatorBuilder;
import org.spongepowered.api.data.manipulator.mutable.common.AbstractData;
import org.spongepowered.api.data.merge.MergeFunction;
import org.spongepowered.api.data.persistence.AbstractDataBuilder;
import org.spongepowered.api.data.persistence.InvalidDataException;
import org.spongepowered.api.data.value.mutable.Value;

import java.util.Optional;
import java.util.UUID;

public class SpongeHologramData extends AbstractData<HologramData, ImmutableHologramData> implements HologramData {

    public static final DataQuery NAME = DataQuery.of("player-holograms:name");
    public static final DataQuery ACTIVE = DataQuery.of("player-holograms:active");
    public static final DataQuery OWNER = DataQuery.of("player-holograms:owner");

    private String name;
    private boolean isHologram;
    private UUID owner;

    public SpongeHologramData() {
        this("Hologram", true, UUID.randomUUID());
    }

    public SpongeHologramData(String name, boolean isHologram, UUID uuid) {
        //super(HologramData.class);
        this.name = name;
        this.isHologram = isHologram;
        this.owner = uuid;
        registerGettersAndSetters();
    }

    private SpongeHologramData setOwnerIfValid(UUID uuid) {
        Utility.getUserFromUUID(uuid).orElseThrow(() -> new IllegalArgumentException("Could not find user by the UUID \"" + uuid.toString() + "\""));
        this.owner = uuid;
        return this;
    }

    @Override
    protected void registerGettersAndSetters() {
        registerFieldGetter(Hologram.HOLOGRAM_NAME, () -> this.name);
        registerFieldSetter(Hologram.HOLOGRAM_NAME, name -> this.name = name);
        registerKeyValue(Hologram.HOLOGRAM_NAME, this::name);

        registerFieldGetter(Hologram.IS_HOLOGRAM, () -> this.isHologram);
        registerFieldSetter(Hologram.IS_HOLOGRAM, hologram -> this.isHologram = hologram);
        registerKeyValue(Hologram.IS_HOLOGRAM, this::isHologram);

        registerFieldGetter(Hologram.HOLOGRAM_OWNER, () -> this.owner);
        registerFieldSetter(Hologram.HOLOGRAM_OWNER, this::setOwnerIfValid);
        registerKeyValue(Hologram.HOLOGRAM_OWNER, this::owner);
    }

    @Override
    public Value<String> name() {
        return Sponge.getRegistry()
                .getValueFactory()
                .createValue(Hologram.HOLOGRAM_NAME, this.name);
    }

    @Override
    public Value<Boolean> isHologram() {
        return Sponge.getRegistry()
                .getValueFactory()
                .createValue(Hologram.IS_HOLOGRAM, this.isHologram);
    }

    @Override
    public Value<UUID> owner() {
        return Sponge.getRegistry()
                .getValueFactory()
                .createValue(Hologram.HOLOGRAM_OWNER, this.owner);
    }

    @Override
    public DataContainer toContainer() {
        return super.toContainer()
                .set(NAME, this.name)
                .set(ACTIVE, this.isHologram)
                .set(OWNER, this.owner);
    }

    @Override
    public Optional<HologramData> fill(DataHolder dataHolder, MergeFunction overlap) {
        dataHolder.get(HologramData.class).ifPresent(data -> data.set(overlap.merge(this, data).getValues()));
        return Optional.of(this);
    }

    @Override
    public Optional<HologramData> from(DataContainer container) {
        if (!container.contains(NAME, ACTIVE, OWNER)) return Optional.empty();

        final String name = container.getString(NAME).get();
        final boolean isHologram = container.getBoolean(ACTIVE).get();
        final UUID owner = container.getObject(OWNER, UUID.class).get();

        System.out.println("Method: From\nName: " + name + "\nActive: " + isHologram + "\nOwner: " + owner);

        this.set(Hologram.HOLOGRAM_NAME, name);
        this.set(Hologram.IS_HOLOGRAM, isHologram);
        this.set(Hologram.HOLOGRAM_OWNER, owner);

        return Optional.of(this);
    }

    @Override
    public HologramData copy() {
        return new SpongeHologramData(this.name, this.isHologram, this.owner);
    }

    @Override
    public ImmutableHologramData asImmutable() {
        return new ImmutableSpongeHologramData(this.name, this.isHologram, this.owner);
    }

    @Override
    public int getContentVersion() {
        return 1;
    }

    public static final class Builder extends AbstractDataBuilder<HologramData> implements DataManipulatorBuilder<HologramData, ImmutableHologramData> {

        public Builder() {
            super(HologramData.class, 1);
        }

        @Override
        protected Optional<HologramData> buildContent(DataView container) throws InvalidDataException {
            if (!container.contains(NAME, ACTIVE, OWNER)) return Optional.empty();

            final String name = container.getString(NAME).get();
            final boolean isHologram = container.getBoolean(ACTIVE).get();
            final UUID owner = container.getObject(OWNER, UUID.class).get();

            System.out.println("Method: Build Content\nName: " + name + "\nActive: " + isHologram + "\nOwner: " + owner);

            return Optional.of(new SpongeHologramData(name, isHologram, owner));
        }

        @Override
        public SpongeHologramData create() {
            return new SpongeHologramData();
        }

        @Override
        public Optional<HologramData> createFrom(DataHolder dataHolder) {
            return Optional.of(dataHolder.get(HologramData.class).orElseGet(this::create));
        }
    }
}
