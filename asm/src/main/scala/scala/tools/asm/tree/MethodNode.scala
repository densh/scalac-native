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
package scala.tools.asm.tree

import java.util.ArrayList
import java.util.Arrays
import java.util.List

import scala.tools.asm.AnnotationVisitor
import scala.tools.asm.Attribute
import scala.tools.asm.ClassVisitor
import scala.tools.asm.Handle
import scala.tools.asm.Label
import scala.tools.asm.MethodVisitor
import scala.tools.asm.Opcodes
import scala.tools.asm.Type
import scala.tools.asm.TypePath

class MethodNode(api: Int) extends MethodVisitor(api) {
    var access                            : Int                              = _
    var name                              : String                           = _
    var desc                              : String                           = _
    var signature                         : String                           = _
    var exceptions                        : List[String]                     = _
    var parameters                        : List[ParameterNode]              = _
    var visibleAnnotations                : List[AnnotationNode]             = _
    var invisibleAnnotations              : List[AnnotationNode]             = _
    var visibleTypeAnnotations            : List[TypeAnnotationNode]         = _
    var invisibleTypeAnnotations          : List[TypeAnnotationNode]         = _
    var attrs                             : List[Attribute]                  = _
    var annotationDefault                 : Object                           = _
    var visibleParameterAnnotations       : Array[List[AnnotationNode]]      = _
    var invisibleParameterAnnotations     : Array[List[AnnotationNode]]      = _
    var instructions                      : InsnList                         = new InsnList()
    var tryCatchBlocks                    : List[TryCatchBlockNode]          = _
    var maxStack                          : Int                              = _
    var maxLocals                         : Int                              = _
    var localVariables                    : List[LocalVariableNode]          = _
    var visibleLocalVariableAnnotations   : List[LocalVariableAnnotationNode]= _
    var invisibleLocalVariableAnnotations : List[LocalVariableAnnotationNode]= _
    private var visited: Boolean = _

    def this() = {
        this(Opcodes.ASM5)
        if (getClass() != classOf[MethodNode]) {
            throw new IllegalStateException()
        }
    }

    def this(api: Int, access: Int, name: String,
             desc: String, signature: String, exceptions: Array[String]) = {
        this(api)
        ???
        /*
        this.access = access
        this.name = name
        this.desc = desc
        this.signature = signature
        this.exceptions = new ArrayList[String](exceptions == null ? 0
                : exceptions.length)
        boolean isAbstract = (access & Opcodes.ACC_ABSTRACT) != 0
        if (!isAbstract) {
            this.localVariables = new ArrayList[LocalVariableNode](5)
        }
        this.tryCatchBlocks = new ArrayList[TryCatchBlockNode]()
        if (exceptions != null) {
            this.exceptions.addAll(Arrays.asList(exceptions))
        }
        this.instructions = new InsnList()
        */
    }

    def this(access: Int, name: String, desc: String,
             signature: String, exceptions: Array[String]) = {
        this(Opcodes.ASM5, access, name, desc, signature, exceptions)
        if (getClass() != classOf[MethodNode]) {
            throw new IllegalStateException()
        }
    }

    override
    def visitParameter(name: String, access: Int): Unit = {
        if (parameters == null) {
            parameters = new ArrayList[ParameterNode](5)
        }
        parameters.add(new ParameterNode(name, access))
    }

    override
    def visitAnnotationDefault(): AnnotationVisitor = ???/*{
        return new AnnotationNode(new ArrayList<Object>(0) {
            override
            boolean add(Object o) {
                annotationDefault = o
                return super.add(o)
            }
        })
    }*/

    override
    def visitAnnotation(desc: String, visible: Boolean): AnnotationVisitor = ???/*{
        AnnotationNode an = new AnnotationNode(desc)
        if (visible) {
            if (visibleAnnotations == null) {
                visibleAnnotations = new ArrayList[AnnotationNode](1)
            }
            visibleAnnotations.add(an)
        } else {
            if (invisibleAnnotations == null) {
                invisibleAnnotations = new ArrayList[AnnotationNode](1)
            }
            invisibleAnnotations.add(an)
        }
        return an
    }*/

