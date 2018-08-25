package pw.kanavis.forgelogin.auth;


import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;


public class AuthProvider implements ICapabilitySerializable<NBTTagCompound> {

    @CapabilityInject(IAuthHandler.class)
    public static final Capability<IAuthHandler> CAPABILITY_FORGELOGIN = null;

    IAuthHandler instance = CAPABILITY_FORGELOGIN.getDefaultInstance();

    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
        return capability == CAPABILITY_FORGELOGIN;
    }

    @Override
    public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
        return hasCapability(capability, facing) ? CAPABILITY_FORGELOGIN.<T>cast(instance) : null;
    }

    @Override
    public NBTTagCompound serializeNBT() {
        return (NBTTagCompound)CAPABILITY_FORGELOGIN.getStorage().writeNBT(CAPABILITY_FORGELOGIN, instance, null);
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt) {
        CAPABILITY_FORGELOGIN.getStorage().readNBT(CAPABILITY_FORGELOGIN, instance, null, nbt);
    }

}
