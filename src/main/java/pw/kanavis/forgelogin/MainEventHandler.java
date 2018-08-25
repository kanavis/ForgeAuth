package pw.kanavis.forgelogin;


import net.minecraft.util.ResourceLocation;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import org.apache.logging.log4j.Logger;
import pw.kanavis.forgelogin.auth.IAuthHandler;
import pw.kanavis.forgelogin.auth.AuthProvider;


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
     * Get auth handler
     */
    public static IAuthHandler getAuthHandler(Entity entity) {
        if (entity.hasCapability(AuthProvider.CAPABILITY_FORGELOGIN, EnumFacing.DOWN)) {
            return entity.getCapability(AuthProvider.CAPABILITY_FORGELOGIN, EnumFacing.DOWN);
        } else {
            return null;
        }
    }

    /**
     * AttachCapabilities event:
     * fired when new entity is created while attaching caps.
     */
    @SubscribeEvent
    public void attachCapabilities(AttachCapabilitiesEvent<Entity> event) {
        // Attach auth event to player entity
        if (event.getObject() instanceof EntityPlayer) {
            event.addCapability(new ResourceLocation("forgelogin", "auth"),
                    new AuthProvider());
        }
    }

    /**
     * PlayerInteract event handler:
     * fired when entity interacts an entity and prohibits it for a non-logged-in player
     */
    @SubscribeEvent(priority = EventPriority.NORMAL, receiveCanceled = false)
    public void onEvent(PlayerInteractEvent event) {
        if (event.getEntity() instanceof EntityPlayer) {
            IAuthHandler authPlayer = getAuthHandler(event.getEntity());
            logger.info("DBG PlayerInteractEvent {} {} {}", event.getEntity().getName(),
                    event.getEntity().getEntityId(), authPlayer.getAuthorized());
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
    public void onEvent(LivingAttackEvent event) {
        Entity trueSource = event.getSource().getTrueSource();
        if (trueSource instanceof EntityPlayer) {
            IAuthHandler authPlayer = getAuthHandler(trueSource);
            logger.info("DBG LivingAttackEvent {} {} {}", event.getEntity().getName(),
                    event.getEntity().getEntityId(), authPlayer.getAuthorized());
            if (!authPlayer.getAuthorized()) {
                if (event.isCancelable())
                    event.setCanceled(true);
            } else {
                logger.info("WWWA");
            }
        }
    }

    /**
     * ServerChat event handler
     */
    @SubscribeEvent(priority = EventPriority.NORMAL, receiveCanceled = false)
    public void onEvent(ServerChatEvent event) {
        IAuthHandler authPlayer = getAuthHandler(event.getPlayer());
        logger.info("DBG ServerChatEvent {} {} {}", event.getPlayer().getName(),
                event.getPlayer().getEntityId(), authPlayer.getAuthorized());
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
     public void onEvent(PlayerLoggedInEvent event) {
         logger.info(">>>> ForgeLogin: Player connected: {} id: {}", event.player.getName(),
                 event.player.getEntityId());
         event.player.sendMessage( new TextComponentString("You need to login!") );
         event.player.sendMessage( new TextComponentString("/login <password>") );
     }

}