    override
    def visitTypeAnnotation(typeRef: Int,
            typePath: TypePath, desc: String, visible: Boolean): AnnotationVisitor = ???/*{
        TypeAnnotationNode an = new TypeAnnotationNode(typeRef, typePath, desc)
        if (visible) {
            if (visibleTypeAnnotations == null) {
                visibleTypeAnnotations = new ArrayList[TypeAnnotationNode](1)
            }
            visibleTypeAnnotations.add(an)
        } else {
            if (invisibleTypeAnnotations == null) {
                invisibleTypeAnnotations = new ArrayList[TypeAnnotationNode](1)
            }
            invisibleTypeAnnotations.add(an)
        }
        return an
    }*/

    override
    def visitParameterAnnotation(parameter: Int,
            desc: String, visible: Boolean): AnnotationVisitor = ???/*{
        AnnotationNode an = new AnnotationNode(desc)
        if (visible) {
            if (visibleParameterAnnotations == null) {
                int params = Type.getArgumentTypes(this.desc).length
                visibleParameterAnnotations = (Array[List[AnnotationNode]]) new List<?>[params]
            }
            if (visibleParameterAnnotations[parameter] == null) {
                visibleParameterAnnotations[parameter] = new ArrayList[AnnotationNode](
                        1)
            }
            visibleParameterAnnotations[parameter].add(an)
        } else {
            if (invisibleParameterAnnotations == null) {
                int params = Type.getArgumentTypes(this.desc).length
                invisibleParameterAnnotations = (Array[List[AnnotationNode]]) new List<?>[params]
            }
            if (invisibleParameterAnnotations[parameter] == null) {
                invisibleParameterAnnotations[parameter] = new ArrayList[AnnotationNode](
                        1)
            }
            invisibleParameterAnnotations[parameter].add(an)
        }
        return an
    }*/

    override
    def visitAttribute(attr: Attribute): Unit = {
        if (attrs == null) {
            attrs = new ArrayList[Attribute](1)
        }
        attrs.add(attr)
    }

    override
    def visitCode(): Unit = ()

    override
    def visitFrame(type_ : Int, nLocal: Int,
            local: Array[Object], nStack: Int, stack: Array[Object]): Unit = ???/*{
        instructions.add(new FrameNode(type, nLocal, local == null ? null
                : getLabelNodes(local), nStack, stack == null ? null
                : getLabelNodes(stack)))
    }*/

    override
    def visitInsn(opcode: Int): Unit =
        instructions.add(new InsnNode(opcode))

    override
    def visitIntInsn(opcode: Int, operand: Int): Unit =
        instructions.add(new IntInsnNode(opcode, operand))

    override
    def visitVarInsn(opcode: Int, var_ : Int): Unit =
        instructions.add(new VarInsnNode(opcode, var_))

    override
    def visitTypeInsn(opcode: Int, type_ : String): Unit =
        instructions.add(new TypeInsnNode(opcode, type_))

    override
    def visitFieldInsn(opcode: Int, owner: String,
            name: String, desc: String): Unit =
        instructions.add(new FieldInsnNode(opcode, owner, name, desc))

    @deprecated
    override
    def visitMethodInsn(opcode: Int, owner: String, name: String,
            desc: String): Unit = {
        if (api >= Opcodes.ASM5) {
            super.visitMethodInsn(opcode, owner, name, desc)
            return
        }
        instructions.add(new MethodInsnNode(opcode, owner, name, desc))
    }

    override
    def visitMethodInsn(opcode: Int, owner: String, name: String,
            desc: String, itf: Boolean): Unit = {
        if (api < Opcodes.ASM5) {
            super.visitMethodInsn(opcode, owner, name, desc, itf)
            return
        }
        instructions.add(new MethodInsnNode(opcode, owner, name, desc, itf))
    }

    override
    def visitInvokeDynamicInsn(name: String, desc: String, bsm: Handle,
            bsmArgs: Object*): Unit =
        instructions.add(new InvokeDynamicInsnNode(name, desc, bsm, bsmArgs.toArray))

