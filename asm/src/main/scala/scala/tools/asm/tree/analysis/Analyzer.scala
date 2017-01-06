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
import java.util.HashMap
import java.util.List
import java.util.Map

import scala.tools.asm.Opcodes, Opcodes._
import scala.tools.asm.Type
import scala.tools.asm.tree.AbstractInsnNode
import scala.tools.asm.tree.IincInsnNode
import scala.tools.asm.tree.InsnList
import scala.tools.asm.tree.JumpInsnNode
import scala.tools.asm.tree.LabelNode
import scala.tools.asm.tree.LookupSwitchInsnNode
import scala.tools.asm.tree.MethodNode
import scala.tools.asm.tree.TableSwitchInsnNode
import scala.tools.asm.tree.TryCatchBlockNode
import scala.tools.asm.tree.VarInsnNode

class Analyzer[V <: Value] {
    private var interpreter: Interpreter[V] = _
    private var n: Int = _
    private var insns: InsnList = _
    private var handlers: Array[List[TryCatchBlockNode]] = _
    private var frames: Array[Frame[V]] = _
    private var subroutines: Array[Subroutine] = _
    private var queued: Array[Boolean] = _
    private var queue: Array[Int] = _
    private var top: Int = _

    def this(interpreter:Interpreter[V]) = {
        this()
        this.interpreter = interpreter
    }

