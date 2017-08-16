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

class ClassWriter extends ClassVisitor(Opcodes.ASM5) {
  import ClassWriter._

    var cr: ClassReader = _
    var version: Int = _
    var index: Int = _
    var pool: ByteVector = _
    var items: Array[Item] = _
    var threshold: Int = _
    var key: Item = _
    var key2: Item = _
    var key3: Item = _
    var key4: Item = _
    var typeTable: Array[Item] = _
    private var typeCount: Short = _
    private var access : Int = _
    private var name   : Int  = _
    var thisName: String = _
    private var signature: Int = _
    private var superName: Int = _
    private var interfaceCount: Int = _
    private var interfaces: Array[Int] = _
    private var sourceFile: Int = _
    private var sourceDebug: ByteVector = _
    private var enclosingMethodOwner : Int = _
    private var enclosingMethod      : Int = _
    private var anns  : AnnotationWriter = _
    private var ianns : AnnotationWriter = _
    private var tanns : AnnotationWriter = _
    private var itanns: AnnotationWriter = _
    private var attrs: Attribute = _
    private var innerClassesCount: Int = _
    private var innerClasses: ByteVector = _
    var bootstrapMethodsCount: Int = _
    var bootstrapMethods : ByteVector   = _
    var firstField       : FieldWriter  = _
    var lastField        : FieldWriter  = _
    var firstMethod      : MethodWriter = _
    var lastMethod       : MethodWriter = _
    private var computeMaxs: Boolean = _
    private var computeFrames: Boolean = _
    var invalidFrames: Boolean = _

    def this(flags: Int) = {
        this()
        index = 1
        pool = new ByteVector()
        items = new Array[Item](256)
        threshold = (0.75d * items.length).toInt
        key = new Item()
        key2 = new Item()
        key3 = new Item()
        key4 = new Item()
        this.computeMaxs = (flags & COMPUTE_MAXS) != 0
        this.computeFrames = (flags & COMPUTE_FRAMES) != 0
    }

    def this(classReader: ClassReader, flags: Int) {
        this(flags)
        classReader.copyPool(this)
        this.cr = classReader
    }

    override def visit(version: Int, access: Int,
            name: String, signature: String, superName: String,
            interfaces: Array[String]): Unit = {
        this.version = version
        this.access = access
        this.name = newClass(name)
        thisName = name
        if (ClassReader.SIGNATURES && signature != null) {
            this.signature = newUTF8(signature)
        }
        this.superName = if(superName == null) 0 else newClass(superName)
        if (interfaces != null && interfaces.length > 0) {
            this.interfaces = interfaces.map(newClass)
        }
    }

    override
    def visitSource(file: String, debug: String): Unit = {
        if (file != null) {
            sourceFile = newUTF8(file)
        }
        if (debug != null) {
            sourceDebug = new ByteVector().encodeUTF8(debug, 0,
                    Integer.MAX_VALUE)
        }
    }

    override
    def visitOuterClass(owner: String, name: String, desc: String): Unit = {
        enclosingMethodOwner = newClass(owner)
        if (name != null && desc != null) {
            enclosingMethod = newNameType(name, desc)
        }
    }

    override
    def visitAnnotation(desc: String, visible: Boolean): AnnotationVisitor = {
        if (!ClassReader.ANNOTATIONS) {
            return null
        }
        val bv = new ByteVector()
        // write type, and reserve space for values count
        bv.putShort(newUTF8(desc)).putShort(0)
        val aw = new AnnotationWriter(this, true, bv, bv, 2)
        if (visible) {
            aw.next = anns
            anns = aw
        } else {
            aw.next = ianns
            ianns = aw
        }
        aw
    }

