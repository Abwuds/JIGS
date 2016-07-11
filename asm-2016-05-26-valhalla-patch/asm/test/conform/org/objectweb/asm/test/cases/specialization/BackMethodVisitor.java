package org.objectweb.asm.test.cases.specialization;


import org.objectweb.asm.Handle;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

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

    private static final Handle BSM_GETBACKFIELD;
    private static final Handle BSM_SETBACKFIELD;

    static {
        MethodType mtGetBackField = MethodType.methodType(CallSite.class, MethodHandles.Lookup.class, String.class, MethodType.class);
        MethodType mtSetBackField = MethodType.methodType(CallSite.class, MethodHandles.Lookup.class, String.class, MethodType.class);
        BSM_GETBACKFIELD = new Handle(Opcodes.H_INVOKESTATIC, "rt/RT", "bsm_getBackField", mtGetBackField.toMethodDescriptorString(), false);
        BSM_SETBACKFIELD = new Handle(Opcodes.H_INVOKESTATIC, "rt/RT", "bsm_setBackField", mtSetBackField.toMethodDescriptorString(), false);
    }

    // The name of the front class of the enclosing class.
    private final String frontOwner;
    private final String methodName;
    // The enclosing class name.
    private final String owner;
    private final InvokeAnyAdapter invokeAnyAdapter;

    BackMethodVisitor(int api, String methodName, String frontOwner, String owner, MethodVisitor mv) {
        super(api, mv);
        this.methodName = methodName;
        this.frontOwner = frontOwner;
        this.owner = owner;
        invokeAnyAdapter = new InvokeAnyAdapter(this);
    }

    /**
     * @return the current {@link BackMethodVisitor} enclosing class name.
     */
    public String getOwner() {
        return owner;
    }

    @Override
    public void visitTypeInsn(int opcode, String type) {
        if (!invokeAnyAdapter.visitTypeInsn(opcode, type)) {
            super.visitTypeInsn(opcode, type);
        }
    }

    @Override
    public void visitInsn(int opcode) {
        if (!invokeAnyAdapter.visitInsn(opcode)) {
            super.visitInsn(opcode);
        }
    }

    @Override
    public void visitMethodInsn(final int opcode, final String owner,
                                final String name, final String desc, final boolean itf) {
        if (!invokeAnyAdapter.visitMethodInsn(opcode, owner, name, desc, itf)) {
            super.visitMethodInsn(opcode, owner, name, desc, itf);
        }
    }

    @Override
    public void visitFieldInsn(int opcode, String owner, String name, String desc) {
        // The description is either an Object, a TypeVar, or a parameterized type.
        // In the last case, we don't want it to propagate to the underlying classWriter.
        owner = Type.rawName(owner);
        if (frontOwner.equals(owner)) { owner = this.owner; }

        // In case the field's owner is not the current back class, nothing is needed and it is
        // a regular field access. Same thing when treating the owner#<init> method,
        // the xxxfield opcodes must not be replaced by invokedynamic.
        if (methodName.equals("<init>")) {
            super.visitFieldInsn(opcode, owner, name, Type.rawDesc(desc));
            return;
        }
        if (!this.owner.equals(owner)) {
            super.visitFieldInsn(opcode, owner, name, Type.rawDesc(desc));
            return;
        }
        if (opcode == Opcodes.GETFIELD) {
            // Every getfield/putfield in method which are not <init>, is transformed in a getfield/putfield
            // on the back field. To do so, we pass the logic to an invokedynamic which will
            // get the field value or push the value in the field contained inside the back class.
            visitLdcInsn(name);
            // TODO in case this particular return descriptor is TypeVar or ParameterizedType, it has to be recorded inside the substitution table !
            //
            String returnDescriptor = Type.rawDesc(desc);
            visitInvokeDynamicInsn("getBackField", "(Ljava/lang/Object;Ljava/lang/String;)" + returnDescriptor, BSM_GETBACKFIELD, opcode);
            return;
        }

        if (opcode == Opcodes.PUTFIELD) {
            visitLdcInsn(name);
            visitInvokeDynamicInsn("setBackField", "(Ljava/lang/Object;" + desc + "Ljava/lang/String;)V", BSM_SETBACKFIELD, opcode);
            return;
        }

        // Neither GETFIELD nor PUTFIELD.
        super.visitFieldInsn(opcode, owner, name, Type.rawDesc(desc));
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
