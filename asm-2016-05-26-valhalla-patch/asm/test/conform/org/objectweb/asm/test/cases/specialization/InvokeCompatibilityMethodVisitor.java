package org.objectweb.asm.test.cases.specialization;


import org.objectweb.asm.MethodVisitor;

/**
 *
 * Created by Jefferson Mangue on 12/06/2016.
 */
class InvokeCompatibilityMethodVisitor extends MethodVisitor {

    private final InvokeAnyAdapter invokeAnyAdapter;

    InvokeCompatibilityMethodVisitor(int api, MethodVisitor mv) {
        super(api, mv);
        invokeAnyAdapter = new InvokeAnyAdapter(this);
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
}
