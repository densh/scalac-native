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

class Attribute(val `type` : String) {
    var value: Array[Byte] = _
    var next: Attribute = _

    def isUnknown(): Boolean = true
    def isCodeAttribute(): Boolean = false

    protected def getLabels(): Array[Label] = null

    protected def read(cr:ClassReader, off: Int,
             len: Int, buf: Array[Char], codeOff: Int,
            labels: Array[Label]): Attribute = {
        val attr = new Attribute(`type`)
        attr.value = new Array[Byte](len)
        System.arraycopy(cr.b, off, attr.value, 0, len)
        attr
    }

    protected def write(cw:ClassWriter , code: Array[Byte],
            len: Int, maxStack: Int, maxLocals: Int): ByteVector = {
        val v = new ByteVector()
        v.data = value
        v.length = value.length
        v
    }

    def getCount(): Int = {
        var count = 0
        var attr = this
        while (attr != null) {
            count += 1
            attr = attr.next
        }
        count
    }

    def getSize(cw: ClassWriter, code: Array[Byte], len: Int,
                maxStack: Int, maxLocals: Int): Int = {
        var attr = this
        var size = 0
        while (attr != null) {
            cw.newUTF8(attr.`type`)
            size += attr.write(cw, code, len, maxStack, maxLocals).length + 6
            attr = attr.next
        }
        size
    }

    def put(cw: ClassWriter, code: Array[Byte], len: Int,
            maxStack: Int, maxLocals: Int, out: ByteVector): Unit = {
        var attr = this
        while (attr != null) {
            val b = attr.write(cw, code, len, maxStack, maxLocals)
            out.putShort(cw.newUTF8(attr.`type`)).putInt(b.length)
            out.putByteArray(b.data, 0, b.length)
            attr = attr.next
        }
    }
}
