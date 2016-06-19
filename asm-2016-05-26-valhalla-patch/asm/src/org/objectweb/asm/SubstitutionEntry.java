package org.objectweb.asm;

/**
 * Created by Jefferson Mangue on 16/06/2016.
 * Used to constitute a substitution table by the {@link MethodWriter}.
 * This attribute will allow substitutions inside the constant pool
 * during the specialisation process at runtime.
 */
public class SubstitutionEntry {

    private final short offsetIndex;
    private final String owner;
    private final String descriptor;


    public SubstitutionEntry(short offsetIndex, String owner, String descriptor) {
        this.offsetIndex = offsetIndex;
        this.owner = owner;
        this.descriptor = descriptor;
    }


}
