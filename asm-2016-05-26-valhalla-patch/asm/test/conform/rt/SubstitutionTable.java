package rt;

import java.util.HashMap;

/**
 *
 * Created by Jefferson Mangue on 20/06/2016.
 */
public class SubstitutionTable  {
    public static final String NAME = "SubstitutionTable";

    private final HashMap<Integer, String> descriptors; // Cache.

    private SubstitutionTable(HashMap<Integer, String> descriptors) {
        this.descriptors = descriptors;
    }
/*
    public void putUTF8(int index, String owner, String descriptor) {
        descriptors.put(index, descriptor);
        vector.putShort(index);
        vector.putUTF8(owner);
        vector.putUTF8(descriptor);
    }

    public boolean contains(int index) {
        return descriptors.containsKey(index);
    }

    public boolean contains(String descriptor) {
        return descriptors.containsValue(descriptor);
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
*/

    public static SubstitutionTable create(SubstitutionTableReader substitutionTableReader, int off, int len) {
        System.out.println("substitutionTableReader = [" + substitutionTableReader + "], off = [" + off + "], len = [" + len + "]");
        HashMap<Integer, String> descriptors = new HashMap<>();
        for (int i = 0; i < len; i++) {
            int index = substitutionTableReader.readShort(off);
            off += 2;
            String owner = substitutionTableReader.readUTF8(off);
            off += owner.length() + 2;
            String descriptor = substitutionTableReader.readUTF8(off);
            off += descriptor.length() + 2;
            System.out.println("INDEX : " + index + " OWNER : " + owner + " DESCRIPTOR : " + descriptor);
        }
        return new SubstitutionTable(descriptors);
    }
}