    override
    def visitTypeAnnotation(typeRef: Int, typePath: TypePath, desc: String, visible: Boolean): AnnotationVisitor = ???/*{
        if (!ClassReader.ANNOTATIONS) {
            return null
        }
        ByteVector bv = new ByteVector()
        // write target_type and target_info
        AnnotationWriter.putTarget(typeRef, typePath, bv)
        // write type, and reserve space for values count
        bv.putShort(newUTF8(desc)).putShort(0)
        AnnotationWriter aw = new AnnotationWriter(this, true, bv, bv,
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
    def visitAttribute(attr: Attribute): Unit = {
        attr.next = attrs
        attrs = attr
    }

    override
    def visitInnerClass(name: String, outerName: String, innerName: String, access: Int): Unit = {
        if (innerClasses == null) {
            innerClasses = new ByteVector()
        }
        // Sec. 4.7.6 of the JVMS states "Every CONSTANT_Class_info entry in the
        // constant_pool table which represents a class or interface C that is
        // not a package member must have exactly one corresponding entry in the
        // classes array". To avoid duplicates we keep track in the intVal field
        // of the Item of each CONSTANT_Class_info entry C whether an inner
        // class entry has already been added for C (this field is unused for
        // class entries, and changing its value does not change the hashcode
        // and equality tests). If so we store the index of this inner class
        // entry (plus one) in intVal. This hack allows duplicate detection in
        // O(1) time.
        val nameItem = newClassItem(name)
        if (nameItem.intVal == 0) {
            innerClassesCount += 1
            innerClasses.putShort(nameItem.index)
            innerClasses.putShort(if (outerName == null) 0 else newClass(outerName))
            innerClasses.putShort(if (innerName == null) 0 else newUTF8(innerName))
            innerClasses.putShort(access)
            nameItem.intVal = innerClassesCount
        } else {
            // Compare the inner classes entry nameItem.intVal - 1 with the
            // arguments of this method and throw an exception if there is a
            // difference?
        }
    }

    override
    def visitField(access: Int, name: String,
            desc: String, signature: String, value: Any): FieldVisitor =
        new FieldWriter(this, access, name, desc, signature, value)

    override
    def visitMethod(access: Int, name: String,
            desc: String, signature: String, exceptions: Array[String]): MethodVisitor =
        new MethodWriter(this, access, name, desc, signature,
                exceptions, computeMaxs, computeFrames)

    override
    def visitEnd(): Unit = ()

    def toByteArray(): Array[Byte] = {
        if (index > 0xFFFF) {
            throw new RuntimeException("Class file too large!")
        }
        // computes the real size of the bytecode of this class
        var size = 24 + 2 * interfaceCount
        var nbFields = 0
        var fb = firstField
        while (fb != null) {
            nbFields += 1
            size += fb.getSize()
            fb = fb.fv.asInstanceOf[FieldWriter]
        }
        var nbMethods = 0
        var mb = firstMethod
        while (mb != null) {
            nbMethods += 1
            size += mb.getSize()
            mb = mb.mv.asInstanceOf[MethodWriter]
        }
        var attributeCount = 0
        if (bootstrapMethods != null) {
            // we put it as first attribute in order to improve a bit
            // ClassReader.copyBootstrapMethods
            attributeCount += 1
            size += 8 + bootstrapMethods.length
            newUTF8("BootstrapMethods")
        }
        if (ClassReader.SIGNATURES && signature != 0) {
            attributeCount += 1
            size += 8
            newUTF8("Signature")
        }
        if (sourceFile != 0) {
            attributeCount += 1
            size += 8
            newUTF8("SourceFile")
        }
        if (sourceDebug != null) {
            attributeCount += 1
            size += sourceDebug.length + 6
            newUTF8("SourceDebugExtension")
        }
        if (enclosingMethodOwner != 0) {
            attributeCount += 1
            size += 10
            newUTF8("EnclosingMethod")
        }
        if ((access & Opcodes.ACC_DEPRECATED) != 0) {
            attributeCount += 1
            size += 6
            newUTF8("Deprecated")
        }
        if ((access & Opcodes.ACC_SYNTHETIC) != 0) {
            if ((version & 0xFFFF) < Opcodes.V1_5
                    || (access & ACC_SYNTHETIC_ATTRIBUTE) != 0) {
                attributeCount += 1
                size += 6
                newUTF8("Synthetic")
            }
        }
        if (innerClasses != null) {
            attributeCount += 1
            size += 8 + innerClasses.length
            newUTF8("InnerClasses")
        }
        if (ClassReader.ANNOTATIONS && anns != null) {
            attributeCount += 1
            size += 8 + anns.getSize()
            newUTF8("RuntimeVisibleAnnotations")
        }
        if (ClassReader.ANNOTATIONS && ianns != null) {
            attributeCount += 1
            size += 8 + ianns.getSize()
            newUTF8("RuntimeInvisibleAnnotations")
        }
        if (ClassReader.ANNOTATIONS && tanns != null) {
            attributeCount += 1
            size += 8 + tanns.getSize()
            newUTF8("RuntimeVisibleTypeAnnotations")
        }
        if (ClassReader.ANNOTATIONS && itanns != null) {
            attributeCount += 1
            size += 8 + itanns.getSize()
            newUTF8("RuntimeInvisibleTypeAnnotations")
        }
        if (attrs != null) {
            attributeCount += attrs.getCount()
            size += attrs.getSize(this, null, 0, -1, -1)
        }
        size += pool.length
        // allocates a byte vector of this size, in order to avoid unnecessary
        // arraycopy operations in the ByteVector.enlarge() method
        val out = new ByteVector(size)
        out.putInt(0xCAFEBABE).putInt(version)
        out.putShort(index).putByteArray(pool.data, 0, pool.length)
        val mask = Opcodes.ACC_DEPRECATED | ACC_SYNTHETIC_ATTRIBUTE | ((access & ACC_SYNTHETIC_ATTRIBUTE) / TO_ACC_SYNTHETIC)
        out.putShort(access & ~mask).putShort(name).putShort(superName)
        out.putShort(interfaceCount)
        (0 until interfaceCount).foreach { i =>
          out.putShort(interfaces(i))
        }
        out.putShort(nbFields)
        fb = firstField
        while (fb != null) {
            fb.put(out)
            fb = fb.fv.asInstanceOf[FieldWriter]
        }
        out.putShort(nbMethods)
        mb = firstMethod
        while (mb != null) {
            mb.put(out)
            mb = mb.mv.asInstanceOf[MethodWriter]
        }
        out.putShort(attributeCount)
        if (bootstrapMethods != null) {
            out.putShort(newUTF8("BootstrapMethods"))
            out.putInt(bootstrapMethods.length + 2).putShort(
                    bootstrapMethodsCount)
            out.putByteArray(bootstrapMethods.data, 0, bootstrapMethods.length)
        }
        if (ClassReader.SIGNATURES && signature != 0) {
            out.putShort(newUTF8("Signature")).putInt(2).putShort(signature)
        }
        if (sourceFile != 0) {
            out.putShort(newUTF8("SourceFile")).putInt(2).putShort(sourceFile)
        }
        if (sourceDebug != null) {
            val len = sourceDebug.length
            out.putShort(newUTF8("SourceDebugExtension")).putInt(len)
            out.putByteArray(sourceDebug.data, 0, len)
        }
        if (enclosingMethodOwner != 0) {
            out.putShort(newUTF8("EnclosingMethod")).putInt(4)
            out.putShort(enclosingMethodOwner).putShort(enclosingMethod)
        }
        if ((access & Opcodes.ACC_DEPRECATED) != 0) {
            out.putShort(newUTF8("Deprecated")).putInt(0)
        }
        if ((access & Opcodes.ACC_SYNTHETIC) != 0) {
            if ((version & 0xFFFF) < Opcodes.V1_5
                    || (access & ACC_SYNTHETIC_ATTRIBUTE) != 0) {
                out.putShort(newUTF8("Synthetic")).putInt(0)
            }
        }
        if (innerClasses != null) {
            out.putShort(newUTF8("InnerClasses"))
            out.putInt(innerClasses.length + 2).putShort(innerClassesCount)
            out.putByteArray(innerClasses.data, 0, innerClasses.length)
        }
        if (ClassReader.ANNOTATIONS && anns != null) {
            out.putShort(newUTF8("RuntimeVisibleAnnotations"))
            anns.put(out)
        }
        if (ClassReader.ANNOTATIONS && ianns != null) {
            out.putShort(newUTF8("RuntimeInvisibleAnnotations"))
            ianns.put(out)
        }
        if (ClassReader.ANNOTATIONS && tanns != null) {
            out.putShort(newUTF8("RuntimeVisibleTypeAnnotations"))
            tanns.put(out)
        }
        if (ClassReader.ANNOTATIONS && itanns != null) {
            out.putShort(newUTF8("RuntimeInvisibleTypeAnnotations"))
            itanns.put(out)
        }
        if (attrs != null) {
            attrs.put(this, null, 0, -1, -1, out)
        }
        if (invalidFrames) {
            anns = null
            ianns = null
            attrs = null
            innerClassesCount = 0
            innerClasses = null
            bootstrapMethodsCount = 0
            bootstrapMethods = null
            firstField = null
            lastField = null
            firstMethod = null
            lastMethod = null
            computeMaxs = false
            computeFrames = true
            invalidFrames = false
            new ClassReader(out.data).accept(this, ClassReader.SKIP_FRAMES)
            return toByteArray()
        }
        out.data
    }

    def newConstItem(cst: Any): Item =
      cst match {
        case value: Integer => newInteger(value.intValue())
        case value: Byte    => newInteger(value.intValue())
        case value: Character => newInteger(value.charValue())
        case value: Short     => newInteger(value.intValue())
        case value: Boolean   => newInteger(if (value.booleanValue()) 1 else 0)
        case value: Float     => newFloat(value.floatValue())
        case value: Long      => newLong(value.longValue())
        case value: Double    => newDouble(value.doubleValue())
        case value: String    => newString(value)
        case t: Type =>
          val s = t.getSort()
          if (s == Type.OBJECT) {
            newClassItem(t.getInternalName())
          } else if (s == Type.METHOD) {
            newMethodTypeItem(t.getDescriptor())
          } else { // s == primitive type or array
            newClassItem(t.getDescriptor())
          }
        case h: Handle =>
          newHandleItem(h.tag, h.owner, h.name, h.desc, h.itf)
        case _ =>
          throw new IllegalArgumentException("value " + cst)
      }

    def newConst(cst: Object): Int =
        newConstItem(cst).index

    def newUTF8(value: String): Int = {
        key.set(UTF8, value, null, null)
        var result = get(key)
        if (result == null) {
          pool.putByte(UTF8).putUTF8(value)
          result = new Item(index, key)
          index += 1
          put(result)
        }
        result.index
    }

    def newClassItem(value: String): Item = {
      key2.set(CLASS, value, null, null)
      var result = get(key2)
      if (result == null) {
        pool.put12(CLASS, newUTF8(value))
        result = new Item(index, key2)
        index += 1
        put(result)
      }
      result
    }

    def newClass(value: String): Int =
      newClassItem(value).index

    def newMethodTypeItem(methodDesc: String): Item = ???/*{
        key2.set(MTYPE, methodDesc, null, null)
        Item result = get(key2)
        if (result == null) {
            pool.put12(MTYPE, newUTF8(methodDesc))
            result = new Item(index++, key2)
            put(result)
        }
        return result
    }*/

