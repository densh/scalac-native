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

class AnnotationWriter extends AnnotationVisitor(Opcodes.ASM5) {
    private var cw    : ClassWriter = _
    private var size  : Int         = _
    private var named : Boolean     = _
    private var bv    : ByteVector  = _
    private var parent: ByteVector  = _
    private var offset: Int         = _
    var next : AnnotationWriter = _
    var prev : AnnotationWriter = _

    def this(cw: ClassWriter, named: Boolean,
             bv: ByteVector, parent: ByteVector, offset: Int) = {
        this()
        this.cw = cw
        this.named = named
        this.bv = bv
        this.parent = parent
        this.offset = offset
    }

    override def visit(name: String, value: Any): Unit = {
      size += 1
      if (named) {
        bv.putShort(cw.newUTF8(name))
      }
      if (value.isInstanceOf[String]) {
        bv.put12('s', cw.newUTF8(value.asInstanceOf[String]))
      } else if (value.isInstanceOf[Byte]) {
        bv.put12('B', cw.newInteger(value.asInstanceOf[Byte].byteValue()).index)
      } else if (value.isInstanceOf[Boolean]) {
        val v = if (value.asInstanceOf[Boolean].booleanValue()) 1 else 0
        bv.put12('Z', cw.newInteger(v).index)
      } else if (value.isInstanceOf[Character]) {
        bv.put12('C', cw.newInteger(value.asInstanceOf[Character].charValue()).index)
      } else if (value.isInstanceOf[Short]) {
        bv.put12('S', cw.newInteger(value.asInstanceOf[Short].shortValue()).index)
      } else if (value.isInstanceOf[Type]) {
        bv.put12('c', cw.newUTF8(value.asInstanceOf[Type].getDescriptor()))
      } else if (value.isInstanceOf[Array[Byte]]) {
        val v = value.asInstanceOf[Array[Byte]]
        bv.put12('x', v.length)
        (0 until v.length).foreach { i =>
          bv.put12('B', cw.newInteger(v(i)).index)
        }
      } else if (value.isInstanceOf[Array[Boolean]]) {
        val v = value.asInstanceOf[Array[Boolean]]
        bv.put12('[', v.length)
        (0 until v.length).foreach { i =>
          bv.put12('Z', cw.newInteger(if (v(i)) 1 else 0).index)
        }
      } else if (value.isInstanceOf[Array[Short]]) {
        val v = value.asInstanceOf[Array[Short]]
        bv.put12('[', v.length)
        (0 until v.length).foreach { i =>
          bv.put12('S', cw.newInteger(v(i)).index)
        }
      } else if (value.isInstanceOf[Array[Char]]) {
        val v = value.asInstanceOf[Array[Char]]
        bv.put12('[', v.length)
        (0 until v.length).foreach { i =>
          bv.put12('C', cw.newInteger(v(i)).index)
        }
      } else if (value.isInstanceOf[Array[Int]]) {
        val v = value.asInstanceOf[Array[Int]]
        bv.put12('[', v.length)
        (0 until v.length).foreach { i =>
          bv.put12('I', cw.newInteger(v(i)).index)
        }
      } else if (value.isInstanceOf[Array[Long]]) {
        val v = value.asInstanceOf[Array[Long]]
        bv.put12('[', v.length)
        (0 until v.length).foreach { i =>
          bv.put12('J', cw.newLong(v(i)).index)
        }
      } else if (value.isInstanceOf[Array[Float]]) {
        val v = value.asInstanceOf[Array[Float]]
        bv.put12('[', v.length)
        (0 until v.length).foreach { i =>
          bv.put12('F', cw.newFloat(v(i)).index)
        }
      } else if (value.isInstanceOf[Array[Double]]) {
        val v = value.asInstanceOf[Array[Double]]
        bv.put12('[', v.length)
        (0 until v.length).foreach { i =>
          bv.put12('D', cw.newDouble(v(i)).index)
        }
      } else {
        val i = cw.newConstItem(value)
        bv.put12(".s.IFJDCS".charAt(i.type_), i.index)
      }
    }

    override
    def visitEnum(name: String, desc: String, value: String): Unit = ???/*{
        ++size
        if (named) {
            bv.putShort(cw.newUTF8(name))
        }
        bv.put12('e', cw.newUTF8(desc)).putShort(cw.newUTF8(value))
    }*/

