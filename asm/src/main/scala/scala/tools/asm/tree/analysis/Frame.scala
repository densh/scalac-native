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
package scala.tools.asm.tree.analysis

import java.util.ArrayList
import java.util.List

import scala.tools.asm.Opcodes
import scala.tools.asm.Type
import scala.tools.asm.tree.AbstractInsnNode
import scala.tools.asm.tree.IincInsnNode
import scala.tools.asm.tree.InvokeDynamicInsnNode
import scala.tools.asm.tree.MethodInsnNode
import scala.tools.asm.tree.MultiANewArrayInsnNode
import scala.tools.asm.tree.VarInsnNode

class Frame[V <: Value] {
    private var returnValue: V = _
    private var values: Array[V] = _
    private var locals: Int = _
    private var top: Int = _

    def this(nLocals: Int, nStack: Int) = {
        this()
        this.values = (new Array[Value](nLocals + nStack)).asInstanceOf[Array[V]]
        this.locals = nLocals
    }

    def this(src: Frame[_ <: V] ) = {
        this(src.locals, src.values.length - src.locals)
        init(src)
    }

    def init(src:Frame[_ <: V] ): Frame[V]  = {
        returnValue = src.returnValue
        System.arraycopy(src.values, 0, values, 0, values.length)
        top = src.top
        return this
    }

    def setReturn(v: V): Unit =
        returnValue = v

    def getLocals(): Int =
        locals

    def getMaxStackSize(): Int =
        values.length - locals

    def getLocal(i: Int): V = {
        if (i >= locals) {
            throw new IndexOutOfBoundsException(
                    "Trying to access an inexistant local variable")
        }
        values(i)
    }

    def setLocal(i: Int, value: V): Unit = {
        if (i >= locals) {
            throw new IndexOutOfBoundsException(
                    "Trying to access an inexistant local variable " + i)
        }
        values(i) = value
    }

    def getStackSize(): Int = top

    def getStack(i: Int): V =
        values(i + locals)

    def setStack(i: Int, value: V): Unit =
        values(i + locals) = value

    def clearStack(): Unit =
        top = 0

    def pop(): V = {
        if (top == 0) {
            throw new IndexOutOfBoundsException(
                    "Cannot pop operand off an empty stack.")
        }
        top -= 1
        return values(top + locals)
    }

    def push(value: V): Unit = {
        if (top + locals >= values.length) {
            throw new IndexOutOfBoundsException(
                    "Insufficient maximum stack size.")
        }
        values(top + locals) = value
        top += 1
    }

