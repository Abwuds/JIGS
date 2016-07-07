package org.objectweb.asm.test.cases.specialization;


import org.objectweb.asm.*;

import java.lang.invoke.CallSite;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.*;

/**
 * Created by Jefferson Mangue on 12/06/2016.
 */
class BackMethodVisitor extends MethodVisitor {

    private static final HashMap<Integer, List<Map.Entry<String, Integer>>> INSTRS = new HashMap<>();

    static {
        INSTRS.put(Opcodes.ARETURN, Arrays.asList(
                new AbstractMap.SimpleEntry<>("I", Opcodes.IRETURN),
                new AbstractMap.SimpleEntry<>("B", Opcodes.IRETURN),
                new AbstractMap.SimpleEntry<>("S", Opcodes.IRETURN),
                new AbstractMap.SimpleEntry<>("C", Opcodes.IRETURN),
                new AbstractMap.SimpleEntry<>("Z", Opcodes.IRETURN),
                new AbstractMap.SimpleEntry<>("J", Opcodes.LRETURN),
                new AbstractMap.SimpleEntry<>("F", Opcodes.FRETURN),
                new AbstractMap.SimpleEntry<>("D", Opcodes.DRETURN)));
        INSTRS.put(Opcodes.ALOAD, Arrays.asList(
                new AbstractMap.SimpleEntry<>("I", Opcodes.ILOAD),
                new AbstractMap.SimpleEntry<>("B", Opcodes.ILOAD),
                new AbstractMap.SimpleEntry<>("S", Opcodes.ILOAD),
                new AbstractMap.SimpleEntry<>("C", Opcodes.ILOAD),
                new AbstractMap.SimpleEntry<>("Z", Opcodes.ILOAD),
                new AbstractMap.SimpleEntry<>("J", Opcodes.LLOAD),
                new AbstractMap.SimpleEntry<>("F", Opcodes.FLOAD),
                new AbstractMap.SimpleEntry<>("D", Opcodes.DLOAD)));
        INSTRS.put(Opcodes.ALOAD_0, Arrays.asList(
                new AbstractMap.SimpleEntry<>("I", Opcodes.ILOAD_0),
                new AbstractMap.SimpleEntry<>("B", Opcodes.ILOAD_0),
                new AbstractMap.SimpleEntry<>("S", Opcodes.ILOAD_0),
                new AbstractMap.SimpleEntry<>("C", Opcodes.ILOAD_0),
                new AbstractMap.SimpleEntry<>("Z", Opcodes.ILOAD_0),
                new AbstractMap.SimpleEntry<>("J", Opcodes.LLOAD_0),
                new AbstractMap.SimpleEntry<>("F", Opcodes.FLOAD_0),
                new AbstractMap.SimpleEntry<>("D", Opcodes.DLOAD_0)));
        INSTRS.put(Opcodes.ALOAD_1, Arrays.asList(
                new AbstractMap.SimpleEntry<>("I", Opcodes.ILOAD_1),
                new AbstractMap.SimpleEntry<>("B", Opcodes.ILOAD_1),
                new AbstractMap.SimpleEntry<>("S", Opcodes.ILOAD_1),
                new AbstractMap.SimpleEntry<>("C", Opcodes.ILOAD_1),
                new AbstractMap.SimpleEntry<>("Z", Opcodes.ILOAD_1),
                new AbstractMap.SimpleEntry<>("J", Opcodes.LLOAD_1),
                new AbstractMap.SimpleEntry<>("F", Opcodes.FLOAD_1),
                new AbstractMap.SimpleEntry<>("D", Opcodes.DLOAD_1)));
        INSTRS.put(Opcodes.ALOAD_2, Arrays.asList(
                new AbstractMap.SimpleEntry<>("I", Opcodes.ILOAD_2),
                new AbstractMap.SimpleEntry<>("B", Opcodes.ILOAD_2),
                new AbstractMap.SimpleEntry<>("S", Opcodes.ILOAD_2),
                new AbstractMap.SimpleEntry<>("C", Opcodes.ILOAD_2),
                new AbstractMap.SimpleEntry<>("Z", Opcodes.ILOAD_2),
                new AbstractMap.SimpleEntry<>("J", Opcodes.LLOAD_2),
                new AbstractMap.SimpleEntry<>("F", Opcodes.FLOAD_2),
                new AbstractMap.SimpleEntry<>("D", Opcodes.DLOAD_2)));
        INSTRS.put(Opcodes.ALOAD_3, Arrays.asList(
                new AbstractMap.SimpleEntry<>("I", Opcodes.ILOAD_3),
                new AbstractMap.SimpleEntry<>("B", Opcodes.ILOAD_3),
                new AbstractMap.SimpleEntry<>("S", Opcodes.ILOAD_3),
                new AbstractMap.SimpleEntry<>("C", Opcodes.ILOAD_3),
                new AbstractMap.SimpleEntry<>("Z", Opcodes.ILOAD_3),
                new AbstractMap.SimpleEntry<>("J", Opcodes.LLOAD_3),
                new AbstractMap.SimpleEntry<>("F", Opcodes.FLOAD_3),
                new AbstractMap.SimpleEntry<>("D", Opcodes.DLOAD_3)));
        INSTRS.put(Opcodes.ASTORE, Arrays.asList(
                new AbstractMap.SimpleEntry<>("I", Opcodes.ISTORE),
                new AbstractMap.SimpleEntry<>("B", Opcodes.ISTORE),
                new AbstractMap.SimpleEntry<>("S", Opcodes.ISTORE),
                new AbstractMap.SimpleEntry<>("C", Opcodes.ISTORE),
                new AbstractMap.SimpleEntry<>("Z", Opcodes.ISTORE),
                new AbstractMap.SimpleEntry<>("J", Opcodes.LSTORE),
                new AbstractMap.SimpleEntry<>("F", Opcodes.FSTORE),
                new AbstractMap.SimpleEntry<>("D", Opcodes.DSTORE)));
        INSTRS.put(Opcodes.ASTORE_0, Arrays.asList(
                new AbstractMap.SimpleEntry<>("I", Opcodes.ISTORE_0),
                new AbstractMap.SimpleEntry<>("B", Opcodes.ISTORE_0),
                new AbstractMap.SimpleEntry<>("S", Opcodes.ISTORE_0),
                new AbstractMap.SimpleEntry<>("C", Opcodes.ISTORE_0),
                new AbstractMap.SimpleEntry<>("Z", Opcodes.ISTORE_0),
                new AbstractMap.SimpleEntry<>("J", Opcodes.LSTORE_0),
                new AbstractMap.SimpleEntry<>("F", Opcodes.FSTORE_0),
                new AbstractMap.SimpleEntry<>("D", Opcodes.DSTORE_0)));
        INSTRS.put(Opcodes.ASTORE_1, Arrays.asList(
                new AbstractMap.SimpleEntry<>("I", Opcodes.ISTORE_1),
                new AbstractMap.SimpleEntry<>("B", Opcodes.ISTORE_1),
                new AbstractMap.SimpleEntry<>("S", Opcodes.ISTORE_1),
                new AbstractMap.SimpleEntry<>("C", Opcodes.ISTORE_1),
                new AbstractMap.SimpleEntry<>("Z", Opcodes.ISTORE_1),
                new AbstractMap.SimpleEntry<>("J", Opcodes.LSTORE_1),
                new AbstractMap.SimpleEntry<>("F", Opcodes.FSTORE_1),
                new AbstractMap.SimpleEntry<>("D", Opcodes.DSTORE_1)));
        INSTRS.put(Opcodes.ASTORE_2, Arrays.asList(
                new AbstractMap.SimpleEntry<>("I", Opcodes.ISTORE_2),
                new AbstractMap.SimpleEntry<>("B", Opcodes.ISTORE_2),
                new AbstractMap.SimpleEntry<>("S", Opcodes.ISTORE_2),
                new AbstractMap.SimpleEntry<>("C", Opcodes.ISTORE_2),
                new AbstractMap.SimpleEntry<>("Z", Opcodes.ISTORE_2),
                new AbstractMap.SimpleEntry<>("J", Opcodes.LSTORE_2),
                new AbstractMap.SimpleEntry<>("F", Opcodes.FSTORE_2),
                new AbstractMap.SimpleEntry<>("D", Opcodes.DSTORE_2)));
        INSTRS.put(Opcodes.ASTORE_3, Arrays.asList(
                new AbstractMap.SimpleEntry<>("I", Opcodes.ASTORE_3),
                new AbstractMap.SimpleEntry<>("B", Opcodes.ASTORE_3),
                new AbstractMap.SimpleEntry<>("S", Opcodes.ASTORE_3),
                new AbstractMap.SimpleEntry<>("C", Opcodes.ASTORE_3),
                new AbstractMap.SimpleEntry<>("Z", Opcodes.ASTORE_3),
                new AbstractMap.SimpleEntry<>("J", Opcodes.ASTORE_3),
                new AbstractMap.SimpleEntry<>("F", Opcodes.ASTORE_3),
                new AbstractMap.SimpleEntry<>("D", Opcodes.ASTORE_3)));
        INSTRS.put(Opcodes.AALOAD, Arrays.asList(
                new AbstractMap.SimpleEntry<>("I", Opcodes.IALOAD),
                new AbstractMap.SimpleEntry<>("B", Opcodes.IALOAD),
                new AbstractMap.SimpleEntry<>("S", Opcodes.IALOAD),
                new AbstractMap.SimpleEntry<>("C", Opcodes.IALOAD),
                new AbstractMap.SimpleEntry<>("Z", Opcodes.IALOAD),
                new AbstractMap.SimpleEntry<>("J", Opcodes.LALOAD),
                new AbstractMap.SimpleEntry<>("F", Opcodes.FALOAD),
                new AbstractMap.SimpleEntry<>("D", Opcodes.DALOAD)));
        INSTRS.put(Opcodes.AASTORE, Arrays.asList(
                new AbstractMap.SimpleEntry<>("I", Opcodes.IASTORE),
                new AbstractMap.SimpleEntry<>("B", Opcodes.IASTORE),
                new AbstractMap.SimpleEntry<>("S", Opcodes.IASTORE),
                new AbstractMap.SimpleEntry<>("C", Opcodes.IASTORE),
                new AbstractMap.SimpleEntry<>("Z", Opcodes.IASTORE),
                new AbstractMap.SimpleEntry<>("J", Opcodes.LASTORE),
                new AbstractMap.SimpleEntry<>("F", Opcodes.FASTORE),
                new AbstractMap.SimpleEntry<>("D", Opcodes.DASTORE)));
        INSTRS.put(Opcodes.ACONST_NULL, Arrays.asList(
                new AbstractMap.SimpleEntry<>("I", Opcodes.ICONST_0),
                new AbstractMap.SimpleEntry<>("B", Opcodes.ICONST_0),
                new AbstractMap.SimpleEntry<>("S", Opcodes.ICONST_0),
                new AbstractMap.SimpleEntry<>("C", Opcodes.ICONST_0),
                new AbstractMap.SimpleEntry<>("Z", Opcodes.ICONST_0),
                new AbstractMap.SimpleEntry<>("J", Opcodes.LCONST_0),
                new AbstractMap.SimpleEntry<>("F", Opcodes.FCONST_0),
                new AbstractMap.SimpleEntry<>("D", Opcodes.DCONST_0)));
        // TODO case IF_ACMPEQ
        // TODO case IF_ACMPNE
        // TODO case ANEWARRAY
    }