    override
    def visitAnnotation(name: String, desc: String): AnnotationVisitor = ???/*{
        ++size
        if (named) {
            bv.putShort(cw.newUTF8(name))
        }
        // write tag and type, and reserve space for values count
        bv.put12('@', cw.newUTF8(desc)).putShort(0)
        return new AnnotationWriter(cw, true, bv, bv, bv.length - 2)
    }*/

    override
    def visitArray(name: String): AnnotationVisitor = ???/*{
        ++size
        if (named) {
            bv.putShort(cw.newUTF8(name))
        }
        // write tag, and reserve space for array size
        bv.put12('[', 0)
        return new AnnotationWriter(cw, false, bv, bv, bv.length - 2)
    }*/

    override def visitEnd(): Unit = {
        if (parent != null) {
            val data = parent.data
            data(offset) = (size >>> 8).toByte
            data(offset + 1) = size.toByte
        }
    }

    def getSize(): Int = ???/*{
        int size = 0
        AnnotationWriter aw = this
        while (aw != null) {
            size += aw.bv.length
            aw = aw.next
        }
        return size
    }*/

    def put(out: ByteVector): Unit = ???/*{
        int n = 0
        int size = 2
        AnnotationWriter aw = this
        AnnotationWriter last = null
        while (aw != null) {
            ++n
            size += aw.bv.length
            aw.visitEnd() // in case user forgot to call visitEnd
            aw.prev = last
            last = aw
            aw = aw.next
        }
        out.putInt(size)
        out.putShort(n)
        aw = last
        while (aw != null) {
            out.putByteArray(aw.bv.data, 0, aw.bv.length)
            aw = aw.prev
        }
    }*/
}


object AnnotationWriter {
    def put(panns: Array[AnnotationWriter], off: Int, out: ByteVector): Unit = ???/*{
        int size = 1 + 2 * (panns.length - off)
        for (int i = off i < panns.length ++i) {
            size += panns[i] == null ? 0 : panns[i].getSize()
        }
        out.putInt(size).putByte(panns.length - off)
        for (int i = off i < panns.length ++i) {
            AnnotationWriter aw = panns[i]
            AnnotationWriter last = null
            int n = 0
            while (aw != null) {
                ++n
                aw.visitEnd() // in case user forgot to call visitEnd
                aw.prev = last
                last = aw
                aw = aw.next
            }
            out.putShort(n)
            aw = last
            while (aw != null) {
                out.putByteArray(aw.bv.data, 0, aw.bv.length)
                aw = aw.prev
            }
        }
    }*/

    def putTarget(typeRef: Int, typePath: TypePath , out: ByteVector ): Unit = ???/*{
        switch (typeRef >>> 24) {
        case 0x00: // CLASS_TYPE_PARAMETER
        case 0x01: // METHOD_TYPE_PARAMETER
        case 0x16: // METHOD_FORMAL_PARAMETER
            out.putShort(typeRef >>> 16)
            break
        case 0x13: // FIELD
        case 0x14: // METHOD_RETURN
        case 0x15: // METHOD_RECEIVER
            out.putByte(typeRef >>> 24)
            break
        case 0x47: // CAST
        case 0x48: // CONSTRUCTOR_INVOCATION_TYPE_ARGUMENT
        case 0x49: // METHOD_INVOCATION_TYPE_ARGUMENT
        case 0x4A: // CONSTRUCTOR_REFERENCE_TYPE_ARGUMENT
        case 0x4B: // METHOD_REFERENCE_TYPE_ARGUMENT
            out.putInt(typeRef)
            break
        // case 0x10: // CLASS_EXTENDS
        // case 0x11: // CLASS_TYPE_PARAMETER_BOUND
        // case 0x12: // METHOD_TYPE_PARAMETER_BOUND
        // case 0x17: // THROWS
        // case 0x42: // EXCEPTION_PARAMETER
        // case 0x43: // INSTANCEOF
        // case 0x44: // NEW
        // case 0x45: // CONSTRUCTOR_REFERENCE
        // case 0x46: // METHOD_REFERENCE
        default:
            out.put12(typeRef >>> 24, (typeRef & 0xFFFF00) >> 8)
            break
        }
        if (typePath == null) {
            out.putByte(0)
        } else {
            int length = typePath.b[typePath.offset] * 2 + 1
            out.putByteArray(typePath.b, typePath.offset, length)
        }
    }*/

}
