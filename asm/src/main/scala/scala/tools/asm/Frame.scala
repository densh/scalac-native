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

class Frame {
    var owner: Label = _
    var inputLocals: Array[Int] = _
    var inputStack: Array[Int] = _
    private var outputLocals: Array[Int] = _
    private var outputStack: Array[Int] = _
    private var outputStackTop: Int = _
    private var initializationCount: Int = _
    private var initializations: Array[Int] = _

    private def get(local: Int): Int = {
        if (outputLocals == null || local >= outputLocals.length) {
            // this local has never been assigned in this basic block,
            // so it is still equal to its value in the input frame
            Frame.LOCAL | local
        } else {
            var type_ = outputLocals(local)
            if (type_ == 0) {
                // this local has never been assigned in this basic block,
                // so it is still equal to its value in the input frame
                outputLocals(local) = Frame.LOCAL | local
                type_ = Frame.LOCAL | local
            }
            type_
        }
    }

    private def set(local: Int, type_ : Int): Unit = ???/*{
        // creates and/or resizes the output local variables array if necessary
        if (outputLocals == null) {
            outputLocals = new int[10]
        }
        int n = outputLocals.length
        if (local >= n) {
            int[] t = new int[Math.max(local + 1, 2 * n)]
            System.arraycopy(outputLocals, 0, t, 0, n)
            outputLocals = t
        }
        // sets the local variable
        outputLocals[local] = type
    }*/

    private def push(type_ : Int): Unit = {
        // creates and/or resizes the output stack array if necessary
        if (outputStack == null) {
            outputStack = new Array[Int](10)
        }
        val n = outputStack.length
        if (outputStackTop >= n) {
            val t = new Array[Int](Math.max(outputStackTop + 1, 2 * n))
            System.arraycopy(outputStack, 0, t, 0, n)
            outputStack = t
        }
        // pushes the type on the output stack
        outputStack(outputStackTop) = type_
        outputStackTop += 1
        // updates the maximun height reached by the output stack, if needed
        val top = owner.inputStackTop + outputStackTop
        if (top > owner.outputStackMax) {
            owner.outputStackMax = top
        }
    }

    private def push(cw: ClassWriter, desc: String): Unit = ??? /*{
        int type = type(cw, desc)
        if (type != 0) {
            push(type)
            if (type == LONG || type == DOUBLE) {
                push(TOP)
            }
        }
    }*/


    private def pop(): Int = {
      if (outputStackTop > 0) {
        outputStackTop -= 1
        outputStack(outputStackTop)
      } else {
        // if the output frame stack is empty, pops from the input stack
        owner.inputStackTop -= 1
        Frame.STACK | -(owner.inputStackTop)
      }
    }

    private def pop(elements: Int): Unit = {
        if (outputStackTop >= elements) {
            outputStackTop -= elements
        } else {
            // if the number of elements to be popped is greater than the number
            // of elements in the output stack, clear it, and pops the remaining
            // elements from the input stack.
            owner.inputStackTop -= elements - outputStackTop
            outputStackTop = 0
        }
    }

    private def pop(desc: String): Unit = {
        val c = desc.charAt(0)
        if (c == '(') {
            pop((Type.getArgumentsAndReturnSizes(desc) >> 2) - 1)
        } else if (c == 'J' || c == 'D') {
            pop(2)
        } else {
            pop(1)
        }
    }

    private def init(var_ : Int): Unit = ??? /*{
        // creates and/or resizes the initializations array if necessary
        if (initializations == null) {
            initializations = new int[2]
        }
        int n = initializations.length
        if (initializationCount >= n) {
            int[] t = new int[Math.max(initializationCount + 1, 2 * n)]
            System.arraycopy(initializations, 0, t, 0, n)
            initializations = t
        }
        // stores the type to be initialized
        initializations[initializationCount++] = var
    }*/

    private def init(cw: ClassWriter , t: Int): Int = ??? /*{
        int s
        if (t == UNINITIALIZED_THIS) {
            s = OBJECT | cw.addType(cw.thisName)
        } else if ((t & (DIM | BASE_KIND)) == UNINITIALIZED) {
            String type = cw.typeTable[t & BASE_VALUE].strVal1
            s = OBJECT | cw.addType(type)
        } else {
            return t
        }
        for (int j = 0 j < initializationCount ++j) {
            int u = initializations[j]
            int dim = u & DIM
            int kind = u & KIND
            if (kind == LOCAL) {
                u = dim + inputLocals[u & VALUE]
            } else if (kind == STACK) {
                u = dim + inputStack[inputStack.length - (u & VALUE)]
            }
            if (t == u) {
                return s
            }
        }
        return t
    }*/

