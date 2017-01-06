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
import java.util.Map

import scala.tools.asm.Label
import scala.tools.asm.MethodVisitor
import scala.tools.asm.Opcodes

class TableSwitchInsnNode(var min: Int, var max: Int, var dflt: LabelNode, _labels: Array[LabelNode]) extends AbstractInsnNode(Opcodes.TABLESWITCH) {
    var labels: List[LabelNode] = new ArrayList[LabelNode]()
    if (_labels != null) {
        labels.addAll(Arrays.asList(_labels: _*))
    }

    override
    def getType(): Int = AbstractInsnNode.TABLESWITCH_INSN

    override
    def accept(mv: MethodVisitor ): Unit = ???/*{
        Label[] labels = new Label[this.labels.size()]
        for (int i = 0 i < labels.length ++i) {
            labels[i] = this.labels.get(i).getLabel()
        }
        mv.visitTableSwitchInsn(min, max, dflt.getLabel(), labels)
        acceptAnnotations(mv)
    }*/

    override
    def clone(labels:Map[LabelNode, LabelNode]): AbstractInsnNode =
        new TableSwitchInsnNode(min, max, AbstractInsnNode.clone(dflt, labels), AbstractInsnNode.clone(
                this.labels, labels)).cloneAnnotations(this)
}
