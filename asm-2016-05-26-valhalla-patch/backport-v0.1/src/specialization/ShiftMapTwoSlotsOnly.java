package specialization;

import org.objectweb.asm.Type;

import java.util.*;

/**
 * A {@link ShiftMapTwoSlotsOnly} contains every shift offsets used to shift all method's arguments.
 * Particularly in the case of an old *anyfied* method written inside a BackClassFactory.
 * It has the particularity to handle every cases contrary to the complex {@link ShiftMapAnyToTwoSlots}
 * shifting only certain values.
 * Created by Jefferson Mangue on 24/10/2016.
 */
class ShiftMapTwoSlotsOnly implements ShiftMap {

    private final String methodName;
    private final LinkedHashMap<Integer, Map.Entry<Integer, Boolean>> finalOffsets;

    private ShiftMapTwoSlotsOnly(String methodName, LinkedHashMap<Integer, Map.Entry<Integer, Boolean>> finalOffsets) {
        this.methodName = methodName;
        this.finalOffsets = finalOffsets;
    }

    static ShiftMap createShiftMapTwoSlotsOnly(String methodName, Type[] params) {
        Objects.requireNonNull(methodName);
        // Computing final offsets of each parameter having an old offset. True if large, false otherwise.
        LinkedHashMap<Integer, Map.Entry<Integer, Boolean>> finalOffsets = new LinkedHashMap<>();
        // Param 0 in method of a Backfactory class is always the front this. Otherwise, (<init>) it has to start by 1,1.
        int offset = 0;
        int oldOffsets = 0;
        // If this is not a static method, this is not in params list, we have to put an artificial one.
        /*if (!isStatic) {
            finalOffsets.put(0, new AbstractMap.SimpleEntry<>(0, false));
            offset += 2;
            oldOffsets += 2;
        }*/
        for (Type ignored : params) {
            finalOffsets.put(oldOffsets, new AbstractMap.SimpleEntry<>(offset, true));
            oldOffsets += 2;
            offset += 2;
        }

        return new ShiftMapTwoSlotsOnly(methodName, finalOffsets);
    }


    @Override
    public String getMethodName() {
        return methodName;
    }

    @Override
    public int getNewParameterShifted(int parameter, boolean isLarge) {
       return ShiftMapAnyToTwoSlots.computeNewParameterShifted(parameter, true, finalOffsets, methodName);
    }
}
