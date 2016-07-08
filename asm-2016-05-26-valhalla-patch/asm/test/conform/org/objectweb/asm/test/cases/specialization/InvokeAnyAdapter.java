package org.objectweb.asm.test.cases.specialization;

import org.objectweb.asm.Handle;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.lang.invoke.CallSite;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.Stack;

/**
 * Created by Jefferson Mangue on 08/07/2016.
 */
public class InvokeAnyAdapter {

    /**
     * Invokedynamic constants.
     */
    private static final Handle BSM_NEW;

    static {
        MethodType mtNew = MethodType.methodType(CallSite.class, MethodHandles.Lookup.class, String.class, MethodType.class, String.class);
        BSM_NEW = new Handle(Opcodes.H_INVOKESTATIC, "rt/RT", "bsm_new", mtNew.toMethodDescriptorString(), false);
    }

    /**
     * Enumeration used for the detection of invoke special calls.
     * This enumeration indicates if an eligible NEW opcodes sequence, for the substitution by an invokedynamic
     * has been visited and then the same for DUP opcode. Only a NEW applied on generics is selected to be replaced.
     * Other new have to be considered and ignored since they must not be replaced.
     */
    private enum InvokeSpecialVisited {
        REPLACED_NEW, REPLACED_DUP, IGNORED_NEW, IGNORED_DUP
    }

    // An int is not sufficient. Because a stack level has multiple states possible.
    // First it has the boolean value "dup visited" to detect if the dup has been visited (and skipped) or not.
    // Otherwise all possible dup between the "new" opcode and the "invokespecial" will be skipped. .
    private final Stack<InvokeSpecialVisited> invokeSpecialStack = new Stack<>();

    private final MethodVisitor mv;

    public InvokeAnyAdapter(MethodVisitor mv) {
        this.mv = mv;
    }

    public boolean visitTypeInsn(int opcode, String type) {
        if (opcode != Opcodes.NEW) {
            return false;
        }

        // Ignoring this new since it does not manipulate generics.
        if (!type.startsWith("$")) {
            invokeSpecialStack.push(InvokeSpecialVisited.IGNORED_NEW);
            return false;
        }

        // Replacing the "NEW" opcode since it will be replaced by invokedynamic.
        invokeSpecialStack.push(InvokeSpecialVisited.REPLACED_NEW);
        return true;
    }

    public boolean visitInsn(int opcode) {
        if (opcode != Opcodes.DUP || invokeSpecialStack.empty()) {
            return false;
        }
        // Replacing the DUP opcode corresponding to a NEW opcode replaced.
        if (InvokeSpecialVisited.REPLACED_NEW.equals(invokeSpecialStack.peek())) {
            invokeSpecialStack.set(invokeSpecialStack.size() - 1, InvokeSpecialVisited.REPLACED_DUP);
            return true;
        }
        // Ignoring the DUP opcode corresponding to a NEW opcode ignored.
        if (InvokeSpecialVisited.IGNORED_NEW.equals(invokeSpecialStack.peek())) {
            invokeSpecialStack.set(invokeSpecialStack.size() - 1, InvokeSpecialVisited.IGNORED_DUP);
        }
        return false;
    }

    public boolean visitMethodInsn(final int opcode, final String owner,
                                final String name, final String desc, final boolean itf) {
        if (opcode != Opcodes.INVOKESPECIAL) {
            // Writing the call inside the class.
            return false;
        }

        // Detect "new" call to substitute it by an invokedynamic.
        if (!invokeSpecialStack.empty()) {
            InvokeSpecialVisited top = invokeSpecialStack.peek();
            if (InvokeSpecialVisited.REPLACED_DUP.equals(top)) {
                Type type = Type.getMethodType(desc);
                String newDesc = Type.translateMethodDescriptor(Type.getMethodType(Type.getType(owner),
                        type.getArgumentTypes()).toString());
                // If name is "<init>" the "<>" can not be placed in a valid name. They have to be replaced.
                mv.visitInvokeDynamicInsn(name.replace('<', '_').replace('>', '_'), newDesc, BSM_NEW, newDesc); // TODO use Type inside the BM to parse desc.
                invokeSpecialStack.pop();
                return true;
            }
            // IGNORED_DUP. Popping the current stack level.
            invokeSpecialStack.pop();
        }
        // Writing method call.
        return false;
    }
}
