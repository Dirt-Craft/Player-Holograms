package net.dirtcraft.plugin.playerholograms.Data;

import org.spongepowered.api.data.manipulator.DataManipulator;
import org.spongepowered.api.data.value.mutable.Value;

import java.util.UUID;

public interface HologramData extends DataManipulator<HologramData, ImmutableHologramData> {

    Value<String> name();

    Value<Boolean> isHologram();

    Value<UUID> owner();

}
