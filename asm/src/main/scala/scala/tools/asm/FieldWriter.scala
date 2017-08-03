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
 * SUBSTITUTE GOODS OR SERVICES LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF
 * THE POSSIBILITY OF SUCH DAMAGE.
 */
package scala.tools.asm

class FieldWriter extends FieldVisitor(Opcodes.ASM5) {
    private var cw: ClassWriter = _
    private var access    : Int = _
    private var name      : Int = _
    private var desc      : Int = _
    private var signature : Int = _
    private var value     : Int = _
    private var anns   : AnnotationWriter = _
    private var ianns  : AnnotationWriter = _
    private var tanns  : AnnotationWriter = _
    private var itanns : AnnotationWriter = _
    private var attrs  : Attribute        = _

    def this(cw: ClassWriter, access: Int, name: String,
            desc: String, signature: String , value: Any) {
        this()
        if (cw.firstField == null) {
            cw.firstField = this
        } else {
            cw.lastField.fv = this
        }
        cw.lastField = this
        this.cw = cw
        this.access = access
        this.name = cw.newUTF8(name)
        this.desc = cw.newUTF8(desc)
        if (ClassReader.SIGNATURES && signature != null) {
            this.signature = cw.newUTF8(signature)
        }
        if (value != null) {
            this.value = cw.newConstItem(value).index
        }
    }

    override
    def visitAnnotation(desc: String, visible: Boolean): AnnotationVisitor = ???/*{
        if (!ClassReader.ANNOTATIONS) {
            return null
        }
        ByteVector bv = new ByteVector()
        // write type, and reserve space for values count
        bv.putShort(cw.newUTF8(desc)).putShort(0)
        AnnotationWriter aw = new AnnotationWriter(cw, true, bv, bv, 2)
        if (visible) {
            aw.next = anns
            anns = aw
        } else {
            aw.next = ianns
            ianns = aw
        }
        return aw
    }*/

    override
    def visitTypeAnnotation(typeRef: Int, typePath: TypePath , desc:String , visible: Boolean): AnnotationVisitor = ???/*{
        if (!ClassReader.ANNOTATIONS) {
            return null
        }
        ByteVector bv = new ByteVector()
        // write target_type and target_info
        AnnotationWriter.putTarget(typeRef, typePath, bv)
        // write type, and reserve space for values count
        bv.putShort(cw.newUTF8(desc)).putShort(0)
        AnnotationWriter aw = new AnnotationWriter(cw, true, bv, bv,
                bv.length - 2)
        if (visible) {
            aw.next = tanns
            tanns = aw
        } else {
            aw.next = itanns
            itanns = aw
        }
        return aw
    }*/

    override
    def visitAttribute(attr: Attribute ): Unit = {
        attr.next = attrs
        attrs = attr
    }

    override
    def visitEnd(): Unit = ()

    def getSize(): Int = {
        var size = 8
        if (value != 0) {
            cw.newUTF8("ConstantValue")
            size += 8
        }
        if ((access & Opcodes.ACC_SYNTHETIC) != 0) {
            if ((cw.version & 0xFFFF) < Opcodes.V1_5
                    || (access & ClassWriter.ACC_SYNTHETIC_ATTRIBUTE) != 0) {
                cw.newUTF8("Synthetic")
                size += 6
            }
        }
        if ((access & Opcodes.ACC_DEPRECATED) != 0) {
            cw.newUTF8("Deprecated")
            size += 6
        }
        if (ClassReader.SIGNATURES && signature != 0) {
            cw.newUTF8("Signature")
            size += 8
        }
        if (ClassReader.ANNOTATIONS && anns != null) {
            cw.newUTF8("RuntimeVisibleAnnotations")
            size += 8 + anns.getSize()
        }
        if (ClassReader.ANNOTATIONS && ianns != null) {
            cw.newUTF8("RuntimeInvisibleAnnotations")
            size += 8 + ianns.getSize()
        }
        if (ClassReader.ANNOTATIONS && tanns != null) {
            cw.newUTF8("RuntimeVisibleTypeAnnotations")
            size += 8 + tanns.getSize()
        }
        if (ClassReader.ANNOTATIONS && itanns != null) {
            cw.newUTF8("RuntimeInvisibleTypeAnnotations")
            size += 8 + itanns.getSize()
        }
        if (attrs != null) {
            size += attrs.getSize(cw, null, 0, -1, -1)
        }
        size
    }

    def put(out: ByteVector): Unit = {
        val FACTOR = ClassWriter.TO_ACC_SYNTHETIC
        val mask = Opcodes.ACC_DEPRECATED | ClassWriter.ACC_SYNTHETIC_ATTRIBUTE | ((access & ClassWriter.ACC_SYNTHETIC_ATTRIBUTE) / FACTOR)
        out.putShort(access & ~mask).putShort(name).putShort(desc)
        var attributeCount = 0
        if (value != 0) {
            attributeCount += 1
        }
        if ((access & Opcodes.ACC_SYNTHETIC) != 0) {
            if ((cw.version & 0xFFFF) < Opcodes.V1_5
                    || (access & ClassWriter.ACC_SYNTHETIC_ATTRIBUTE) != 0) {
                attributeCount += 1
            }
        }
        if ((access & Opcodes.ACC_DEPRECATED) != 0) {
            attributeCount += 1
        }
        if (ClassReader.SIGNATURES && signature != 0) {
            attributeCount += 1
        }
        if (ClassReader.ANNOTATIONS && anns != null) {
            attributeCount += 1
        }
        if (ClassReader.ANNOTATIONS && ianns != null) {
            attributeCount += 1
        }
        if (ClassReader.ANNOTATIONS && tanns != null) {
            attributeCount += 1
        }
        if (ClassReader.ANNOTATIONS && itanns != null) {
            attributeCount += 1
        }
        if (attrs != null) {
            attributeCount += attrs.getCount()
        }
        out.putShort(attributeCount)
        if (value != 0) {
            out.putShort(cw.newUTF8("ConstantValue"))
            out.putInt(2).putShort(value)
        }
        if ((access & Opcodes.ACC_SYNTHETIC) != 0) {
            if ((cw.version & 0xFFFF) < Opcodes.V1_5
                    || (access & ClassWriter.ACC_SYNTHETIC_ATTRIBUTE) != 0) {
                out.putShort(cw.newUTF8("Synthetic")).putInt(0)
            }
        }
        if ((access & Opcodes.ACC_DEPRECATED) != 0) {
            out.putShort(cw.newUTF8("Deprecated")).putInt(0)
        }
        if (ClassReader.SIGNATURES && signature != 0) {
            out.putShort(cw.newUTF8("Signature"))
            out.putInt(2).putShort(signature)
        }
        if (ClassReader.ANNOTATIONS && anns != null) {
            out.putShort(cw.newUTF8("RuntimeVisibleAnnotations"))
            anns.put(out)
        }
        if (ClassReader.ANNOTATIONS && ianns != null) {
            out.putShort(cw.newUTF8("RuntimeInvisibleAnnotations"))
            ianns.put(out)
        }
        if (ClassReader.ANNOTATIONS && tanns != null) {
            out.putShort(cw.newUTF8("RuntimeVisibleTypeAnnotations"))
            tanns.put(out)
        }
        if (ClassReader.ANNOTATIONS && itanns != null) {
            out.putShort(cw.newUTF8("RuntimeInvisibleTypeAnnotations"))
            itanns.put(out)
        }
        if (attrs != null) {
            attrs.put(cw, null, 0, -1, -1, out)
        }
    }
}
