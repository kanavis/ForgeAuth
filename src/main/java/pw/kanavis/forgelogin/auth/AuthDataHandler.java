package pw.kanavis.forgelogin.auth;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import pw.kanavis.forgelogin.ForgeLogin;


public class AuthDataHandler {

    // NBT key for isauth
    private static String AUTH_NBT_KEY = "isauth";

    // Capability object for checks and references
    // Initialized when forge registers the capability.
    @CapabilityInject(IAuthHandler.class)
    public static final Capability<IAuthHandler> CAPABILITY_AUTH = null;

    // Capability registration method
    public static void register() {
        CapabilityManager.INSTANCE.register(IAuthHandler.class, new Storage(), DefaultAuthHandler::new);
        MinecraftForge.EVENT_BUS.register(new AuthDataHandler());
    }

    // Attach provider to an entity in AttachCapabilities event handler
    @SubscribeEvent
    public void attachCapabilities (AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof EntityPlayer)
            event.addCapability(new ResourceLocation(ForgeLogin.MODID, "auth.isauth"), new Provider());
    }

    // Persist capability on player clone in ClonePlayer event handler
    @SubscribeEvent
    public void clonePlayer (PlayerEvent.Clone event) {
        final IAuthHandler original = getHandler(event.getOriginal());
        final IAuthHandler clone = getHandler(event.getEntity());
        clone.setAuthorized(original.getAuthorized());
    }

    // Get handler from entity
    public static IAuthHandler getHandler (Entity entity) {
        if (entity.hasCapability(CAPABILITY_AUTH, EnumFacing.DOWN)){
            return entity.getCapability(CAPABILITY_AUTH, EnumFacing.DOWN);
        } else {
            return null;
        }
    }

    // Auth interface
    public interface IAuthHandler {
        boolean getAuthorized();
        void setAuthorized (boolean authorized);
    }

    // Auth interface implementation
    public static class DefaultAuthHandler implements IAuthHandler {

        private boolean authorized;

        @Override
        public boolean getAuthorized () {
            return this.authorized;
        }

        @Override
        public void setAuthorized (boolean authorized) {
            this.authorized = authorized;
        }

    }

    // NBT read/write handler
    public static class Storage implements Capability.IStorage<IAuthHandler> {

        @Override
        public NBTBase writeNBT (Capability<IAuthHandler> cap, IAuthHandler instance, EnumFacing side) {
            final NBTTagCompound tag = new NBTTagCompound();
            tag.setBoolean(AUTH_NBT_KEY, instance.getAuthorized());
            return tag;
        }

        @Override
        public void readNBT (Capability<IAuthHandler> cap, IAuthHandler instance, EnumFacing side, NBTBase nbt) {
            final NBTTagCompound tag = (NBTTagCompound) nbt;
            instance.setAuthorized(tag.getBoolean(AUTH_NBT_KEY));
        }
    }

    // Delegates all of the system calls to capability
    public static class Provider implements ICapabilitySerializable<NBTTagCompound> {

        IAuthHandler instance = CAPABILITY_AUTH.getDefaultInstance();

        @Override
        public boolean hasCapability(Capability<?> cap, EnumFacing facing) {
            return cap == CAPABILITY_AUTH;
        }

        @Override
        public <T> T getCapability(Capability<T> cap, EnumFacing facing) {
            return hasCapability(cap, facing)? CAPABILITY_AUTH.<T>cast(instance) : null;
        }

        @Override
        public NBTTagCompound serializeNBT() {
            return (NBTTagCompound) CAPABILITY_AUTH.getStorage().writeNBT(CAPABILITY_AUTH, instance, null);
        }

        @Override
        public void deserializeNBT(NBTTagCompound nbt) {
            CAPABILITY_AUTH.getStorage().readNBT(CAPABILITY_AUTH, instance, null, nbt);
        }

    }

}
