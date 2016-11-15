package specialization;


import org.objectweb.asm.Label;
import org.objectweb.asm.Type;
import rt.Opcodes;

import java.util.*;

/**
 * A {@link ShiftMap} contains every shift offsets used to shift method's arguments which need
 * to get aligned on doubles and longs. Particularly in the case of an old *anyfied* method
 * written inside a BackClassFactory.
 * Created by Jefferson Mangue on 24/10/2016.
 */
public class ShiftMap {

    private final HashMap<Integer, ArrayList<AnyTernaryTuple>>[] data;
    /**
     * Either the i_eth parameter is any or not in the method's descriptor.
     */
    private final boolean[] isAny;
    private final String methodName;
    private final String methodDescriptor;
    private final String[] descriptors;
    private final LinkedHashMap<Integer, Map.Entry<Integer, Boolean>> finalOffsets;

    private ShiftMap(HashMap<Integer, ArrayList<AnyTernaryTuple>>[] data, boolean[] isAny, String methodName, String[] descriptors,
                     String methodDescriptor, LinkedHashMap<Integer, Map.Entry<Integer, Boolean>> finalOffsets) {
        this.data = data;
        this.isAny = isAny;
        this.methodName = methodName;
        this.methodDescriptor = methodDescriptor;
        this.descriptors = descriptors;
        this.finalOffsets = finalOffsets;
    }

    /**
     * A tuple where each entry is a int in {0, 1, 2} which can be interpreted as
     * a ternary number. It can be used to represent a method signature where
     * 0 is the size of a known type. 1 the size of a type variable instantiated
     * by a small primitive value. 2 the size of a type variable instantiated by a
     * long primitive value which takes 2 slots in the local_variable table like
     * long or double.
     */
    private static class AnyTernaryTuple {
        private final int[] tuple;
        private final int base3;
        private final int anySize;
        private final String encode;

        AnyTernaryTuple(int[] tuple) {
            checkTuple(tuple);
            this.tuple = tuple;
            this.base3 = computeBase3(tuple);
            this.anySize = computeAnySize(tuple);
            this.encode = computeEncode(tuple);
        }

        /**
         * The tuple has to be interpretable in a Base 3 value.
         */
        private void checkTuple(int[] tuple) {
            for (int i : tuple) {
                if (i < 0 || i > 3) {
                    throw new IllegalArgumentException("The tuple passed has to contain only elements in {0, 1, 2}.");
                }
            }
        }