    /**
     * Enumeration used for the detection of invoke special calls.
     * This enumeration indicates if an eligible NEW opcodes sequence, for the substitution by an invokedynamic
     * has been visited and then the same for DUP opcode. Only a NEW applied on generics is selected to be replaced.
     * Other new have to be considered and ignored since they must not be replaced.
     */
    private enum InvokeSpecialVisited {
        REPLACED_NEW, REPLACED_DUP, IGNORED_NEW, IGNORED_DUP
    }

    // An int is not sufficient. Because a stack level has multiple states possible.
    // First it has the boolean value "dup visited" to detect if the dup has been visited (and skipped) or not.
    // Otherwise all possible dup between the "new" opcode and the "invokespecial" will be skipped. .
    private final Stack<InvokeSpecialVisited> invokeSpecialStack = new Stack<>();
    // The enclosing class name.
    private final String owner;

    BackMethodVisitor(int api, String owner, MethodVisitor mv) {
        super(api, mv);
        this.owner = owner;
    }

    /**
     * @return the current {@link BackMethodVisitor} enclosing class name.
     */
    public String getOwner() {
        return owner;
    }

    @Override
    public void visitTypeInsn(int opcode, String type) {
        System.out.println("visitTypeInsn : opcode = [" + opcode + "], type = [" + type + "]");
        if (opcode != Opcodes.NEW) {
            super.visitTypeInsn(opcode, type);
            return;
        }

        // Ignoring this new since it does not manipulate generics.
        if (!type.startsWith("$")) {
            invokeSpecialStack.push(InvokeSpecialVisited.IGNORED_NEW);
            super.visitTypeInsn(opcode, Type.rawName(type));
            return;
        }

        // Replacing the "NEW" opcode since it will be replaced by invokedynamic.
        invokeSpecialStack.push(InvokeSpecialVisited.REPLACED_NEW);
    }

