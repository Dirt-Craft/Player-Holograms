package net.dirtcraft.plugin.playerholograms;

import com.google.inject.Inject;
import net.dirtcraft.plugin.playerholograms.Commands.Menu;
import net.dirtcraft.plugin.playerholograms.Data.*;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.DataRegistration;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.GameRegistryEvent;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;

@Plugin(
        id = "player-holograms",
        name = "Player Holograms",
        description = "Allows players to create holograms",
        url = "https://dirtcraft.net",
        authors = "juliann"
)
public class PlayerHolograms {

    public static boolean isFtbLoaded = false;
    public static boolean isGpLoaded = false;

    @Inject
    private Logger logger;

    @Inject
    private PluginContainer container;

    @Listener
    public void onGameInit(GameInitializationEvent event) {
        if (Sponge.getPluginManager().isLoaded("pixelmon")) return;
        Sponge.getCommandManager().register(this, Menu.getSpec(), "hologram", "holo", "holos", "holograms");
        if (Sponge.getPluginManager().isLoaded("griefprevention")) isGpLoaded = true;
        if (Sponge.getPluginManager().isLoaded("ftbutilities")) isFtbLoaded = true;
    }

    @Listener
    public void onDataRegistry(GameRegistryEvent.Register<DataRegistration<?, ?>> event) {
        if (Sponge.getPluginManager().isLoaded("pixelmon")) return;
        DataRegistration.builder()
                .id(container.getId())
                .name(container.getName())
                .dataClass(HologramData.class)
                .immutableClass(ImmutableHologramData.class)
                .dataImplementation(SpongeHologramData.class)
                .immutableImplementation(ImmutableSpongeHologramData.class)
                .builder(new SpongeHologramData.Builder())
                .build();
    }

    @Listener
    public void onKeyRegistry(GameRegistryEvent.Register<Key<?>> event) {
        if (Sponge.getPluginManager().isLoaded("pixelmon")) return;
        event.register(Hologram.HOLOGRAM_NAME);
        event.register(Hologram.IS_HOLOGRAM);
        event.register(Hologram.HOLOGRAM_OWNER);
    }

}