    def analyze(owner: String, m: MethodNode): Array[Frame[V]] = ??? /*{
        if ((m.access & (ACC_ABSTRACT | ACC_NATIVE)) != 0) {
            frames = (Array[Frame[V]]) new Frame<?>[0]
            return frames
        }
        n = m.instructions.size()
        insns = m.instructions
        handlers = (Array[List[TryCatchBlockNode]]) new List<?>[n]
        frames = (Array[Frame[V]]) new Frame<?>[n]
        subroutines = new Subroutine[n]
        queued = new boolean[n]
        queue = new int[n]
        top = 0

        // computes exception handlers for each instruction
        for (int i = 0 i < m.tryCatchBlocks.size() ++i) {
            TryCatchBlockNode tcb = m.tryCatchBlocks.get(i)
            int begin = insns.indexOf(tcb.start)
            int end = insns.indexOf(tcb.end)
            for (int j = begin j < end ++j) {
                List<TryCatchBlockNode> insnHandlers = handlers[j]
                if (insnHandlers == null) {
                    insnHandlers = new ArrayList<TryCatchBlockNode>()
                    handlers[j] = insnHandlers
                }
                insnHandlers.add(tcb)
            }
        }

        // computes the subroutine for each instruction:
        Subroutine main = new Subroutine(null, m.maxLocals, null)
        List<AbstractInsnNode> subroutineCalls = new ArrayList<AbstractInsnNode>()
        Map<LabelNode, Subroutine> subroutineHeads = new HashMap<LabelNode, Subroutine>()
        findSubroutine(0, main, subroutineCalls)
        while (!subroutineCalls.isEmpty()) {
            JumpInsnNode jsr = (JumpInsnNode) subroutineCalls.remove(0)
            Subroutine sub = subroutineHeads.get(jsr.label)
            if (sub == null) {
                sub = new Subroutine(jsr.label, m.maxLocals, jsr)
                subroutineHeads.put(jsr.label, sub)
                findSubroutine(insns.indexOf(jsr.label), sub, subroutineCalls)
            } else {
                sub.callers.add(jsr)
            }
        }
        for (int i = 0 i < n ++i) {
            if (subroutines[i] != null && subroutines[i].start == null) {
                subroutines[i] = null
            }
        }

        // initializes the data structures for the control flow analysis
        Frame[V] current = newFrame(m.maxLocals, m.maxStack)
        Frame[V] handler = newFrame(m.maxLocals, m.maxStack)
        current.setReturn(interpreter.newReturnTypeValue(Type.getReturnType(m.desc)))
        Type[] args = Type.getArgumentTypes(m.desc)
        int local = 0
        boolean isInstanceMethod = (m.access & ACC_STATIC) == 0
        if (isInstanceMethod) {
            Type ctype = Type.getObjectType(owner)
            current.setLocal(local, interpreter.newParameterValue(isInstanceMethod, local, ctype))
            local++
        }
        for (int i = 0 i < args.length ++i) {
            current.setLocal(local, interpreter.newParameterValue(isInstanceMethod, local, args[i]))
            local++
            if (args[i].getSize() == 2) {
                current.setLocal(local, interpreter.newEmptyValueAfterSize2Local(local))
                local++
            }
        }
        while (local < m.maxLocals) {
            current.setLocal(local, interpreter.newEmptyNonParameterLocalValue(local))
            local++
        }
        merge(0, current, null)

        init(owner, m)

        // control flow analysis
        while (top > 0) {
            int insn = queue[--top]
            Frame[V] f = frames[insn]
            Subroutine subroutine = subroutines[insn]
            queued[insn] = false

            AbstractInsnNode insnNode = null
            try {
                insnNode = m.instructions.get(insn)
                int insnOpcode = insnNode.getOpcode()
                int insnType = insnNode.getType()

                if (insnType == AbstractInsnNode.LABEL
                        || insnType == AbstractInsnNode.LINE
                        || insnType == AbstractInsnNode.FRAME) {
                    merge(insn + 1, f, subroutine)
                    newControlFlowEdge(insn, insn + 1)
                } else {
                    current.init(f).execute(insnNode, interpreter)
                    subroutine = subroutine == null ? null : subroutine.copy()

                    if (insnNode instanceof JumpInsnNode) {
                        JumpInsnNode j = (JumpInsnNode) insnNode
                        if (insnOpcode != GOTO && insnOpcode != JSR) {
                            merge(insn + 1, current, subroutine)
                            newControlFlowEdge(insn, insn + 1)
                        }
                        int jump = insns.indexOf(j.label)
                        if (insnOpcode == JSR) {
                            merge(jump, current, new Subroutine(j.label,
                                    m.maxLocals, j))
                        } else {
                            merge(jump, current, subroutine)
                        }
                        newControlFlowEdge(insn, jump)
                    } else if (insnNode instanceof LookupSwitchInsnNode) {
                        LookupSwitchInsnNode lsi = (LookupSwitchInsnNode) insnNode
                        int jump = insns.indexOf(lsi.dflt)
                        merge(jump, current, subroutine)
                        newControlFlowEdge(insn, jump)
                        for (int j = 0 j < lsi.labels.size() ++j) {
                            LabelNode label = lsi.labels.get(j)
                            jump = insns.indexOf(label)
                            merge(jump, current, subroutine)
                            newControlFlowEdge(insn, jump)
                        }
                    } else if (insnNode instanceof TableSwitchInsnNode) {
                        TableSwitchInsnNode tsi = (TableSwitchInsnNode) insnNode
                        int jump = insns.indexOf(tsi.dflt)
                        merge(jump, current, subroutine)
                        newControlFlowEdge(insn, jump)
                        for (int j = 0 j < tsi.labels.size() ++j) {
                            LabelNode label = tsi.labels.get(j)
                            jump = insns.indexOf(label)
                            merge(jump, current, subroutine)
                            newControlFlowEdge(insn, jump)
                        }
                    } else if (insnOpcode == RET) {
                        if (subroutine == null) {
                            throw new AnalyzerException(insnNode,
                                    "RET instruction outside of a sub routine")
                        }
                        for (int i = 0 i < subroutine.callers.size() ++i) {
                            JumpInsnNode caller = subroutine.callers.get(i)
                            int call = insns.indexOf(caller)
                            if (frames[call] != null) {
                                merge(call + 1, frames[call], current,
                                        subroutines[call], subroutine.access)
                                newControlFlowEdge(insn, call + 1)
                            }
                        }
                    } else if (insnOpcode != ATHROW
                            && (insnOpcode < IRETURN || insnOpcode > RETURN)) {
                        if (subroutine != null) {
                            if (insnNode instanceof VarInsnNode) {
                                int var = ((VarInsnNode) insnNode).var
                                subroutine.access[var] = true
                                if (insnOpcode == LLOAD || insnOpcode == DLOAD
                                        || insnOpcode == LSTORE
                                        || insnOpcode == DSTORE) {
                                    subroutine.access[var + 1] = true
                                }
                            } else if (insnNode instanceof IincInsnNode) {
                                int var = ((IincInsnNode) insnNode).var
                                subroutine.access[var] = true
                            }
                        }
                        merge(insn + 1, current, subroutine)
                        newControlFlowEdge(insn, insn + 1)
                    }
                }

                List<TryCatchBlockNode> insnHandlers = handlers[insn]
                if (insnHandlers != null) {
                    for (int i = 0 i < insnHandlers.size() ++i) {
                        TryCatchBlockNode tcb = insnHandlers.get(i)
                        Type type
                        if (tcb.type == null) {
                            type = Type.getObjectType("java/lang/Throwable")
                        } else {
                            type = Type.getObjectType(tcb.type)
                        }
                        int jump = insns.indexOf(tcb.handler)
                        if (newControlFlowExceptionEdge(insn, tcb)) {
                            handler.init(f)
                            handler.clearStack()
                            handler.push(interpreter.newExceptionValue(tcb, handler, type))
                            merge(jump, handler, subroutine)
                        }
                    }
                }
            } catch (AnalyzerException e) {
                throw new AnalyzerException(e.node, "Error at instruction "
                        + insn + ": " + e.getMessage(), e)
            } catch (Exception e) {
                throw new AnalyzerException(insnNode, "Error at instruction "
                        + insn + ": " + e.getMessage(), e)
            }
        }

        return frames
    }*/

