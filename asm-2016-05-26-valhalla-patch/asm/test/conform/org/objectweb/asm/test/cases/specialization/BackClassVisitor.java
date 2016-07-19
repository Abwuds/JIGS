package org.objectweb.asm.test.cases.specialization;

import org.objectweb.asm.*;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.Objects;

/**
 *
 * Created by Jefferson Mangue on 09/06/2016.
 */
public class BackClassVisitor extends ClassVisitor {

    public static final String ANY_PACKAGE = "any/";
    public static final String BACK_FACTORY_NAME = "$BackFactory";
    public static final String RT_METHOD_HANDLE_TYPE = "RTMethodHandle";
    public static final String HANDLE_RT_BSM_NEW = "handle_rt_bsm_new";
    public static final String HANDLE_RT_BSM_GET_FIELD = "handle_rt_bsm_getField";
    public static final String HANDLE_RT_BSM_PUT_FIELD = "handle_rt_bsm_putField";
    public static final String BSM_RT_BRIDGE = "bsm_rtBridge";
    public static final String BSM_RT_BRIDGE_DESC = "(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodHandle;)Ljava/lang/invoke/CallSite;";

    // Sets when visiting the class prototype.
    private String name;
    private String frontName;

    BackClassVisitor(int api, ClassWriter cw) {
        super(api, cw);
        // Telling the substitution table contained inside the ClassWriter to register placeholder referencing method handles
        // of the RT package at runtime.
        cw.registerConstantPoolPlaceHolder(RT_METHOD_HANDLE_TYPE, HANDLE_RT_BSM_NEW);
        cw.registerConstantPoolPlaceHolder(RT_METHOD_HANDLE_TYPE, HANDLE_RT_BSM_GET_FIELD);
        cw.registerConstantPoolPlaceHolder(RT_METHOD_HANDLE_TYPE, HANDLE_RT_BSM_PUT_FIELD);
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        frontName = name;
        this.name = ANY_PACKAGE + name + BACK_FACTORY_NAME;
        super.visit(version, access, this.name, signature, superName, interfaces);
        visitBSMRTBridge();
    }

    @Override
    public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
        return super.visitField(access, name, desc, null, value); // No parameterized signature.
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        int methodAccess;
        // Performing the erasure first.
        desc = Type.eraseNotJavaLangMethod(desc);
        String methodDescriptor;
        if (name.equals("<init>")) {
            // We want to insert the front class lookup in each Back class species.
            methodAccess = access;
            methodDescriptor = desc;
        } else {
            // For each method of the back but the constructor, transforming it into a static method taking
            // in first parameter the front class.
            methodAccess = access + Opcodes.ACC_STATIC;
            // Inserting the front name. Erasure of : Type.getType('L' + frontName + ';')
            methodDescriptor =  insertMethodArgumentType(desc, Type.getType(Object.class));
        }
        return new BackMethodVisitor(api, name, frontName, this.name, super.visitMethod(methodAccess, name, methodDescriptor, null, exceptions));
    }

    public String getName() {
        return name;
    }

    private String insertMethodArgumentType(String desc, Type insertedType) {
        Type mType = Type.getMethodType(desc);
        Type[] argumentTypes = mType.getArgumentTypes();
        Type[] parameterTypes = new Type[argumentTypes.length + 1];
        parameterTypes[0] = insertedType;
        for (int i = 1; i < parameterTypes.length; i++) { parameterTypes[i] = argumentTypes[i - 1]; }
        return Type.getMethodDescriptor(mType.getReturnType(), parameterTypes);
    }

    private void visitBSMRTBridge() {
        // Visiting the inner bootstrap method.
        MethodVisitor mv = super.visitMethod(Opcodes.ACC_PRIVATE + Opcodes.ACC_STATIC, BSM_RT_BRIDGE, BSM_RT_BRIDGE_DESC,
                null, null);
        mv.visitCode();

        mv.visitVarInsn(Opcodes.ALOAD, 3);

        // Boxing arguments.
        mv.visitInsn(Opcodes.ICONST_3);
        mv.visitTypeInsn(Opcodes.ANEWARRAY, "java/lang/Object");
        mv.visitInsn(Opcodes.DUP);
        mv.visitInsn(Opcodes.ICONST_0);
        mv.visitVarInsn(Opcodes.ALOAD, 0);
        mv.visitInsn(Opcodes.AASTORE);
        mv.visitInsn(Opcodes.DUP);
        mv.visitInsn(Opcodes.ICONST_1);
        mv.visitVarInsn(Opcodes.ALOAD, 1);
        mv.visitInsn(Opcodes.AASTORE);
        mv.visitInsn(Opcodes.DUP);
        mv.visitInsn(Opcodes.ICONST_2);
        mv.visitVarInsn(Opcodes.ALOAD, 2);
        mv.visitInsn(Opcodes.AASTORE);

        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/invoke/MethodHandle", "invoke", "([Ljava/lang/Object;)Ljava/lang/Object;", false);
        mv.visitInsn(Opcodes.ARETURN);
        mv.visitMaxs(0, 0);
        mv.visitEnd();
    }
}