        private String computeEncode(int[] tuple) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < tuple.length; i++) {
                sb.append(tuple[i]);
            }
            return sb.toString();
        }

        /**
         * Computes the base 3 representation of a given tuple.
         *
         * @param tuple the tuple computed in base three representation.
         * @return the base three representation of the given tuple.
         */
        private int computeBase3(int[] tuple) {
            int base3 = 0;
            for (int i = 0; i < tuple.length; i++) {
                base3 += Math.pow(3, i) * tuple[tuple.length - i - 1];
            }
            return base3;
        }

        int get(int position) {
            return tuple[position];
        }

        int getBase3() {
            return base3;
        }

        int size() {
            return tuple.length;
        }

        int getAnySize() {
            return anySize;
        }

        int[] getTuple() {
            return tuple;
        }

        private int computeAnySize(int[] tuple) {
            int result = 0;
            // The last parameter never count in this computation.
            for (int i = 0; i < tuple.length - 1; i++) {
                result += tuple[i];
            }
            return result;
        }

        @Override
        public String toString() {
            return "Tuple : [ " + Arrays.toString(tuple) + "]"; // ", anySize : " + anySize + " tuple : " + Arrays.toString(tuple) + " ]";
        }

        String getEncode() {
            return encode;
        }
    }


    /**
     * Creates an instance of {@link ShiftMap} according to the given method descriptor.
     *
     * @param methodDescriptor the method descriptor which has to be of TYPE.METHOD.
     * @return the {@link ShiftMap} requested, correctly parameterized.
     */
    static ShiftMap createShiftMap(String methodName, Type methodDescriptor, boolean isStatic) {
        System.out.println("Shifmap for method : " + methodName + " desc : " + methodDescriptor);
        // Checking types.
        if (methodDescriptor.getSort() != Type.METHOD) {
            throw new IllegalArgumentException("The argument methodDescriptor has to be a METHOD instead of" +
                    methodDescriptor);
        }
        Type[] params = methodDescriptor.getArgumentTypes();
        boolean[] isAny = computeIsAny(params);

        HashMap<Integer, ArrayList<AnyTernaryTuple>>[] data = computeShiftMapOffsets(params);
        String[] descriptors = new String[params.length];
        for (int i = 0; i < params.length; i++) {
            descriptors[i] = params[i].getDescriptor();
        }

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
            switch (param.getSort()) {
                case Type.TYPE_VAR:
                    finalOffsets.put(oldOffsets, new AbstractMap.SimpleEntry<>(offset, true));
                    oldOffsets += 1;
                    offset += 2;
                    break;
                case Type.DOUBLE:
                case Type.LONG:
                    finalOffsets.put(oldOffsets, new AbstractMap.SimpleEntry<>(offset, true));
                    oldOffsets += 2;
                    offset += 2;
                    break;
                default:
                    finalOffsets.put(oldOffsets, new AbstractMap.SimpleEntry<>(offset, false));
                    oldOffsets += 1;
                    offset += 1;
                    break;
            }
        }
        return new ShiftMap(data, isAny, methodName, descriptors, methodDescriptor.getDescriptor(), finalOffsets);
    }

    private static boolean[] computeIsAny(Type[] params) {
        boolean[] isAny = new boolean[params.length];
        for (int i = 0; i < params.length; i++) {
            isAny[i] = params[i].isTypeVar();
        }
        return isAny;
    }

    private static HashMap<Integer, ArrayList<AnyTernaryTuple>>[] computeShiftMapOffsets(Type[] params) {
        int anyCount = numberOfUsefulAnyParameters(params); // Summing all any except the last one which involve no shifts.
        int largestAnySize = 2 * anyCount; // Summing all important any by their max size (2 for doubles/longs)
        HashMap<Integer, ArrayList<AnyTernaryTuple>>[] data = initHashMap(params);
        ArrayList<AnyTernaryTuple> instances = generateAllInstances(params);


        for (AnyTernaryTuple tuple : instances) {
            int shiftOffset = largestAnySize - tuple.getAnySize();
            System.out.println("For tuple : " + tuple + " Moving at most of : " + shiftOffset + " offsets giving a largest any size of : " + largestAnySize);
            for (int paramPos = tuple.size() - 1; paramPos >= 0 && shiftOffset > 0; paramPos--) {
                // Decreasing the shift value. The last parameter does not count in the decrease step.
                if (paramPos < tuple.size() - 1 && tuple.get(paramPos) == 1) {
                    shiftOffset--;
                }
                if (shiftOffset == 0) {
                    break;
                }
                putOffsetInMap(data, paramPos, shiftOffset, tuple);

            }
        }
        return data;
    }

    private static HashMap<Integer, ArrayList<AnyTernaryTuple>>[] initHashMap(Type[] params) {
        int size = params.length;
        HashMap<Integer, ArrayList<AnyTernaryTuple>>[] hashMaps = new HashMap[size];
        for (int i = 0; i < size; i++) {
            hashMaps[i] = new HashMap<>();
        }
        return hashMaps;
    }


    int getNewParameterShifted(int parameter, boolean isLarge) {
        // Security because certain visit method uses the index -1 to mean "no index".
        if (parameter <= -1) {
            return parameter;
        }
        if (!finalOffsets.containsKey(parameter)) {
            System.err.println("Computing new offset for the parameter : " + parameter + " for the method : "
                    + methodName + " in the current map : " + finalOffsets);
            // Computing the new value :
            Iterator<Map.Entry<Integer, Boolean>> it = finalOffsets.values().iterator();
            Map.Entry<Integer, Boolean> last = null;
            while (it.hasNext()) {
                last = it.next();
            }
            if (last == null) {
                throw new IllegalStateException("Last element can not be null for the method : " + methodName + " in the map : " + finalOffsets);
            }
            finalOffsets.put(parameter, new AbstractMap.SimpleEntry<>(last.getKey() + (last.getValue() ? 2 : 1), isLarge));
        }
        return finalOffsets.get(parameter).getKey();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("ShiftMap : \n");
        for (int i = 0; i < data.length; i++) {
            sb.append("\tShift of param : ").append(i).append(" :\n");
            HashMap<Integer, ArrayList<AnyTernaryTuple>> m = data[i];
            for (Map.Entry<Integer, ArrayList<AnyTernaryTuple>> e : m.entrySet()) {
                sb.append("\t\tMove ").append(e.getKey()).append(" for : ").append(e.getValue());
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    private static void putOffsetInMap(HashMap<Integer, ArrayList<AnyTernaryTuple>>[] data, int paramPos, int shiftOffset, AnyTernaryTuple tuple) {
        HashMap<Integer, ArrayList<AnyTernaryTuple>> offsets = data[paramPos];
        if (offsets == null) {
            offsets = new HashMap<>();
            data[paramPos] = offsets;
        }
        ArrayList<AnyTernaryTuple> base3IDs = offsets.get(shiftOffset);
        if (base3IDs == null) {
            base3IDs = new ArrayList<>();
            offsets.put(shiftOffset, base3IDs);
        }
        base3IDs.add(tuple);
    }

    /**
     * Generates every instantiation of a list of parameters. Containing 0 for
     * every types except for {@link Type#TYPE_VAR}, instantiated with 1, or 2,
     * to simulate a small variable instantiation or a big one (long and double, taking 2 slots
     * in the local variable table).
     *
     * @param params the list of parameters to instantiate.
     * @return the list of instantiations, interpretable in Base 3.
     */
    private static ArrayList<AnyTernaryTuple> generateAllInstances(Type[] params) {
        ArrayList<AnyTernaryTuple> result = new ArrayList<>();
        generateInstance(params, result, new int[params.length], 0);
        return result;
    }

    /**
     * Recursive methods generating all possible instantiations.
     *
     * @param params   the list of parameters to instantiate.
     * @param tuples   the list filled which will contain every instantiation.
     * @param instance the current instance filled. It will be duplicated for each TypeVariable contained in descriptors.
     * @param index    the current index of the recursion.
     */
    private static void generateInstance(Type[] params, ArrayList<AnyTernaryTuple> tuples, int[] instance, int index) {
        // We have filled all the current Instance
        if (index == instance.length) {
            tuples.add(new AnyTernaryTuple(instance));
            return;
        }

        switch (params[index].getSort()) {
            case Type.TYPE_VAR:
                int[] instance2 = Arrays.copyOf(instance, instance.length);
                instance[index] = 1;
                instance2[index] = 2;
                generateInstance(params, tuples, instance, index + 1);
                generateInstance(params, tuples, instance2, index + 1);
                break;
            default:
                instance[index] = 0;
                generateInstance(params, tuples, instance, index + 1);
                break;
        }
    }

    /**
     * @param params
     * @return the number of anyfied parameters useful, that are taking in account when shifting parameters inside the descriptors list.
     * The last parameter is not useful since it does not involve in any shift.
     */
    private static int numberOfUsefulAnyParameters(Type[] params) {
        int count = 0;
        for (int i = 0; i < params.length - 1; i++) {
            count += params[i].getSort() == Type.TYPE_VAR ? 1 : 0;
        }
        return count;
    }

    /**
     * Dumps a {@link ShiftMap} using ASM.
     * Created by Jefferson Mangue on 24/10/2016.
     */
    static class ShiftMapDumper {

        static String dumpJavaDebugCode(ShiftMap map) {
            Objects.requireNonNull(map);
            StringBuilder sb = new StringBuilder("MethodHeader : \n");
            HashMap<Integer, ArrayList<AnyTernaryTuple>>[] data = map.data;
            for (int i = 0; i < data.length; i++) {
                HashMap<Integer, ArrayList<AnyTernaryTuple>> offsets = data[i];
                for (Map.Entry<Integer, ArrayList<AnyTernaryTuple>> e : offsets.entrySet()) {
                    sb.append("if (");
                    String separator = "";
                    for (AnyTernaryTuple tuple : e.getValue()) {
                        sb.append(separator).append(Arrays.toString(tuple.getTuple()));
                        separator = " v ";
                    }
                    sb.append(") {\n\tdec(param[").append(i).append("], ").append(e.getKey()).append(')').append("\n}\n");
                }
            }
            return sb.toString();
        }

        static void writeHeader(ShiftMap map, BackMethodVisitor visitor) {
            // Label end = new Label();

            HashMap<Integer, ArrayList<AnyTernaryTuple>>[] data = map.data;
            for (int i = data.length - 1; i >= 0; i--) {
                for (Map.Entry<Integer, ArrayList<AnyTernaryTuple>> e : data[i].entrySet()) {
                    // Offset and instantiations for this offset.
                    ArrayList<AnyTernaryTuple> instantiations = e.getValue();
                    Integer offset = e.getKey();
                    // Loading condition between the instantiation placeholder and the instantiation string.
                    // The key in the substitutionMap, the constant value to show in the class, the value in the substitutionMap, either this is a TypeVar manipulation or not.
                    String methodPrefix = map.methodName + '_';
                    visitor.visitLdcPlaceHolderString(BackClassVisitor.RT_METHOD_INSTANTIATION_TYPE_KEY, methodPrefix + "INSTANTIATION", methodPrefix + map.methodDescriptor, false); // Test on TX.
                    String instantiation = computeInstantiations(instantiations);
                    visitor.visitLdcPlaceHolderString(BackClassVisitor.RT_METHOD_INSTANTIATIONS_TYPE_TESTS, methodPrefix + instantiation, methodPrefix + instantiation, false);
                    Label label = new Label();
                    visitor.visitJumpInsn(Opcodes.IF_ACMPNE, label);
                    // Parameter index.
                    int j = i + 1;
                    if (map.isAny[i]) {
                        String descriptor = map.descriptors[i];
                        // Visiting any parameter.
                        visitor.visitTypeVarSpecialization(descriptor, Opcodes.ALOAD, map.getNewParameterShifted(j, true) - offset);
                        visitor.visitTypeVarSpecialization(descriptor, Opcodes.ASTORE, map.getNewParameterShifted(j, true));
                    } else {
                        // Visiting non any parameter.
                        visitor.superVisitVarInsn(Opcodes.ALOAD, j);
                        visitor.superVisitVarInsn(Opcodes.ASTORE, j + offset);
                    }
                    // Else.
                    visitor.visitLabel(label);
                }
            }
            // visitor.visitLabel(end);
        }

        private static String computeInstantiations(ArrayList<AnyTernaryTuple> tuples) {
            StringBuilder sb = new StringBuilder();
            String separator = "";
            for (AnyTernaryTuple tuple : tuples) {
                sb.append(separator).append(tuple.getEncode());
                separator = "_";
            }
            return sb.toString();
        }
    }
}