    def initInputFrame(cw: ClassWriter , access: Int,
            args: Array[Type], maxLocals: Int): Unit = ??? /*{
        inputLocals = new int[maxLocals]
        inputStack = new int[0]
        int i = 0
        if ((access & Opcodes.ACC_STATIC) == 0) {
            if ((access & MethodWriter.ACC_CONSTRUCTOR) == 0) {
                inputLocals[i++] = OBJECT | cw.addType(cw.thisName)
            } else {
                inputLocals[i++] = UNINITIALIZED_THIS
            }
        }
        for (int j = 0 j < args.length ++j) {
            int t = type(cw, args[j].getDescriptor())
            inputLocals[i++] = t
            if (t == LONG || t == DOUBLE) {
                inputLocals[i++] = TOP
            }
        }
        while (i < maxLocals) {
            inputLocals[i++] = TOP
        }
    }*/

    def execute(opcode: Int, arg: Int, cw: ClassWriter, item: Item): Unit = {
      import Frame._
      var t1, t2, t3, t4 = 0
      opcode match {
        case Opcodes.NOP | Opcodes.INEG | Opcodes.LNEG | Opcodes.FNEG | Opcodes.DNEG | Opcodes.I2B | Opcodes.I2C | Opcodes.I2S | Opcodes.GOTO | Opcodes.RETURN => ()

        case Opcodes.ACONST_NULL =>
          push(NULL)
        case Opcodes.ICONST_M1 | Opcodes.ICONST_0 | Opcodes.ICONST_1 | Opcodes.ICONST_2 | Opcodes.ICONST_3 | Opcodes.ICONST_4 | Opcodes.ICONST_5 | Opcodes.BIPUSH | Opcodes.SIPUSH | Opcodes.ILOAD =>
            push(INTEGER)

        case Opcodes.LCONST_0 | Opcodes.LCONST_1 | Opcodes.LLOAD =>
            push(LONG)
            push(TOP)

        case Opcodes.FCONST_0 | Opcodes.FCONST_1 | Opcodes.FCONST_2 | Opcodes.FLOAD =>
            push(FLOAT)

        case Opcodes.DCONST_0 | Opcodes.DCONST_1 | Opcodes.DLOAD =>
            push(DOUBLE)
            push(TOP)

        case Opcodes.LDC =>
            item.type_ match {
              case ClassWriter.INT =>
                push(INTEGER)
              case ClassWriter.LONG =>
                push(LONG)
                push(TOP)
              case ClassWriter.FLOAT =>
                push(FLOAT)
              case ClassWriter.DOUBLE =>
                push(DOUBLE)
                push(TOP)
              case ClassWriter.CLASS =>
                push(OBJECT | cw.addType("java/lang/Class"))
              case ClassWriter.STR =>
                push(OBJECT | cw.addType("java/lang/String"))
              case ClassWriter.MTYPE =>
                push(OBJECT | cw.addType("java/lang/invoke/MethodType"))
                // case ClassWriter.HANDLE_BASE + [1..9]:
              case _ =>
                push(OBJECT | cw.addType("java/lang/invoke/MethodHandle"))
            }

        case Opcodes.ALOAD =>
            push(get(arg))

        case Opcodes.IALOAD | Opcodes.BALOAD | Opcodes.CALOAD | Opcodes.SALOAD =>
            pop(2)
            push(INTEGER)
        case Opcodes.LALOAD | Opcodes.D2L =>
            pop(2)
            push(LONG)
            push(TOP)

        case Opcodes.FALOAD =>
            pop(2)
            push(FLOAT)

        case Opcodes.DALOAD | Opcodes.L2D =>
            pop(2)
            push(DOUBLE)
            push(TOP)

        case Opcodes.AALOAD =>
            pop(1)
            t1 = pop()
            push(ELEMENT_OF + t1)

        case Opcodes.ISTORE | Opcodes.FSTORE | Opcodes.ASTORE =>
            t1 = pop()
            set(arg, t1)
            if (arg > 0) {
                t2 = get(arg - 1)
                // if t2 is of kind STACK or LOCAL we cannot know its size!
                if (t2 == LONG || t2 == DOUBLE) {
                    set(arg - 1, TOP)
                } else if ((t2 & KIND) != BASE) {
                    set(arg - 1, t2 | TOP_IF_LONG_OR_DOUBLE)
                }
            }
        case Opcodes.LSTORE | Opcodes.DSTORE =>
            pop(1)
            t1 = pop()
            set(arg, t1)
            set(arg + 1, TOP)
            if (arg > 0) {
                t2 = get(arg - 1)
                // if t2 is of kind STACK or LOCAL we cannot know its size!
                if (t2 == LONG || t2 == DOUBLE) {
                    set(arg - 1, TOP)
                } else if ((t2 & KIND) != BASE) {
                    set(arg - 1, t2 | TOP_IF_LONG_OR_DOUBLE)
                }
            }
        case Opcodes.IASTORE | Opcodes.BASTORE | Opcodes.CASTORE | Opcodes.SASTORE | Opcodes.FASTORE | Opcodes.AASTORE =>
            pop(3)
        case Opcodes.LASTORE | Opcodes.DASTORE =>
            pop(4)
        case Opcodes.POP | Opcodes.IFEQ | Opcodes.IFNE | Opcodes.IFLT | Opcodes.IFGE | Opcodes.IFGT | Opcodes.IFLE | Opcodes.IRETURN | Opcodes.FRETURN | Opcodes.ARETURN | Opcodes.TABLESWITCH | Opcodes.LOOKUPSWITCH | Opcodes.ATHROW | Opcodes.MONITORENTER | Opcodes.MONITOREXIT | Opcodes.IFNULL | Opcodes.IFNONNULL =>
            pop(1)

        case Opcodes.POP2 | Opcodes.IF_ICMPEQ | Opcodes.IF_ICMPNE | Opcodes.IF_ICMPLT | Opcodes.IF_ICMPGE | Opcodes.IF_ICMPGT | Opcodes.IF_ICMPLE | Opcodes.IF_ACMPEQ | Opcodes.IF_ACMPNE | Opcodes.LRETURN | Opcodes.DRETURN =>
            pop(2)
        case Opcodes.DUP =>
            t1 = pop()
            push(t1)
            push(t1)
        case Opcodes.DUP_X1 =>
            t1 = pop()
            t2 = pop()
            push(t1)
            push(t2)
            push(t1)
        case Opcodes.DUP_X2 =>
            t1 = pop()
            t2 = pop()
            t3 = pop()
            push(t1)
            push(t3)
            push(t2)
            push(t1)
        case Opcodes.DUP2 =>
            t1 = pop()
            t2 = pop()
            push(t2)
            push(t1)
            push(t2)
            push(t1)
        case Opcodes.DUP2_X1 =>
            t1 = pop()
            t2 = pop()
            t3 = pop()
            push(t2)
            push(t1)
            push(t3)
            push(t2)
            push(t1)
        case Opcodes.DUP2_X2 =>
            t1 = pop()
            t2 = pop()
            t3 = pop()
            t4 = pop()
            push(t2)
            push(t1)
            push(t4)
            push(t3)
            push(t2)
            push(t1)
        case Opcodes.SWAP =>
            t1 = pop()
            t2 = pop()
            push(t1)
            push(t2)
        case Opcodes.IADD | Opcodes.ISUB | Opcodes.IMUL | Opcodes.IDIV | Opcodes.IREM | Opcodes.IAND | Opcodes.IOR | Opcodes.IXOR | Opcodes.ISHL | Opcodes.ISHR | Opcodes.IUSHR | Opcodes.L2I | Opcodes.D2I | Opcodes.FCMPL | Opcodes.FCMPG =>
            pop(2)
            push(INTEGER)
        case Opcodes.LADD | Opcodes.LSUB | Opcodes.LMUL | Opcodes.LDIV | Opcodes.LREM | Opcodes.LAND | Opcodes.LOR | Opcodes.LXOR =>
            pop(4)
            push(LONG)
            push(TOP)
        case Opcodes.FADD | Opcodes.FSUB | Opcodes.FMUL | Opcodes.FDIV | Opcodes.FREM | Opcodes.L2F | Opcodes.D2F =>
            pop(2)
            push(FLOAT)
        case Opcodes.DADD | Opcodes.DSUB | Opcodes.DMUL | Opcodes.DDIV | Opcodes.DREM =>
            pop(4)
            push(DOUBLE)
            push(TOP)
        case Opcodes.LSHL | Opcodes.LSHR | Opcodes.LUSHR =>
            pop(3)
            push(LONG)
            push(TOP)
        case Opcodes.IINC =>
            set(arg, INTEGER)
        case Opcodes.I2L |  Opcodes.F2L =>
            pop(1)
            push(LONG)
            push(TOP)
        case Opcodes.I2F =>
            pop(1)
            push(FLOAT)
        case Opcodes.I2D | Opcodes.F2D =>
            pop(1)
            push(DOUBLE)
            push(TOP)
        case Opcodes.F2I | Opcodes.ARRAYLENGTH | Opcodes.INSTANCEOF =>
            pop(1)
            push(INTEGER)
        case Opcodes.LCMP | Opcodes.DCMPL | Opcodes.DCMPG =>
            pop(4)
            push(INTEGER)
        case Opcodes.JSR | Opcodes.RET =>
            throw new RuntimeException(
                    "JSR/RET are not supported with computeFrames option")
        case Opcodes.GETSTATIC =>
            push(cw, item.strVal3)
        case Opcodes.PUTSTATIC =>
            pop(item.strVal3)
        case Opcodes.GETFIELD =>
            pop(1)
            push(cw, item.strVal3)
        case Opcodes.PUTFIELD =>
            pop(item.strVal3)
            pop()
        case Opcodes.INVOKEVIRTUAL | Opcodes.INVOKESPECIAL | Opcodes.INVOKESTATIC | Opcodes.INVOKEINTERFACE =>
            pop(item.strVal3)
            if (opcode != Opcodes.INVOKESTATIC) {
                t1 = pop()
                if (opcode == Opcodes.INVOKESPECIAL
                        && item.strVal2.charAt(0) == '<') {
                    init(t1)
                }
            }
            push(cw, item.strVal3)
        case Opcodes.INVOKEDYNAMIC =>
            pop(item.strVal2)
            push(cw, item.strVal2)
        case Opcodes.NEW =>
            push(UNINITIALIZED | cw.addUninitializedType(item.strVal1, arg))
        case Opcodes.NEWARRAY =>
            pop()
            arg match {
              case Opcodes.T_BOOLEAN =>
                push(ARRAY_OF | BOOLEAN)
              case Opcodes.T_CHAR =>
                push(ARRAY_OF | CHAR)
              case Opcodes.T_BYTE =>
                push(ARRAY_OF | BYTE)
              case Opcodes.T_SHORT =>
                push(ARRAY_OF | SHORT)
              case Opcodes.T_INT =>
                push(ARRAY_OF | INTEGER)
              case Opcodes.T_FLOAT =>
                push(ARRAY_OF | FLOAT)
              case Opcodes.T_DOUBLE =>
                push(ARRAY_OF | DOUBLE)
              // case Opcodes.T_LONG:
              case _ =>
                push(ARRAY_OF | LONG)
            }
         case Opcodes.ANEWARRAY =>
            val s = item.strVal1
            pop()
            if (s.charAt(0) == '[') {
                push(cw, '[' + s)
            } else {
                push(ARRAY_OF | OBJECT | cw.addType(s))
            }
          case Opcodes.CHECKCAST =>
            val s = item.strVal1
            pop()
            if (s.charAt(0) == '[') {
                push(cw, s)
            } else {
                push(OBJECT | cw.addType(s))
            }
          // case Opcodes.MULTIANEWARRAY:
          case _ =>
            pop(arg)
            push(cw, item.strVal1)
        }
    }

