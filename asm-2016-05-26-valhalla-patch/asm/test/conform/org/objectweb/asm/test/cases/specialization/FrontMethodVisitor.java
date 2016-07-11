package org.objectweb.asm.test.cases.specialization;


import org.objectweb.asm.Handle;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.lang.invoke.CallSite;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

/**
 * Created by Jefferson Mangue on 12/06/2016.
 */
class FrontMethodVisitor extends MethodVisitor {
    /**
     * Invokedynamic constants.
     */
    private static final String BSM_NAME = "newSpeciesObject";
    private static final Handle BSM_NEW_BACK;
    private static final Handle BSM_DELEGATE_CALL;

    static {
        MethodType mtNewBack = MethodType.methodType(CallSite.class, MethodHandles.Lookup.class, String.class, MethodType.class, String.class);
        MethodType mtDelegateCall = MethodType.methodType(CallSite.class, MethodHandles.Lookup.class, String.class, MethodType.class);
        BSM_NEW_BACK = new Handle(Opcodes.H_INVOKESTATIC, "rt/RT", "bsm_newSpeciesObject", mtNewBack.toMethodDescriptorString(), false);
        BSM_DELEGATE_CALL = new Handle(Opcodes.H_INVOKESTATIC, "rt/RT", "bsm_delegateCall", mtDelegateCall.toMethodDescriptorString(), false);
    }

    private FrontMethodVisitor(int api, MethodVisitor mv) {
        super(api, mv);
    }

    public static void visitFrontMethod(int api, String frontName, String methodName, String desc, String signature, String[] exceptions, MethodVisitor fmw) {
        System.out.println("visitFrontMethod api = [" + api + "], frontName = [" + frontName + "], methodName = [" + methodName + "], desc = [" + desc + "], signature = [" + signature + "], exceptions = [" + exceptions + "], fmw = [" + fmw + "]");
        MethodVisitor mv = new FrontMethodVisitor(api, fmw);
        Type type = Type.getType(desc);
        // For the front method :
        if (methodName.equals("<init>")) {
            // Creating compatibility constructor.
            mv.visitCode();
            mv.visitInsn(Opcodes.ALOAD_0);// PutField on this for the field _back__.

            // TODO Change the constructor "Void, Object" because we can not pass null to Void.

            // Loading constructor arguments.
            Type[] argumentTypes = loadArguments(mv, type);
            String indyDescriptor = Type.getMethodDescriptor(Type.getType("Ljava/lang/Object;"), argumentTypes);
            mv.visitInvokeDynamicInsn(BSM_NAME, indyDescriptor, BSM_NEW_BACK, frontName);
            mv.visitFieldInsn(Opcodes.PUTFIELD, frontName, FrontClassVisitor.BACK_FIELD, "Ljava/lang/Object;");

            mv.visitInsn(Opcodes.RETURN);
            mv.visitMaxs(0, 0);
            mv.visitEnd();
        } else {
            // TODO handle static method elsewhere.
            // Instance methods :
            mv.visitCode();
            // Getting the back field to delegate the call.
            mv.visitLdcInsn(methodName);
            mv.visitInsn(Opcodes.ALOAD_0);
            mv.visitFieldInsn(Opcodes.GETFIELD, frontName, FrontClassVisitor.BACK_FIELD, "Ljava/lang/Object;");
            // Delegating the call and the arguments.
            mv.visitInsn(Opcodes.ALOAD_0);
            loadArguments(mv, type);
            String delegateDesc = createDelegateCallDescriptor(type, 'L' + frontName + ';');
            mv.visitInvokeDynamicInsn("delegateCall", delegateDesc, BSM_DELEGATE_CALL);

            // The return.
            Type returnType = type.getReturnType();
            int sort = returnType.getSort();
            if (sort == Type.OBJECT || sort == Type.TYPE_VAR || sort == Type.PARAMETERIZED_TYPE) {
                mv.visitInsn(Opcodes.ARETURN);
            } else {
                mv.visitInsn(Opcodes.RETURN);
            }
            mv.visitMaxs(0, 0);
            mv.visitEnd();
        }
    }

    private static String createDelegateCallDescriptor(Type type, String frontDesc) {
        Type[] argsSrc = type.getArgumentTypes();
        Type[] args = new Type[argsSrc.length + 3];
        args[0] = Type.getType("Ljava/lang/String;"); // method name.
        args[1] = Type.getType("Ljava/lang/Object;"); // receiver.
        args[2] = Type.getType(frontDesc); // receiver.
        // args[2] = Type.getType("[Ljava/lang/Object;"); // receiver.
        System.arraycopy(argsSrc, 0, args, 3, argsSrc.length); // method args.
        return Type.getMethodDescriptor(type.getReturnType(), args);
    }

    private static Type[] loadArguments(MethodVisitor mv, Type type) {
        Type[] argumentTypes = type.getArgumentTypes();
        // TODO REGISTER the argument if it is a typeVar value so we will be able to modify the bsm signature !!
        for (int i = 0; i < argumentTypes.length; i++) {
            Type arg = argumentTypes[i];
            int sort = arg.getSort();
            switch (sort) {
                case Type.BOOLEAN:
                case Type.SHORT:
                case Type.BYTE:
                case Type.INT:
                    mv.visitVarInsn(Opcodes.ILOAD, i + 1);
                    break;
                case Type.LONG:
                    mv.visitVarInsn(Opcodes.LLOAD, i + 1);
                    break;
                case Type.DOUBLE:
                    mv.visitVarInsn(Opcodes.DLOAD, i + 1);
                    break;
                case Type.OBJECT:
                case Type.TYPE_VAR:
                case Type.PARAMETERIZED_TYPE:
                    mv.visitVarInsn(Opcodes.ALOAD, i + 1);
                    break;
                default:
                    throw new AssertionError("Type not handled : " + sort);
            }
        }
        return argumentTypes;
    }

    public static void createFrontSpecializerConstructor(String rawName, String backField, MethodVisitor mv) {
        mv.visitCode();
        mv.visitInsn(Opcodes.ALOAD_0);
        mv.visitInsn(Opcodes.ALOAD_2);
        mv.visitFieldInsn(Opcodes.PUTFIELD, rawName, backField, "Ljava/lang/Object;");
        mv.visitInsn(Opcodes.RETURN);
        mv.visitMaxs(0, 0);
        mv.visitEnd();
    }
}
