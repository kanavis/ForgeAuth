package pw.kanavis.forgelogin.auth;


import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;

import net.minecraft.entity.Entity;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import org.apache.logging.log4j.Logger;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;


public class AuthCommand extends CommandBase {

    private Logger logger;
    private AuthProvider auth;

    public static final Style STYLE_OK   = new Style().setBold(true).setColor(TextFormatting.GREEN);
    public static final Style STYLE_WARN = new Style().setBold(true).setColor(TextFormatting.YELLOW);
    public static final Style STYLE_ERR  = new Style().setBold(true).setColor(TextFormatting.RED);

    public AuthCommand(Logger logger, AuthProvider authProvider) {

        this.logger = logger;
        this.auth = authProvider;
    }

    private void chatReply(ICommandSender sender, ITextComponent textComponent) {
        sender.getCommandSenderEntity().sendMessage(textComponent);
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
        Entity senderEntity = sender.getCommandSenderEntity();
        if (senderEntity == null) {
            chatReply(sender, new TextComponentString("Wrong command sender").setStyle(STYLE_ERR));
            return;
        }
        AuthDataHandler.IAuthHandler authPlayer = AuthDataHandler.getHandler(senderEntity);
        if (authPlayer.getAuthorized()) {
            // Already authorized
            throw new CommandException("Already logged in");
        } else {
            // Authorize
            if (args.length == 1) {
                // Correct usage, auth
                String login = sender.getCommandSenderEntity().getName();
                String password = args[0];

                // Check auth
                try {
                    AuthProvider.AuthResult authResult = this.auth.checkAuth(login, password);

                    String replyString = String.format(
                        "User %s auth reply: exists=%b, ok=%b, initial=%b, debug=%s",
                        login, authResult.exists(), authResult.isOk(), authResult.isInitial(), authResult.getDebug()
                    );
                    logger.debug(replyString);

                    if (authResult.isOk()) {
                        // Player authorized
                        authPlayer.setAuthorized(true);
                        logger.info("User {} authorized.", login);
                        chatReply(sender, new TextComponentString("You have been logged in.").setStyle(STYLE_OK));
                    } else if (authResult.isInitial()) {
                        // Player is initial
                        chatReply(sender, new TextComponentString("Initialize please!").setStyle(STYLE_WARN));
                    } else {
                        // Login failure
                        chatReply(sender, new TextComponentString("Login failed.").setStyle(STYLE_ERR));
                    }

                } catch (IOException e) {
                    this.logger.error("Auth file IO exception: {}", e.getMessage());
                    chatReply(sender, new TextComponentString("Auth file IO exception").setStyle(STYLE_ERR));
                } catch (NoSuchAlgorithmException e) {
                    this.logger.error("Auth file NoSuchAlgorithm exception: {}", e.getMessage());
                    chatReply(sender, new TextComponentString("Auth file NoSuchAlgorithm exception")
                            .setStyle(STYLE_ERR));
                }

            } else {
                // Wrong usage
                chatReply(sender, new TextComponentString("Usage: /login <password>").setStyle(STYLE_ERR));
            }
        }
    }

}
