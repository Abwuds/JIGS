package org.objectweb.asm.test.cases.specialization;

import org.objectweb.asm.*;
import org.objectweb.asm.test.cases.Generator;
import org.objectweb.asm.util.TraceClassVisitor;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Created by Jefferson Mangue on 26/05/2016.
 */
public class Reader extends Generator {

    private static final String CLASS = "Reader";
    private static final String PKG_SPECIALIZATION = "pkg/specialization/";

    @Override
    public void generate(final String dir) throws IOException {
        read();
        generate(dir, PKG_SPECIALIZATION + CLASS + ".class", dump());
    }

    private void read() throws IOException {
        byte[] bytes = Files.readAllBytes(Paths.get("asm/test/resources/Class5$Classe5Inner.class"));
        ClassReader cr = new ClassReader(bytes);
         cr.accept(new TraceClassVisitor(new PrintWriter(System.out)), 0);
        // Using my own visitor.
        // cr.accept(new Java10Visitor(), 0);
    }

    public byte[] dump() {
        return new byte[0];
    }

    private static class Java10Visitor extends ClassVisitor {

        public Java10Visitor() {
            super(Opcodes.ASM5);
        }

        @Override
        public void visitEnd() {
            super.visitEnd();
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
            System.out.println("VisitMethod.");
            return new Java10MethodVisitor();
        }

        @Override
        public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
            System.out.println("visitField.");
            return super.visitField(access, name, desc, signature, value);
        }

        @Override
        public void visitInnerClass(String name, String outerName, String innerName, int access) {
            System.out.println("visitInnerClass.");
            super.visitInnerClass(name, outerName, innerName, access);
        }

        @Override
        public void visitAttribute(Attribute attr) {
            System.out.println("visitAttribute.");
            super.visitAttribute(attr);
        }

        @Override
        public AnnotationVisitor visitTypeAnnotation(int typeRef, TypePath typePath, String desc, boolean visible) {
            System.out.println("visitTypeAnnotation.");
            return super.visitTypeAnnotation(typeRef, typePath, desc, visible);
        }

        @Override
        public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
            System.out.println("visitAnnotation.");
            return super.visitAnnotation(desc, visible);
        }

        @Override
        public void visitOuterClass(String owner, String name, String desc) {
            System.out.println("visitOuterClass.");
            super.visitOuterClass(owner, name, desc);
        }

        @Override
        public void visitSource(String source, String debug) {
            System.out.println("visitSource.");
            super.visitSource(source, debug);
        }

        @Override
        public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
            System.out.println("visit.");
            super.visit(version, access, name, signature, superName, interfaces);
        }
    }

    private static class Java10MethodVisitor extends MethodVisitor {

        public Java10MethodVisitor() {
            super(ASM5);
        }


    }
}
