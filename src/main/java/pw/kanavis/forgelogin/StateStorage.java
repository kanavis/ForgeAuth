package pw.kanavis.forgelogin;

import jdk.nashorn.internal.ir.Block;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;

import java.util.Hashtable;


public class StateStorage {

    private Hashtable<String,IEntityAnchor> anchors;

    public StateStorage() {
        this.anchors = new Hashtable<>();
    }

    /**
     * Tick all anchors
     */
    public void tickAnchors() {
        for (StateStorage.IEntityAnchor anchor: this.anchors.values()) {
            anchor.tick();
        }
    }

    /**
     * Anchor entity on it's position
     */
    public void anchorEntityPos(Entity entity) {
        this.anchors.put(entity.getName(), new EntityAnchorPos(entity));
    }

    /**
     * Anchor entity on spawn point
     */
    /*public void anchorEntitySpawn(Entity entity) {
        this.anchors.put(entity.getName(), new EntityAnchorSpawn(entity));
    }*/

    /**
     * Un-anchor entity
     */
    public void unAnchorEntity(Entity entity) {
        this.anchors.get(entity.getName()).unlock();
        this.anchors.remove(entity.getName());
    }

    /**
     * Ancors entities position
    */
    interface IEntityAnchor {
        void tick();
        void unlock();
    }
    public class EntityAnchorPos implements IEntityAnchor {
        protected Entity entity;
        protected BlockPos pos;

        public EntityAnchorPos(Entity entity) {
            this.entity = entity;
            this.pos = getPos();
        }

        protected BlockPos getPos() {
            return this.entity.getPosition();
        }

        public void tick() {
            this.entity.setPositionAndUpdate(this.pos.getX(), this.pos.getY(), this.pos.getZ());
        }
        public void unlock() {}
    }
    /*public class EntityAnchorSpawn extends EntityAnchorPos {
        protected BlockPos oldPos;
        protected int oldDimension;

        public EntityAnchorSpawn(Entity entity) {
            super(entity);
            this.oldPos = entity.getPosition();
            this.oldDimension = entity.dimension;
        }

        @Override
        protected BlockPos getPos() {
            BlockPos pos = this.entity.getEntityWorld().getSpawnPoint();
            pos = pos.add(0, 200, 0);
            if (this.entity.dimension != 0) {
                this.entity.setPortal(pos);
                this.entity.changeDimension(0);
            }
            return pos;
        }

        @Override
        public void unlock() {
            if (this.oldDimension != this.entity.dimension) {
                this.entity.changeDimension(this.oldDimension);
            }
            this.entity.motionY = 0;
            this.entity.setPositionAndUpdate(this.oldPos.getX(), this.oldPos.getY(), this.oldPos.getZ());
        }

    }*/

}
