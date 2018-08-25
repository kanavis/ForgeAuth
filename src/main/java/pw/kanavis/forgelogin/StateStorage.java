package pw.kanavis.forgelogin;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;

import java.util.Hashtable;


public class StateStorage {

    private Hashtable<String,EntityAnchor> anchors;

    public StateStorage() {
        this.anchors = new Hashtable<>();
    }

    /**
     * Tick all anchors
     */
    public void tickAnchors() {
        for (StateStorage.EntityAnchor anchor: this.anchors.values()) {
            anchor.tick();
        }
    }

    /**
     * Anchor entity
     */
    public void anchorEntity(Entity entity) {
        this.anchors.put(entity.getName(), new EntityAnchor(entity));
    }

    /**
     * Un-anchor entity
     */
    public void unAnchorEntity(Entity entity) {
        this.anchors.remove(entity.getName());
    }

    /**
     * Ancors entities position
     */
    public class EntityAnchor {
        private Entity entity;
        private BlockPos pos;

        public EntityAnchor(Entity entity) {
            this.entity = entity;
            this.pos = entity.getPosition();
        }

        public void tick() {
            this.entity.setPositionAndUpdate(this.pos.getX(), this.pos.getY(), this.pos.getZ());
        }
    }
}
