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

import scala.tools.asm.MethodVisitor
import scala.tools.asm.Opcodes

class FrameNode extends AbstractInsnNode(-1) {
    var type_ : Int = _
    var local : List[Object] = _
    var stack : List[Object] = _

    def this(type_ : Int, nLocal: Int, local: Array[Object],
            nStack: Int, stack: Array[Object]) = {
        this()
        this.type_ = type_
        type_ match {
          case Opcodes.F_NEW =>
          case Opcodes.F_FULL=>
            this.local = asList(nLocal, local)
            this.stack = asList(nStack, stack)
          case Opcodes.F_APPEND =>
            this.local = asList(nLocal, local)
          case Opcodes.F_CHOP =>
            this.local = Arrays.asList((new Array[Object](nLocal)): _*)
          case Opcodes.F_SAME =>
          case Opcodes.F_SAME1 =>
            this.stack = asList(1, stack)
        }
    }

    override
    def getType(): Int = AbstractInsnNode.FRAME

    override
    def accept(mv: MethodVisitor ): Unit = ???/*{
        switch (type) {
        case Opcodes.F_NEW:
        case Opcodes.F_FULL:
            mv.visitFrame(type, local.size(), asArray(local), stack.size(),
                    asArray(stack))
            break
        case Opcodes.F_APPEND:
            mv.visitFrame(type, local.size(), asArray(local), 0, null)
            break
        case Opcodes.F_CHOP:
            mv.visitFrame(type, local.size(), null, 0, null)
            break
        case Opcodes.F_SAME:
            mv.visitFrame(type, 0, null, 0, null)
            break
        case Opcodes.F_SAME1:
            mv.visitFrame(type, 0, null, 1, asArray(stack))
            break
        }
    }*/

    override
    def clone(labels: Map[LabelNode, LabelNode]): AbstractInsnNode = ???/*{
        FrameNode clone = new FrameNode()
        clone.type = type
        if (local != null) {
            clone.local = new ArrayList<Object>()
            for (int i = 0 i < local.size() ++i) {
                Object l = local.get(i)
                if (l instanceof LabelNode) {
                    l = labels.get(l)
                }
                clone.local.add(l)
            }
        }
        if (stack != null) {
            clone.stack = new ArrayList<Object>()
            for (int i = 0 i < stack.size() ++i) {
                Object s = stack.get(i)
                if (s instanceof LabelNode) {
                    s = labels.get(s)
                }
                clone.stack.add(s)
            }
        }
        return clone
    }*/

    private def asList(n: Int, o: Array[Object]): List[Object] =
        Arrays.asList(o: _*).subList(0, n)

    private def asArray(l: List[Object]): Array[Object] = ???/*{
        Object[] objs = new Object[l.size()]
        for (int i = 0 i < objs.length ++i) {
            Object o = l.get(i)
            if (o instanceof LabelNode) {
                o = ((LabelNode) o).getLabel()
            }
            objs[i] = o
        }
        return objs
    }*/
}
