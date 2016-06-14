package org.objectweb.asm;

/**
 * Created by Jefferson Mangue on 01/06/2016.
 */
public class TypeVariablesEntry {
    private final int isAny;
    private final int tVarNameIndex;
    private final int boundIndex;

    public TypeVariablesEntry(int flag, int tVarNameIndex, int boundIndex) {
        this.isAny = flag;
        this.tVarNameIndex = tVarNameIndex;
        this.boundIndex = boundIndex;
    }

    public int getIsAny() {
        return isAny;
    }

    public int gettVarNameIndex() {
        return tVarNameIndex;
    }

    public int getBoundIndex() {
        return boundIndex;
    }

    @Override
    public String toString() {
        return "[TypeVariablesEntry - TvarName idx : " + tVarNameIndex +
                " FLAG : " + isAny + " Bound idx : " + boundIndex + ']';
    }
}
