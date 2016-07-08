package org.objectweb.asm.test.cases.specialization;


import org.objectweb.asm.*;

/**
 * Created by Jefferson Mangue on 12/06/2016.
 */
class FrontMethodVisitor extends MethodVisitor {

    private final String owner;

    FrontMethodVisitor(int api, String owner, MethodVisitor mv) {
        super(api, mv);
        this.owner = owner;
    }

    public String getOwner() {
        return owner;
    }
}