    @Override
    public void visitInsn(int opcode) {
        if (opcode != Opcodes.DUP || invokeSpecialStack.empty()) {
            super.visitInsn(opcode);
            return;
        }
        // Replacing the DUP opcode corresponding to a NEW opcode replaced.
        if (InvokeSpecialVisited.REPLACED_NEW.equals(invokeSpecialStack.peek())) {
            invokeSpecialStack.set(invokeSpecialStack.size() - 1, InvokeSpecialVisited.REPLACED_DUP);
            return;
        }
        // Ignoring the DUP opcode corresponding to a NEW opcode ignored.
        if (InvokeSpecialVisited.IGNORED_NEW.equals(invokeSpecialStack.peek())) {
            invokeSpecialStack.set(invokeSpecialStack.size() - 1, InvokeSpecialVisited.IGNORED_DUP);
            super.visitInsn(opcode);
            return;
        }
    }

    private static final Handle BSM_NEW;

    static {
        MethodType mt = MethodType.methodType(CallSite.class, MethodHandles.Lookup.class, String.class, MethodType.class, String.class);
        BSM_NEW = new Handle(Opcodes.H_INVOKESTATIC, "rt/RT", "bsm_new", mt.toMethodDescriptorString(), false);
    }

    @Override
    public void visitMethodInsn(final int opcode, final String owner,
                                final String name, final String desc, final boolean itf) {
        if (opcode != Opcodes.INVOKESPECIAL) {
            // Writing the call inside the class.
            super.visitMethodInsn(opcode, owner, name, desc, itf);
        }

        // Detect "new" call to substitute it by an invokedynamic.
        if (!invokeSpecialStack.empty()) {
            InvokeSpecialVisited top = invokeSpecialStack.peek();
            if (InvokeSpecialVisited.REPLACED_DUP.equals(top)) {
                Type type = Type.getMethodType(desc);
                String ddesc = Type.translateMethodDescriptor(Type.getMethodType(Type.getType(owner),
                        type.getArgumentTypes()).toString());
                visitInvokeDynamicInsn(name, ddesc, BSM_NEW, ddesc); // TODO use Type inside the BM to parse desc.
                invokeSpecialStack.pop();
                return;
            }
            // IGNORED_DUP. Popping the current stack level.
            invokeSpecialStack.pop();
        }
    }

    @Override
    public void visitTypedInsn(String name, int typedOpcode) {
        super.visitTypedInsn(name, typedOpcode);
        super.visitInsn(typedOpcode);
       /* Label end = new Label();
        List<Map.Entry<String, Integer>> tests = INSTRS.get(typedOpcode);
        if (tests == null) {
            throw new IllegalArgumentException("Invalid Opcode following TYPED instruction : " + typedOpcode);
        }

        for (Map.Entry<String, Integer> test : tests) {
            String key = test.getKey();
            int newOpcode = test.getValue();
            visitLdcInsn(key);
            visitLdcInsn(name);
            visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/String", "equals", "(Ljava/lang/Object;)Z", false);
            Label label = new Label();
            visitJumpInsn(Opcodes.IFNE, label);
            visitInsn(newOpcode);
            visitJumpInsn(Opcodes.GOTO, end);
            visitLabel(label);
        }

        // If none of them worked, doing the original then.
        visitInsn(typedOpcode);
        visitLabel(end);*/
    }
}