    override
    def visitJumpInsn(opcode: Int, label: Label): Unit =
        instructions.add(new JumpInsnNode(opcode, getLabelNode(label)))

    override
    def visitLabel(label: Label): Unit =
        instructions.add(getLabelNode(label))

    override
    def visitLdcInsn(cst: Object): Unit =
        instructions.add(new LdcInsnNode(cst))

    override
    def visitIincInsn(var_ : Int, increment: Int): Unit =
        instructions.add(new IincInsnNode(var_, increment))

    override
    def visitTableSwitchInsn(min: Int, max: Int,
            dflt: Label, labels: Label*): Unit =
        instructions.add(new TableSwitchInsnNode(min, max, getLabelNode(dflt),
                getLabelNodes(labels.toArray)))

    override
    def visitLookupSwitchInsn(dflt: Label, keys: Array[Int],
            labels: Array[Label]): Unit =
        instructions.add(new LookupSwitchInsnNode(getLabelNode(dflt), keys,
                getLabelNodes(labels)))

    override
    def visitMultiANewArrayInsn(desc: String, dims: Int): Unit =
        instructions.add(new MultiANewArrayInsnNode(desc, dims))

    override
    def visitInsnAnnotation(typeRef: Int,
            typePath: TypePath, desc: String, visible: Boolean): AnnotationVisitor = ???/*{
        // Finds the last real instruction, i.e. the instruction targeted by
        // this annotation.
        AbstractInsnNode insn = instructions.getLast()
        while (insn.getOpcode() == -1) {
            insn = insn.getPrevious()
        }
        // Adds the annotation to this instruction.
        TypeAnnotationNode an = new TypeAnnotationNode(typeRef, typePath, desc)
        if (visible) {
            if (insn.visibleTypeAnnotations == null) {
                insn.visibleTypeAnnotations = new ArrayList[TypeAnnotationNode](
                        1)
            }
            insn.visibleTypeAnnotations.add(an)
        } else {
            if (insn.invisibleTypeAnnotations == null) {
                insn.invisibleTypeAnnotations = new ArrayList[TypeAnnotationNode](
                        1)
            }
            insn.invisibleTypeAnnotations.add(an)
        }
        return an
    }*/

    override
    def visitTryCatchBlock(start: Label, end: Label,
            handler: Label, type_ : String): Unit =
        tryCatchBlocks.add(new TryCatchBlockNode(getLabelNode(start),
                getLabelNode(end), getLabelNode(handler), type_))

    override
    def visitTryCatchAnnotation(typeRef: Int,
            typePath: TypePath, desc: String, visible: Boolean): AnnotationVisitor = ???/*{
        TryCatchBlockNode tcb = tryCatchBlocks.get((typeRef & 0x00FFFF00) >> 8)
        TypeAnnotationNode an = new TypeAnnotationNode(typeRef, typePath, desc)
        if (visible) {
            if (tcb.visibleTypeAnnotations == null) {
                tcb.visibleTypeAnnotations = new ArrayList[TypeAnnotationNode](
                        1)
            }
            tcb.visibleTypeAnnotations.add(an)
        } else {
            if (tcb.invisibleTypeAnnotations == null) {
                tcb.invisibleTypeAnnotations = new ArrayList[TypeAnnotationNode](
                        1)
            }
            tcb.invisibleTypeAnnotations.add(an)
        }
        return an
    }*/

    override
    def visitLocalVariable(name: String, desc: String,
            signature: String, start: Label, end:Label ,
            index: Int): Unit =
        localVariables.add(new LocalVariableNode(name, desc, signature,
                getLabelNode(start), getLabelNode(end), index))

    override
    def visitLocalVariableAnnotation(typeRef: Int,
            typePath: TypePath, start: Array[Label], end: Array[Label], index: Array[Int],
            desc: String, visible: Boolean): AnnotationVisitor = ???/*{
        LocalVariableAnnotationNode an = new LocalVariableAnnotationNode(
                typeRef, typePath, getLabelNodes(start), getLabelNodes(end),
                index, desc)
        if (visible) {
            if (visibleLocalVariableAnnotations == null) {
                visibleLocalVariableAnnotations = new ArrayList[LocalVariableAnnotationNode](
                        1)
            }
            visibleLocalVariableAnnotations.add(an)
        } else {
            if (invisibleLocalVariableAnnotations == null) {
                invisibleLocalVariableAnnotations = new ArrayList[LocalVariableAnnotationNode](
                        1)
            }
            invisibleLocalVariableAnnotations.add(an)
        }
        return an
    }*/