    def merge(cw: ClassWriter , frame:Frame  , edge: Int): Boolean = ??? /*{
        boolean changed = false
        int i, s, dim, kind, t

        int nLocal = inputLocals.length
        int nStack = inputStack.length
        if (frame.inputLocals == null) {
            frame.inputLocals = new int[nLocal]
            changed = true
        }

        for (i = 0 i < nLocal ++i) {
            if (outputLocals != null && i < outputLocals.length) {
                s = outputLocals[i]
                if (s == 0) {
                    t = inputLocals[i]
                } else {
                    dim = s & DIM
                    kind = s & KIND
                    if (kind == BASE) {
                        t = s
                    } else {
                        if (kind == LOCAL) {
                            t = dim + inputLocals[s & VALUE]
                        } else {
                            t = dim + inputStack[nStack - (s & VALUE)]
                        }
                        if ((s & TOP_IF_LONG_OR_DOUBLE) != 0
                                && (t == LONG || t == DOUBLE)) {
                            t = TOP
                        }
                    }
                }
            } else {
                t = inputLocals[i]
            }
            if (initializations != null) {
                t = init(cw, t)
            }
            changed |= merge(cw, t, frame.inputLocals, i)
        }

        if (edge > 0) {
            for (i = 0 i < nLocal ++i) {
                t = inputLocals[i]
                changed |= merge(cw, t, frame.inputLocals, i)
            }
            if (frame.inputStack == null) {
                frame.inputStack = new int[1]
                changed = true
            }
            changed |= merge(cw, edge, frame.inputStack, 0)
            return changed
        }

        int nInputStack = inputStack.length + owner.inputStackTop
        if (frame.inputStack == null) {
            frame.inputStack = new int[nInputStack + outputStackTop]
            changed = true
        }

        for (i = 0 i < nInputStack ++i) {
            t = inputStack[i]
            if (initializations != null) {
                t = init(cw, t)
            }
            changed |= merge(cw, t, frame.inputStack, i)
        }
        for (i = 0 i < outputStackTop ++i) {
            s = outputStack[i]
            dim = s & DIM
            kind = s & KIND
            if (kind == BASE) {
                t = s
            } else {
                if (kind == LOCAL) {
                    t = dim + inputLocals[s & VALUE]
                } else {
                    t = dim + inputStack[nStack - (s & VALUE)]
                }
                if ((s & TOP_IF_LONG_OR_DOUBLE) != 0
                        && (t == LONG || t == DOUBLE)) {
                    t = TOP
                }
            }
            if (initializations != null) {
                t = init(cw, t)
            }
            changed |= merge(cw, t, frame.inputStack, nInputStack + i)
        }
        return changed
    }*/

}

