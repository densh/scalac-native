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
package scala.tools.asm.tree.analysis

import java.util.List

import scala.tools.asm.Type
import scala.tools.asm.Opcodes

class SimpleVerifier (api: Int,
            var currentClass:Type ,
            var currentSuperClass:Type ,
            var currentClassInterfaces:List[Type] ,
            var isInterface: Boolean) extends BasicVerifier(api) {
    private var loader = getClass().getClassLoader()

    def this(currentClass: Type,
            currentSuperClass: Type,
            currentClassInterfaces:List[Type] , isInterface: Boolean) =
        this(Opcodes.ASM5, currentClass, currentSuperClass, currentClassInterfaces,
                isInterface)

    def this(currentClass: Type,
            currentSuperClass: Type, isInterface: Boolean) =
        this(currentClass, currentSuperClass, null, isInterface)

    def this() =
        this(null, null, false)

    def setClassLoader(loader: ClassLoader ): Unit =
        this.loader = loader

    override
    def newValue(type_ : Type): BasicValue = ???/*{
        if (type == null) {
            return BasicValue.UNINITIALIZED_VALUE
        }

        boolean isArray = type.getSort() == Type.ARRAY
        if (isArray) {
            switch (type.getElementType().getSort()) {
            case Type.BOOLEAN:
            case Type.CHAR:
            case Type.BYTE:
            case Type.SHORT:
                return new BasicValue(type)
            }
        }

        BasicValue v = super.newValue(type)
        if (BasicValue.REFERENCE_VALUE.equals(v)) {
            if (isArray) {
                v = newValue(type.getElementType())
                String desc = v.getType().getDescriptor()
                for (int i = 0 i < type.getDimensions() ++i) {
                    desc = '[' + desc
                }
                v = new BasicValue(Type.getType(desc))
            } else {
                v = new BasicValue(type)
            }
        }
        return v
    }*/

    override
    protected def isArrayValue(value:BasicValue ): Boolean = {
        val t = value.getType()

        t != null && ("Lnull".equals(t.getDescriptor()) || t.getSort() == Type.ARRAY)
    }

    override
    protected def getElementValue(objectArrayValue:BasicValue ): BasicValue = ???/*{
        Type arrayType = objectArrayValue.getType()
        if (arrayType != null) {
            if (arrayType.getSort() == Type.ARRAY) {
                return newValue(Type.getType(arrayType.getDescriptor()
                        .substring(1)))
            } else if ("Lnull".equals(arrayType.getDescriptor())) {
                return objectArrayValue
            }
        }
        throw new Error("Internal error")
    }*/

    override
    protected def isSubTypeOf(value:BasicValue ,
            expected:BasicValue ): Boolean = ???/*{
        Type expectedType = expected.getType()
        Type type = value.getType()
        switch (expectedType.getSort()) {
        case Type.INT:
        case Type.FLOAT:
        case Type.LONG:
        case Type.DOUBLE:
            return type.equals(expectedType)
        case Type.ARRAY:
        case Type.OBJECT:
            if ("Lnull".equals(type.getDescriptor())) {
                return true
            } else if (type.getSort() == Type.OBJECT
                    || type.getSort() == Type.ARRAY) {
                return isAssignableFrom(expectedType, type)
            } else {
                return false
            }
        default:
            throw new Error("Internal error")
        }
    }*/

    override
    def merge(v:BasicValue , w:BasicValue ):BasicValue = ???/*{
        if (!v.equals(w)) {
            Type t = v.getType()
            Type u = w.getType()
            if (t != null
                    && (t.getSort() == Type.OBJECT || t.getSort() == Type.ARRAY)) {
                if (u != null
                        && (u.getSort() == Type.OBJECT || u.getSort() == Type.ARRAY)) {
                    if ("Lnull".equals(t.getDescriptor())) {
                        return w
                    }
                    if ("Lnull".equals(u.getDescriptor())) {
                        return v
                    }
                    if (isAssignableFrom(t, u)) {
                        return v
                    }
                    if (isAssignableFrom(u, t)) {
                        return w
                    }
                    // TODO case of array classes of the same dimension
                    // TODO should we look also for a common super interface?
                    // problem: there may be several possible common super
                    // interfaces
                    do {
                        if (t == null || isInterface(t)) {
                            return BasicValue.REFERENCE_VALUE
                        }
                        t = getSuperClass(t)
                        if (isAssignableFrom(t, u)) {
                            return newValue(t)
                        }
                    } while (true)
                }
            }
            return BasicValue.UNINITIALIZED_VALUE
        }
        return v
    }*/

    protected def isInterface(t: Type): Boolean = {
        if (currentClass != null && t.equals(currentClass)) {
            return isInterface
        }
        return getClass(t).isInterface()
    }

    protected def getSuperClass(t: Type): Type = {
        if (currentClass != null && t.equals(currentClass)) {
            return currentSuperClass
        }
        val c = getClass(t).getSuperclass()
        if (c == null) null else Type.getType(c)
    }

    protected def isAssignableFrom(t: Type, u: Type): Boolean = ???/*{
        if (t.equals(u)) {
            return true
        }
        if (currentClass != null && t.equals(currentClass)) {
            if (getSuperClass(u) == null) {
                return false
            } else {
                if (isInterface) {
                    return u.getSort() == Type.OBJECT
                            || u.getSort() == Type.ARRAY
                }
                return isAssignableFrom(t, getSuperClass(u))
            }
        }
        if (currentClass != null && u.equals(currentClass)) {
            if (isAssignableFrom(t, currentSuperClass)) {
                return true
            }
            if (currentClassInterfaces != null) {
                for (int i = 0 i < currentClassInterfaces.size() ++i) {
                    Type v = currentClassInterfaces.get(i)
                    if (isAssignableFrom(t, v)) {
                        return true
                    }
                }
            }
            return false
        }
        Class<?> tc = getClass(t)
        if (tc.isInterface()) {
            tc = Object.class
        }
        return tc.isAssignableFrom(getClass(u))
    }*/

    protected def getClass(t: Type): Class[_] = ???/*{
        try {
            if (t.getSort() == Type.ARRAY) {
                return Class.forName(t.getDescriptor().replace('/', '.'),
                        false, loader)
            }
            return Class.forName(t.getClassName(), false, loader)
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e.toString())
        }
    }*/
}