    def newMethodType(methodDesc: String): Int =
        newMethodTypeItem(methodDesc).index

    def newHandleItem(tag: Int, owner: String, name: String,
            desc: String, itf: Boolean): Item = ???/*{
        key4.set(HANDLE_BASE + tag, owner, name, desc)
        Item result = get(key4)
        if (result == null) {
            if (tag <= Opcodes.H_PUTSTATIC) {
                put112(HANDLE, tag, newField(owner, name, desc))
            } else {
                put112(HANDLE,
                        tag,
                        newMethod(owner, name, desc, itf))
            }
            result = new Item(index++, key4)
            put(result)
        }
        return result
    }*/

    @deprecated
    def newHandle(tag: Int, owner: String, name: String, desc: String): Int =
        newHandle(tag, owner, name, desc, tag == Opcodes.H_INVOKEINTERFACE)

    def newHandle(tag: Int, owner: String, name: String, desc: String, itf: Boolean): Int =
        newHandleItem(tag, owner, name, desc, itf).index

    def newInvokeDynamicItem(name: String, desc: String,
            bsm: Handle, bsmArgs: Object*): Item = ???/*{
        // cache for performance
        ByteVector bootstrapMethods = this.bootstrapMethods
        if (bootstrapMethods == null) {
            bootstrapMethods = this.bootstrapMethods = new ByteVector()
        }

        int position = bootstrapMethods.length // record current position

        int hashCode = bsm.hashCode()
        bootstrapMethods.putShort(newHandle(bsm.tag, bsm.owner, bsm.name,
                bsm.desc, bsm.isInterface()))

        int argsLength = bsmArgs.length
        bootstrapMethods.putShort(argsLength)

        for (int i = 0 i < argsLength i++) {
            Object bsmArg = bsmArgs[i]
            hashCode ^= bsmArg.hashCode()
            bootstrapMethods.putShort(newConst(bsmArg))
        }

        byte[] data = bootstrapMethods.data
        int length = (1 + 1 + argsLength) << 1 // (bsm + argCount + arguments)
        hashCode &= 0x7FFFFFFF
        Item result = items[hashCode % items.length]
        loop: while (result != null) {
            if (result.type != BSM || result.hashCode != hashCode) {
                result = result.next
                continue
            }

            // because the data encode the size of the argument
            // we don't need to test if these size are equals
            int resultPosition = result.intVal
            for (int p = 0 p < length p++) {
                if (data[position + p] != data[resultPosition + p]) {
                    result = result.next
                    continue loop
                }
            }
            break
        }

        int bootstrapMethodIndex
        if (result != null) {
            bootstrapMethodIndex = result.index
            bootstrapMethods.length = position // revert to old position
        } else {
            bootstrapMethodIndex = bootstrapMethodsCount++
            result = new Item(bootstrapMethodIndex)
            result.set(position, hashCode)
            put(result)
        }

        // now, create the InvokeDynamic constant
        key3.set(name, desc, bootstrapMethodIndex)
        result = get(key3)
        if (result == null) {
            put122(INDY, bootstrapMethodIndex, newNameType(name, desc))
            result = new Item(index++, key3)
            put(result)
        }
        return result
    }*/

