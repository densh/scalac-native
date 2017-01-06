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

import scala.tools.asm.Label
import scala.tools.asm.MethodVisitor
import scala.tools.asm.Opcodes
import scala.tools.asm.TypePath
import scala.tools.asm.TypeReference

/**
 * A node that represents a type annotation on a local or resource variable.
 *
 * @author Eric Bruneton
 */
class LocalVariableAnnotationNode(api: Int, typeRef: Int, typePath: TypePath,
            _start:Array[LabelNode], _end:Array[LabelNode] , _index: Array[Int], desc: String) extends TypeAnnotationNode(api, typeRef, typePath, desc) {
    var start: ArrayList[LabelNode] = {
      val arrlst = new ArrayList[LabelNode](_start.length)
      arrlst.addAll(Arrays.asList(_start: _*))
      arrlst
    }
    var end : ArrayList[LabelNode]= {
      val arrlst = new ArrayList[LabelNode](_end.length)
      arrlst.addAll(Arrays.asList(_end: _*))
      arrlst
    }
    var index : ArrayList[Integer]= {
      val arrlst = new ArrayList[Integer](_index.length)
      var i = 0
      while (i < _index.length) {
          arrlst.add(_index(i))
          i += 1
      }
      arrlst
    }

    def this(typeRef: Int, typePath:TypePath ,
            start:Array[LabelNode] ,  end:Array[LabelNode],  index:Array[Int], desc:String ) =
        this(Opcodes.ASM5, typeRef, typePath, start, end, index, desc)

    def accept( mv:MethodVisitor, visible: Boolean): Unit = ???/*{
        Label[] start = new Label[this.start.size()]
        Label[] end = new Label[this.end.size()]
        int[] index = new int[this.index.size()]
        for (int i = 0 i < start.length ++i) {
            start[i] = this.start.get(i).getLabel()
            end[i] = this.end.get(i).getLabel()
            index[i] = this.index.get(i)
        }
        accept(mv.visitLocalVariableAnnotation(typeRef, typePath, start, end,
                index, desc, true))
    }*/
}
