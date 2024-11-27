package io.xeros.content.instances;

import com.google.common.base.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;

/**
 * Class that manages instance height levels to separate instances by height.
 * Simply needed because old code wasn't written to have multiple instances
 * on the same height level and in the same coordinate. OSRS solves this by
 * having instances in unused map areas but this will have to suffice.
 */
public class InstanceHeight {

    private static final Logger logger = LoggerFactory.getLogger(InstanceHeight.class);

    /**
     * 0/4/8/12 are all height levels that will appear as the
     * base height level zero in the client.
     */
    private static final int STEP = 4;

    /**
     * Maximum height level we can go to.
     */
    private static final int MAXIMUM_HEIGHT = STEP * 2048;

    /**
     * Contains the currently occupied height levels.
     */
    private static final HashSet<Integer> RESERVED = new HashSet<>();

    /**
     * Get a free height level with a multiple of four between zero and {@link InstanceHeight#MAXIMUM_HEIGHT}
     * that must be reserved with {@link InstanceHeight#reserve(int)}.
     * @return a free height level.
     */
    public static int getFree() {
        for (int height = 0; height <= MAXIMUM_HEIGHT; height += STEP) {
            if (!RESERVED.contains(height)) {
                return height;
            }
        }

        throw new IllegalStateException("No open heights.");
    }

    /**
     * Reserve a height level so no other instance can use it until the instance is disposed,
     * in which case it will automatically call {@link InstanceHeight#free(int)}.
     * @param height the height level to reserve
     */
    public static void reserve(int height) {
        Preconditions.checkState(!RESERVED.contains(height), "Already reserved.");
        logger.debug("Reserved height level {}", height);
        RESERVED.add(height);
    }

    /**
     * Dispose of a height level, freeing it for other instances.
     * @param height the height level to free.
     */
    public static void free(int height) {
        logger.debug("Freed height level {}", height);
        RESERVED.remove(height);
    }

    /**
     * Get a free height level using {@link InstanceHeight#getFree()} and then reserves it
     * using {@link InstanceHeight#reserve(int)}.
     * @return the reserved height level.
     */
    public static int getFreeAndReserve() {
        int height = getFree();
        reserve(height);
        return height;
    }

    public static int getReservedCount() {
        return RESERVED.size();
    }
}
