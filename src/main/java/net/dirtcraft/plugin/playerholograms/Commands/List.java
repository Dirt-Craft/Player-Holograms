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
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.service.pagination.PaginationList;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

import static net.dirtcraft.plugin.playerholograms.Utility.format;

public class List implements CommandExecutor {

    @Override
    public CommandResult execute(CommandSource source, CommandContext args) throws CommandException {
        Optional<User> target = args.<User>getOne("user");
        String userName;
        UUID owner;
        if (!(source instanceof Player)) {
            if (!target.isPresent()) throw new CommandException(format("&cOnly a player can use this command!"));
            if (!source.hasPermission("holograms.others")) throw new CommandException(format("&cYou do not have permission to view other player's holograms!"));
            User user = target.get();
            owner = user.getUniqueId();
            userName = user.getName();
        } else {
            owner = ((Player) source).getUniqueId();
            userName = source.getName();
        }

        java.util.List<Hologram> holograms = Utility.getHologramsByUUID(owner);
        java.util.List<Text> texts = new ArrayList<>();
        for (Hologram hologram : holograms) {
            Location<World> location = hologram.getLocation();
            Text.Builder text = Text.builder();
            text.append(format("&l&a» &6" + hologram.getName()));
            text.onHover(TextActions.showText(
                    format(
                            "&7Name&8: &6" + hologram.getName() + "\n" +
                            "&7Location&8: &7X: " + "&6" + location.getBlockX() + " &7Y: &6" + location.getBlockY() + "&7 Z: &6" + location.getBlockZ() + "\n\n" +
                            "&7&oClick To Teleport\n" +
                            "&7&oShift Click To &c&l&oRemove")));
            text.onClick(TextActions.executeCallback(src -> { if (src instanceof Player) ((Player) src).setLocation(location); }));
            text.onShiftClick(TextActions.insertText("/hologram remove " + hologram.getName()));
            texts.add(text.build());
        }

        PaginationList.Builder pagination = Utility.getPaginationBuilder();
        pagination.title(format("&6" + userName + "&7's Holograms"));
        pagination.contents(texts);
        if (texts.isEmpty()) pagination.header(format("&7&oYou do not have any active holograms!"));
        pagination.sendTo(source);

        return CommandResult.success();
    }

    public static CommandSpec getSpec() {
        return CommandSpec.builder()
                .description(Text.of("Lists all holograms for a user"))
                .arguments(
                        GenericArguments.optional(
                                GenericArguments.user(Text.of("user"))))
                .executor(new List())
                .build();
    }

}
