package net.dirtcraft.plugin.playerholograms.Data;

import org.spongepowered.api.data.manipulator.ImmutableDataManipulator;
import org.spongepowered.api.data.value.immutable.ImmutableValue;

import java.util.UUID;

public interface ImmutableHologramData extends ImmutableDataManipulator<ImmutableHologramData, HologramData> {

    ImmutableValue<String> name();

    ImmutableValue<Boolean> isHologram();

    ImmutableValue<UUID> owner();

}
