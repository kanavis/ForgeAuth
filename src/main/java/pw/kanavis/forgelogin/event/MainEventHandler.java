package pw.kanavis.forgelogin.event;

import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.apache.logging.log4j.Logger;

import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;

import pw.kanavis.forgelogin.StateStorage;
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
     * Long server tick handler, executes every LONG_TICK_DURATION server ticks.
     * Fire anchors' tick event.
     */
    private void longServerTick() {
        // tick all anchors
        this.storage.tickAnchors();
    }

    /**
     * ServerTick event handler:
     * fired when server makes a tick.
     * Process to long tick here.
     */
    @SubscribeEvent(priority = EventPriority.NORMAL)
    public void onServerTick(TickEvent.ServerTickEvent event) {
        if (++this.serverTickCounter == LONG_TICK_DURATION) {
            // Long tick counter oveflow. Process to long tick event.
            this.serverTickCounter = 0;
            longServerTick();
        }
    }

    /**
     * Player Clone event. Processes when player instance is cloned.
     * Usually happens on respawn and teleport.
     * Re-anchor unauthorized player on death here.
     */
     @SubscribeEvent(priority = EventPriority.NORMAL, receiveCanceled = true)
     public void onPlayerClone(PlayerEvent.Clone event) {
         if (event.isWasDeath()) {
             // Cause of event is death
             AuthDataHandler.IAuthHandler authPlayer = AuthDataHandler.getHandler(event.getOriginal());
             if (!authPlayer.getAuthorized()) {
                 // Unauthorized player died, re-anchor
                 storage.unAnchorEntity(event.getOriginal());
                 storage.anchorEntityPos(event.getEntityPlayer());
             }
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
