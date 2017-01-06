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
import java.util.List
import java.util.Map

import scala.tools.asm.MethodVisitor

abstract class AbstractInsnNode (protected var opcode: Int) {
    var visibleTypeAnnotations  : List[TypeAnnotationNode] = _
    var invisibleTypeAnnotations: List[TypeAnnotationNode] = _
    var prev: AbstractInsnNode = _
    var next: AbstractInsnNode = _
    var index = -1

    def getOpcode(): Int = opcode
    def getType(): Int
    def getPrevious(): AbstractInsnNode = prev
    def getNext(): AbstractInsnNode = next
    def accept(cv: MethodVisitor ): Unit

    protected def acceptAnnotations(mv: MethodVisitor ): Unit = ???/*{
        int n = visibleTypeAnnotations == null ? 0 : visibleTypeAnnotations
                .size()
        for (int i = 0 i < n ++i) {
            TypeAnnotationNode an = visibleTypeAnnotations.get(i)
            an.accept(mv.visitInsnAnnotation(an.typeRef, an.typePath, an.desc,
                    true))
        }
        n = invisibleTypeAnnotations == null ? 0 : invisibleTypeAnnotations
                .size()
        for (int i = 0 i < n ++i) {
            TypeAnnotationNode an = invisibleTypeAnnotations.get(i)
            an.accept(mv.visitInsnAnnotation(an.typeRef, an.typePath, an.desc,
                    false))
        }
    }*/

    def clone(labels: Map[LabelNode, LabelNode]): AbstractInsnNode

    protected def cloneAnnotations(insn: AbstractInsnNode): AbstractInsnNode = ???/*{
        if (insn.visibleTypeAnnotations != null) {
            this.visibleTypeAnnotations = new ArrayList<TypeAnnotationNode>()
            for (int i = 0 i < insn.visibleTypeAnnotations.size() ++i) {
                TypeAnnotationNode src = insn.visibleTypeAnnotations.get(i)
                TypeAnnotationNode ann = new TypeAnnotationNode(src.typeRef,
                        src.typePath, src.desc)
                src.accept(ann)
                this.visibleTypeAnnotations.add(ann)
            }
        }
        if (insn.invisibleTypeAnnotations != null) {
            this.invisibleTypeAnnotations = new ArrayList<TypeAnnotationNode>()
            for (int i = 0 i < insn.invisibleTypeAnnotations.size() ++i) {
                TypeAnnotationNode src = insn.invisibleTypeAnnotations.get(i)
                TypeAnnotationNode ann = new TypeAnnotationNode(src.typeRef,
                        src.typePath, src.desc)
                src.accept(ann)
                this.invisibleTypeAnnotations.add(ann)
            }
        }
        return this
    }*/
}

object AbstractInsnNode {
    final val INSN = 0
    final val INT_INSN = 1
    final val VAR_INSN = 2
    final val TYPE_INSN = 3
    final val FIELD_INSN = 4
    final val METHOD_INSN = 5
    final val INVOKE_DYNAMIC_INSN = 6
    final val JUMP_INSN = 7
    final val LABEL = 8
    final val LDC_INSN = 9
    final val IINC_INSN = 10
    final val TABLESWITCH_INSN = 11
    final val LOOKUPSWITCH_INSN = 12
    final val MULTIANEWARRAY_INSN = 13
    final val FRAME = 14
    final val LINE = 15

    def clone(label: LabelNode ,  map:Map[LabelNode, LabelNode]): LabelNode  =
        return map.get(label)

    def clone( labels:List[LabelNode],
             map:Map[LabelNode, LabelNode]): Array[LabelNode] = ???/*{
        LabelNode[] clones = new LabelNode[labels.size()]
        for (int i = 0 i < clones.length ++i) {
            clones[i] = map.get(labels.get(i))
        }
        return clones
    }*/
}
