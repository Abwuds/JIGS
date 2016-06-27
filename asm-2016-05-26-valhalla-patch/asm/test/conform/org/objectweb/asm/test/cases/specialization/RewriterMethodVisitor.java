package org.objectweb.asm.test.cases.specialization;


import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.util.AbstractMap.SimpleEntry;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
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

    @Override
    public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
        super.visitMethodInsn(opcode, owner, name, desc, itf);
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
