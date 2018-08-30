package pw.kanavis.forgelogin;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.Logger;
import pw.kanavis.forgelogin.auth.AuthDataHandler;
import pw.kanavis.forgelogin.auth.AuthCommand;
import pw.kanavis.forgelogin.auth.AuthProvider;
import pw.kanavis.forgelogin.auth.PasswordCommand;


@Mod(modid = ForgeLogin.MODID, name = ForgeLogin.NAME, version = ForgeLogin.VERSION, acceptableRemoteVersions = "*")
public class ForgeLogin {
    public static final String MODID = "forgelogin";
    public static final String NAME = "Forge Login";
    public static final String VERSION = "0.3";

    private static Logger logger;
    private static AuthProvider authProvider;
    private static StateStorage storage;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        // Obtain logger
        logger = event.getModLog();

        // Register auth capability
        logger.debug("Registering caps");
        AuthDataHandler.register();

        // Obtain auth provider
        logger.debug("Initializing auth provider");
        authProvider = new AuthProvider();

        // Obtain auth provider
        logger.debug("Initializing state storage");
        storage = new StateStorage();
    }

    @EventHandler
    public void serverStart(FMLServerStartingEvent event) {
        // Register server commands
        logger.debug(">>>>ForgeLogin: registering commands");
        event.registerServerCommand(new AuthCommand(logger, authProvider, storage));
        event.registerServerCommand(new PasswordCommand(logger, authProvider));
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        // Register events
        logger.debug(">>>>ForgeLogin: registering events");
        MinecraftForge.EVENT_BUS.register( new MainEventHandler(logger, storage) );
    }
}