    override
    def visitLineNumber(line: Int, start: Label): Unit =
        instructions.add(new LineNumberNode(line, getLabelNode(start)))

    override
    def visitMaxs(maxStack: Int, maxLocals: Int): Unit = {
        this.maxStack = maxStack
        this.maxLocals = maxLocals
    }

    override
    def visitEnd(): Unit = ()

    /**
     * Returns the LabelNode corresponding to the given Label. Creates a new
     * LabelNode if necessary. The default implementation of this method uses
     * the {@link Label#info} field to store associations between labels and
     * label nodes.
     *
     * @param l
     *            a Label.
     * @return the LabelNode corresponding to l.
     */
    protected def getLabelNode(l: Label): LabelNode = {
        if (!(l.info.isInstanceOf[LabelNode])) {
            l.info = new LabelNode(l)
        }
        l.info.asInstanceOf[LabelNode]
    }

    private def getLabelNodes(l: Array[Label]): Array[LabelNode] = ???/*{
        LabelNode[] nodes = new LabelNode[l.length]
        for (int i = 0 i < l.length ++i) {
            nodes[i] = getLabelNode(l[i])
        }
        return nodes
    }*/

    private def getLabelNodes(objs: Array[Object]): Array[Object] = ???/*{
        Object[] nodes = new Object[objs.length]
        for (int i = 0 i < objs.length ++i) {
            Object o = objs[i]
            if (o instanceof Label) {
                o = getLabelNode((Label) o)
            }
            nodes[i] = o
        }
        return nodes
    }*/

    def check(api: Int): Unit = ???/*{
        if (api == Opcodes.ASM4) {
            if (visibleTypeAnnotations != null
                    && visibleTypeAnnotations.size() > 0) {
                throw new RuntimeException()
            }
            if (invisibleTypeAnnotations != null
                    && invisibleTypeAnnotations.size() > 0) {
                throw new RuntimeException()
            }
            int n = tryCatchBlocks == null ? 0 : tryCatchBlocks.size()
            for (int i = 0 i < n ++i) {
                TryCatchBlockNode tcb = tryCatchBlocks.get(i)
                if (tcb.visibleTypeAnnotations != null
                        && tcb.visibleTypeAnnotations.size() > 0) {
                    throw new RuntimeException()
                }
                if (tcb.invisibleTypeAnnotations != null
                        && tcb.invisibleTypeAnnotations.size() > 0) {
                    throw new RuntimeException()
                }
            }
            for (int i = 0 i < instructions.size() ++i) {
                AbstractInsnNode insn = instructions.get(i)
                if (insn.visibleTypeAnnotations != null
                        && insn.visibleTypeAnnotations.size() > 0) {
                    throw new RuntimeException()
                }
                if (insn.invisibleTypeAnnotations != null
                        && insn.invisibleTypeAnnotations.size() > 0) {
                    throw new RuntimeException()
                }
                if (insn instanceof MethodInsnNode) {
                    boolean itf = ((MethodInsnNode) insn).itf
                    if (itf != (insn.opcode == Opcodes.INVOKEINTERFACE)) {
                        throw new RuntimeException()
                    }
                }
            }
            if (visibleLocalVariableAnnotations != null
                    && visibleLocalVariableAnnotations.size() > 0) {
                throw new RuntimeException()
            }
            if (invisibleLocalVariableAnnotations != null
                    && invisibleLocalVariableAnnotations.size() > 0) {
                throw new RuntimeException()
            }
        }
    }*/

    def accept(cv:ClassVisitor ): Unit = ???/*{
        String[] exceptions = new String[this.exceptions.size()]
        this.exceptions.toArray(exceptions)
        MethodVisitor mv = cv.visitMethod(access, name, desc, signature,
                exceptions)
        if (mv != null) {
            accept(mv)
        }
    }*/