object Frame {
    final val DIM = 0xF0000000
    final val ARRAY_OF = 0x10000000
    final val ELEMENT_OF = 0xF0000000
    final val KIND = 0xF000000
    final val TOP_IF_LONG_OR_DOUBLE = 0x800000
    final val VALUE = 0x7FFFFF
    final val BASE_KIND = 0xFF00000
    final val BASE_VALUE = 0xFFFFF
    final val BASE = 0x1000000
    final val OBJECT = BASE | 0x700000
    final val UNINITIALIZED = BASE | 0x800000
    final val LOCAL = 0x2000000
    final val STACK = 0x3000000
    final val TOP = BASE | 0
    final val BOOLEAN = BASE | 9
    final val BYTE = BASE | 10
    final val CHAR = BASE | 11
    final val SHORT = BASE | 12
    final val INTEGER = BASE | 1
    final val FLOAT = BASE | 2
    final val DOUBLE = BASE | 3
    final val LONG = BASE | 4
    final val NULL = BASE | 5
    final val UNINITIALIZED_THIS = BASE | 6
    final val SIZE = {
        var i = 0
        var b = new Array[Int](202)
        val s = ("EFFFFFFFFGGFFFGGFFFEEFGFGFEEEEEEEEEEEEEEEEEEEEDEDEDDDDD"
                 + "CDCDEEEEEEEEEEEEEEEEEEEEBABABBBBDCFFFGGGEDCDCDCDCDCDCDCDCD"
                 + "CDCEEEEDDDDDDDCDCDCEFEFDDEEFFDEDEEEBDDBBDDDDDDCCCCCCCCEFED"
                 + "DDCDCDEEEEEEEEEEFEEEEEEDDEEDDEE")
        while (i < b.length) {
            b(i) = s.charAt(i) - 'E'
            i += 1
        }
        b

        // code to generate the above string
        //
        // int NA = 0 // not applicable (unused opcode or variable size opcode)
        //
        // b = new int[] {
        // 0, //NOP, // visitInsn
        // 1, //ACONST_NULL, // -
        // 1, //ICONST_M1, // -
        // 1, //ICONST_0, // -
        // 1, //ICONST_1, // -
        // 1, //ICONST_2, // -
        // 1, //ICONST_3, // -
        // 1, //ICONST_4, // -
        // 1, //ICONST_5, // -
        // 2, //LCONST_0, // -
        // 2, //LCONST_1, // -
        // 1, //FCONST_0, // -
        // 1, //FCONST_1, // -
        // 1, //FCONST_2, // -
        // 2, //DCONST_0, // -
        // 2, //DCONST_1, // -
        // 1, //BIPUSH, // visitIntInsn
        // 1, //SIPUSH, // -
        // 1, //LDC, // visitLdcInsn
        // NA, //LDC_W, // -
        // NA, //LDC2_W, // -
        // 1, //ILOAD, // visitVarInsn
        // 2, //LLOAD, // -
        // 1, //FLOAD, // -
        // 2, //DLOAD, // -
        // 1, //ALOAD, // -
        // NA, //ILOAD_0, // -
        // NA, //ILOAD_1, // -
        // NA, //ILOAD_2, // -
        // NA, //ILOAD_3, // -
        // NA, //LLOAD_0, // -
        // NA, //LLOAD_1, // -
        // NA, //LLOAD_2, // -
        // NA, //LLOAD_3, // -
        // NA, //FLOAD_0, // -
        // NA, //FLOAD_1, // -
        // NA, //FLOAD_2, // -
        // NA, //FLOAD_3, // -
        // NA, //DLOAD_0, // -
        // NA, //DLOAD_1, // -
        // NA, //DLOAD_2, // -
        // NA, //DLOAD_3, // -
        // NA, //ALOAD_0, // -
        // NA, //ALOAD_1, // -
        // NA, //ALOAD_2, // -
        // NA, //ALOAD_3, // -
        // -1, //IALOAD, // visitInsn
        // 0, //LALOAD, // -
        // -1, //FALOAD, // -
        // 0, //DALOAD, // -
        // -1, //AALOAD, // -
        // -1, //BALOAD, // -
        // -1, //CALOAD, // -
        // -1, //SALOAD, // -
        // -1, //ISTORE, // visitVarInsn
        // -2, //LSTORE, // -
        // -1, //FSTORE, // -
        // -2, //DSTORE, // -
        // -1, //ASTORE, // -
        // NA, //ISTORE_0, // -
        // NA, //ISTORE_1, // -
        // NA, //ISTORE_2, // -
        // NA, //ISTORE_3, // -
        // NA, //LSTORE_0, // -
        // NA, //LSTORE_1, // -
        // NA, //LSTORE_2, // -
        // NA, //LSTORE_3, // -
        // NA, //FSTORE_0, // -
        // NA, //FSTORE_1, // -
        // NA, //FSTORE_2, // -
        // NA, //FSTORE_3, // -
        // NA, //DSTORE_0, // -
        // NA, //DSTORE_1, // -
        // NA, //DSTORE_2, // -
        // NA, //DSTORE_3, // -
        // NA, //ASTORE_0, // -
        // NA, //ASTORE_1, // -
        // NA, //ASTORE_2, // -
        // NA, //ASTORE_3, // -
        // -3, //IASTORE, // visitInsn
        // -4, //LASTORE, // -
        // -3, //FASTORE, // -
        // -4, //DASTORE, // -
        // -3, //AASTORE, // -
        // -3, //BASTORE, // -
        // -3, //CASTORE, // -
        // -3, //SASTORE, // -
        // -1, //POP, // -
        // -2, //POP2, // -
        // 1, //DUP, // -
        // 1, //DUP_X1, // -
        // 1, //DUP_X2, // -
        // 2, //DUP2, // -
        // 2, //DUP2_X1, // -
        // 2, //DUP2_X2, // -
        // 0, //SWAP, // -
        // -1, //IADD, // -
        // -2, //LADD, // -
        // -1, //FADD, // -
        // -2, //DADD, // -
        // -1, //ISUB, // -
        // -2, //LSUB, // -
        // -1, //FSUB, // -
        // -2, //DSUB, // -
        // -1, //IMUL, // -
        // -2, //LMUL, // -
        // -1, //FMUL, // -
        // -2, //DMUL, // -
        // -1, //IDIV, // -
        // -2, //LDIV, // -
        // -1, //FDIV, // -
        // -2, //DDIV, // -
        // -1, //IREM, // -
        // -2, //LREM, // -
        // -1, //FREM, // -
        // -2, //DREM, // -
        // 0, //INEG, // -
        // 0, //LNEG, // -
        // 0, //FNEG, // -
        // 0, //DNEG, // -
        // -1, //ISHL, // -
        // -1, //LSHL, // -
        // -1, //ISHR, // -
        // -1, //LSHR, // -
        // -1, //IUSHR, // -
        // -1, //LUSHR, // -
        // -1, //IAND, // -
        // -2, //LAND, // -
        // -1, //IOR, // -
        // -2, //LOR, // -
        // -1, //IXOR, // -
        // -2, //LXOR, // -
        // 0, //IINC, // visitIincInsn
        // 1, //I2L, // visitInsn
        // 0, //I2F, // -
        // 1, //I2D, // -
        // -1, //L2I, // -
        // -1, //L2F, // -
        // 0, //L2D, // -
        // 0, //F2I, // -
        // 1, //F2L, // -
        // 1, //F2D, // -
        // -1, //D2I, // -
        // 0, //D2L, // -
        // -1, //D2F, // -
        // 0, //I2B, // -
        // 0, //I2C, // -
        // 0, //I2S, // -
        // -3, //LCMP, // -
        // -1, //FCMPL, // -
        // -1, //FCMPG, // -
        // -3, //DCMPL, // -
        // -3, //DCMPG, // -
        // -1, //IFEQ, // visitJumpInsn
        // -1, //IFNE, // -
        // -1, //IFLT, // -
        // -1, //IFGE, // -
        // -1, //IFGT, // -
        // -1, //IFLE, // -
        // -2, //IF_ICMPEQ, // -
        // -2, //IF_ICMPNE, // -
        // -2, //IF_ICMPLT, // -
        // -2, //IF_ICMPGE, // -
        // -2, //IF_ICMPGT, // -
        // -2, //IF_ICMPLE, // -
        // -2, //IF_ACMPEQ, // -
        // -2, //IF_ACMPNE, // -
        // 0, //GOTO, // -
        // 1, //JSR, // -
        // 0, //RET, // visitVarInsn
        // -1, //TABLESWITCH, // visiTableSwitchInsn
        // -1, //LOOKUPSWITCH, // visitLookupSwitch
        // -1, //IRETURN, // visitInsn
        // -2, //LRETURN, // -
        // -1, //FRETURN, // -
        // -2, //DRETURN, // -
        // -1, //ARETURN, // -
        // 0, //RETURN, // -
        // NA, //GETSTATIC, // visitFieldInsn
        // NA, //PUTSTATIC, // -
        // NA, //GETFIELD, // -
        // NA, //PUTFIELD, // -
        // NA, //INVOKEVIRTUAL, // visitMethodInsn
        // NA, //INVOKESPECIAL, // -
        // NA, //INVOKESTATIC, // -
        // NA, //INVOKEINTERFACE, // -
        // NA, //INVOKEDYNAMIC, // visitInvokeDynamicInsn
        // 1, //NEW, // visitTypeInsn
        // 0, //NEWARRAY, // visitIntInsn
        // 0, //ANEWARRAY, // visitTypeInsn
        // 0, //ARRAYLENGTH, // visitInsn
        // NA, //ATHROW, // -
        // 0, //CHECKCAST, // visitTypeInsn
        // 0, //INSTANCEOF, // -
        // -1, //MONITORENTER, // visitInsn
        // -1, //MONITOREXIT, // -
        // NA, //WIDE, // NOT VISITED
        // NA, //MULTIANEWARRAY, // visitMultiANewArrayInsn
        // -1, //IFNULL, // visitJumpInsn
        // -1, //IFNONNULL, // -
        // NA, //GOTO_W, // -
        // NA, //JSR_W, // -
        // }
        // for (i = 0 i < b.length ++i) {
        // System.err.print((char)('E' + b[i]))
        // }
        // System.err.println()
    }

