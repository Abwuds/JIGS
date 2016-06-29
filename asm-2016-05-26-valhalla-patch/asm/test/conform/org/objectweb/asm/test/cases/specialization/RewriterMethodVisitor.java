package org.objectweb.asm.test.cases.specialization;


import org.objectweb.asm.*;

import java.lang.invoke.CallSite;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.*;
import java.util.AbstractMap.SimpleEntry;

/**
 * Created by Jefferson Mangue on 12/06/2016.
 */
class RewriterMethodVisitor extends MethodVisitor {

    private static final HashMap<Integer, List<Map.Entry<String, Integer>>> INSTRS = new HashMap<>();

    static {
        INSTRS.put(Opcodes.ARETURN, Arrays.asList(
                new SimpleEntry<>("I", Opcodes.IRETURN),
                new SimpleEntry<>("B", Opcodes.IRETURN),
                new SimpleEntry<>("S", Opcodes.IRETURN),
                new SimpleEntry<>("C", Opcodes.IRETURN),
                new SimpleEntry<>("Z", Opcodes.IRETURN),
                new SimpleEntry<>("J", Opcodes.LRETURN),
                new SimpleEntry<>("F", Opcodes.FRETURN),
                new SimpleEntry<>("D", Opcodes.DRETURN)));
        INSTRS.put(Opcodes.ALOAD, Arrays.asList(
                new SimpleEntry<>("I", Opcodes.ILOAD),
                new SimpleEntry<>("B", Opcodes.ILOAD),
                new SimpleEntry<>("S", Opcodes.ILOAD),
                new SimpleEntry<>("C", Opcodes.ILOAD),
                new SimpleEntry<>("Z", Opcodes.ILOAD),
                new SimpleEntry<>("J", Opcodes.LLOAD),
                new SimpleEntry<>("F", Opcodes.FLOAD),
                new SimpleEntry<>("D", Opcodes.DLOAD)));
        INSTRS.put(Opcodes.ALOAD_0, Arrays.asList(
                new SimpleEntry<>("I", Opcodes.ILOAD_0),
                new SimpleEntry<>("B", Opcodes.ILOAD_0),
                new SimpleEntry<>("S", Opcodes.ILOAD_0),
                new SimpleEntry<>("C", Opcodes.ILOAD_0),
                new SimpleEntry<>("Z", Opcodes.ILOAD_0),
                new SimpleEntry<>("J", Opcodes.LLOAD_0),
                new SimpleEntry<>("F", Opcodes.FLOAD_0),
                new SimpleEntry<>("D", Opcodes.DLOAD_0)));
        INSTRS.put(Opcodes.ALOAD_1, Arrays.asList(
                new SimpleEntry<>("I", Opcodes.ILOAD_1),
                new SimpleEntry<>("B", Opcodes.ILOAD_1),
                new SimpleEntry<>("S", Opcodes.ILOAD_1),
                new SimpleEntry<>("C", Opcodes.ILOAD_1),
                new SimpleEntry<>("Z", Opcodes.ILOAD_1),
                new SimpleEntry<>("J", Opcodes.LLOAD_1),
                new SimpleEntry<>("F", Opcodes.FLOAD_1),
                new SimpleEntry<>("D", Opcodes.DLOAD_1)));
        INSTRS.put(Opcodes.ALOAD_2, Arrays.asList(
                new SimpleEntry<>("I", Opcodes.ILOAD_2),
                new SimpleEntry<>("B", Opcodes.ILOAD_2),
                new SimpleEntry<>("S", Opcodes.ILOAD_2),
                new SimpleEntry<>("C", Opcodes.ILOAD_2),
                new SimpleEntry<>("Z", Opcodes.ILOAD_2),
                new SimpleEntry<>("J", Opcodes.LLOAD_2),
                new SimpleEntry<>("F", Opcodes.FLOAD_2),
                new SimpleEntry<>("D", Opcodes.DLOAD_2)));
        INSTRS.put(Opcodes.ALOAD_3, Arrays.asList(
                new SimpleEntry<>("I", Opcodes.ILOAD_3),
                new SimpleEntry<>("B", Opcodes.ILOAD_3),
                new SimpleEntry<>("S", Opcodes.ILOAD_3),
                new SimpleEntry<>("C", Opcodes.ILOAD_3),
                new SimpleEntry<>("Z", Opcodes.ILOAD_3),
                new SimpleEntry<>("J", Opcodes.LLOAD_3),
                new SimpleEntry<>("F", Opcodes.FLOAD_3),
                new SimpleEntry<>("D", Opcodes.DLOAD_3)));
        INSTRS.put(Opcodes.ASTORE, Arrays.asList(
                new SimpleEntry<>("I", Opcodes.ISTORE),
                new SimpleEntry<>("B", Opcodes.ISTORE),
                new SimpleEntry<>("S", Opcodes.ISTORE),
                new SimpleEntry<>("C", Opcodes.ISTORE),
                new SimpleEntry<>("Z", Opcodes.ISTORE),
                new SimpleEntry<>("J", Opcodes.LSTORE),
                new SimpleEntry<>("F", Opcodes.FSTORE),
                new SimpleEntry<>("D", Opcodes.DSTORE)));
        INSTRS.put(Opcodes.ASTORE_0, Arrays.asList(
                new SimpleEntry<>("I", Opcodes.ISTORE_0),
                new SimpleEntry<>("B", Opcodes.ISTORE_0),
                new SimpleEntry<>("S", Opcodes.ISTORE_0),
                new SimpleEntry<>("C", Opcodes.ISTORE_0),
                new SimpleEntry<>("Z", Opcodes.ISTORE_0),
                new SimpleEntry<>("J", Opcodes.LSTORE_0),
                new SimpleEntry<>("F", Opcodes.FSTORE_0),
                new SimpleEntry<>("D", Opcodes.DSTORE_0)));
        INSTRS.put(Opcodes.ASTORE_1, Arrays.asList(
                new SimpleEntry<>("I", Opcodes.ISTORE_1),
                new SimpleEntry<>("B", Opcodes.ISTORE_1),
                new SimpleEntry<>("S", Opcodes.ISTORE_1),
                new SimpleEntry<>("C", Opcodes.ISTORE_1),
                new SimpleEntry<>("Z", Opcodes.ISTORE_1),
                new SimpleEntry<>("J", Opcodes.LSTORE_1),
                new SimpleEntry<>("F", Opcodes.FSTORE_1),
                new SimpleEntry<>("D", Opcodes.DSTORE_1)));
        INSTRS.put(Opcodes.ASTORE_2, Arrays.asList(
                new SimpleEntry<>("I", Opcodes.ISTORE_2),
                new SimpleEntry<>("B", Opcodes.ISTORE_2),
                new SimpleEntry<>("S", Opcodes.ISTORE_2),
                new SimpleEntry<>("C", Opcodes.ISTORE_2),
                new SimpleEntry<>("Z", Opcodes.ISTORE_2),
                new SimpleEntry<>("J", Opcodes.LSTORE_2),
                new SimpleEntry<>("F", Opcodes.FSTORE_2),
                new SimpleEntry<>("D", Opcodes.DSTORE_2)));
        INSTRS.put(Opcodes.ASTORE_3, Arrays.asList(
                new SimpleEntry<>("I", Opcodes.ASTORE_3),
                new SimpleEntry<>("B", Opcodes.ASTORE_3),
                new SimpleEntry<>("S", Opcodes.ASTORE_3),
                new SimpleEntry<>("C", Opcodes.ASTORE_3),
                new SimpleEntry<>("Z", Opcodes.ASTORE_3),
                new SimpleEntry<>("J", Opcodes.ASTORE_3),
                new SimpleEntry<>("F", Opcodes.ASTORE_3),
                new SimpleEntry<>("D", Opcodes.ASTORE_3)));
        INSTRS.put(Opcodes.AALOAD, Arrays.asList(
                new SimpleEntry<>("I", Opcodes.IALOAD),
                new SimpleEntry<>("B", Opcodes.IALOAD),
                new SimpleEntry<>("S", Opcodes.IALOAD),
                new SimpleEntry<>("C", Opcodes.IALOAD),
                new SimpleEntry<>("Z", Opcodes.IALOAD),
                new SimpleEntry<>("J", Opcodes.LALOAD),
                new SimpleEntry<>("F", Opcodes.FALOAD),
                new SimpleEntry<>("D", Opcodes.DALOAD)));
        INSTRS.put(Opcodes.AASTORE, Arrays.asList(
                new SimpleEntry<>("I", Opcodes.IASTORE),
                new SimpleEntry<>("B", Opcodes.IASTORE),
                new SimpleEntry<>("S", Opcodes.IASTORE),
                new SimpleEntry<>("C", Opcodes.IASTORE),
                new SimpleEntry<>("Z", Opcodes.IASTORE),
                new SimpleEntry<>("J", Opcodes.LASTORE),
                new SimpleEntry<>("F", Opcodes.FASTORE),
                new SimpleEntry<>("D", Opcodes.DASTORE)));
        INSTRS.put(Opcodes.ACONST_NULL, Arrays.asList(
                new SimpleEntry<>("I", Opcodes.ICONST_0),
                new SimpleEntry<>("B", Opcodes.ICONST_0),
                new SimpleEntry<>("S", Opcodes.ICONST_0),
                new SimpleEntry<>("C", Opcodes.ICONST_0),
                new SimpleEntry<>("Z", Opcodes.ICONST_0),
                new SimpleEntry<>("J", Opcodes.LCONST_0),
                new SimpleEntry<>("F", Opcodes.FCONST_0),
                new SimpleEntry<>("D", Opcodes.DCONST_0)));
        // TODO case IF_ACMPEQ
        // TODO case IF_ACMPNE
        // TODO case ANEWARRAY
    }