    private def findSubroutine(insn: Int, sub: Subroutine ,
             calls:List[AbstractInsnNode]): Unit = ???/*{
        while (true) {
            if (insn < 0 || insn >= n) {
                throw new AnalyzerException(null,
                        "Execution can fall off end of the code")
            }
            if (subroutines[insn] != null) {
                return
            }
            subroutines[insn] = sub.copy()
            AbstractInsnNode node = insns.get(insn)

            // calls findSubroutine recursively on normal successors
            if (node instanceof JumpInsnNode) {
                if (node.getOpcode() == JSR) {
                    // do not follow a JSR, it leads to another subroutine!
                    calls.add(node)
                } else {
                    JumpInsnNode jnode = (JumpInsnNode) node
                    findSubroutine(insns.indexOf(jnode.label), sub, calls)
                }
            } else if (node instanceof TableSwitchInsnNode) {
                TableSwitchInsnNode tsnode = (TableSwitchInsnNode) node
                findSubroutine(insns.indexOf(tsnode.dflt), sub, calls)
                for (int i = tsnode.labels.size() - 1 i >= 0 --i) {
                    LabelNode l = tsnode.labels.get(i)
                    findSubroutine(insns.indexOf(l), sub, calls)
                }
            } else if (node instanceof LookupSwitchInsnNode) {
                LookupSwitchInsnNode lsnode = (LookupSwitchInsnNode) node
                findSubroutine(insns.indexOf(lsnode.dflt), sub, calls)
                for (int i = lsnode.labels.size() - 1 i >= 0 --i) {
                    LabelNode l = lsnode.labels.get(i)
                    findSubroutine(insns.indexOf(l), sub, calls)
                }
            }

            // calls findSubroutine recursively on exception handler successors
            List<TryCatchBlockNode> insnHandlers = handlers[insn]
            if (insnHandlers != null) {
                for (int i = 0 i < insnHandlers.size() ++i) {
                    TryCatchBlockNode tcb = insnHandlers.get(i)
                    findSubroutine(insns.indexOf(tcb.handler), sub, calls)
                }
            }

            // if insn does not falls through to the next instruction, return.
            switch (node.getOpcode()) {
            case GOTO:
            case RET:
            case TABLESWITCH:
            case LOOKUPSWITCH:
            case IRETURN:
            case LRETURN:
            case FRETURN:
            case DRETURN:
            case ARETURN:
            case RETURN:
            case ATHROW:
                return
            }
            insn++
        }
    }*/

    def getFrames(): Array[Frame[V]] = frames

    def getHandlers(insn: Int): List[TryCatchBlockNode] = handlers(insn)

    protected def init(owner: String, m: MethodNode): Unit = ()

    protected def newFrame(nLocals: Int, nStack: Int):Frame[V] =
        new Frame[V](nLocals, nStack)

    protected def newFrame(src: Frame[_ <: V]): Frame[V] =
        new Frame[V](src)

    protected def newControlFlowEdge(insn: Int, successor: Int): Unit = ()

    protected def newControlFlowExceptionEdge(insn: Int,
            successor: Int): Boolean = true

    protected def newControlFlowExceptionEdge(insn: Int, tcb: TryCatchBlockNode): Boolean =
        newControlFlowExceptionEdge(insn, insns.indexOf(tcb.handler))

    private def merge(insn: Int,frame:Frame[V], subroutine: Subroutine ): Unit = ???/*{
        Frame[V] oldFrame = frames[insn]
        Subroutine oldSubroutine = subroutines[insn]
        boolean changes

        if (oldFrame == null) {
            frames[insn] = newFrame(frame)
            changes = true
        } else {
            changes = oldFrame.merge(frame, interpreter)
        }

        if (oldSubroutine == null) {
            if (subroutine != null) {
                subroutines[insn] = subroutine.copy()
                changes = true
            }
        } else {
            if (subroutine != null) {
                changes |= oldSubroutine.merge(subroutine)
            }
        }
        if (changes && !queued[insn]) {
            queued[insn] = true
            queue[top++] = insn
        }
    }*/

    private def merge(insn: Int, beforeJSR: Frame[V],
             afterRET:Frame[V], subroutineBeforeJSR:Subroutine ,
             access:Array[Boolean]): Unit = ???/*{
        Frame[V] oldFrame = frames[insn]
        Subroutine oldSubroutine = subroutines[insn]
        boolean changes

        afterRET.merge(beforeJSR, access)

        if (oldFrame == null) {
            frames[insn] = newFrame(afterRET)
            changes = true
        } else {
            changes = oldFrame.merge(afterRET, interpreter)
        }

        if (oldSubroutine != null && subroutineBeforeJSR != null) {
            changes |= oldSubroutine.merge(subroutineBeforeJSR)
        }
        if (changes && !queued[insn]) {
            queued[insn] = true
            queue[top++] = insn
        }
    }*/
}