    def execute(insn: AbstractInsnNode, interpreter: Interpreter[V]): Unit = ???/*{
        V value1, value2, value3, value4
        List<V> values
        int var

        switch (insn.getOpcode()) {
        case Opcodes.NOP:
            break
        case Opcodes.ACONST_NULL:
        case Opcodes.ICONST_M1:
        case Opcodes.ICONST_0:
        case Opcodes.ICONST_1:
        case Opcodes.ICONST_2:
        case Opcodes.ICONST_3:
        case Opcodes.ICONST_4:
        case Opcodes.ICONST_5:
        case Opcodes.LCONST_0:
        case Opcodes.LCONST_1:
        case Opcodes.FCONST_0:
        case Opcodes.FCONST_1:
        case Opcodes.FCONST_2:
        case Opcodes.DCONST_0:
        case Opcodes.DCONST_1:
        case Opcodes.BIPUSH:
        case Opcodes.SIPUSH:
        case Opcodes.LDC:
            push(interpreter.newOperation(insn))
            break
        case Opcodes.ILOAD:
        case Opcodes.LLOAD:
        case Opcodes.FLOAD:
        case Opcodes.DLOAD:
        case Opcodes.ALOAD:
            push(interpreter.copyOperation(insn,
                    getLocal(((VarInsnNode) insn).var)))
            break
        case Opcodes.IALOAD:
        case Opcodes.LALOAD:
        case Opcodes.FALOAD:
        case Opcodes.DALOAD:
        case Opcodes.AALOAD:
        case Opcodes.BALOAD:
        case Opcodes.CALOAD:
        case Opcodes.SALOAD:
            value2 = pop()
            value1 = pop()
            push(interpreter.binaryOperation(insn, value1, value2))
            break
        case Opcodes.ISTORE:
        case Opcodes.LSTORE:
        case Opcodes.FSTORE:
        case Opcodes.DSTORE:
        case Opcodes.ASTORE:
            value1 = interpreter.copyOperation(insn, pop())
            var = ((VarInsnNode) insn).var
            setLocal(var, value1)
            if (value1.getSize() == 2) {
                setLocal(var + 1, interpreter.newEmptyValueAfterSize2Local(var + 1))
            }
            if (var > 0) {
                Value local = getLocal(var - 1)
                if (local != null && local.getSize() == 2) {
                    setLocal(var - 1, interpreter.newEmptyValueForPreviousSize2Local(var - 1))
                }
            }
            break
        case Opcodes.IASTORE:
        case Opcodes.LASTORE:
        case Opcodes.FASTORE:
        case Opcodes.DASTORE:
        case Opcodes.AASTORE:
        case Opcodes.BASTORE:
        case Opcodes.CASTORE:
        case Opcodes.SASTORE:
            value3 = pop()
            value2 = pop()
            value1 = pop()
            interpreter.ternaryOperation(insn, value1, value2, value3)
            break
        case Opcodes.POP:
            if (pop().getSize() == 2) {
                throw new AnalyzerException(insn, "Illegal use of POP")
            }
            break
        case Opcodes.POP2:
            if (pop().getSize() == 1) {
                if (pop().getSize() != 1) {
                    throw new AnalyzerException(insn, "Illegal use of POP2")
                }
            }
            break
        case Opcodes.DUP:
            value1 = pop()
            if (value1.getSize() != 1) {
                throw new AnalyzerException(insn, "Illegal use of DUP")
            }
            push(interpreter.copyOperation(insn, value1))
            push(interpreter.copyOperation(insn, value1))
            break
        case Opcodes.DUP_X1:
            value1 = pop()
            value2 = pop()
            if (value1.getSize() != 1 || value2.getSize() != 1) {
                throw new AnalyzerException(insn, "Illegal use of DUP_X1")
            }
            push(interpreter.copyOperation(insn, value1))
            push(interpreter.copyOperation(insn, value2))
            push(interpreter.copyOperation(insn, value1))
            break
        case Opcodes.DUP_X2:
            value1 = pop()
            if (value1.getSize() == 1) {
                value2 = pop()
                if (value2.getSize() == 1) {
                    value3 = pop()
                    if (value3.getSize() == 1) {
                        push(interpreter.copyOperation(insn, value1))
                        push(interpreter.copyOperation(insn, value3))
                        push(interpreter.copyOperation(insn, value2))
                        push(interpreter.copyOperation(insn, value1))
                        break
                    }
                } else {
                    push(interpreter.copyOperation(insn, value1))
                    push(interpreter.copyOperation(insn, value2))
                    push(interpreter.copyOperation(insn, value1))
                    break
                }
            }
            throw new AnalyzerException(insn, "Illegal use of DUP_X2")
        case Opcodes.DUP2:
            value1 = pop()
            if (value1.getSize() == 1) {
                value2 = pop()
                if (value2.getSize() == 1) {
                    push(interpreter.copyOperation(insn, value2))
                    push(interpreter.copyOperation(insn, value1))
                    push(interpreter.copyOperation(insn, value2))
                    push(interpreter.copyOperation(insn, value1))
                    break
                }
            } else {
                push(interpreter.copyOperation(insn, value1))
                push(interpreter.copyOperation(insn, value1))
                break
            }
            throw new AnalyzerException(insn, "Illegal use of DUP2")
        case Opcodes.DUP2_X1:
            value1 = pop()
            if (value1.getSize() == 1) {
                value2 = pop()
                if (value2.getSize() == 1) {
                    value3 = pop()
                    if (value3.getSize() == 1) {
                        push(interpreter.copyOperation(insn, value2))
                        push(interpreter.copyOperation(insn, value1))
                        push(interpreter.copyOperation(insn, value3))
                        push(interpreter.copyOperation(insn, value2))
                        push(interpreter.copyOperation(insn, value1))
                        break
                    }
                }
            } else {
                value2 = pop()
                if (value2.getSize() == 1) {
                    push(interpreter.copyOperation(insn, value1))
                    push(interpreter.copyOperation(insn, value2))
                    push(interpreter.copyOperation(insn, value1))
                    break
                }
            }
            throw new AnalyzerException(insn, "Illegal use of DUP2_X1")
        case Opcodes.DUP2_X2:
            value1 = pop()
            if (value1.getSize() == 1) {
                value2 = pop()
                if (value2.getSize() == 1) {
                    value3 = pop()
                    if (value3.getSize() == 1) {
                        value4 = pop()
                        if (value4.getSize() == 1) {
                            push(interpreter.copyOperation(insn, value2))
                            push(interpreter.copyOperation(insn, value1))
                            push(interpreter.copyOperation(insn, value4))
                            push(interpreter.copyOperation(insn, value3))
                            push(interpreter.copyOperation(insn, value2))
                            push(interpreter.copyOperation(insn, value1))
                            break
                        }
                    } else {
                        push(interpreter.copyOperation(insn, value2))
                        push(interpreter.copyOperation(insn, value1))
                        push(interpreter.copyOperation(insn, value3))
                        push(interpreter.copyOperation(insn, value2))
                        push(interpreter.copyOperation(insn, value1))
                        break
                    }
                }
            } else {
                value2 = pop()
                if (value2.getSize() == 1) {
                    value3 = pop()
                    if (value3.getSize() == 1) {
                        push(interpreter.copyOperation(insn, value1))
                        push(interpreter.copyOperation(insn, value3))
                        push(interpreter.copyOperation(insn, value2))
                        push(interpreter.copyOperation(insn, value1))
                        break
                    }
                } else {
                    push(interpreter.copyOperation(insn, value1))
                    push(interpreter.copyOperation(insn, value2))
                    push(interpreter.copyOperation(insn, value1))
                    break
                }
            }
            throw new AnalyzerException(insn, "Illegal use of DUP2_X2")
        case Opcodes.SWAP:
            value2 = pop()
            value1 = pop()
            if (value1.getSize() != 1 || value2.getSize() != 1) {
                throw new AnalyzerException(insn, "Illegal use of SWAP")
            }
            push(interpreter.copyOperation(insn, value2))
            push(interpreter.copyOperation(insn, value1))
            break
        case Opcodes.IADD:
        case Opcodes.LADD:
        case Opcodes.FADD:
        case Opcodes.DADD:
        case Opcodes.ISUB:
        case Opcodes.LSUB:
        case Opcodes.FSUB:
        case Opcodes.DSUB:
        case Opcodes.IMUL:
        case Opcodes.LMUL:
        case Opcodes.FMUL:
        case Opcodes.DMUL:
        case Opcodes.IDIV:
        case Opcodes.LDIV:
        case Opcodes.FDIV:
        case Opcodes.DDIV:
        case Opcodes.IREM:
        case Opcodes.LREM:
        case Opcodes.FREM:
        case Opcodes.DREM:
            value2 = pop()
            value1 = pop()
            push(interpreter.binaryOperation(insn, value1, value2))
            break
        case Opcodes.INEG:
        case Opcodes.LNEG:
        case Opcodes.FNEG:
        case Opcodes.DNEG:
            push(interpreter.unaryOperation(insn, pop()))
            break
        case Opcodes.ISHL:
        case Opcodes.LSHL:
        case Opcodes.ISHR:
        case Opcodes.LSHR:
        case Opcodes.IUSHR:
        case Opcodes.LUSHR:
        case Opcodes.IAND:
        case Opcodes.LAND:
        case Opcodes.IOR:
        case Opcodes.LOR:
        case Opcodes.IXOR:
        case Opcodes.LXOR:
            value2 = pop()
            value1 = pop()
            push(interpreter.binaryOperation(insn, value1, value2))
            break
        case Opcodes.IINC:
            var = ((IincInsnNode) insn).var
            setLocal(var, interpreter.unaryOperation(insn, getLocal(var)))
            break
        case Opcodes.I2L:
        case Opcodes.I2F:
        case Opcodes.I2D:
        case Opcodes.L2I:
        case Opcodes.L2F:
        case Opcodes.L2D:
        case Opcodes.F2I:
        case Opcodes.F2L:
        case Opcodes.F2D:
        case Opcodes.D2I:
        case Opcodes.D2L:
        case Opcodes.D2F:
        case Opcodes.I2B:
        case Opcodes.I2C:
        case Opcodes.I2S:
            push(interpreter.unaryOperation(insn, pop()))
            break
        case Opcodes.LCMP:
        case Opcodes.FCMPL:
        case Opcodes.FCMPG:
        case Opcodes.DCMPL:
        case Opcodes.DCMPG:
            value2 = pop()
            value1 = pop()
            push(interpreter.binaryOperation(insn, value1, value2))
            break
        case Opcodes.IFEQ:
        case Opcodes.IFNE:
        case Opcodes.IFLT:
        case Opcodes.IFGE:
        case Opcodes.IFGT:
        case Opcodes.IFLE:
            interpreter.unaryOperation(insn, pop())
            break
        case Opcodes.IF_ICMPEQ:
        case Opcodes.IF_ICMPNE:
        case Opcodes.IF_ICMPLT:
        case Opcodes.IF_ICMPGE:
        case Opcodes.IF_ICMPGT:
        case Opcodes.IF_ICMPLE:
        case Opcodes.IF_ACMPEQ:
        case Opcodes.IF_ACMPNE:
            value2 = pop()
            value1 = pop()
            interpreter.binaryOperation(insn, value1, value2)
            break
        case Opcodes.GOTO:
            break
        case Opcodes.JSR:
            push(interpreter.newOperation(insn))
            break
        case Opcodes.RET:
            break
        case Opcodes.TABLESWITCH:
        case Opcodes.LOOKUPSWITCH:
            interpreter.unaryOperation(insn, pop())
            break
        case Opcodes.IRETURN:
        case Opcodes.LRETURN:
        case Opcodes.FRETURN:
        case Opcodes.DRETURN:
        case Opcodes.ARETURN:
            value1 = pop()
            interpreter.unaryOperation(insn, value1)
            interpreter.returnOperation(insn, value1, returnValue)
            break
        case Opcodes.RETURN:
            if (returnValue != null) {
                throw new AnalyzerException(insn, "Incompatible return type")
            }
            break
        case Opcodes.GETSTATIC:
            push(interpreter.newOperation(insn))
            break
        case Opcodes.PUTSTATIC:
            interpreter.unaryOperation(insn, pop())
            break
        case Opcodes.GETFIELD:
            push(interpreter.unaryOperation(insn, pop()))
            break
        case Opcodes.PUTFIELD:
            value2 = pop()
            value1 = pop()
            interpreter.binaryOperation(insn, value1, value2)
            break
        case Opcodes.INVOKEVIRTUAL:
        case Opcodes.INVOKESPECIAL:
        case Opcodes.INVOKESTATIC:
        case Opcodes.INVOKEINTERFACE: {
            values = new ArrayList<V>()
            String desc = ((MethodInsnNode) insn).desc
            for (int i = Type.getArgumentTypes(desc).length i > 0 --i) {
                values.add(0, pop())
            }
            if (insn.getOpcode() != Opcodes.INVOKESTATIC) {
                values.add(0, pop())
            }
            if (Type.getReturnType(desc) == Type.VOID_TYPE) {
                interpreter.naryOperation(insn, values)
            } else {
                push(interpreter.naryOperation(insn, values))
            }
            break
        }
        case Opcodes.INVOKEDYNAMIC: {
            values = new ArrayList<V>()
            String desc = ((InvokeDynamicInsnNode) insn).desc
            for (int i = Type.getArgumentTypes(desc).length i > 0 --i) {
                values.add(0, pop())
            }
            if (Type.getReturnType(desc) == Type.VOID_TYPE) {
                interpreter.naryOperation(insn, values)
            } else {
                push(interpreter.naryOperation(insn, values))
            }
            break
        }
        case Opcodes.NEW:
            push(interpreter.newOperation(insn))
            break
        case Opcodes.NEWARRAY:
        case Opcodes.ANEWARRAY:
        case Opcodes.ARRAYLENGTH:
            push(interpreter.unaryOperation(insn, pop()))
            break
        case Opcodes.ATHROW:
            interpreter.unaryOperation(insn, pop())
            break
        case Opcodes.CHECKCAST:
        case Opcodes.INSTANCEOF:
            push(interpreter.unaryOperation(insn, pop()))
            break
        case Opcodes.MONITORENTER:
        case Opcodes.MONITOREXIT:
            interpreter.unaryOperation(insn, pop())
            break
        case Opcodes.MULTIANEWARRAY:
            values = new ArrayList<V>()
            for (int i = ((MultiANewArrayInsnNode) insn).dims i > 0 --i) {
                values.add(0, pop())
            }
            push(interpreter.naryOperation(insn, values))
            break
        case Opcodes.IFNULL:
        case Opcodes.IFNONNULL:
            interpreter.unaryOperation(insn, pop())
            break
        default:
            throw new RuntimeException("Illegal opcode " + insn.getOpcode())
        }
    }*/

    def merge(frame:Frame[_ <: V] , interpreter: Interpreter[V]): Boolean = ???/*{
        if (top != frame.top) {
            throw new AnalyzerException(null, "Incompatible stack heights")
        }
        boolean changes = false
        for (int i = 0 i < locals + top ++i) {
            V v = interpreter.merge(values[i], frame.values[i])
            if (!v.equals(values[i])) {
                values[i] = v
                changes = true
            }
        }
        return changes
    }*/

    def merge( frame:Frame[_ <: V], access:Array[Boolean] ): Boolean = ???/*{
        boolean changes = false
        for (int i = 0 i < locals ++i) {
            if (!access[i] && !values[i].equals(frame.values[i])) {
                values[i] = frame.values[i]
                changes = true
            }
        }
        return changes
    }*/

    override
    def toString() = ???/*{
        StringBuilder sb = new StringBuilder()
        for (int i = 0 i < getLocals() ++i) {
            sb.append(getLocal(i))
        }
        sb.append(' ')
        for (int i = 0 i < getStackSize() ++i) {
            sb.append(getStack(i).toString())
        }
        return sb.toString()
    }*/
}
