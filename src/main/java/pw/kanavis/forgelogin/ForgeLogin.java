package pw.kanavis.forgelogin;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.Logger;
import pw.kanavis.forgelogin.auth.IAuthHandler;
import pw.kanavis.forgelogin.auth.BasicAuthHandler;
import pw.kanavis.forgelogin.auth.AuthStorage;


@Mod(modid = ForgeLogin.MODID, name = ForgeLogin.NAME, version = ForgeLogin.VERSION)
public class ForgeLogin {
    public static final String MODID = "forgelogin";
    public static final String NAME = "Forge Login";
    public static final String VERSION = "1.0";

    private static Logger logger;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        // Obtain logger
        logger = event.getModLog();

        // Register auth capability
        logger.debug(">>>>ForgeLogin: registering caps");
        CapabilityManager.INSTANCE.register(IAuthHandler.class, new AuthStorage(), BasicAuthHandler.class);
    }

    @EventHandler
    public void serverStart(FMLServerStartingEvent event) {
        // Register server commands
        logger.debug(">>>>ForgeLogin: registering commands");
        event.registerServerCommand(new AuthCommand(logger));
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        // Register events
        logger.debug(">>>>ForgeLogin: registering events");
        MinecraftForge.EVENT_BUS.register( new MainEventHandler(logger) );
    }
}
