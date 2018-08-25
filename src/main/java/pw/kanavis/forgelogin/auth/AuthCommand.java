package pw.kanavis.forgelogin.auth;


import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import org.apache.logging.log4j.Logger;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;


public class AuthCommand extends CommandBase {

    private Logger logger;

    public AuthCommand(Logger logger) {
        this.logger = logger;
    }

    @Override
    public String getName() {
        return "login";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "<password> - password";
    }

    @Override
    public List<String> getAliases() {
        return new ArrayList<>();
    }

    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
        return true;
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args,
                                          @Nullable BlockPos targetPos) {
        return new ArrayList<>();
    }

    @Override
    public boolean isUsernameIndex(String[] args, int index) {
        return false;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        AuthDataHandler.IAuthHandler authPlayer = AuthDataHandler.getHandler(sender.getCommandSenderEntity());
        if (authPlayer.getAuthorized()) {
            // Already authorized
            throw new CommandException("Already logged in");
        } else {
            // Authorize
            if (args.length == 1) {
                // Correct usage, auth
                logger.info("Password: {}", args[0]);
                authPlayer.setAuthorized(true);
            } else {
                // Wrong usage
                sender.getCommandSenderEntity().sendMessage(new TextComponentString("Usage: /login <password>"));
            }
        }
    }

}
