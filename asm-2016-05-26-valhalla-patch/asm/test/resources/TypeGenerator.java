
/**
 * Created by Baxtalou on 19/05/2016.
 */
public class TypeGenerator {

    // Codes from the JDK.
    private static int NEWARRAY = 0xbc;
    private static int BIPUSH = 0x10;
    private static int SIPUSH = 0x11;
    private static int RET = 0xa9;
    private static int ILOAD = 0x15;
    private static int ALOAD = 0x19;
    private static int ASTORE = 0x3a;
    private static int ISTORE = 0x36;
    private static int NEW = 0xbb;
    private static int ANEWARRAY = 0xbd;
    private static int CHECKCAST = 0xc0;
    private static int INSTANCEOF = 0xc1;
    private static int GETSTATIC = 0xb2;
    private static int INVOKESTATIC = 0xb8;
    private static int INVOKEINTERFACE = 0xb9;
    private static int INVOKEDYNAMIC = 0xba;
    private static int IFEQ = 0x99;
    private static int JSR = 0xa8;
    private static int IFNULL = 0xc6;
    private static int IFNONNULL = 0xc7;
    private static int LDC = 0x12;
    private static int IINC = 0x84;
    private static int TABLESWITCH = 0xaa;
    private static int LOOKUPSWITCH = 0xab;
    private static int MULTIANEWARRAY = 0xc5;

    // Codes from org.objectweb.asm.ClassWriter

    /**
     * The type of instructions without any argument.
     */
    static final int NOARG_INSN = 0;

    /**
     * The type of instructions with an signed byte argument.
     */
    static final int SBYTE_INSN = 1;

    /**
     * The type of instructions with an signed short argument.
     */
    static final int SHORT_INSN = 2;

    /**
     * The type of instructions with a local variable index argument.
     */
    static final int VAR_INSN = 3;

    /**
     * The type of instructions with an implicit local variable index argument.
     */
    static final int IMPLVAR_INSN = 4;

    /**
     * The type of instructions with a type descriptor argument.
     */
    static final int TYPE_INSN = 5;

    /**
     * The type of field and method invocations instructions.
     */
    static final int FIELDORMETH_INSN = 6;

    /**
     * The type of the INVOKEINTERFACE/INVOKEDYNAMIC instruction.
     */
    static final int ITFMETH_INSN = 7;

    /**
     * The type of the INVOKEDYNAMIC instruction.
     */
    static final int INDYMETH_INSN = 8;

    /**
     * The type of instructions with a 2 bytes bytecode offset label.
     */
    static final int LABEL_INSN = 9;

    /**
     * The type of instructions with a 4 bytes bytecode offset label.
     */
    static final int LABELW_INSN = 10;

    /**
     * The type of the LDC instruction.
     */
    static final int LDC_INSN = 11;

    /**
     * The type of the LDC_W and LDC2_W instructions.
     */
    static final int LDCW_INSN = 12;

    /**
     * The type of the IINC instruction.
     */
    static final int IINC_INSN = 13;

    /**
     * The type of the TABLESWITCH instruction.
     */
    static final int TABL_INSN = 14;

    /**
     * The type of the LOOKUPSWITCH instruction.
     */
    static final int LOOK_INSN = 15;

    /**
     * The type of the MULTIANEWARRAY instruction.
     */
    static final int MANA_INSN = 16;

    /**
     * The type of the WIDE instruction.
     */
    static final int WIDE_INSN = 17;

    /**
     * The type of the TYPED instruction.
     */
    static final int TYPED_INSN = 18;

    public static void generate() {
        // code to generate the above string

        int i = 0;
        byte[] b = new byte[231];
        // SBYTE_INSN instructions
        b[NEWARRAY] = SBYTE_INSN;
        b[BIPUSH] = SBYTE_INSN;

        // SHORT_INSN instructions
        b[SIPUSH] = SHORT_INSN;

        // (IMPL)VAR_INSN instructions
        b[RET] = VAR_INSN;
        for (i = ILOAD; i <= ALOAD; ++i) {
            b[i] = VAR_INSN;
        }
        for (i = ISTORE; i <= ASTORE; ++i) {
            b[i] = VAR_INSN;
        }
        for (i = 26; i <= 45; ++i) { // ILOAD_0 to ALOAD_3
            b[i] = IMPLVAR_INSN;
        }
        for (i = 59; i <= 78; ++i) { // ISTORE_0 to ASTORE_3
            b[i] = IMPLVAR_INSN;
        }

        // TYPE_INSN instructions
        b[NEW] = TYPE_INSN;
        b[ANEWARRAY] = TYPE_INSN;
        b[CHECKCAST] = TYPE_INSN;
        b[INSTANCEOF] = TYPE_INSN;

        // (Set)FIELDORMETH_INSN instructions
        for (i = GETSTATIC; i <= INVOKESTATIC; ++i) {
            b[i] = FIELDORMETH_INSN;
        }
        b[INVOKEINTERFACE] = ITFMETH_INSN;
        b[INVOKEDYNAMIC] = INDYMETH_INSN;

        // LABEL(W)_INSN instructions
        for (i = IFEQ; i <= JSR; ++i) {
            b[i] = LABEL_INSN;
        }
        b[IFNULL] = LABEL_INSN;
        b[IFNONNULL] = LABEL_INSN;
        b[200] = LABELW_INSN;       // GOTO_W
        b[201] = LABELW_INSN;       // JSR_W
        // New Valhalla instructions.
        // They implie the shift of the all label substitution 202 -> 209
        // These 9 one are not implemented in the jdk yet.
        b[203] = NOARG_INSN;        // VLOAD
        b[204] = NOARG_INSN;        // VSTORE
        b[205] = NOARG_INSN;        // VALOAD
        b[206] = NOARG_INSN;        // VASTORE
        b[207] = NOARG_INSN;        // VNEW
        b[208] = NOARG_INSN;        // VNEWARRAY
        b[209] = NOARG_INSN;        // MULTIVNEWARRAY
        b[210] = NOARG_INSN;        // VRETURN
        b[211] = NOARG_INSN;        // VGETFIELD
        // This instruction is the only one added for the Valhalla project.
        b[212] = TYPED_INSN;         // TYPED

        // temporary opcodes used internally by ASM - see Label and MethodWriter
        // Used to be from 202 to 219. Was shifted by 12 since we added 203 -> 212.
        for (i = 213; i < 231; ++i) {
            b[i] = LABEL_INSN;
        }

        // LDC(_W) instructions
        b[LDC] = LDC_INSN;
        b[19] = LDCW_INSN; // LDC_W
        b[20] = LDCW_INSN; // LDC2_W

        // special instructions
        b[IINC] = IINC_INSN;
        b[TABLESWITCH] = TABL_INSN;
        b[LOOKUPSWITCH] = LOOK_INSN;
        b[MULTIANEWARRAY] = MANA_INSN;
        b[196] = WIDE_INSN; // WIDE

        for (i = 0; i < b.length; ++i) {
            System.err.print((char) ('A' + b[i]));
        }
        System.err.println();
    }

    public static void main(final String[] args)  {
        generate();
    }
}
