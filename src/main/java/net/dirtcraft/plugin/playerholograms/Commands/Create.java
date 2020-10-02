package net.dirtcraft.plugin.playerholograms.Commands;

import net.dirtcraft.plugin.playerholograms.Data.Hologram;
import net.dirtcraft.plugin.playerholograms.PlayerHolograms;
import net.dirtcraft.plugin.playerholograms.Utility;
import org.apache.commons.lang3.StringUtils;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.serializer.TextSerializers;

import static net.dirtcraft.plugin.playerholograms.Utility.format;

public class Create implements CommandExecutor {

    @Override
    public CommandResult execute(CommandSource source, CommandContext args) throws CommandException {
        if (!(source instanceof Player)) throw new CommandException(format("&cOnly a player can use this command!"));
        Player player = (Player) source;
        String name = args.<String>getOne("name").get();
        Text text = args.<Text>getOne("text").get();

        if (!StringUtils.isAlphanumeric(name)) throw new CommandException(format("&cPlease enter a valid name for the hologram!"));

        if (Utility.getHologramsByUUID(player.getUniqueId()).stream().map(Hologram::getName).anyMatch(name::equalsIgnoreCase)) throw new CommandException(Utility.format("&cYou already have a hologram named &e" + name));

        if (PlayerHolograms.isGpLoaded && !Utility.canUseGpClaim(player)) throw new CommandException(format("&cYou can only create a hologram in your claimed land!"));
        else if (PlayerHolograms.isFtbLoaded && !Utility.canUseFtbChunk(player)) throw new CommandException(format("&cYou can only create a hologram in your claimed land!"));

        Hologram hologram = new Hologram(player.getUniqueId(), name, player.getLocation(), text);

        player.sendMessage(format("&6" + hologram.getName() + "&7 has been &asuccessfully&7 created!"));

        return CommandResult.success();
    }

    public static CommandSpec getSpec() {
        return CommandSpec.builder()
                .executor(new Create())
                .arguments(
                        GenericArguments.string(Text.of("name")),
                        GenericArguments.text(Text.of("text"), TextSerializers.FORMATTING_CODE, true)
                )
                .description(Text.of("Creates a hologram"))
                .build();
    }

}
