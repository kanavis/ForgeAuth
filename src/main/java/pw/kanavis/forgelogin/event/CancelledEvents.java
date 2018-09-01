package pw.kanavis.forgelogin.event;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.CommandEvent;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.entity.player.FillBucketEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import org.apache.logging.log4j.Logger;
import pw.kanavis.forgelogin.auth.AuthDataHandler;


public class CancelledEvents {

    private Logger logger;

    /**
     * Constructor
     */
    public CancelledEvents(Logger logger) {
        this.logger = logger;
        this.logger.debug(">>>>ForgeLogin: Initializing Cancelled event handler");
    }

    /**
     * Check and cancel wrapper
     * @return boolean: if event was cancelled
     */
    private boolean checkCancel(Event event, Entity... entities) {
        for (Entity entity: entities) {
            if (entity instanceof EntityPlayer) {
                AuthDataHandler.IAuthHandler authPlayer = AuthDataHandler.getHandler(entity);
                try {
                    if (!authPlayer.getAuthorized()) {
                        if (event.isCancelable()) {
                            event.setCanceled(true);
                            return true;
                        }
                    }
                } catch (NullPointerException err) {
                    logger.error("NullPointerException in getAuthorized");
                }
            }
        }

        return false;
    }

    /**
     * PlayerInteract event handler:
     * fired when entity interacts an entity
     */
    @SubscribeEvent(priority = EventPriority.NORMAL, receiveCanceled = false)
    public void onPlayerInteract(PlayerInteractEvent event) {

        checkCancel(event, event.getEntity());
    }

    /**
     * LivingAttack event handler:
     * fired when entity attacks an entity
     */
    @SubscribeEvent(priority = EventPriority.NORMAL, receiveCanceled = false)
    public void onLivingAttack(LivingAttackEvent event) {

        checkCancel(event, event.getEntity(), event.getSource().getTrueSource());
    }

    /**
     * LivingHurt event handler:
     * fired when living entity is hurt
     */
    @SubscribeEvent(priority = EventPriority.NORMAL, receiveCanceled = false)
    public void onLivingHurt(LivingHurtEvent event) {

        checkCancel(event, event.getEntity());
    }

    /**
     * AttackEntity event handler:
     * fired when a player attacks an Entity
     */
    @SubscribeEvent(priority = EventPriority.NORMAL, receiveCanceled = false)
    public void onAttackEntity(AttackEntityEvent event) {

        checkCancel(event, event.getEntityPlayer());
    }

    /**
     * FillBucket event handler:
     * fired when a player attempts to fill a bucket
     */
    @SubscribeEvent(priority = EventPriority.NORMAL, receiveCanceled = false)
    public void onFillBucket(FillBucketEvent event) {

        checkCancel(event, event.getEntityPlayer());
    }

    /**
     * EntityItemPickup event handler:
     * fired when player attempts to pick up an item
     */
    @SubscribeEvent(priority = EventPriority.NORMAL, receiveCanceled = false)
    public void onEntityItemPickup(EntityItemPickupEvent event) {

        checkCancel(event, event.getEntityPlayer());
    }

    /**
     * ServerChat event handler:
     * fired when player attempts to pick up an item
     */
    @SubscribeEvent(priority = EventPriority.NORMAL, receiveCanceled = false)
    public void onServerChat(ServerChatEvent event) {

        EntityPlayer player = event.getPlayer();
        if (!(player instanceof FakePlayer))
            if (checkCancel(event, player))
                event.getPlayer().sendMessage( new TextComponentString("Cannot chat before login") );
    }

    /**
     * Command event handler:
     * fired when player attempts to send a command
     */
    @SubscribeEvent(priority = EventPriority.NORMAL, receiveCanceled = false)
    public void onCommandEvent(CommandEvent event) {

        Entity sender = event.getSender().getCommandSenderEntity();
        if ((sender instanceof EntityPlayer) && !(sender instanceof FakePlayer) &&
                !event.getCommand().getName().equals("login") && !event.getCommand().getName().equals("password")) {
            if (checkCancel(event, sender))
                sender.sendMessage( new TextComponentString("Cannot send commands before login") );
        }
    }

}
