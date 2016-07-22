package org.objectweb.asm.test.cases.specialization;


import jdk.nashorn.internal.runtime.regexp.joni.constants.OPCode;
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
                new AbstractMap.SimpleEntry<>("I", Opcodes.ILOAD),
                new AbstractMap.SimpleEntry<>("B", Opcodes.ILOAD),
                new AbstractMap.SimpleEntry<>("S", Opcodes.ILOAD),
                new AbstractMap.SimpleEntry<>("C", Opcodes.ILOAD),
                new AbstractMap.SimpleEntry<>("Z", Opcodes.ILOAD),
                new AbstractMap.SimpleEntry<>("J", Opcodes.LLOAD),
                new AbstractMap.SimpleEntry<>("F", Opcodes.FLOAD),
                new AbstractMap.SimpleEntry<>("D", Opcodes.DLOAD)));
        INSTRS.put(Opcodes.ALOAD_1, Arrays.asList(
                new AbstractMap.SimpleEntry<>("I", Opcodes.ILOAD),
                new AbstractMap.SimpleEntry<>("B", Opcodes.ILOAD),
                new AbstractMap.SimpleEntry<>("S", Opcodes.ILOAD),
                new AbstractMap.SimpleEntry<>("C", Opcodes.ILOAD),
                new AbstractMap.SimpleEntry<>("Z", Opcodes.ILOAD),
                new AbstractMap.SimpleEntry<>("J", Opcodes.LLOAD),
                new AbstractMap.SimpleEntry<>("F", Opcodes.FLOAD),
                new AbstractMap.SimpleEntry<>("D", Opcodes.DLOAD)));
        INSTRS.put(Opcodes.ALOAD_2, Arrays.asList(
                new AbstractMap.SimpleEntry<>("I", Opcodes.ILOAD),
                new AbstractMap.SimpleEntry<>("B", Opcodes.ILOAD),
                new AbstractMap.SimpleEntry<>("S", Opcodes.ILOAD),
                new AbstractMap.SimpleEntry<>("C", Opcodes.ILOAD),
                new AbstractMap.SimpleEntry<>("Z", Opcodes.ILOAD),
                new AbstractMap.SimpleEntry<>("J", Opcodes.LLOAD),
                new AbstractMap.SimpleEntry<>("F", Opcodes.FLOAD),
                new AbstractMap.SimpleEntry<>("D", Opcodes.DLOAD)));
        INSTRS.put(Opcodes.ALOAD_3, Arrays.asList(
                new AbstractMap.SimpleEntry<>("I", Opcodes.ILOAD),
                new AbstractMap.SimpleEntry<>("B", Opcodes.ILOAD),
                new AbstractMap.SimpleEntry<>("S", Opcodes.ILOAD),
                new AbstractMap.SimpleEntry<>("C", Opcodes.ILOAD),
                new AbstractMap.SimpleEntry<>("Z", Opcodes.ILOAD),
                new AbstractMap.SimpleEntry<>("J", Opcodes.LLOAD),
                new AbstractMap.SimpleEntry<>("F", Opcodes.FLOAD),
                new AbstractMap.SimpleEntry<>("D", Opcodes.DLOAD)));
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
                new AbstractMap.SimpleEntry<>("I", Opcodes.ISTORE),
                new AbstractMap.SimpleEntry<>("B", Opcodes.ISTORE),
                new AbstractMap.SimpleEntry<>("S", Opcodes.ISTORE),
                new AbstractMap.SimpleEntry<>("C", Opcodes.ISTORE),
                new AbstractMap.SimpleEntry<>("Z", Opcodes.ISTORE),
                new AbstractMap.SimpleEntry<>("J", Opcodes.LSTORE),
                new AbstractMap.SimpleEntry<>("F", Opcodes.FSTORE),
                new AbstractMap.SimpleEntry<>("D", Opcodes.DSTORE)));
        INSTRS.put(Opcodes.ASTORE_1, Arrays.asList(
                new AbstractMap.SimpleEntry<>("I", Opcodes.ISTORE),
                new AbstractMap.SimpleEntry<>("B", Opcodes.ISTORE),
                new AbstractMap.SimpleEntry<>("S", Opcodes.ISTORE),
                new AbstractMap.SimpleEntry<>("C", Opcodes.ISTORE),
                new AbstractMap.SimpleEntry<>("Z", Opcodes.ISTORE),
                new AbstractMap.SimpleEntry<>("J", Opcodes.LSTORE),
                new AbstractMap.SimpleEntry<>("F", Opcodes.FSTORE),
                new AbstractMap.SimpleEntry<>("D", Opcodes.DSTORE)));
        INSTRS.put(Opcodes.ASTORE_2, Arrays.asList(
                new AbstractMap.SimpleEntry<>("I", Opcodes.ISTORE),
                new AbstractMap.SimpleEntry<>("B", Opcodes.ISTORE),
                new AbstractMap.SimpleEntry<>("S", Opcodes.ISTORE),
                new AbstractMap.SimpleEntry<>("C", Opcodes.ISTORE),
                new AbstractMap.SimpleEntry<>("Z", Opcodes.ISTORE),
                new AbstractMap.SimpleEntry<>("J", Opcodes.LSTORE),
                new AbstractMap.SimpleEntry<>("F", Opcodes.FSTORE),
                new AbstractMap.SimpleEntry<>("D", Opcodes.DSTORE)));
        INSTRS.put(Opcodes.ASTORE_3, Arrays.asList(
                new AbstractMap.SimpleEntry<>("I", Opcodes.ISTORE),
                new AbstractMap.SimpleEntry<>("B", Opcodes.ISTORE),
                new AbstractMap.SimpleEntry<>("S", Opcodes.ISTORE),
                new AbstractMap.SimpleEntry<>("C", Opcodes.ISTORE),
                new AbstractMap.SimpleEntry<>("Z", Opcodes.ISTORE),
                new AbstractMap.SimpleEntry<>("J", Opcodes.LSTORE),
                new AbstractMap.SimpleEntry<>("F", Opcodes.FSTORE),
                new AbstractMap.SimpleEntry<>("D", Opcodes.DSTORE)));
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
        INSTRS.put(Opcodes.ANEWARRAY, Arrays.asList(
                new AbstractMap.SimpleEntry<>("I", Opcodes.T_INT),
                new AbstractMap.SimpleEntry<>("B", Opcodes.T_BYTE),
                new AbstractMap.SimpleEntry<>("S", Opcodes.T_SHORT),
                new AbstractMap.SimpleEntry<>("C", Opcodes.T_CHAR),
                new AbstractMap.SimpleEntry<>("Z", Opcodes.T_BOOLEAN),
                new AbstractMap.SimpleEntry<>("J", Opcodes.T_LONG),
                new AbstractMap.SimpleEntry<>("F", Opcodes.T_FLOAT),
                new AbstractMap.SimpleEntry<>("D", Opcodes.T_DOUBLE)));
        // TODO case IF_ACMPEQ
        // TODO case IF_ACMPNE
        // TODO case MULTIANEWARRAY ??
    }

    // The name of the front class of the enclosing class.
    private final String frontOwner;
    private final String methodName;
    // The enclosing class name.
    private final String owner;
    private final InvokeAnyAdapter invokeAnyAdapter;
    private final Handle bsmRTBridge;

    // Variable for the anewarray substitution.
    private boolean isInstallingANewArray;
    private Label end;

    BackMethodVisitor(int api, String methodName, String frontOwner, String owner, MethodVisitor mv) {
        super(api, mv);
        this.methodName = methodName;
        this.frontOwner = frontOwner;
        this.owner = owner;
        invokeAnyAdapter = new InvokeAnyAdapter(this);
        bsmRTBridge = new Handle(Opcodes.H_INVOKESTATIC, owner, BackClassVisitor.BSM_RT_BRIDGE, BackClassVisitor.BSM_RT_BRIDGE_DESC, false);
    }

    /**
     * @return the current {@link BackMethodVisitor} enclosing class name.
     */
    public String getOwner() {
        return owner;
    }

    @Override
    public void visitTypeInsn(int opcode, String type) {
        if (opcode == Opcodes.CHECKCAST && isInstallingANewArray) {
            visitTypedTypeInsn(Opcodes.CHECKCAST, "CHECKCAST ON " + type.substring(0, 2), type);
            visitLabel(end);
            isInstallingANewArray = false;
            return;
        }
        if (!invokeAnyAdapter.visitTypeInsn(opcode, type)) {
            super.visitTypeInsn(opcode, type);
            return;
        }
    }

    @Override
    public void visitLdcInsn(Object cst) {
        super.visitLdcInsn(cst);
    }

    @Override
    public void visitVarInsn(int opcode, int var) {
        super.visitVarInsn(opcode, var);
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

            String returnDescriptor = Type.rawDesc(desc); // Removing parameterized types.
            // Normally inserting the front owner : "(L" + frontOwner + ";". But instead inserting its Object erasure.
            visitInvokeDynamicInsn(name, "(Ljava/lang/Object;)" + returnDescriptor, bsmRTBridge, BackClassVisitor.HANDLE_RT_BSM_GET_FIELD);
            return;
        }

        if (opcode == Opcodes.PUTFIELD) {
            visitInvokeDynamicInsn(name, "(Ljava/lang/Object;" + desc + ")V", bsmRTBridge, BackClassVisitor.HANDLE_RT_BSM_PUT_FIELD);
            return;
        }

        // Neither GETFIELD nor PUTFIELD.
        super.visitFieldInsn(opcode, owner, name, Type.rawDesc(desc));
    }

    private void noReplacedTyped(String name, int typedOpcode) {
        super.visitTypedInsn(name, typedOpcode);
        // TODO replace this by the switch of typed opcode.
        if (typedOpcode <= Opcodes.ALOAD_0 || typedOpcode <= Opcodes.ALOAD_3){
            visitVarInsn(Opcodes.ALOAD, typedOpcode - Opcodes.ALOAD_0);
            return;
        }
        visitInsn(typedOpcode);
    }

    @Override
    public void visitTypedInsn(String name, int typedOpcode) {
       // noReplacedTyped(name, typedOpcode);

        end = new Label();
        List<Map.Entry<String, Integer>> tests = INSTRS.get(typedOpcode);
        if (tests == null) {
            throw new IllegalArgumentException("Invalid Opcode following TYPED instruction : " + typedOpcode);
        }

        for (Map.Entry<String, Integer> test : tests) {
            String key = test.getKey();
            int newOpcode = test.getValue();
            visitLdcInsn(key);
            visitLdcTypedString("Type test on : " + name.substring(0, 2), name); // Test on TX.
            visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/String", "equals", "(Ljava/lang/Object;)Z", false);
            Label label = new Label();
            visitJumpInsn(Opcodes.IFEQ, label);
            switch(typedOpcode) {
                case Opcodes.ALOAD_0:
                case Opcodes.ALOAD_1:
                case Opcodes.ALOAD_2:
                case Opcodes.ALOAD_3:
                    printASMMsg("Choosing : " + name + " Type : " + newOpcode, this);
                    visitVarInsn(newOpcode, typedOpcode - Opcodes.ALOAD_0);
                    break;
                case Opcodes.ASTORE_0:
                case Opcodes.ASTORE_1:
                case Opcodes.ASTORE_2:
                case Opcodes.ASTORE_3:
                    printASMMsg("Choosing : " + name + " Type : " + newOpcode, this);
                    visitVarInsn(newOpcode, typedOpcode - Opcodes.ASTORE_0);
                    break;
                case Opcodes.AASTORE:
                case Opcodes.AALOAD:
                    printASMMsg("Choosing : " + name + " Type : " + newOpcode, this);
                    visitInsn(newOpcode);
                    break;
                case Opcodes.ARETURN:
                    printASMMsg("Choosing : " + name + " Type : " + newOpcode, this);
                    visitInsn(newOpcode);
                    break;
                case Opcodes.ANEWARRAY:
                    visitIntInsn(Opcodes.NEWARRAY, newOpcode);
                    printASMMsg("Choosing : " + name + " Type : " + newOpcode, this);
                    break;
                default:
                    // TODO ACONST_NULL, AASTORE, AALOAD.
                    System.err.println("BackMethodVisitor#visitTypedInsn : TypedCode not handled : " + typedOpcode);
                    break;
            }
            visitJumpInsn(Opcodes.GOTO, end);
            visitLabel(label);
        }

        // If none of them worked, doing the original then.
        printASMMsg("Choosing : " + name + " Type[Object] : " + typedOpcode, this);
        // TODO MULTIANEWARRAY !!
        if (typedOpcode == Opcodes.ANEWARRAY) {
            isInstallingANewArray = true;
            visitTypedTypeInsn(Opcodes.ANEWARRAY, "ANEWARRAY ON : " + name.substring(0, 2), name);
            // Visiting the following CHECKCAST and visitLabel(end) inside the BackMethodVisitor#visitTypeIns to not
            // Put the end label before the following CHECKCAST, and also retrieve right informations for it.
        } else {
            visitInsn(typedOpcode);
            visitLabel(end);
        }
    }

    private static void printASMMsg(String msg, MethodVisitor mv) {
        mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
        mv.visitLdcInsn(msg);
        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);
    }

}