    def newInvokeDynamic(name: String, desc: String, bsm: Handle, bsmArgs: Object*): Int =
        newInvokeDynamicItem(name, desc, bsm, bsmArgs).index

    def newFieldItem(owner: String, name: String, desc: String): Item = {
        key3.set(FIELD, owner, name, desc)
        var result = get(key3)
        if (result == null) {
            put122(FIELD, newClass(owner), newNameType(name, desc))
            result = new Item(index, key3)
            index += 1
            put(result)
        }
        result
    }

    def newField(owner: String, name: String, desc: String): Int =
        newFieldItem(owner, name, desc).index

    def newMethodItem(owner: String, name: String,
            desc: String, itf: Boolean): Item = {
        val type_ = if (itf) IMETH else METH
        key3.set(type_, owner, name, desc)
        var result = get(key3)
        if (result == null) {
            put122(type_, newClass(owner), newNameType(name, desc))
            result = new Item(index, key3)
            index += 1
            put(result)
        }
        result
    }

    def newMethod(owner: String, name: String,
            desc: String, itf: Boolean): Int =
        newMethodItem(owner, name, desc, itf).index

    def newInteger(value: Int): Item = ???/*{
        key.set(value)
        Item result = get(key)
        if (result == null) {
            pool.putByte(INT).putInt(value)
            result = new Item(index++, key)
            put(result)
        }
        return result
    }*/

