package org.objectweb.asm.test.cases.specialization;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.test.cases.Generator;

import java.io.IOException;

/**
 * Created by Jefferson Mangue on 26/05/2016.
 */
public class Writer extends Generator {

    private static final String CLASS = "Writer";
    private static final String PKG_SPECIALIZATION = "pkg/specialization/";

    @Override
    public void generate(final String dir) throws IOException {
        generate(dir, PKG_SPECIALIZATION + CLASS + ".class", dump());
    }

    public byte[] dump() {
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        MethodVisitor mv;

        cw.visit(V1_7, ACC_PUBLIC, PKG_SPECIALIZATION + CLASS, null, "java/lang/Object", null);

        // Main method.
        mv = cw.visitMethod(ACC_PUBLIC + ACC_STATIC, "main", "([Ljava/lang/String;)V", null, null);
        mv.visitCode();
        mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
        mv.visitMethodInsn(INVOKESTATIC, "pkg/specialization/Writer", "foo", "()Ljava/lang/String;", false);
        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);
        mv.visitInsn(RETURN);
        mv.visitEnd();
        mv.visitMaxs(0, 0);

        // Foo method printing Hello world.
        mv = cw.visitMethod(ACC_PUBLIC + ACC_STATIC, "foo", "()Ljava/lang/String;", null, null);
        mv.visitCode();
        mv.visitLdcInsn("Hello world");
        mv.visitInsn(ARETURN);
        mv.visitEnd();
        mv.visitMaxs(0, 0);

        cw.visitEnd();
        return cw.toByteArray();
    }
}
