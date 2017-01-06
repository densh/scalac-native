/***
 * ASM: a very small and fast Java bytecode manipulation framework
 * Copyright (c) 2000-2011 INRIA, France Telecom
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. Neither the name of the copyright holders nor the names of its
 *    contributors may be used to endorse or promote products derived from
 *    this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES LOSS OF USE, DATA, OR PROFITS OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF
 * THE POSSIBILITY OF SUCH DAMAGE.
 */
package scala.tools.asm

object Opcodes {

    // ASM API versions

    final val ASM4 = 4 << 16 | 0 << 8 | 0
    final val ASM5 = 5 << 16 | 0 << 8 | 0

    // versions

    final val V1_1 = 3 << 16 | 45
    final val V1_2 = 0 << 16 | 46
    final val V1_3 = 0 << 16 | 47
    final val V1_4 = 0 << 16 | 48
    final val V1_5 = 0 << 16 | 49
    final val V1_6 = 0 << 16 | 50
    final val V1_7 = 0 << 16 | 51
    final val V1_8 = 0 << 16 | 52
    final val V1_9 = 0 << 16 | 53

    // access flags

    final val ACC_PUBLIC = 0x0001 // class, field, method
    final val ACC_PRIVATE = 0x0002 // class, field, method
    final val ACC_PROTECTED = 0x0004 // class, field, method
    final val ACC_STATIC = 0x0008 // field, method
    final val ACC_FINAL = 0x0010 // class, field, method, parameter
    final val ACC_SUPER = 0x0020 // class
    final val ACC_SYNCHRONIZED = 0x0020 // method
    final val ACC_VOLATILE = 0x0040 // field
    final val ACC_BRIDGE = 0x0040 // method
    final val ACC_VARARGS = 0x0080 // method
    final val ACC_TRANSIENT = 0x0080 // field
    final val ACC_NATIVE = 0x0100 // method
    final val ACC_INTERFACE = 0x0200 // class
    final val ACC_ABSTRACT = 0x0400 // class, method
    final val ACC_STRICT = 0x0800 // method
    final val ACC_SYNTHETIC = 0x1000 // class, field, method, parameter
    final val ACC_ANNOTATION = 0x2000 // class
    final val ACC_ENUM = 0x4000 // class(?) field inner
    final val ACC_MANDATED = 0x8000 // parameter

    // ASM specific pseudo access flags

    final val ACC_DEPRECATED = 0x20000 // class, field, method

    // types for NEWARRAY

    final val T_BOOLEAN = 4
    final val T_CHAR = 5
    final val T_FLOAT = 6
    final val T_DOUBLE = 7
    final val T_BYTE = 8
    final val T_SHORT = 9
    final val T_INT = 10
    final val T_LONG = 11

    // tags for Handle

    final val H_GETFIELD = 1
    final val H_GETSTATIC = 2
    final val H_PUTFIELD = 3
    final val H_PUTSTATIC = 4
    final val H_INVOKEVIRTUAL = 5
    final val H_INVOKESTATIC = 6
    final val H_INVOKESPECIAL = 7
    final val H_NEWINVOKESPECIAL = 8
    final val H_INVOKEINTERFACE = 9

    // stack map frame types

    /**
     * Represents an expanded frame. See {@link ClassReader#EXPAND_FRAMES}.
     */
    final val F_NEW = -1

    /**
     * Represents a compressed frame with complete frame data.
     */
    final val F_FULL = 0

    /**
     * Represents a compressed frame where locals are the same as the locals in
     * the previous frame, except that additional 1-3 locals are defined, and
     * with an empty stack.
     */
    final val F_APPEND = 1

    /**
     * Represents a compressed frame where locals are the same as the locals in
     * the previous frame, except that the last 1-3 locals are absent and with
     * an empty stack.
     */
    final val F_CHOP = 2

    /**
     * Represents a compressed frame with exactly the same locals as the
     * previous frame and with an empty stack.
     */
    final val F_SAME = 3

    /**
     * Represents a compressed frame with exactly the same locals as the
     * previous frame and with a single value on the stack.
     */
    final val F_SAME1 = 4

    final val TOP = new Integer(0)
    final val INTEGER = new Integer(1)
    final val FLOAT = new Integer(2)
    final val DOUBLE = new Integer(3)
    final val LONG = new Integer(4)
    final val NULL = new Integer(5)
    final val UNINITIALIZED_THIS = new Integer(6)

