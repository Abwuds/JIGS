package specialization;

import org.objectweb.asm.Type;

import java.util.AbstractMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * A {@link ShiftMapAllSlotsOnly} contains every shift offsets used to shift all method's arguments.
 * Particularly in the case of an old *anyfied* method written inside a BackClassFactory.
 * It has the particularity to handle every cases contrary to the complex {@link ShiftMap}
 * shifting only certain values.
 * Created by Jefferson Mangue on 24/10/2016.
 */
class ShiftMapAllSlotsOnly {

    private final LinkedHashMap<Integer, Map.Entry<Integer, Boolean>> finalOffsets;

    private ShiftMapAllSlotsOnly(LinkedHashMap<Integer, Map.Entry<Integer, Boolean>> finalOffsets) {
        this.finalOffsets = finalOffsets;
    }

    static ShiftMapAllSlotsOnly createShiftMapTwoSlotsOnly(boolean isStatic, Type[] params) {
        // Computing final offsets of each parameter having an old offset. True if large, false otherwise.
        LinkedHashMap<Integer, Map.Entry<Integer, Boolean>> finalOffsets = new LinkedHashMap<>();
        // Param 0 in method of a Backfactory class is always the front this. Otherwise, (<init>) it has to start by 1,1.
        int offset = 0;
        int oldOffsets = 0;
        // If this is not a static method, this is not in params list, we have to put an artificial one.
        if (!isStatic) {
            finalOffsets.put(0, new AbstractMap.SimpleEntry<>(0, false));
            offset += 1;
            oldOffsets += 1;
        }
        for (Type param : params) {
            finalOffsets.put(oldOffsets, new AbstractMap.SimpleEntry<>(offset, true));
            oldOffsets += 2;
            offset += 2;
        }

        return new ShiftMapAllSlotsOnly(finalOffsets);

    }
}
