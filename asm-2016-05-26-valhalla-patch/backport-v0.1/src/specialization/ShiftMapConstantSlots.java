package specialization;


import org.objectweb.asm.Label;
import org.objectweb.asm.Type;
import rt.Opcodes;

import java.util.*;

class ShiftMapConstantSlots implements ShiftMap {

    private final HashMap<Integer, ArrayList<AnyInstantiation>>[] data;
    /**
     * Either the i_eth parameter is any or not in the method's descriptor.
     */
    private final boolean[] isAny;
    private final String methodName;
    private final String methodDescriptor;
    private final String[] descriptors;
    private final LinkedHashMap<Integer, Map.Entry<Integer, Boolean>> finalOffsets;

    private ShiftMapConstantSlots(HashMap<Integer, ArrayList<AnyInstantiation>>[] data, boolean[] isAny, String methodName, String[] descriptors,
                                  String methodDescriptor, LinkedHashMap<Integer, Map.Entry<Integer, Boolean>> finalOffsets) {
        this.data = data;
        this.isAny = isAny;
        this.methodName = methodName;
        this.methodDescriptor = methodDescriptor;
        this.descriptors = descriptors;
        this.finalOffsets = finalOffsets;
    }

    /**
     * Tuple representing a method instantiation.
     */
    private static class AnyInstantiation {
        private final int[] tuple;
        private final String strRepr;

        AnyInstantiation(int[] tuple) {
            checkTuple(tuple);
            this.tuple = tuple;
            this.strRepr = computeStrRepr(tuple);
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

        private String computeStrRepr(int[] tuple) {
            StringBuilder sb = new StringBuilder();
            for (int aTuple : tuple) {
                sb.append(aTuple);
            }
            return sb.toString();
        }

        int get(int position) {
            return tuple[position];
        }

        int size() {
            return tuple.length;
        }

        @Override
        public String toString() {
            return "Tuple : [ " + Arrays.toString(tuple) + "]"; // ", maxShift : " + maxShift + " tuple : " + Arrays.toString(tuple) + " ]";
        }

        String getStrRepr() {
            return strRepr;
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof AnyInstantiation && Arrays.equals(((AnyInstantiation) obj).tuple, tuple);
        }
    }


    /**
     * Creates an instance of {@link ShiftMapAnyToTwoSlots} according to the given method descriptor.
     *
     * @param methodDescriptor the method descriptor which has to be of TYPE.METHOD.
     * @return the {@link ShiftMapAnyToTwoSlots} requested, correctly parameterized.
     */
    static ShiftMapConstantSlots createInstanceMethodShiftMap(String methodName, Type methodDescriptor) {
        if (methodDescriptor.getSort() != Type.METHOD) {
            throw new IllegalArgumentException("The argument methodDescriptor has to be a METHOD instead of" +
                    methodDescriptor);
        }

        // Adding "this" to the non static methods like the constructor.
        Type[] params = methodDescriptor.getArgumentTypes();
        Type[] paramsWithThis = new Type[params.length + 1];
        System.arraycopy(params, 0, paramsWithThis, 1, params.length);
        paramsWithThis[0] = Type.getType(Object.class);
        params = paramsWithThis;

        return createShiftMap(methodName, methodDescriptor, params);
    }

    static ShiftMapConstantSlots createStaticMethodShiftMap(String methodName, Type methodDescriptor) {
        // Checking types.
        if (methodDescriptor.getSort() != Type.METHOD) {
            throw new IllegalArgumentException("The argument methodDescriptor has to be a METHOD instead of" +
                    methodDescriptor);
        }
        Type[] params = methodDescriptor.getArgumentTypes();
        return createShiftMap(methodName, methodDescriptor, params);
    }

    private static ShiftMapConstantSlots createShiftMap(String methodName, Type methodDescriptor, Type[] params) {

        boolean[] isAny = computeIsAny(params);
        HashMap<Integer, ArrayList<AnyInstantiation>>[] data = computeShiftMapPositions(params);
        String[] descriptors = new String[params.length];
        for (int i = 0; i < params.length; i++) {
            descriptors[i] = params[i].getDescriptor();
        }

        // Computing final offsets of each parameter having an old offset. True if large, false otherwise.
        LinkedHashMap<Integer, Map.Entry<Integer, Boolean>> finalOffsets = computeFinalOffsets(params);
        return new ShiftMapConstantSlots(data, isAny, methodName, descriptors, methodDescriptor.getDescriptor(), finalOffsets);
    }