    def accept(mv:MethodVisitor ): Unit = ???/*{
        // visits the method parameters
        int i, j, n
        n = parameters == null ? 0 : parameters.size()
        for (i = 0 i < n i++) {
            ParameterNode parameter = parameters.get(i)
            mv.visitParameter(parameter.name, parameter.access)
        }
        // visits the method attributes
        if (annotationDefault != null) {
            AnnotationVisitor av = mv.visitAnnotationDefault()
            AnnotationNode.accept(av, null, annotationDefault)
            if (av != null) {
                av.visitEnd()
            }
        }
        n = visibleAnnotations == null ? 0 : visibleAnnotations.size()
        for (i = 0 i < n ++i) {
            AnnotationNode an = visibleAnnotations.get(i)
            an.accept(mv.visitAnnotation(an.desc, true))
        }
        n = invisibleAnnotations == null ? 0 : invisibleAnnotations.size()
        for (i = 0 i < n ++i) {
            AnnotationNode an = invisibleAnnotations.get(i)
            an.accept(mv.visitAnnotation(an.desc, false))
        }
        n = visibleTypeAnnotations == null ? 0 : visibleTypeAnnotations.size()
        for (i = 0 i < n ++i) {
            TypeAnnotationNode an = visibleTypeAnnotations.get(i)
            an.accept(mv.visitTypeAnnotation(an.typeRef, an.typePath, an.desc,
                    true))
        }
        n = invisibleTypeAnnotations == null ? 0 : invisibleTypeAnnotations
                .size()
        for (i = 0 i < n ++i) {
            TypeAnnotationNode an = invisibleTypeAnnotations.get(i)
            an.accept(mv.visitTypeAnnotation(an.typeRef, an.typePath, an.desc,
                    false))
        }
        n = visibleParameterAnnotations == null ? 0
                : visibleParameterAnnotations.length
        for (i = 0 i < n ++i) {
            List<?> l = visibleParameterAnnotations[i]
            if (l == null) {
                continue
            }
            for (j = 0 j < l.size() ++j) {
                AnnotationNode an = (AnnotationNode) l.get(j)
                an.accept(mv.visitParameterAnnotation(i, an.desc, true))
            }
        }
        n = invisibleParameterAnnotations == null ? 0
                : invisibleParameterAnnotations.length
        for (i = 0 i < n ++i) {
            List<?> l = invisibleParameterAnnotations[i]
            if (l == null) {
                continue
            }
            for (j = 0 j < l.size() ++j) {
                AnnotationNode an = (AnnotationNode) l.get(j)
                an.accept(mv.visitParameterAnnotation(i, an.desc, false))
            }
        }
        if (visited) {
            instructions.resetLabels()
        }
        n = attrs == null ? 0 : attrs.size()
        for (i = 0 i < n ++i) {
            mv.visitAttribute(attrs.get(i))
        }
        // visits the method's code
        if (instructions.size() > 0) {
            mv.visitCode()
            // visits try catch blocks
            n = tryCatchBlocks == null ? 0 : tryCatchBlocks.size()
            for (i = 0 i < n ++i) {
                tryCatchBlocks.get(i).updateIndex(i)
                tryCatchBlocks.get(i).accept(mv)
            }
            // visits instructions
            instructions.accept(mv)
            // visits local variables
            n = localVariables == null ? 0 : localVariables.size()
            for (i = 0 i < n ++i) {
                localVariables.get(i).accept(mv)
            }
            // visits local variable annotations
            n = visibleLocalVariableAnnotations == null ? 0
                    : visibleLocalVariableAnnotations.size()
            for (i = 0 i < n ++i) {
                visibleLocalVariableAnnotations.get(i).accept(mv, true)
            }
            n = invisibleLocalVariableAnnotations == null ? 0
                    : invisibleLocalVariableAnnotations.size()
            for (i = 0 i < n ++i) {
                invisibleLocalVariableAnnotations.get(i).accept(mv, false)
            }
            // visits maxs
            mv.visitMaxs(maxStack, maxLocals)
            visited = true
        }
        mv.visitEnd()
    }*/
}
