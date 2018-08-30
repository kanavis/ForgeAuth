package pw.kanavis.forgelogin;

import net.minecraftforge.fml.common.gameevent.TickEvent;
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
    private StateStorage storage;
    private static final int LONG_TICK_DURATION = 10;
    private int serverTickCounter;

    /**
     * Constructor
     */
    public MainEventHandler(Logger logger, StateStorage storage) {
        this.logger = logger;
        this.logger.debug(">>>>ForgeLogin: Initializing MainEventHandler");
        this.serverTickCounter = 0;
        this.storage = storage;
    }



    /**
     * Long server tick handler, executes every LONG_TICK_DURATION server ticks
     */
    private void longServerTick() {
        // tick all anchors
        this.storage.tickAnchors();
    }

    /**
     * ServerTick event handler:
     * fired when server makes a tick
     */
    @SubscribeEvent(priority = EventPriority.NORMAL)
    public void onServerTick(TickEvent.ServerTickEvent event) {
        if (++this.serverTickCounter == LONG_TICK_DURATION) {
            this.serverTickCounter = 0;
            longServerTick();
        }
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
        Entity entity = event.getEntity();
        if (trueSource instanceof EntityPlayer) {
            // Player attacks
            AuthDataHandler.IAuthHandler authPlayer = AuthDataHandler.getHandler(trueSource);
            if (!authPlayer.getAuthorized()) {
                if (event.isCancelable())
                    event.setCanceled(true);
            }
        }
        if (entity instanceof EntityPlayer) {
            // Player is attacked
            AuthDataHandler.IAuthHandler authPlayer = AuthDataHandler.getHandler(entity);
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
         storage.anchorEntityPos(event.player);
         logger.info(">>>> Player connected: {} id: {} pos: {} {} {} dimension: {}",
                 event.player.getName(),
                 event.player.getEntityId(),
                 event.player.getPosition().getX(),
                 event.player.getPosition().getY(),
                 event.player.getPosition().getZ(),
                 event.player.dimension
         );
         /*logger.info(">>>> Spawn point: x: {} y: {} z: {}",
                 event.player.getEntityWorld().getSpawnPoint().getX(),
                 event.player.getEntityWorld().getSpawnPoint().getY(),
                 event.player.getEntityWorld().getSpawnPoint().getZ()
         );*/
         event.player.sendMessage( new TextComponentString("You need to login!") );
         event.player.sendMessage( new TextComponentString("/login <password>") );
     }

}