    def type_(cw: ClassWriter, desc: String): Int = ???/*{
        String t
        int index = desc.charAt(0) == '(' ? desc.indexOf(')') + 1 : 0
        switch (desc.charAt(index)) {
        case 'V':
            return 0
        case 'Z':
        case 'C':
        case 'B':
        case 'S':
        case 'I':
            return INTEGER
        case 'F':
            return FLOAT
        case 'J':
            return LONG
        case 'D':
            return DOUBLE
        case 'L':
            // stores the internal name, not the descriptor!
            t = desc.substring(index + 1, desc.length() - 1)
            return OBJECT | cw.addType(t)
            // case '[':
        default:
            // extracts the dimensions and the element type
            int data
            int dims = index + 1
            while (desc.charAt(dims) == '[') {
                ++dims
            }
            switch (desc.charAt(dims)) {
            case 'Z':
                data = BOOLEAN
                break
            case 'C':
                data = CHAR
                break
            case 'B':
                data = BYTE
                break
            case 'S':
                data = SHORT
                break
            case 'I':
                data = INTEGER
                break
            case 'F':
                data = FLOAT
                break
            case 'J':
                data = LONG
                break
            case 'D':
                data = DOUBLE
                break
            // case 'L':
            default:
                // stores the internal name, not the descriptor
                t = desc.substring(dims + 1, desc.length() - 1)
                data = OBJECT | cw.addType(t)
            }
            return (dims - index) << 28 | data
        }
    }*/

