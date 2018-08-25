
package pw.kanavis.forgelogin.auth;


import net.minecraftforge.common.capabilities.Capability;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;


public class AuthStorage implements Capability.IStorage<IAuthHandler>  {

    private static String ISAUTH_KEY = "forgelogin.auth.isauth";

    @Override
    public NBTBase writeNBT (Capability<IAuthHandler> capability, IAuthHandler instance, EnumFacing side) {
        final NBTTagCompound tag = new NBTTagCompound();
        tag.setBoolean(ISAUTH_KEY, instance.getAuthorized());
        return tag;
    }

    @Override
    public void readNBT (Capability<IAuthHandler> capability, IAuthHandler instance, EnumFacing side, NBTBase nbt) {
        final NBTTagCompound tag = (NBTTagCompound) nbt;
        instance.setAuthorized(tag.getBoolean(ISAUTH_KEY));
    }

}
