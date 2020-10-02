package net.dirtcraft.plugin.playerholograms.Data;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.manipulator.immutable.common.AbstractImmutableData;
import org.spongepowered.api.data.value.immutable.ImmutableValue;

import java.util.UUID;

public class ImmutableSpongeHologramData extends AbstractImmutableData<ImmutableHologramData, HologramData> implements ImmutableHologramData {

    private final ImmutableValue<String> immutableName;
    private final ImmutableValue<Boolean> immutableIsHologram;
    private final ImmutableValue<UUID> immutableUuid;

    public ImmutableSpongeHologramData(String name, boolean isHologram, UUID uuid) {
        //super(ImmutableHologramData.class);
        this.immutableName = Sponge.getRegistry().getValueFactory()
                .createValue(Hologram.HOLOGRAM_NAME, name)
                .asImmutable();

        this.immutableIsHologram = Sponge.getRegistry().getValueFactory()
                .createValue(Hologram.IS_HOLOGRAM, isHologram)
                .asImmutable();

        this.immutableUuid = Sponge.getRegistry().getValueFactory()
                .createValue(Hologram.HOLOGRAM_OWNER, uuid)
                .asImmutable();

        registerGetters();
    }

    @Override
    public ImmutableValue<String> name() {
        return this.immutableName;
    }

    @Override
    public ImmutableValue<Boolean> isHologram() {
        return this.immutableIsHologram;
    }

    @Override
    public ImmutableValue<UUID> owner() {
        return this.immutableUuid;
    }

    @Override
    protected void registerGetters() {
        registerKeyValue(Hologram.HOLOGRAM_NAME, this::name);
        registerKeyValue(Hologram.IS_HOLOGRAM, this::isHologram);
        registerKeyValue(Hologram.HOLOGRAM_OWNER, this::owner);

        registerFieldGetter(Hologram.HOLOGRAM_NAME, this::name);
        registerFieldGetter(Hologram.IS_HOLOGRAM, this::isHologram);
        registerFieldGetter(Hologram.HOLOGRAM_OWNER, this::owner);
    }

    @Override
    public SpongeHologramData asMutable() {
        return new SpongeHologramData(this.name().get(), this.immutableIsHologram.get(), this.immutableUuid.get());
    }

    @Override
    public int getContentVersion() {
        return 1;
    }
}
