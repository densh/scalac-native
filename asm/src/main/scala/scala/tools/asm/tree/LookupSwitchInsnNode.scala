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

/**
 * A node that represents a LOOKUPSWITCH instruction.
 *
 * @author Eric Bruneton
 */
class LookupSwitchInsnNode extends AbstractInsnNode(Opcodes.LOOKUPSWITCH) {
    var dflt   : LabelNode       = _
    var keys   : List[Integer]   = _
    var labels : List[LabelNode] = _

    def this(dflt:LabelNode , keys: Array[Int],
            labels:Array[LabelNode]) = {
        this()
        this.dflt = dflt
        this.keys = new ArrayList[Integer](if(keys == null) 0 else keys.length)
        this.labels = new ArrayList[LabelNode](if(labels == null) 0 else labels.length)
        if (keys != null) {
            var i = 0
            while (i < keys.length) {
                this.keys.add(keys(i))
                i += 1
            }
        }
        if (labels != null) {
            this.labels.addAll(Arrays.asList(labels: _*))
        }
    }

    override
    def getType() = AbstractInsnNode.LOOKUPSWITCH_INSN

    override
    def accept(mv:MethodVisitor ): Unit = ???/*{
        int[] keys = new int[this.keys.size()]
        for (int i = 0 i < keys.length ++i) {
            keys[i] = this.keys.get(i).intValue()
        }
        Label[] labels = new Label[this.labels.size()]
        for (int i = 0 i < labels.length ++i) {
            labels[i] = this.labels.get(i).getLabel()
        }
        mv.visitLookupSwitchInsn(dflt.getLabel(), keys, labels)
        acceptAnnotations(mv)
    }*/

    override
    def clone(labels: Map[LabelNode, LabelNode]):AbstractInsnNode = {
        val clone = new LookupSwitchInsnNode(AbstractInsnNode.clone(dflt,
                labels), null, AbstractInsnNode.clone(this.labels, labels))
        clone.keys.addAll(keys)
        clone.cloneAnnotations(this)
    }
}
