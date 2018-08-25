package pw.kanavis.forgelogin.auth;


import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;


public class PasswordCommand extends CommandBase {

    private Logger logger;
    private AuthProvider auth;

    public static final Style STYLE_OK   = new Style().setBold(true).setColor(TextFormatting.GREEN);
    public static final Style STYLE_ERR  = new Style().setBold(true).setColor(TextFormatting.RED);

    public PasswordCommand(Logger logger, AuthProvider authProvider) {

        this.logger = logger;
        this.auth = authProvider;
    }

    private void chatReply(ICommandSender sender, ITextComponent textComponent) {
        sender.getCommandSenderEntity().sendMessage(textComponent);
    }

    @Override
    public String getName() {
        return "password";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "<password> - set password";
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

    private void setPassword(ICommandSender sender, String login, String password) {
        try {
            this.auth.setPassword(login, password);
            chatReply(sender, new TextComponentString("Password set").setStyle(STYLE_OK));
        } catch (IOException e) {
            this.logger.error("Auth file IO exception: {}", e.getMessage());
            chatReply(sender, new TextComponentString("Auth file IO exception").setStyle(STYLE_ERR));
        } catch (NoSuchAlgorithmException e) {
            this.logger.error("Auth file NoSuchAlgorithm exception: {}", e.getMessage());
            chatReply(sender, new TextComponentString("Auth file NoSuchAlgorithm exception")
                    .setStyle(STYLE_ERR));
        }
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
        if (args.length != 1) {
            // Wrong usage
            chatReply(sender, new TextComponentString("Usage: /password password").setStyle(STYLE_ERR));
        } else {
            Entity senderEntity = sender.getCommandSenderEntity();
            if (senderEntity == null) {
                chatReply(sender, new TextComponentString("Wrong command sender").setStyle(STYLE_ERR));
                return;
            }
            AuthDataHandler.IAuthHandler authPlayer = AuthDataHandler.getHandler(senderEntity);
            String login = sender.getCommandSenderEntity().getName();

            // Check auth
            if (authPlayer.getAuthorized()) {
                // Authorized player, can set password. Set it.
                this.setPassword(sender, login, args[0]);
            } else {
                // Not authorized. Check if is initial
                try {
                    AuthProvider.AuthResult authResult = this.auth.checkAuth(login, "empty");
                    if (authResult.isInitial()) {
                        // Auth state is initial, can set password. Set it.
                        this.setPassword(sender, login, args[0]);
                    } else {
                        // Auth state is not initial. Drop attempt.
                        chatReply(sender, new TextComponentString("Cannot into password!").setStyle(STYLE_ERR));
                    }
                } catch (IOException e) {
                    this.logger.error("Auth file IO exception: {}", e.getMessage());
                    chatReply(sender, new TextComponentString("Auth file IO exception").setStyle(STYLE_ERR));
                } catch (NoSuchAlgorithmException e) {
                    this.logger.error("Auth file NoSuchAlgorithm exception: {}", e.getMessage());
                    chatReply(sender, new TextComponentString("Auth file NoSuchAlgorithm exception")
                            .setStyle(STYLE_ERR));
                }
            }
        }
    }

}
