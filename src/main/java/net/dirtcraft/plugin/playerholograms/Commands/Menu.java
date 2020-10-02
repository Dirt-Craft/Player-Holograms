package net.dirtcraft.plugin.playerholograms.Commands;

import net.dirtcraft.plugin.playerholograms.Data.Hologram;
import net.dirtcraft.plugin.playerholograms.Utility;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;

import java.util.ArrayList;
import java.util.List;

import static net.dirtcraft.plugin.playerholograms.Utility.format;

public class Menu implements CommandExecutor {

    @Override
    public CommandResult execute(CommandSource source, CommandContext args) throws CommandException {
        if (!(source instanceof Player)) throw new CommandException(format("&cOnly a player can use this command!"));
        Player player = (Player) source;

        int maxHolograms = Utility.getHologramsAllowed(player.getUniqueId());
        List<Hologram> holograms = Utility.getHologramsByUUID(player.getUniqueId());
        int activeHolograms = holograms.size();
        int available = maxHolograms - activeHolograms;

        List<Text> texts = new ArrayList<>();
        texts.add(Text.of(""));

        Text.Builder create = Text.builder();
        create.append(format("&l" + (maxHolograms > activeHolograms ? "&a" : "&c") + "»&f "));
        create.append(format("&8[&aCreate&8]"));
        if (maxHolograms <= activeHolograms) {
            create.onHover(TextActions.showText(format("&7You have used &6" + activeHolograms + "&8/&6" + maxHolograms + "&7 holograms" +
                    "\n\n&7<name>&8 - &6Name of the hologram\n&7<text>&8 - &6Text displayed on the hologram")));
            create.onClick(TextActions.executeCallback(src -> src.sendMessage(format("&cYou do not have any available holograms!"))));
        } else {
            create.onHover(TextActions.showText(format("&7You have &6" + available + "&7 available out of &6" + maxHolograms + "&7 total " + (isPlural(maxHolograms) ? "holograms" : "hologram") +
                    "\n\n&7<name>&8 - &6Name of the hologram\n&7<text>&8 - &6Text displayed on the hologram")));
            create.onClick(TextActions.suggestCommand("/holograms create <name> <text>"));
        }
        texts.add(create.build());

        Text.Builder list = Text.builder();
        list.append(format("&l" + (activeHolograms > 0 ? "&a" : "&c") + "»&f "));
        list.append(format("&8[&eList&8]"));
        Text.Builder hover = Text.builder().append(format("&7You have &6" + activeHolograms + "&7 " + (activeHolograms != 1 ? "holograms" : "hologram") + " in use"));
        if (player.hasPermission("holograms.others")) hover.append(format("\n\n&7[user]&8 - &6Username of other player's holograms to view"));
        list.onHover(TextActions.showText(hover.build()));
        if (activeHolograms > 0) list.onClick(TextActions.runCommand("/holograms list"));
        else list.onClick(TextActions.executeCallback(src -> src.sendMessage(format("&cYou do not have any active holograms!"))));
        texts.add(list.build());

        Text.Builder delete = Text.builder();
        delete.append(format("&l" + (activeHolograms > 0 ? "&a" : "&c") + "»&f "));
        delete.append(format("&8[&4Delete&8]"));
        delete.onHover(TextActions.showText(format("&7You have &6" + activeHolograms + "&7 " + (activeHolograms != 1 ? "holograms" : "hologram") + " in use\n\n&7<name> &8 - &6Name of hologram to be removed")));
        if (activeHolograms > 0) delete.onClick(TextActions.suggestCommand("/hologram remove <name>"));
        else delete.onClick(TextActions.executeCallback(src -> src.sendMessage(format("&cYou do not have any active holograms!"))));
        texts.add(delete.build());
        texts.add(Text.of(""));

        Utility.getPaginationBuilder()
                .contents(texts)
                .sendTo(player);

        return CommandResult.success();
    }

    public static CommandSpec getSpec() {
        return CommandSpec.builder()
                .executor(new Menu())
                .description(Text.of("Opens menu for holograms"))
                .child(Create.getSpec(), "create")
                .child(net.dirtcraft.plugin.playerholograms.Commands.List.getSpec(), "list")
                .child(Remove.getSpec(), "remove", "delete")
                .build();
    }

    private boolean isPlural(int number) {
        return number != 0;
    }

}
