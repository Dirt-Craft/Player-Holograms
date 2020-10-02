package net.dirtcraft.plugin.playerholograms;

import com.feed_the_beast.ftblib.lib.math.ChunkDimPos;
import com.feed_the_beast.ftbutilities.data.ClaimedChunk;
import com.feed_the_beast.ftbutilities.data.ClaimedChunks;
import me.ryanhamshire.griefprevention.GriefPrevention;
import me.ryanhamshire.griefprevention.api.claim.Claim;
import net.dirtcraft.plugin.playerholograms.Data.Hologram;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.context.DefaultContextKeys;
import net.luckperms.api.node.Node;
import net.luckperms.api.node.NodeType;
import net.luckperms.api.node.types.MetaNode;
import net.luckperms.api.query.QueryOptions;
import net.minecraft.entity.player.EntityPlayerMP;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.service.pagination.PaginationList;
import org.spongepowered.api.service.user.UserStorageService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.serializer.TextSerializers;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.extent.EntityUniverse;

import java.util.*;
import java.util.stream.Collectors;

public class Utility {

    public static LuckPerms api = null;

    public static Text format(String message) {
        return TextSerializers.FORMATTING_CODE.deserialize(message);
    }

    public static Optional<User> getUserFromUUID(UUID uuid) {
        return Sponge.getServiceManager().provideUnchecked(UserStorageService.class).get(uuid);
    }

    public static PaginationList.Builder getPaginationBuilder() {
        return PaginationList.builder()
                .padding(Utility.format("&4&m-"))
                .title(format("&cDirtCraft &bHolograms"));
    }

    public static LuckPerms getLuckPerms() {
        if (api == null) api = LuckPermsProvider.get();
        return api;
    }

    public static void removeHolograms(UUID owner, String name) {
        getHologramsByUUID(owner).stream().filter(holo -> holo.getName().equalsIgnoreCase(name)).forEach(Hologram::removeHologram);
    }

    public static List<Hologram> getHologramsByUUID(UUID owner) {
        List<Entity> entities = new ArrayList<>();
        Sponge.getServer().getWorlds().stream().map(EntityUniverse::getEntities).forEach(entities::addAll);

        List<Hologram> holograms = new ArrayList<>();
        for (Entity entity : entities) {
            if (!entity.get(Hologram.IS_HOLOGRAM).orElse(false)) continue;
            if (!entity.get(Hologram.HOLOGRAM_OWNER).orElse(UUID.randomUUID()).equals(owner)) continue;
            holograms.add(new Hologram(entity));
        }
        return holograms;
    }

    public static net.luckperms.api.model.user.User getLuckPermsUser(UUID uuid) throws CommandException {
        if (uuid == null) throw new CommandException(Utility.format("&cCould not retrieve LuckPerms data. Please contact an administrator."));
        return getLuckPerms().getUserManager().getUser(uuid);
    }

    private static MetaNode.Builder getHologramNode() {
        return MetaNode.builder()
                .key("holograms");
    }

    public static void setHologramNode(UUID uuid, int holograms) throws CommandException {
        removeOldNode(uuid);
        MetaNode node = getHologramNode()
                .value(String.valueOf(holograms))
                .withContext(DefaultContextKeys.SERVER_KEY, getLuckPerms().getServerName())
                .build();
        net.luckperms.api.model.user.User user = getLuckPermsUser(uuid);
        user.getNodes().add(node);
        getLuckPerms().getUserManager().saveUser(user);
    }

    private static void removeOldNode(UUID uuid) throws CommandException {
        net.luckperms.api.model.user.User user = getLuckPermsUser(uuid);
        List<Node> nodes = user
                .getNodes()
                .stream()
                .filter(node -> node.getType() == NodeType.META && node.getKey().equals("holograms"))
                .collect(Collectors.toList());
        user.getNodes().removeAll(nodes);
        getLuckPerms().getUserManager().saveUser(user);
    }

    public static int getHologramsAllowed(UUID uuid) throws CommandException {
        net.luckperms.api.model.user.User user = getLuckPermsUser(uuid);
        List<MetaNode> nodes = user
                .resolveInheritedNodes(NodeType.META, QueryOptions.nonContextual())
                .stream()
                .filter(node -> node.getMetaKey().equals("holograms"))
                .collect(Collectors.toList());

        List<Integer> values = nodes
                .stream()
                .map(node -> parseIntSafe(node.getMetaValue()))
                .collect(Collectors.toList());
        if (values.isEmpty()) values.add(0);
        Integer amount = Collections.max(values);
        if (amount == -1) return Integer.MAX_VALUE;
        else return amount;
    }

    private static int parseIntSafe(String number) {
        try {
            return Integer.parseInt(number);
        } catch (NumberFormatException exception) {
            exception.printStackTrace();
            return 0;
        }
    }

    public static boolean canUseGpClaim(Player player) {
        if (player.hasPermission("holograms.bypass")) return true;
        Location<World> location = player.getLocation();
        Claim claim = GriefPrevention.getApi()
                .getClaimManager(player.getWorld())
                .getClaimAt(location);
        return GriefPrevention.getApi()
                .getAllPlayerClaims(player.getUniqueId())
                .contains(claim);
    }

    public static boolean canUseFtbChunk(Player player) throws CommandException {
        if (player.hasPermission("holograms.bypass")) return true;
        Optional<ClaimedChunk> optionalChunk = Optional.ofNullable(
                ClaimedChunks.instance.getChunk(new ChunkDimPos((net.minecraft.entity.Entity) player)));
        if (!optionalChunk.isPresent()) throw new CommandException(format("&cPlease claim this chunk before creating a hologram!"));
        ClaimedChunk chunk = optionalChunk.get();
        System.out.println(chunk.getTeam().players.toString());
        return chunk.getTeam().players.containsKey(chunk.getTeam().universe.players.get(((EntityPlayerMP) player).getUniqueID()));
    }

}