    private static LinkedHashMap<Integer, Map.Entry<Integer, Boolean>> computeFinalOffsets(Type[] params) {
        LinkedHashMap<Integer, Map.Entry<Integer, Boolean>> finalOffsets = new LinkedHashMap<>();
        int offset = 0;
        int oldOffsets = 0;
        for (Type param : params) {
            finalOffsets.put(oldOffsets, new AbstractMap.SimpleEntry<>(offset, true));
            switch (param.getSort()) {
                case Type.DOUBLE:
                case Type.LONG:
                    oldOffsets += 2;
                    offset += 2;
                    break;
                default:
                    oldOffsets += 1;
                    offset += 2;
                    break;
            }
        }
        return finalOffsets;
    }

    private static boolean[] computeIsAny(Type[] params) {
        boolean[] isAny = new boolean[params.length];
        for (int i = 0; i < params.length; i++) {
            isAny[i] = params[i].isTypeVar();
        }
        return isAny;
    }

    /**
     * Computes every offset to shift according to the corresponding instantiations.
     *
     * @param params
     * @return
     */
    private static HashMap<Integer, ArrayList<AnyInstantiation>>[] computeShiftMapPositions(Type[] params) {
        HashMap<Integer, ArrayList<AnyInstantiation>>[] data = initInnerHashMap(params);
        ArrayList<AnyInstantiation> instances = generateAllInstances(params);

        // Going through the parameters and detecting their dynamic indices according to all the possible
        // instantiations. Then saving the couples in a map for each parameters : index -> instantiations.
        for (AnyInstantiation tuple : instances) {
            int oldIndex = 0;
            for (int paramNumber = 0; paramNumber < tuple.size(); paramNumber++) {
                // Adding old indices.
                if (oldIndex > 0) {
                    putOldIndexWithInstantiation(data, paramNumber, oldIndex, tuple);
                }
                oldIndex += tuple.get(paramNumber);
            }
        }
        return data;
    }