    def newFloat(value: Float): Item = ???/*{
        key.set(value)
        Item result = get(key)
        if (result == null) {
            pool.putByte(FLOAT).putInt(key.intVal)
            result = new Item(index++, key)
            put(result)
        }
        return result
    }*/

    def newLong(value: Long): Item = {
        key.set(value)
        var result = get(key)
        if (result == null) {
            pool.putByte(LONG).putLong(value)
            result = new Item(index, key)
            index += 2
            put(result)
        }
        result
    }

    def newDouble(value: Double): Item = ???/*{
        key.set(value)
        Item result = get(key)
        if (result == null) {
            pool.putByte(DOUBLE).putLong(key.longVal)
            result = new Item(index, key)
            index += 2
            put(result)
        }
        return result
    }*/

    private def newString(value: String): Item = {
        key2.set(STR, value, null, null)
        var result = get(key2)
        if (result == null) {
            pool.put12(STR, newUTF8(value))
            result = new Item(index, key2)
            index += 1
            put(result)
        }
        result
    }

    def newNameType(name: String, desc: String): Int =
        newNameTypeItem(name, desc).index

    def newNameTypeItem(name: String, desc: String): Item = {
        key2.set(NAME_TYPE, name, desc, null)
        var result = get(key2)
        if (result == null) {
            put122(NAME_TYPE, newUTF8(name), newUTF8(desc))
            result = new Item(index, key2)
            index += 1
            put(result)
        }
        result
    }

    def addType(type_ : String): Int = {
        key.set(TYPE_NORMAL, type_, null, null)
        var result = get(key)
        if (result == null) {
            result = addType(key)
        }
        result.index
    }