    // opcodes // visit method (- = idem)

    final val NOP = 0 // visitInsn
    final val ACONST_NULL = 1 // -
    final val ICONST_M1 = 2 // -
    final val ICONST_0 = 3 // -
    final val ICONST_1 = 4 // -
    final val ICONST_2 = 5 // -
    final val ICONST_3 = 6 // -
    final val ICONST_4 = 7 // -
    final val ICONST_5 = 8 // -
    final val LCONST_0 = 9 // -
    final val LCONST_1 = 10 // -
    final val FCONST_0 = 11 // -
    final val FCONST_1 = 12 // -
    final val FCONST_2 = 13 // -
    final val DCONST_0 = 14 // -
    final val DCONST_1 = 15 // -
    final val BIPUSH = 16 // visitIntInsn
    final val SIPUSH = 17 // -
    final val LDC = 18 // visitLdcInsn
    // final val LDC_W = 19 // -
    // final val LDC2_W = 20 // -
    final val ILOAD = 21 // visitVarInsn
    final val LLOAD = 22 // -
    final val FLOAD = 23 // -
    final val DLOAD = 24 // -
    final val ALOAD = 25 // -
    // final val ILOAD_0 = 26 // -
    // final val ILOAD_1 = 27 // -
    // final val ILOAD_2 = 28 // -
    // final val ILOAD_3 = 29 // -
    // final val LLOAD_0 = 30 // -
    // final val LLOAD_1 = 31 // -
    // final val LLOAD_2 = 32 // -
    // final val LLOAD_3 = 33 // -
    // final val FLOAD_0 = 34 // -
    // final val FLOAD_1 = 35 // -
    // final val FLOAD_2 = 36 // -
    // final val FLOAD_3 = 37 // -
    // final val DLOAD_0 = 38 // -
    // final val DLOAD_1 = 39 // -
    // final val DLOAD_2 = 40 // -
    // final val DLOAD_3 = 41 // -
    // final val ALOAD_0 = 42 // -
    // final val ALOAD_1 = 43 // -
    // final val ALOAD_2 = 44 // -
    // final val ALOAD_3 = 45 // -
    final val IALOAD = 46 // visitInsn
    final val LALOAD = 47 // -
    final val FALOAD = 48 // -
    final val DALOAD = 49 // -
    final val AALOAD = 50 // -
    final val BALOAD = 51 // -
    final val CALOAD = 52 // -
    final val SALOAD = 53 // -
    final val ISTORE = 54 // visitVarInsn
    final val LSTORE = 55 // -
    final val FSTORE = 56 // -
    final val DSTORE = 57 // -
    final val ASTORE = 58 // -
    // final val ISTORE_0 = 59 // -
    // final val ISTORE_1 = 60 // -
    // final val ISTORE_2 = 61 // -
    // final val ISTORE_3 = 62 // -
    // final val LSTORE_0 = 63 // -
    // final val LSTORE_1 = 64 // -
    // final val LSTORE_2 = 65 // -
    // final val LSTORE_3 = 66 // -
    // final val FSTORE_0 = 67 // -
    // final val FSTORE_1 = 68 // -
    // final val FSTORE_2 = 69 // -
    // final val FSTORE_3 = 70 // -
    // final val DSTORE_0 = 71 // -
    // final val DSTORE_1 = 72 // -
    // final val DSTORE_2 = 73 // -
    // final val DSTORE_3 = 74 // -
    // final val ASTORE_0 = 75 // -
    // final val ASTORE_1 = 76 // -
    // final val ASTORE_2 = 77 // -
    // final val ASTORE_3 = 78 // -
    final val IASTORE = 79 // visitInsn
    final val LASTORE = 80 // -
    final val FASTORE = 81 // -
    final val DASTORE = 82 // -
    final val AASTORE = 83 // -
    final val BASTORE = 84 // -
    final val CASTORE = 85 // -
    final val SASTORE = 86 // -
    final val POP = 87 // -
    final val POP2 = 88 // -
    final val DUP = 89 // -
    final val DUP_X1 = 90 // -
    final val DUP_X2 = 91 // -
    final val DUP2 = 92 // -
    final val DUP2_X1 = 93 // -
    final val DUP2_X2 = 94 // -
    final val SWAP = 95 // -
    final val IADD = 96 // -
    final val LADD = 97 // -
    final val FADD = 98 // -
    final val DADD = 99 // -
    final val ISUB = 100 // -
    final val LSUB = 101 // -
    final val FSUB = 102 // -
    final val DSUB = 103 // -
    final val IMUL = 104 // -
    final val LMUL = 105 // -
    final val FMUL = 106 // -
    final val DMUL = 107 // -
    final val IDIV = 108 // -
    final val LDIV = 109 // -
    final val FDIV = 110 // -
    final val DDIV = 111 // -
    final val IREM = 112 // -
    final val LREM = 113 // -
    final val FREM = 114 // -
    final val DREM = 115 // -
    final val INEG = 116 // -
    final val LNEG = 117 // -
    final val FNEG = 118 // -
    final val DNEG = 119 // -
    final val ISHL = 120 // -
    final val LSHL = 121 // -
    final val ISHR = 122 // -
    final val LSHR = 123 // -
    final val IUSHR = 124 // -
    final val LUSHR = 125 // -
    final val IAND = 126 // -
    final val LAND = 127 // -
    final val IOR = 128 // -
    final val LOR = 129 // -
    final val IXOR = 130 // -
    final val LXOR = 131 // -
    final val IINC = 132 // visitIincInsn
    final val I2L = 133 // visitInsn
    final val I2F = 134 // -
    final val I2D = 135 // -
    final val L2I = 136 // -
    final val L2F = 137 // -
    final val L2D = 138 // -
    final val F2I = 139 // -
    final val F2L = 140 // -
    final val F2D = 141 // -
    final val D2I = 142 // -
    final val D2L = 143 // -
    final val D2F = 144 // -
    final val I2B = 145 // -
    final val I2C = 146 // -
    final val I2S = 147 // -
    final val LCMP = 148 // -
    final val FCMPL = 149 // -
    final val FCMPG = 150 // -
    final val DCMPL = 151 // -
    final val DCMPG = 152 // -
    final val IFEQ = 153 // visitJumpInsn
    final val IFNE = 154 // -
    final val IFLT = 155 // -
    final val IFGE = 156 // -
    final val IFGT = 157 // -
    final val IFLE = 158 // -
    final val IF_ICMPEQ = 159 // -
    final val IF_ICMPNE = 160 // -
    final val IF_ICMPLT = 161 // -
    final val IF_ICMPGE = 162 // -
    final val IF_ICMPGT = 163 // -
    final val IF_ICMPLE = 164 // -
    final val IF_ACMPEQ = 165 // -
    final val IF_ACMPNE = 166 // -
    final val GOTO = 167 // -
    final val JSR = 168 // -
    final val RET = 169 // visitVarInsn
    final val TABLESWITCH = 170 // visiTableSwitchInsn
    final val LOOKUPSWITCH = 171 // visitLookupSwitch
    final val IRETURN = 172 // visitInsn
    final val LRETURN = 173 // -
    final val FRETURN = 174 // -
    final val DRETURN = 175 // -
    final val ARETURN = 176 // -
    final val RETURN = 177 // -
    final val GETSTATIC = 178 // visitFieldInsn
    final val PUTSTATIC = 179 // -
    final val GETFIELD = 180 // -
    final val PUTFIELD = 181 // -
    final val INVOKEVIRTUAL = 182 // visitMethodInsn
    final val INVOKESPECIAL = 183 // -
    final val INVOKESTATIC = 184 // -
    final val INVOKEINTERFACE = 185 // -
    final val INVOKEDYNAMIC = 186 // visitInvokeDynamicInsn
    final val NEW = 187 // visitTypeInsn
    final val NEWARRAY = 188 // visitIntInsn
    final val ANEWARRAY = 189 // visitTypeInsn
    final val ARRAYLENGTH = 190 // visitInsn
    final val ATHROW = 191 // -
    final val CHECKCAST = 192 // visitTypeInsn
    final val INSTANCEOF = 193 // -
    final val MONITORENTER = 194 // visitInsn
    final val MONITOREXIT = 195 // -
    // final val WIDE = 196 // NOT VISITED
    final val MULTIANEWARRAY = 197 // visitMultiANewArrayInsn
    final val IFNULL = 198 // visitJumpInsn
    final val IFNONNULL = 199 // -
    // final val GOTO_W = 200 // -
    // final val JSR_W = 201 // -
}