    private static void putOldIndexWithInstantiation(HashMap<Integer, ArrayList<AnyInstantiation>>[] data, int paramNumber,
                                                     int oldIndex, AnyInstantiation instantiation) {
        HashMap<Integer, ArrayList<AnyInstantiation>> offsets = data[paramNumber];
        assert offsets != null;
        ArrayList<AnyInstantiation> instantiations = offsets.get(oldIndex);
        if (instantiations == null) {
            instantiations = new ArrayList<>();
            offsets.put(oldIndex, instantiations);
        }
        instantiations.add(instantiation);
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
    private static ArrayList<AnyInstantiation> generateAllInstances(Type[] params) {
        ArrayList<AnyInstantiation> result = new ArrayList<>();
        generateInstance(params, result, new int[params.length], 0);
        return result;
    }

    /**
     * Recursive methods generating all possible instantiations.
     * <p>
     * 1 or 2 are the size of the parameters' sizes.
     *
     * @param params   the list of parameters to instantiate.
     * @param tuples   the list filled which will contain every instantiation.
     * @param instance the current instance filled. It will be duplicated for each TypeVariable contained in descriptors.
     * @param index    the current index of the recursion.
     */
    private static void generateInstance(Type[] params, ArrayList<AnyInstantiation> tuples, int[] instance, int index) {
        // We have filled all the current Instance
        if (index == instance.length) {
            tuples.add(new AnyInstantiation(instance));
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
            case Type.DOUBLE:
            case Type.LONG:
                instance[index] = 2;
                generateInstance(params, tuples, instance, index + 1);
                break;
            default:
                instance[index] = 1;
                generateInstance(params, tuples, instance, index + 1);
                break;
        }
    }

    private static HashMap<Integer, ArrayList<AnyInstantiation>>[] initInnerHashMap(Type[] params) {
        int size = params.length;
        HashMap<Integer, ArrayList<AnyInstantiation>>[] hashMaps = new HashMap[size];
        for (int i = 0; i < size; i++) {
            hashMaps[i] = new HashMap<>();
        }
        return hashMaps;
    }

    @Override
    public String getMethodName() {
        return methodName;
    }

    @Override
    public int getNewParameterIndex(int parameter, boolean isLarge) {
        // Security because certain visit method uses the index -1 to mean "no index".
        return computeNewParameterShifted(parameter, isLarge, finalOffsets, methodName);
    }

    private static int computeNewParameterShifted(int parameter, boolean isLarge, LinkedHashMap<Integer, Map.Entry<Integer, Boolean>> finalOffsets, String methodName) {
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
        StringBuilder sb = new StringBuilder("ShiftMapAnyToTwoSlots : \n");
        for (int i = 0; i < data.length; i++) {
            sb.append("\tShift of param : ").append(i).append(" :\n");
            HashMap<Integer, ArrayList<AnyInstantiation>> m = data[i];
            for (Map.Entry<Integer, ArrayList<AnyInstantiation>> e : m.entrySet()) {
                sb.append("\t\tMove ").append(e.getKey()).append(" for : ").append(e.getValue());
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    @Override
    public String dump() {
        return "No dump for now.";
    }


    @Override
    public void writeHeader(BackMethodVisitor backMethodVisitor) {
        ShiftMapDumper.writeHeader(this, backMethodVisitor);
    }

    /**
     * Dumps a {@link ShiftMapAnyToTwoSlots} using ASM.
     * Created by Jefferson Mangue on 24/10/2016.
     */
    private static class ShiftMapDumper {

        static void writeHeader(ShiftMapConstantSlots map, BackMethodVisitor visitor) {
            HashMap<Integer, ArrayList<AnyInstantiation>>[] data = map.data;
            // For each arguments.
            for (int i = data.length - 1; i >= 0; i--) {
                // For each offsets.
                for (Map.Entry<Integer, ArrayList<AnyInstantiation>> entries : data[i].entrySet()) {
                    Integer oldIndex = entries.getKey();
                    ArrayList<AnyInstantiation> instantiations = entries.getValue();
                    String instantiation = computeInstantiations(instantiations);
                    // Loading condition between the instantiation placeholder and the instantiation string.
                    // The key in the substitutionMap, the constant value to show in the class, the value in the substitutionMap, either this is a TypeVar manipulation or not.
                    String methodPrefix = map.methodName + '_';
                    visitor.visitLdcPlaceHolderString(BackClassVisitor.RT_METHOD_INSTANTIATION_TYPE_KEY, methodPrefix + "INSTANTIATION", methodPrefix + map.methodDescriptor, false); // Test on TX.
                    visitor.visitLdcPlaceHolderString(BackClassVisitor.RT_METHOD_INSTANTIATIONS_TYPE_TESTS, methodPrefix + instantiation, methodPrefix + instantiation, false);
                    Label label = new Label();
                    visitor.visitJumpInsn(Opcodes.IF_ACMPNE, label);
                    // Parameter index.
                    // "This" is not present in non static method, (only <init> here) so i = 0 is not the first param, i + 1 is the first param.
                    if (map.isAny[i]) {
                        String descriptor = map.descriptors[i];
                        // Visiting any parameter.
                        int from = oldIndex;
                        int to = map.getNewParameterIndex(i );
                        visitor.visitShiftTypeVar(descriptor, from, to);
                    } else {
                        // Visiting non any parameter.
                        int from = j;
                        int to = from + oldIndex;
                        visitor.superVisitVarInsn(Opcodes.ALOAD, from);
                        visitor.superVisitVarInsn(Opcodes.ASTORE, to);
                    }
                    // Else.
                    visitor.visitLabel(label);
                }
            }
            // visitor.visitLabel(end);
        }

        private static String computeInstantiations(ArrayList<AnyInstantiation> tuples) {
            StringBuilder sb = new StringBuilder();
            String separator = "";
            for (AnyInstantiation tuple : tuples) {
                sb.append(separator).append(tuple.getStrRepr());
                separator = "_";
            }
            return sb.toString();
        }
    }
}
