package org.objectweb.asm;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * Created by Jefferson Mangue on 20/06/2016.
 */
public class SubstitutionTable extends Attribute {
    public static final String NAME = "SubstitutionTable";

    private final ByteVector vector;
    private final HashMap<Integer, String> descriptors; // Cache.

    public SubstitutionTable() {
        super(NAME);
        vector = new ByteVector();
        descriptors = new HashMap<>();
    }


    public void putUTF8(int index, String owner, String descriptor) {
        descriptors.put(index, descriptor);
        vector.putShort(index);
        vector.putUTF8(owner);
        vector.putUTF8(descriptor);
    }

    public boolean contains(int index) {
        return descriptors.containsKey(index);
    }

    public boolean contains(String owner, String descriptor) {
        // TODO test on owner AND descriptor since x/Object is replaced by t/Object.
        return descriptors.containsValue(descriptor) && ;
    }

    public int get(String desc) {
        // Since the contains test using index is used inside a loop inside ClassWriter#get method.
        // The index has been chosen to be the key.
        for (Map.Entry<Integer, String> e : descriptors.entrySet()) {
            if (e.getValue().equals(desc)) {
                return e.getKey();
            }
        }
        throw new IllegalArgumentException("No key : " + desc);
    }

    @Override
    public String toString() {
        return descriptors.toString();
    }

    public boolean isEmpty() {
        return descriptors.isEmpty();
    }

    public byte[] getByteArray() {
        return vector.data;
    }


    @Override
    protected Attribute read(ClassReader cr, int off, int len, char[] buf, int codeOff, Label[] labels) {
        return super.read(cr, off, len, buf, codeOff, labels);
    }

    @Override
    protected ByteVector write(ClassWriter cw, byte[] code, int len, int maxStack, int maxLocals) {
        return vector;
    }
}