    def addUninitializedType(type_ : String, offset: Int): Int = {
        key.type_ = TYPE_UNINIT
        key.intVal = offset
        key.strVal1 = type_
        key.hashCode_ = 0x7FFFFFFF & (TYPE_UNINIT + type_.hashCode() + offset)
        var result = get(key)
        if (result == null) {
            result = addType(key)
        }
        result.index
    }

    private def addType(item: Item): Item = {
        typeCount = (typeCount + 1).toShort
        val result = new Item(typeCount, key)
        put(result)
        if (typeTable == null) {
            typeTable = new Array[Item](16)
        }
        if (typeCount == typeTable.length) {
            val newTable = new Array[Item](2 * typeTable.length)
            System.arraycopy(typeTable, 0, newTable, 0, typeTable.length)
            typeTable = newTable
        }
        typeTable(typeCount) = result
        result
    }

    def getMergedType(type1: Int, type2: Int): Int = {
        key2.type_ = TYPE_MERGED
        key2.longVal = type1 | (type2.toLong << 32)
        key2.hashCode_ = 0x7FFFFFFF & (TYPE_MERGED + type1 + type2)
        var result = get(key2)
        if (result == null) {
            val t = typeTable(type1).strVal1
            val u = typeTable(type2).strVal1
            key2.intVal = addType(getCommonSuperClass(t, u))
            result = new Item(0.toShort, key2)
            put(result)
        }
        result.intVal
    }

    protected def getCommonSuperClass(type1: String, type2: String): String = ???/*{
        Class<?> c, d
        ClassLoader classLoader = getClass().getClassLoader()
        try {
            c = Class.forName(type1.replace('/', '.'), false, classLoader)
            d = Class.forName(type2.replace('/', '.'), false, classLoader)
        } catch (Exception e) {
            throw new RuntimeException(e.toString())
        }
        if (c.isAssignableFrom(d)) {
            return type1
        }
        if (d.isAssignableFrom(c)) {
            return type2
        }
        if (c.isInterface() || d.isInterface()) {
            return "java/lang/Object"
        } else {
            do {
                c = c.getSuperclass()
            } while (!c.isAssignableFrom(d))
            return c.getName().replace('.', '/')
        }
    }*/

    private def get(key: Item): Item = {
      var i = items(key.hashCode % items.length)
      while (i != null && (i.type_ != key.type_ || !key.isEqualTo(i))) {
        i = i.next
      }
      i
    }

    private def put(i: Item): Unit = {
      if (this.index + typeCount > threshold) {
        val ll = items.length
        val nl = ll * 2 + 1
        val newItems = new Array[Item](nl)
        (ll - 1) to 0 by -1 foreach { l =>
          var j = items(l)
          while (j != null) {
            val index = j.hashCode_ % newItems.length
            val k = j.next
            j.next = newItems(index)
            newItems(index) = j
            j = k
          }
        }
        items = newItems
        threshold = (nl * 0.75).toInt
      }
      val index = i.hashCode % items.length
      i.next = items(index)
      items(index) = i
    }

    def findItemByIndex(index: Int): Item = ???/*{
        for (Item item : items) {
            while (item != null) {
                if (item.index == index) return item
                item = item.next
            }
        }
        return null
    }*/

    private def put122(b: Int, s1: Int, s2: Int): Unit =
        pool.put12(b, s1).putShort(s2)

    private def put112(b1: Int, b2: Int, s: Int): Unit =
        pool.put11(b1, b2).putShort(s)
}

object ClassWriter {
    final val COMPUTE_MAXS = 1
    final val COMPUTE_FRAMES = 2
    final val ACC_SYNTHETIC_ATTRIBUTE = 0x40000
    final val TO_ACC_SYNTHETIC = ACC_SYNTHETIC_ATTRIBUTE / Opcodes.ACC_SYNTHETIC
    final val NOARG_INSN = 0
    final val SBYTE_INSN = 1
    final val SHORT_INSN = 2
    final val VAR_INSN = 3
    final val IMPLVAR_INSN = 4
    final val TYPE_INSN = 5
    final val FIELDORMETH_INSN = 6
    final val ITFMETH_INSN = 7
    final val INDYMETH_INSN = 8
    final val LABEL_INSN = 9
    final val LABELW_INSN = 10
    final val LDC_INSN = 11
    final val LDCW_INSN = 12
    final val IINC_INSN = 13
    final val TABL_INSN = 14
    final val LOOK_INSN = 15
    final val MANA_INSN = 16
    final val WIDE_INSN = 17

