package pw.kanavis.forgelogin;

import org.apache.logging.log4j.Logger;

import net.minecraft.util.text.TextComponentString;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;

import pw.kanavis.forgelogin.auth.AuthDataHandler;


public class MainEventHandler {

    private Logger logger;

    /**
     * Constructor
     */
    public MainEventHandler(Logger logger) {
        this.logger = logger;
        this.logger.debug(">>>>ForgeLogin: Initializing MainEventHandler");
    }

    /**
     * PlayerInteract event handler:
     * fired when entity interacts an entity and prohibits it for a non-logged-in player
     */
    @SubscribeEvent(priority = EventPriority.NORMAL, receiveCanceled = false)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getEntity() instanceof EntityPlayer) {
            AuthDataHandler.IAuthHandler authPlayer = AuthDataHandler.getHandler(event.getEntity());
            if (!authPlayer.getAuthorized()) {
                if (event.isCancelable())
                    event.setCanceled(true);
            }
        }
    }

    /**
     * LivingAttack event handler:
     * fired when entity attacks an entity and prohibits it for a non-logged-in player
     */
    @SubscribeEvent(priority = EventPriority.NORMAL, receiveCanceled = false)
    public void onLivingAttack(LivingAttackEvent event) {
        Entity trueSource = event.getSource().getTrueSource();
        if (trueSource instanceof EntityPlayer) {
            AuthDataHandler.IAuthHandler authPlayer = AuthDataHandler.getHandler(trueSource);
            if (!authPlayer.getAuthorized()) {
                if (event.isCancelable())
                    event.setCanceled(true);
            }
        }
    }

    /**
     * ServerChat event handler
     */
    @SubscribeEvent(priority = EventPriority.NORMAL, receiveCanceled = false)
    public void onServerChat(ServerChatEvent event) {
        AuthDataHandler.IAuthHandler authPlayer = AuthDataHandler.getHandler(event.getPlayer());
        if (!authPlayer.getAuthorized()) {
            // Player not authorized, deny chat
            if (event.isCancelable())
                event.setCanceled(true);
            event.getPlayer().sendMessage( new TextComponentString("Cannot chat before login") );
        }
    }

    /**
     * PlayerLoggedIn event handler
    */
     @SubscribeEvent(priority = EventPriority.NORMAL, receiveCanceled = true)
     public void onPlayerLoggedIn(PlayerLoggedInEvent event) {
         AuthDataHandler.IAuthHandler authPlayer = AuthDataHandler.getHandler(event.player);
         authPlayer.setAuthorized(false);
         logger.info(">>>> Player connected: {} id: {}", event.player.getName(),
                 event.player.getEntityId());
         event.player.sendMessage( new TextComponentString("You need to login!") );
         event.player.sendMessage( new TextComponentString("/login <password>") );
     }

}
