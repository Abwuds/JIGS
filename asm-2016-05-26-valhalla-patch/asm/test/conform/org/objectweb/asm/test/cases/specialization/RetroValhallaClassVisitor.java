package org.objectweb.asm.test.cases.specialization;

import org.objectweb.asm.*;


/**
 *
 * Created by Jefferson Mangue on 09/06/2016.
 */
class RetroValhallaClassVisitor extends ClassVisitor {

    public static final int API = Opcodes.ASM5;
    public static final String BACK_FIELD = "_back__";
    public static final String ANY_PACKAGE = "java/any/";
    public static final String BACK_FACTORY_NAME = "_BackFactory";
    private static final int COMPILER_VERSION = 52;
    private final ClassWriter backClassWriter;
    private final BackClassVisitor backClassVisitor;

    // Created classes names.
    private String name;
    private String backFactoryName;

    public RetroValhallaClassVisitor(ClassVisitor cv) {
        super(API, cv);
        backClassWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        this.backClassVisitor = new BackClassVisitor(backClassWriter);
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        // At this step, only anyfied classes starts with the token '$'.
        if (!name.startsWith("$")) {
            this.name = name;
            super.visit(COMPILER_VERSION, access, name, signature, superName, interfaces);
            return;
        }
        // The class is anyfied. Cleaning the class name into the raw name.
        String rawName = Type.rawName(name);
        this.name = rawName;
        // The inheritance is not handled for anyfied class yet.
        if (superName != null && !superName.equals("java/lang/Object")) {
            throw new IllegalStateException("Not inheritance allowed.");
        }
        super.visit(COMPILER_VERSION, access, rawName, signature, superName, interfaces);
        // Creating the back class inside the any package, by concatenating "_BackFactory".
        // Now creating a back factory class, placed inside the package java/any".
        createBackClassVisitor(version, rawName);
        // Creating an Object field inside the class. It will be used to store the back class at runtime.
        createBackField();
        createFrontSpecializationConstructor(rawName);
    }

    @Override
    public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
        // Each fields are moved inside the back class.
        return backClassVisitor.visitField(access, name, desc, signature, value);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        // If we have not created a back class, we only copy the method.
        // TODO the calling code can have special invokeDynamic to insert in place of invokespecial.
        if (!hasBackFactory()) { return new InvokeCompatibilityMethodVisitor(api, super.visitMethod(access, name, desc, signature, exceptions)); }
        // We have to turn every method into static method inside the back class.
        return createRetroValhallaMethodVisitor(access, name, desc, signature, exceptions);
    }

    @Override
    public void visitInnerClass(String name, String outerName, String innerName, int access) {
        super.visitInnerClass(Type.rawName(name), outerName, innerName, access);
    }

    public byte[] getBackFactoryBytes() {
        return backClassWriter.toByteArray();
    }

    public boolean hasBackFactory() {
        return backFactoryName != null;
    }

    public String getBackFactoryName() {
        return backFactoryName;
    }

    private void createBackClassVisitor(int version, String rawName) {
        backFactoryName = ANY_PACKAGE + rawName + BACK_FACTORY_NAME;
        backClassVisitor.visit(version, Opcodes.ACC_PUBLIC, backFactoryName, null, "java/lang/Object", null);
    }

    private void createBackField() {
        super.visitField(Opcodes.ACC_PUBLIC + Opcodes.ACC_FINAL, BACK_FIELD, "Ljava/lang/Object;", null, null);
    }

    private void createFrontSpecializationConstructor(String rawName) {
        // Creating the constructor storing the back object.
        MethodVisitor mv = super.visitMethod(Opcodes.ACC_PUBLIC, "<init>", "(Ljava/lang/Void;Ljava/lang/Object;)V", null, null);
        mv.visitCode();
        mv.visitInsn(Opcodes.ALOAD_0);
        mv.visitInsn(Opcodes.ALOAD_2);
        mv.visitFieldInsn(Opcodes.PUTFIELD, rawName, BACK_FIELD, "Ljava/lang/Object;");
        mv.visitInsn(Opcodes.RETURN);
        mv.visitMaxs(0, 0);
        mv.visitEnd();
    }

    private MethodVisitor createRetroValhallaMethodVisitor(int access, String name, String desc, String signature, String[] exceptions) {
        int backAccess;
        String backDesc;
        // For each method of the back but the constructor, transforming it into a static method taking
        // in first parameter the front class.
        if (!name.equals("<init>")) {
            backAccess = access + Opcodes.ACC_STATIC;
            Type mType = Type.getType(desc);
            Type[] argumentTypes = mType.getArgumentTypes();
            Type[] parameterTypes = new Type[argumentTypes.length + 1];
            parameterTypes[0] = Type.getType('L' + this.name + ';');
            for (int i = 1; i < parameterTypes.length; i++) { parameterTypes[i] = argumentTypes[i - 1]; }
            backDesc =  Type.getMethodDescriptor(mType.getReturnType(), parameterTypes);
        } else {
            backAccess = access;
            backDesc = desc;
        }
        // Back and front method visitors.
        MethodVisitor fmw = super.visitMethod(access, name, desc, signature, exceptions);
        MethodVisitor bmw = backClassVisitor.visitMethod(backAccess, name, backDesc, null, exceptions);
        FrontMethodVisitor mv = new FrontMethodVisitor(api, this.name, fmw);
        BackMethodVisitor bmv = new BackMethodVisitor(api, name, this.name, backFactoryName, bmw);
        // Special method visitor visiting the initial class and splitting informations inside
        // the front and the back class.
        return new RetroValhallaMethodVisitor(API, mv, bmv);
    }

}