    def merge(cw: ClassWriter , t: Int,
            types: Array[Int], index: Int): Boolean = ??? /*{
        int u = types[index]
        if (u == t) {
            // if the types are equal, merge(u,t)=u, so there is no change
            return false
        }
        if ((t & ~DIM) == NULL) {
            if (u == NULL) {
                return false
            }
            t = NULL
        }
        if (u == 0) {
            // if types[index] has never been assigned, merge(u,t)=t
            types[index] = t
            return true
        }
        int v
        if ((u & BASE_KIND) == OBJECT || (u & DIM) != 0) {
            // if u is a reference type of any dimension
            if (t == NULL) {
                // if t is the NULL type, merge(u,t)=u, so there is no change
                return false
            } else if ((t & (DIM | BASE_KIND)) == (u & (DIM | BASE_KIND))) {
                // if t and u have the same dimension and same base kind
                if ((u & BASE_KIND) == OBJECT) {
                    // if t is also a reference type, and if u and t have the
                    // same dimension merge(u,t) = dim(t) | common parent of the
                    // element types of u and t
                    v = (t & DIM) | OBJECT
                            | cw.getMergedType(t & BASE_VALUE, u & BASE_VALUE)
                } else {
                    // if u and t are array types, but not with the same element
                    // type, merge(u,t) = dim(u) - 1 | java/lang/Object
                    int vdim = ELEMENT_OF + (u & DIM)
                    v = vdim | OBJECT | cw.addType("java/lang/Object")
                }
            } else if ((t & BASE_KIND) == OBJECT || (t & DIM) != 0) {
                // if t is any other reference or array type, the merged type
                // is min(udim, tdim) | java/lang/Object, where udim is the
                // array dimension of u, minus 1 if u is an array type with a
                // primitive element type (and similarly for tdim).
                int tdim = (((t & DIM) == 0 || (t & BASE_KIND) == OBJECT) ? 0
                        : ELEMENT_OF) + (t & DIM)
                int udim = (((u & DIM) == 0 || (u & BASE_KIND) == OBJECT) ? 0
                        : ELEMENT_OF) + (u & DIM)
                v = Math.min(tdim, udim) | OBJECT
                        | cw.addType("java/lang/Object")
            } else {
                // if t is any other type, merge(u,t)=TOP
                v = TOP
            }
        } else if (u == NULL) {
            // if u is the NULL type, merge(u,t)=t,
            // or TOP if t is not a reference type
            v = (t & BASE_KIND) == OBJECT || (t & DIM) != 0 ? t : TOP
        } else {
            // if u is any other type, merge(u,t)=TOP whatever t
            v = TOP
        }
        if (u != v) {
            types[index] = v
            return true
        }
        return false
    }*/
}