    RewriterMethodVisitor(int api, MethodVisitor mv) {
        super(api, mv);
    }

    // An int is not sufficient. Because a stack level has multiple states possible.
    // First it has the boolean value "dup visited" to detect if the dup has been visited (and skipped) or not.
    // Otherwise all possible dup between the "new" opcode and the "invokespecial" will be skipped. .
    private final Stack<Integer> invokeSpecialStack = new Stack<>();

    @Override
    public void visitTypeInsn(int opcode, String type) {
        // TODO Detect if it is an invocation with typevar and so on.
        System.out.println("Type insn : " + type);
        if (opcode != Opcodes.NEW) {
            super.visitTypeInsn(opcode, type);
            return;
        }
        // Avoiding the write of the "NEW" bytecode since they are replaced by invokedynamic.
        System.out.println("opcode = [" + opcode + "], type = [" + type + "]");
       // invokeSpecialStack++;
    }
/*
    @Override
    public void visitInsn(int opcode) {
        // Skipping the opcode DUP inside
        if (invokeSpecialStack > 0 && opcode == Opcodes.DUP) {
            return;
        }
        super.visitInsn(opcode);
    }

    private static final Handle BSM_NEW;

    static {
        MethodType mt = MethodType.methodType(CallSite.class, MethodHandles.Lookup.class, String.class, MethodType.class, String.class);
        BSM_NEW = new Handle(Opcodes.H_INVOKESTATIC, "rt/RT", "bsm_new", mt.toMethodDescriptorString(), false);
    }

    @Override
    public void visitMethodInsn(final int opcode, final String owner,
                                final String name, final String desc, final boolean itf) {
        System.out.println("opcode = [" + opcode + "], owner = [" + owner + "], name = [" + name + "], desc = [" + desc + "], itf = [" + itf + "]");
        if (invokeSpecialStack > 0 && opcode == Opcodes.INVOKESPECIAL) {
            Type type = Type.getMethodType(desc);
            String ddesc = Type.getMethodType(Type.getObjectType(owner), type.getArgumentTypes()).toString();
            System.out.println(ddesc);
            visitInvokeDynamicInsn(name, ddesc, BSM_NEW, "I");
            invokeSpecialStack--;
            return;
        }
        super.visitMethodInsn(opcode, owner, name, desc, itf);
    }*/

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
