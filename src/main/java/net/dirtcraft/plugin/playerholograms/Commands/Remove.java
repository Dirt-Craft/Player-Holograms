package net.dirtcraft.plugin.playerholograms.Commands;

import net.dirtcraft.plugin.playerholograms.Data.Hologram;
import net.dirtcraft.plugin.playerholograms.Utility;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import static net.dirtcraft.plugin.playerholograms.Utility.format;

public class Remove implements CommandExecutor {

    @Override
    public CommandResult execute(CommandSource source, CommandContext args) throws CommandException {
        if (!(source instanceof Player)) throw new CommandException(format("&cOnly a player can use this command!"));
        Player player = (Player) source;
        String name = args.<String>getOne("name").get();

        Utility.removeHolograms(player.getUniqueId(), name);

        player.sendMessage(format("&7The hologram &6" + name + "&7 has &asuccessfully&7 been removed"));

        return CommandResult.success();
    }

    public static CommandSpec getSpec() {
        return CommandSpec.builder()
                .executor(new Remove())
                .arguments(GenericArguments.withSuggestions(
                        GenericArguments.string(Text.of("name")),
                        getNames()))
                .arguments(GenericArguments.string(Text.of("name")))
                .description(Text.of("Remove a hologram"))
                .build();
    }

    private static Function<CommandSource, Iterable<String>> getNames() {
        return source -> {
            List<String> names = new ArrayList<>();
            if (!(source instanceof Player)) return names;
            Player player = (Player) source;
            Utility.getHologramsByUUID(player.getUniqueId()).stream().map(Hologram::getName).forEach(names::add);
            return names;
        };
    }

}
