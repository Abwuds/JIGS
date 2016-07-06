package org.objectweb.asm.test.cases.specialization;


import org.objectweb.asm.*;

import java.lang.invoke.CallSite;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.AbstractMap.SimpleEntry;
import java.util.*;

/**
 * Created by Jefferson Mangue on 12/06/2016.
 */
class CompatibilityMethodVisitor extends MethodVisitor {

    CompatibilityMethodVisitor(int api, MethodVisitor mv) {
        super(api, mv);
    }
}
