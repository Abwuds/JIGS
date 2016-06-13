package org.objectweb.asm.test.cases.specialization;


import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.util.*;

/**
 * Created by Jefferson Mangue on 12/06/2016.
 */
public class RewriterMethodVisitor extends MethodVisitor {

    private static final HashMap<Integer, List<Map.Entry<String, Integer>>> INSTRS = new HashMap();

    static {
        INSTRS.put(Opcodes.ARETURN, Arrays.asList(new AbstractMap.SimpleEntry("I", Opcodes.IRETURN),
                                                new AbstractMap.SimpleEntry("B", Opcodes.IRETURN),
                                                new AbstractMap.SimpleEntry("S", Opcodes.IRETURN),
                                                new AbstractMap.SimpleEntry("C", Opcodes.IRETURN),
                                                new AbstractMap.SimpleEntry("Z", Opcodes.IRETURN),
                                                new AbstractMap.SimpleEntry("L", Opcodes.LRETURN),
                                                new AbstractMap.SimpleEntry("F", Opcodes.FRETURN),
                                                new AbstractMap.SimpleEntry("D", Opcodes.DRETURN)));
    }

    public RewriterMethodVisitor(int api, MethodVisitor mv) {
        super(api, mv);
    }


    @Override
    public void visitTypedInsn(String name, int typedOpcode) {
        Label end = new Label();
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
        visitLabel(end);
    }
}