    final val CLASS = 7
    final val FIELD = 9
    final val METH = 10
    final val IMETH = 11
    final val STR = 8
    final val INT = 3
    final val FLOAT = 4
    final val LONG = 5
    final val DOUBLE = 6
    final val NAME_TYPE = 12
    final val UTF8 = 1
    final val MTYPE = 16
    final val HANDLE = 15
    final val INDY = 18
    final val HANDLE_BASE = 20
    final val TYPE_NORMAL = 30
    final val TYPE_UNINIT = 31
    final val TYPE_MERGED = 32
    final val BSM = 33

    val TYPE = {
        var i = 0
        val b = new Array[Byte](220)
        val s = ("AAAAAAAAAAAAAAAABCLMMDDDDDEEEEEEEEEEEEEEEEEEEEAAAAAAAADD"
                 + "DDDEEEEEEEEEEEEEEEEEEEEAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"
                 + "AAAAAAAAAAAAAAAAANAAAAAAAAAAAAAAAAAAAAJJJJJJJJJJJJJJJJDOPAA"
                 + "AAAAGGGGGGGHIFBFAAFFAARQJJKKJJJJJJJJJJJJJJJJJJ")
        while (i < b.length) {
          b(i) = (s.charAt(i) - 'A').toByte
          i += 1
        }
        b

        // code to generate the above string
        //
        // // SBYTE_INSN instructions
        // b[Constants.NEWARRAY] = SBYTE_INSN
        // b[Constants.BIPUSH] = SBYTE_INSN
        //
        // // SHORT_INSN instructions
        // b[Constants.SIPUSH] = SHORT_INSN
        //
        // // (IMPL)VAR_INSN instructions
        // b[Constants.RET] = VAR_INSN
        // for (i = Constants.ILOAD i <= Constants.ALOAD ++i) {
        // b[i] = VAR_INSN
        // }
        // for (i = Constants.ISTORE i <= Constants.ASTORE ++i) {
        // b[i] = VAR_INSN
        // }
        // for (i = 26 i <= 45 ++i) { // ILOAD_0 to ALOAD_3
        // b[i] = IMPLVAR_INSN
        // }
        // for (i = 59 i <= 78 ++i) { // ISTORE_0 to ASTORE_3
        // b[i] = IMPLVAR_INSN
        // }
        //
        // // TYPE_INSN instructions
        // b[Constants.NEW] = TYPE_INSN
        // b[Constants.ANEWARRAY] = TYPE_INSN
        // b[Constants.CHECKCAST] = TYPE_INSN
        // b[Constants.INSTANCEOF] = TYPE_INSN
        //
        // // (Set)FIELDORMETH_INSN instructions
        // for (i = Constants.GETSTATIC i <= Constants.INVOKESTATIC ++i) {
        // b[i] = FIELDORMETH_INSN
        // }
        // b[Constants.INVOKEINTERFACE] = ITFMETH_INSN
        // b[Constants.INVOKEDYNAMIC] = INDYMETH_INSN
        //
        // // LABEL(W)_INSN instructions
        // for (i = Constants.IFEQ i <= Constants.JSR ++i) {
        // b[i] = LABEL_INSN
        // }
        // b[Constants.IFNULL] = LABEL_INSN
        // b[Constants.IFNONNULL] = LABEL_INSN
        // b[200] = LABELW_INSN // GOTO_W
        // b[201] = LABELW_INSN // JSR_W
        // // temporary opcodes used internally by ASM - see Label and
        // MethodWriter
        // for (i = 202 i < 220 ++i) {
        // b[i] = LABEL_INSN
        // }
        //
        // // LDC(_W) instructions
        // b[Constants.LDC] = LDC_INSN
        // b[19] = LDCW_INSN // LDC_W
        // b[20] = LDCW_INSN // LDC2_W
        //
        // // special instructions
        // b[Constants.IINC] = IINC_INSN
        // b[Constants.TABLESWITCH] = TABL_INSN
        // b[Constants.LOOKUPSWITCH] = LOOK_INSN
        // b[Constants.MULTIANEWARRAY] = MANA_INSN
        // b[196] = WIDE_INSN // WIDE
        //
        // for (i = 0 i < b.length ++i) {
        // System.err.print((char)('A' + b[i]))
        // }
        // System.err.println()
    }
}
