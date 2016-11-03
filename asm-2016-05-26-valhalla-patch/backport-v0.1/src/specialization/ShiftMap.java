package specialization;


import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

import java.util.*;

/**
 * A {@link ShiftMap} contains every shift offsets used to shift method's arguments which need
 * to get aligned on doubles and longs. Particularly in the case of an old *anyfied* method
 * written inside a BackClassFactory.
 * Created by Jefferson Mangue on 24/10/2016.
 */
public class ShiftMap {

    private final HashMap<Integer, ArrayList<AnyTernaryTuple>>[] data;

    private ShiftMap(HashMap<Integer, ArrayList<AnyTernaryTuple>>[] data) {
        this.data = data;
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

        AnyTernaryTuple(int[] tuple) {
            checkTuple(tuple);
            this.tuple = tuple;
            this.base3 = computeBase3(tuple);
            this.anySize = computeAnySize(tuple);
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
    }

    /**
     * Creates an instance of {@link ShiftMap} according to the given method descriptor.
     *
     * @param methodDescriptor the method descriptor which has to be of TYPE.METHOD.
     * @return the {@link ShiftMap} requested, correctly parameterized.
     */
    public static ShiftMap createShiftMap(Type methodDescriptor) {
        // Checking types.
        if (methodDescriptor.getSort() != Type.METHOD) {
            throw new IllegalArgumentException("The argument methodDescriptor has to be a METHOD instead of" +
                    methodDescriptor);
        }

        Type[] params = methodDescriptor.getArgumentTypes();
        int anyCount = numberOfUsefulAnyParameters(params); // Summing all any except the last one which involve no shifts.
        int largestAnySize = 2 * anyCount; // Summing all important any by their max size (2 for doubles/longs)
        HashMap<Integer, ArrayList<AnyTernaryTuple>>[] data = initHashMap(params);
        ArrayList<AnyTernaryTuple> instances = generateAllInstances(params);
        System.out.println(instances);


        for (AnyTernaryTuple tuple : instances) {
            int shiftOffset = largestAnySize - tuple.getAnySize();
            System.out.println("For tuple : " + tuple + " Moving at most of : " + shiftOffset + " offsets giving a largest any size of : " + largestAnySize);
            for (int paramPos = tuple.size() - 1; paramPos >= 0 && shiftOffset > 0; paramPos--) {
                if (tuple.get(paramPos) == 1) {
                    shiftOffset--;
                }
                if (shiftOffset == 0) {
                    break;
                }
                putOffsetInMap(data, paramPos, shiftOffset, tuple);

            }
        }
        return new ShiftMap(data);
    }

    private static HashMap<Integer, ArrayList<AnyTernaryTuple>>[] initHashMap(Type[] params) {
        int size = params.length;
        HashMap<Integer, ArrayList<AnyTernaryTuple>>[] hashMaps = new HashMap[size];
        for (int i = 0; i < size; i++) {
            hashMaps[i] = new HashMap<>();
        }
        return hashMaps;
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
     * @param instance the current instance filled. It will be duplicated for each TypeVariable contained in params.
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
     * @return the number of anyfied parameters useful, that are taking in account when shifting parameters inside the params list.
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

        static void writeHeader(ShiftMap map, MethodVisitor visitor) {
            Label end = new Label();

            HashMap<Integer, ArrayList<AnyTernaryTuple>>[] data = map.data;
            for (HashMap<Integer, ArrayList<AnyTernaryTuple>> offsets : data) {
                for (Map.Entry<Integer, ArrayList<AnyTernaryTuple>> e : offsets.entrySet()) {
                    // TODO
                }
            }
        }
    }
}
