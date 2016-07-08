package org.objectweb.asm.test.cases.specialization;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Opcodes;

/**
 *
 * Created by Jefferson Mangue on 09/06/2016.
 */
class RetroValhallaBackClassVisitor extends ClassVisitor {

    public static final int API = Opcodes.ASM5;
    private static final int COMPILER_VERSION = 52;

    RetroValhallaBackClassVisitor(ClassVisitor cv) {
        super(API, cv);
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        super.visit(COMPILER_VERSION, access, name, signature, superName, interfaces);
    }

    @Override
    public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
        return super.visitField(access, name, desc, null, value);
    }
}
