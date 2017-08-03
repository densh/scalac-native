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

class MethodWriter extends MethodVisitor(Opcodes.ASM5) {
    private var access : Int = _
    private var name   : Int = _
    private var desc   : Int = _
    private var descriptor: String = _
    var signature: String = _
    var classReaderOffset : Int = _
    var classReaderLength : Int = _
    var exceptionCount    : Int = _
    var exceptions: Array[Int] = _
    private var annd : ByteVector = _
    private var anns   : AnnotationWriter = _
    private var ianns  : AnnotationWriter = _
    private var tanns  : AnnotationWriter = _
    private var itanns : AnnotationWriter = _
    private var panns  : Array[AnnotationWriter] = _
    private var ipanns : Array[AnnotationWriter] = _
    private var synthetics : Int = _
    private var attrs: Attribute  = _
    private var code: ByteVector = new ByteVector()
    private var maxStack : Int = _
    private var maxLocals : Int = _
    private var currentLocals : Int = _
    private var frameCount : Int = _
    private var stackMap: ByteVector = _
    private var previousFrameOffset: Int = _
    private var previousFrame: Array[Int] = _
    private var frame: Array[Int] = _
    private var handlerCount: Int = _
    private var firstHandler: Handler = _
    private var lastHandler:  Handler = _
    private var methodParametersCount: Int = _
    private var methodParameters: ByteVector = _
    private var localVarCount: Int = _
    private var localVar: ByteVector = _
    private var localVarTypeCount: Int = _
    private var localVarType: ByteVector = _
    private var lineNumberCount: Int = _
    private var lineNumber: ByteVector = _
    private var lastCodeOffset: Int = _
    private var ctanns : AnnotationWriter = _
    private var ictanns: AnnotationWriter = _
    private var cattrs: Attribute = _
    private var resize: Boolean = _
    private var subroutines: Int  = _
    private var compute: Int  = _
    private var labels        : Label = _
    private var previousBlock : Label = _
    private var currentBlock  : Label = _
    private var stackSize: Int = _
    private var maxStackSize: Int = _
    private var cw: ClassWriter = _

    def getMaxStack(): Int = maxStack
    def getMaxLocals(): Int = maxLocals

    def this(cw: ClassWriter, access: Int, name: String,
            desc: String, signature: String,
            exceptions: Array[String], computeMaxs: Boolean,
            computeFrames: Boolean) = {
        this()

        if (cw.firstMethod == null) {
            cw.firstMethod = this
        } else {
            cw.lastMethod.mv = this
        }
        cw.lastMethod = this
        this.cw = cw
        this.access = access
        if ("<init>".equals(name)) {
            this.access |= MethodWriter.ACC_CONSTRUCTOR
        }
        this.name = cw.newUTF8(name)
        this.desc = cw.newUTF8(desc)
        this.descriptor = desc
        if (ClassReader.SIGNATURES) {
            this.signature = signature
        }
        if (exceptions != null && exceptions.length > 0) {
            exceptionCount = exceptions.length
            this.exceptions = new Array[Int](exceptionCount)
            (0 until exceptionCount).foreach { i =>
              this.exceptions(i) = cw.newClass(exceptions(i))
            }
        }
        this.compute = if (computeFrames) MethodWriter.FRAMES else (if (computeMaxs) MethodWriter.MAXS else MethodWriter.NOTHING)
        if (computeMaxs || computeFrames) {
            // updates maxLocals
            var size = Type.getArgumentsAndReturnSizes(descriptor) >> 2
            if ((access & Opcodes.ACC_STATIC) != 0) {
              size -= 1
            }
            maxLocals = size
            currentLocals = size
            // creates and visits the label for the first basic block
            labels = new Label()
            labels.status |= Label.PUSHED
            visitLabel(labels)
        }
    }

    override
    def visitParameter(name: String, access: Int): Unit = ???/*{
        if (methodParameters == null) {
            methodParameters = new ByteVector()
        }
        ++methodParametersCount
        methodParameters.putShort((name == null) ? 0 : cw.newUTF8(name))
                .putShort(access)
    }*/

    override
    def visitAnnotationDefault(): AnnotationVisitor = ???/*{
        if (!ClassReader.ANNOTATIONS) {
            return null
        }
        annd = new ByteVector()
        return new AnnotationWriter(cw, false, annd, null, 0)
    }*/

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
    def visitTypeAnnotation(typeRef: Int, typePath: TypePath, desc: String, visible: Boolean): AnnotationVisitor = ???/*{
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
    def visitParameterAnnotation(parameter: Int, desc: String, visible: Boolean): AnnotationVisitor = ???/*{
        if (!ClassReader.ANNOTATIONS) {
            return null
        }
        ByteVector bv = new ByteVector()
        if ("Ljava/lang/Synthetic".equals(desc)) {
            // workaround for a bug in javac with synthetic parameters
            // see ClassReader.readParameterAnnotations
            synthetics = Math.max(synthetics, parameter + 1)
            return new AnnotationWriter(cw, false, bv, null, 0)
        }
        // write type, and reserve space for values count
        bv.putShort(cw.newUTF8(desc)).putShort(0)
        AnnotationWriter aw = new AnnotationWriter(cw, true, bv, bv, 2)
        if (visible) {
            if (panns == null) {
                panns = new AnnotationWriter[Type.getArgumentTypes(descriptor).length]
            }
            aw.next = panns[parameter]
            panns[parameter] = aw
        } else {
            if (ipanns == null) {
                ipanns = new AnnotationWriter[Type.getArgumentTypes(descriptor).length]
            }
            aw.next = ipanns[parameter]
            ipanns[parameter] = aw
        }
        return aw
    }*/

    override
    def visitAttribute(attr: Attribute): Unit = ???/*{
        if (attr.isCodeAttribute()) {
            attr.next = cattrs
            cattrs = attr
        } else {
            attr.next = attrs
            attrs = attr
        }
    }*/

    override
    def visitCode(): Unit = ()

    override
    def visitFrame(type_ : Int, nLocal: Int,
            local: Array[Object], nStack: Int, stack: Array[Object]): Unit = ???/*{
        if (!ClassReader.FRAMES || compute == FRAMES) {
            return
        }

        if (type == Opcodes.F_NEW) {
            if (previousFrame == null) {
                visitImplicitFirstFrame()
            }
            currentLocals = nLocal
            int frameIndex = startFrame(code.length, nLocal, nStack)
            for (int i = 0 i < nLocal ++i) {
                if (local(i) instanceof String) {
                    frame[frameIndex++] = Frame.OBJECT
                            | cw.addType((String) local(i))
                } else if (local(i) instanceof Integer) {
                    frame[frameIndex++] = ((Integer) local(i)).intValue()
                } else {
                    frame[frameIndex++] = Frame.UNINITIALIZED
                            | cw.addUninitializedType("",
                                    ((Label) local(i)).position)
                }
            }
            for (int i = 0 i < nStack ++i) {
                if (stack(i) instanceof String) {
                    frame[frameIndex++] = Frame.OBJECT
                            | cw.addType((String) stack(i))
                } else if (stack(i) instanceof Integer) {
                    frame[frameIndex++] = ((Integer) stack(i)).intValue()
                } else {
                    frame[frameIndex++] = Frame.UNINITIALIZED
                            | cw.addUninitializedType("",
                                    ((Label) stack(i)).position)
                }
            }
            endFrame()
        } else {
            int delta
            if (stackMap == null) {
                stackMap = new ByteVector()
                delta = code.length
            } else {
                delta = code.length - previousFrameOffset - 1
                if (delta < 0) {
                    if (type == Opcodes.F_SAME) {
                        return
                    } else {
                        throw new IllegalStateException()
                    }
                }
            }

            switch (type) {
            case Opcodes.F_FULL:
                currentLocals = nLocal
                stackMap.putByte(FULL_FRAME).putShort(delta).putShort(nLocal)
                for (int i = 0 i < nLocal ++i) {
                    writeFrameType(local(i))
                }
                stackMap.putShort(nStack)
                for (int i = 0 i < nStack ++i) {
                    writeFrameType(stack(i))
                }
                break
            case Opcodes.F_APPEND:
                currentLocals += nLocal
                stackMap.putByte(SAME_FRAME_EXTENDED + nLocal).putShort(delta)
                for (int i = 0 i < nLocal ++i) {
                    writeFrameType(local(i))
                }
                break
            case Opcodes.F_CHOP:
                currentLocals -= nLocal
                stackMap.putByte(SAME_FRAME_EXTENDED - nLocal).putShort(delta)
                break
            case Opcodes.F_SAME:
                if (delta < 64) {
                    stackMap.putByte(delta)
                } else {
                    stackMap.putByte(SAME_FRAME_EXTENDED).putShort(delta)
                }
                break
            case Opcodes.F_SAME1:
                if (delta < 64) {
                    stackMap.putByte(SAME_LOCALS_1_STACK_ITEM_FRAME + delta)
                } else {
                    stackMap.putByte(SAME_LOCALS_1_STACK_ITEM_FRAME_EXTENDED)
                            .putShort(delta)
                }
                writeFrameType(stack[0])
                break
            }

            previousFrameOffset = code.length
            ++frameCount
        }

        maxStack = Math.max(maxStack, nStack)
        maxLocals = Math.max(maxLocals, currentLocals)
    }*/

    override
    def visitInsn(opcode: Int): Unit = {
        lastCodeOffset = code.length
        // adds the instruction to the bytecode of the method
        code.putByte(opcode)
        // update currentBlock
        // Label currentBlock = this.currentBlock
        if (currentBlock != null) {
            if (compute == MethodWriter.FRAMES) {
                currentBlock.frame.execute(opcode, 0, null, null)
            } else {
                // updates current and max stack sizes
                val size = stackSize + Frame.SIZE(opcode)
                if (size > maxStackSize) {
                    maxStackSize = size
                }
                stackSize = size
            }
            // if opcode == ATHROW or xRETURN, ends current block (no successor)
            if ((opcode >= Opcodes.IRETURN && opcode <= Opcodes.RETURN)
                    || opcode == Opcodes.ATHROW) {
                noSuccessor()
            }
        }
    }

    override
    def visitIntInsn(opcode: Int, operand: Int): Unit = ???/*{
        lastCodeOffset = code.length
        // Label currentBlock = this.currentBlock
        if (currentBlock != null) {
            if (compute == FRAMES) {
                currentBlock.frame.execute(opcode, operand, null, null)
            } else if (opcode != Opcodes.NEWARRAY) {
                // updates current and max stack sizes only for NEWARRAY
                // (stack size variation = 0 for BIPUSH or SIPUSH)
                int size = stackSize + 1
                if (size > maxStackSize) {
                    maxStackSize = size
                }
                stackSize = size
            }
        }
        // adds the instruction to the bytecode of the method
        if (opcode == Opcodes.SIPUSH) {
            code.put12(opcode, operand)
        } else { // BIPUSH or NEWARRAY
            code.put11(opcode, operand)
        }
    }*/

    override def visitVarInsn(opcode: Int, var_ : Int): Unit = {
        lastCodeOffset = code.length
        // Label currentBlock = this.currentBlock
        if (currentBlock != null) {
            if (compute == MethodWriter.FRAMES) {
                currentBlock.frame.execute(opcode, var_, null, null)
            } else {
                // updates current and max stack sizes
                if (opcode == Opcodes.RET) {
                    // no stack change, but end of current block (no successor)
                    currentBlock.status |= Label.RET
                    // save 'stackSize' here for future use
                    // (see {@link #findSubroutineSuccessors})
                    currentBlock.inputStackTop = stackSize
                    noSuccessor()
                } else { // xLOAD or xSTORE
                    val size = stackSize + Frame.SIZE(opcode)
                    if (size > maxStackSize) {
                        maxStackSize = size
                    }
                    stackSize = size
                }
            }
        }
        if (compute != MethodWriter.NOTHING) {
            // updates max locals
            var n = 0
            if (opcode == Opcodes.LLOAD || opcode == Opcodes.DLOAD
                    || opcode == Opcodes.LSTORE || opcode == Opcodes.DSTORE) {
                n = var_ + 2
            } else {
                n = var_ + 1
            }
            if (n > maxLocals) {
                maxLocals = n
            }
        }
        // adds the instruction to the bytecode of the method
        if (var_ < 4 && opcode != Opcodes.RET) {
            var opt = 0
            if (opcode < Opcodes.ISTORE) {
                opt = 26 + ((opcode - Opcodes.ILOAD) << 2) + var_
            } else {
                opt = 59 + ((opcode - Opcodes.ISTORE) << 2) + var_
            }
            code.putByte(opt)
        } else if (var_ >= 256) {
            code.putByte(196).put12(opcode, var_)
        } else {
            code.put11(opcode, var_)
        }
        if (opcode >= Opcodes.ISTORE && compute == MethodWriter.FRAMES && handlerCount > 0) {
            visitLabel(new Label())
        }
    }

    override
    def visitTypeInsn(opcode: Int, type_ : String): Unit = ???/*{
        lastCodeOffset = code.length
        Item i = cw.newClassItem(type)
        // Label currentBlock = this.currentBlock
        if (currentBlock != null) {
            if (compute == FRAMES) {
                currentBlock.frame.execute(opcode, code.length, cw, i)
            } else if (opcode == Opcodes.NEW) {
                // updates current and max stack sizes only if opcode == NEW
                // (no stack change for ANEWARRAY, CHECKCAST, INSTANCEOF)
                int size = stackSize + 1
                if (size > maxStackSize) {
                    maxStackSize = size
                }
                stackSize = size
            }
        }
        // adds the instruction to the bytecode of the method
        code.put12(opcode, i.index)
    }*/

    override
    def visitFieldInsn(opcode: Int, owner: String,
            name: String, desc: String): Unit = ???/*{
        lastCodeOffset = code.length
        Item i = cw.newFieldItem(owner, name, desc)
        // Label currentBlock = this.currentBlock
        if (currentBlock != null) {
            if (compute == FRAMES) {
                currentBlock.frame.execute(opcode, 0, cw, i)
            } else {
                int size
                // computes the stack size variation
                char c = desc.charAt(0)
                switch (opcode) {
                case Opcodes.GETSTATIC:
                    size = stackSize + (c == 'D' || c == 'J' ? 2 : 1)
                    break
                case Opcodes.PUTSTATIC:
                    size = stackSize + (c == 'D' || c == 'J' ? -2 : -1)
                    break
                case Opcodes.GETFIELD:
                    size = stackSize + (c == 'D' || c == 'J' ? 1 : 0)
                    break
                // case Constants.PUTFIELD:
                default:
                    size = stackSize + (c == 'D' || c == 'J' ? -3 : -2)
                    break
                }
                // updates current and max stack sizes
                if (size > maxStackSize) {
                    maxStackSize = size
                }
                stackSize = size
            }
        }
        // adds the instruction to the bytecode of the method
        code.put12(opcode, i.index)
    }*/

    override
    def visitMethodInsn(opcode: Int, owner: String,
            name: String, desc: String, itf: Boolean): Unit = {
        lastCodeOffset = code.length
        val i = cw.newMethodItem(owner, name, desc, itf)
        var argSize = i.intVal
        // Label currentBlock = this.currentBlock
        if (currentBlock != null) {
            if (compute == MethodWriter.FRAMES) {
                currentBlock.frame.execute(opcode, 0, cw, i)
            } else {
                if (argSize == 0) {
                    // the above sizes have not been computed yet,
                    // so we compute them...
                    argSize = Type.getArgumentsAndReturnSizes(desc)
                    // ... and we save them in order
                    // not to recompute them in the future
                    i.intVal = argSize
                }
                var size = 0
                if (opcode == Opcodes.INVOKESTATIC) {
                    size = stackSize - (argSize >> 2) + (argSize & 0x03) + 1
                } else {
                    size = stackSize - (argSize >> 2) + (argSize & 0x03)
                }
                // updates current and max stack sizes
                if (size > maxStackSize) {
                    maxStackSize = size
                }
                stackSize = size
            }
        }
        // adds the instruction to the bytecode of the method
        if (opcode == Opcodes.INVOKEINTERFACE) {
            if (argSize == 0) {
                argSize = Type.getArgumentsAndReturnSizes(desc)
                i.intVal = argSize
            }
            code.put12(Opcodes.INVOKEINTERFACE, i.index).put11(argSize >> 2, 0)
        } else {
            code.put12(opcode, i.index)
        }
    }

    override
    def visitInvokeDynamicInsn(name: String, desc: String,
            bsm: Handle, bsmArgs: Object*): Unit = ???/*{
        lastCodeOffset = code.length
        Item i = cw.newInvokeDynamicItem(name, desc, bsm, bsmArgs)
        int argSize = i.intVal
        // Label currentBlock = this.currentBlock
        if (currentBlock != null) {
            if (compute == FRAMES) {
                currentBlock.frame.execute(Opcodes.INVOKEDYNAMIC, 0, cw, i)
            } else {
                if (argSize == 0) {
                    // the above sizes have not been computed yet,
                    // so we compute them...
                    argSize = Type.getArgumentsAndReturnSizes(desc)
                    // ... and we save them in order
                    // not to recompute them in the future
                    i.intVal = argSize
                }
                int size = stackSize - (argSize >> 2) + (argSize & 0x03) + 1

                // updates current and max stack sizes
                if (size > maxStackSize) {
                    maxStackSize = size
                }
                stackSize = size
            }
        }
        // adds the instruction to the bytecode of the method
        code.put12(Opcodes.INVOKEDYNAMIC, i.index)
        code.putShort(0)
    }*/

    override
    def visitJumpInsn(opcode: Int, label: Label): Unit = ???/*{
        lastCodeOffset = code.length
        Label nextInsn = null
        // Label currentBlock = this.currentBlock
        if (currentBlock != null) {
            if (compute == FRAMES) {
                currentBlock.frame.execute(opcode, 0, null, null)
                // 'label' is the target of a jump instruction
                label.getFirst().status |= Label.TARGET
                // adds 'label' as a successor of this basic block
                addSuccessor(Edge.NORMAL, label)
                if (opcode != Opcodes.GOTO) {
                    // creates a Label for the next basic block
                    nextInsn = new Label()
                }
            } else {
                if (opcode == Opcodes.JSR) {
                    if ((label.status & Label.SUBROUTINE) == 0) {
                        label.status |= Label.SUBROUTINE
                        ++subroutines
                    }
                    currentBlock.status |= Label.JSR
                    addSuccessor(stackSize + 1, label)
                    // creates a Label for the next basic block
                    nextInsn = new Label()
                } else {
                    // updates current stack size (max stack size unchanged
                    // because stack size variation always negative in this
                    // case)
                    stackSize += Frame.SIZE[opcode]
                    addSuccessor(stackSize, label)
                }
            }
        }
        // adds the instruction to the bytecode of the method
        if ((label.status & Label.RESOLVED) != 0
                && label.position - code.length < Short.MIN_VALUE) {
            if (opcode == Opcodes.GOTO) {
                code.putByte(200) // GOTO_W
            } else if (opcode == Opcodes.JSR) {
                code.putByte(201) // JSR_W
            } else {
                // if the IF instruction is transformed into IFNOT GOTO_W the
                // next instruction becomes the target of the IFNOT instruction
                if (nextInsn != null) {
                    nextInsn.status |= Label.TARGET
                }
                code.putByte(opcode <= 166 ? ((opcode + 1) ^ 1) - 1
                        : opcode ^ 1)
                code.putShort(8) // jump offset
                code.putByte(200) // GOTO_W
            }
            label.put(this, code, code.length - 1, true)
        } else {
            code.putByte(opcode)
            label.put(this, code, code.length - 1, false)
        }
        if (currentBlock != null) {
            if (nextInsn != null) {
                // if the jump instruction is not a GOTO, the next instruction
                // is also a successor of this instruction. Calling visitLabel
                // adds the label of this next instruction as a successor of the
                // current block, and starts a new basic block
                visitLabel(nextInsn)
            }
            if (opcode == Opcodes.GOTO) {
                noSuccessor()
            }
        }
    }*/

    override def visitLabel(label: Label): Unit = {
        // resolves previous forward references to label, if any
        resize |= label.resolve(this, code.length, code.data)
        // updates currentBlock
        if ((label.status & Label.DEBUG) != 0) {
            return
        }
        if (compute == MethodWriter.FRAMES) {
            if (currentBlock != null) {
                if (label.position == currentBlock.position) {
                    // successive labels, do not start a new basic block
                    currentBlock.status |= (label.status & Label.TARGET)
                    label.frame = currentBlock.frame
                    return
                }
                // ends current block (with one new successor)
                addSuccessor(Edge.NORMAL, label)
            }
            // begins a new current block
            currentBlock = label
            if (label.frame == null) {
                label.frame = new Frame()
                label.frame.owner = label
            }
            // updates the basic block list
            if (previousBlock != null) {
                if (label.position == previousBlock.position) {
                    previousBlock.status |= (label.status & Label.TARGET)
                    label.frame = previousBlock.frame
                    currentBlock = previousBlock
                    return
                }
                previousBlock.successor = label
            }
            previousBlock = label
        } else if (compute == MethodWriter.MAXS) {
            if (currentBlock != null) {
                // ends current block (with one new successor)
                currentBlock.outputStackMax = maxStackSize
                addSuccessor(stackSize, label)
            }
            // begins a new current block
            currentBlock = label
            // resets the relative current and max stack sizes
            stackSize = 0
            maxStackSize = 0
            // updates the basic block list
            if (previousBlock != null) {
                previousBlock.successor = label
            }
            previousBlock = label
        }
    }

    override
    def visitLdcInsn(cst: Object): Unit = ???/*{
        lastCodeOffset = code.length
        Item i = cw.newConstItem(cst)
        // Label currentBlock = this.currentBlock
        if (currentBlock != null) {
            if (compute == FRAMES) {
                currentBlock.frame.execute(Opcodes.LDC, 0, cw, i)
            } else {
                int size
                // computes the stack size variation
                if (i.type == ClassWriter.LONG || i.type == ClassWriter.DOUBLE) {
                    size = stackSize + 2
                } else {
                    size = stackSize + 1
                }
                // updates current and max stack sizes
                if (size > maxStackSize) {
                    maxStackSize = size
                }
                stackSize = size
            }
        }
        // adds the instruction to the bytecode of the method
        int index = i.index
        if (i.type == ClassWriter.LONG || i.type == ClassWriter.DOUBLE) {
            code.put12(20 /* LDC2_W */, index)
        } else if (index >= 256) {
            code.put12(19 /* LDC_W */, index)
        } else {
            code.put11(Opcodes.LDC, index)
        }
    }*/

    override
    def visitIincInsn(var_ : Int, increment: Int): Unit = ???/*{
        lastCodeOffset = code.length
        if (currentBlock != null) {
            if (compute == FRAMES) {
                currentBlock.frame.execute(Opcodes.IINC, var, null, null)
            }
        }
        if (compute != NOTHING) {
            // updates max locals
            int n = var + 1
            if (n > maxLocals) {
                maxLocals = n
            }
        }
        // adds the instruction to the bytecode of the method
        if ((var > 255) || (increment > 127) || (increment < -128)) {
            code.putByte(196 /* WIDE */).put12(Opcodes.IINC, var)
                    .putShort(increment)
        } else {
            code.putByte(Opcodes.IINC).put11(var, increment)
        }
    }*/

    override
    def visitTableSwitchInsn(min: Int, max: Int,
            dflt: Label, labels: Label*): Unit = ???/*{
        lastCodeOffset = code.length
        // adds the instruction to the bytecode of the method
        int source = code.length
        code.putByte(Opcodes.TABLESWITCH)
        code.putByteArray(null, 0, (4 - code.length % 4) % 4)
        dflt.put(this, code, source, true)
        code.putInt(min).putInt(max)
        for (int i = 0 i < labels.length ++i) {
            labels(i).put(this, code, source, true)
        }
        // updates currentBlock
        visitSwitchInsn(dflt, labels)
    }*/

    override
    def visitLookupSwitchInsn(dflt: Label, keys: Array[Int],
            labels: Array[Label]): Unit = ???/*{
        lastCodeOffset = code.length
        // adds the instruction to the bytecode of the method
        int source = code.length
        code.putByte(Opcodes.LOOKUPSWITCH)
        code.putByteArray(null, 0, (4 - code.length % 4) % 4)
        dflt.put(this, code, source, true)
        code.putInt(labels.length)
        for (int i = 0 i < labels.length ++i) {
            code.putInt(keys(i))
            labels(i).put(this, code, source, true)
        }
        // updates currentBlock
        visitSwitchInsn(dflt, labels)
    }*/

    private def visitSwitchInsn(dflt: Label, labels: Array[Label]): Unit = ???/*{
        // Label currentBlock = this.currentBlock
        if (currentBlock != null) {
            if (compute == FRAMES) {
                currentBlock.frame.execute(Opcodes.LOOKUPSWITCH, 0, null, null)
                // adds current block successors
                addSuccessor(Edge.NORMAL, dflt)
                dflt.getFirst().status |= Label.TARGET
                for (int i = 0 i < labels.length ++i) {
                    addSuccessor(Edge.NORMAL, labels(i))
                    labels(i).getFirst().status |= Label.TARGET
                }
            } else {
                // updates current stack size (max stack size unchanged)
                --stackSize
                // adds current block successors
                addSuccessor(stackSize, dflt)
                for (int i = 0 i < labels.length ++i) {
                    addSuccessor(stackSize, labels(i))
                }
            }
            // ends current block
            noSuccessor()
        }
    }*/

    override
    def visitMultiANewArrayInsn(desc: String, dims: Int): Unit = ???/*{
        lastCodeOffset = code.length
        Item i = cw.newClassItem(desc)
        // Label currentBlock = this.currentBlock
        if (currentBlock != null) {
            if (compute == FRAMES) {
                currentBlock.frame.execute(Opcodes.MULTIANEWARRAY, dims, cw, i)
            } else {
                // updates current stack size (max stack size unchanged because
                // stack size variation always negative or null)
                stackSize += 1 - dims
            }
        }
        // adds the instruction to the bytecode of the method
        code.put12(Opcodes.MULTIANEWARRAY, i.index).putByte(dims)
    }*/

    override
    def visitInsnAnnotation(typeRef: Int,
            typePath: TypePath, desc: String, visible: Boolean):AnnotationVisitor = ??? /*{
        if (!ClassReader.ANNOTATIONS) {
            return null
        }
        ByteVector bv = new ByteVector()
        // write target_type and target_info
        typeRef = (typeRef & 0xFF0000FF) | (lastCodeOffset << 8)
        AnnotationWriter.putTarget(typeRef, typePath, bv)
        // write type, and reserve space for values count
        bv.putShort(cw.newUTF8(desc)).putShort(0)
        AnnotationWriter aw = new AnnotationWriter(cw, true, bv, bv,
                bv.length - 2)
        if (visible) {
            aw.next = ctanns
            ctanns = aw
        } else {
            aw.next = ictanns
            ictanns = aw
        }
        return aw
    }*/

    override
    def visitTryCatchBlock(start: Label, end:Label ,
            handler:Label , type_ : String): Unit = ???/*{
        ++handlerCount
        Handler h = new Handler()
        h.start = start
        h.end = end
        h.handler = handler
        h.desc = type
        h.type = type != null ? cw.newClass(type) : 0
        if (lastHandler == null) {
            firstHandler = h
        } else {
            lastHandler.next = h
        }
        lastHandler = h
    }*/

    override
    def visitTryCatchAnnotation(typeRef: Int,
            typePath: TypePath, desc: String, visible: Boolean): AnnotationVisitor = ???/*{
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
            aw.next = ctanns
            ctanns = aw
        } else {
            aw.next = ictanns
            ictanns = aw
        }
        return aw
    }*/

    override
    def visitLocalVariable(name: String, desc: String,
            signature: String, start: Label, end:Label ,
            index: Int): Unit = ???/*{
        if (signature != null) {
            if (localVarType == null) {
                localVarType = new ByteVector()
            }
            ++localVarTypeCount
            localVarType.putShort(start.position)
                    .putShort(end.position - start.position)
                    .putShort(cw.newUTF8(name)).putShort(cw.newUTF8(signature))
                    .putShort(index)
        }
        if (localVar == null) {
            localVar = new ByteVector()
        }
        ++localVarCount
        localVar.putShort(start.position)
                .putShort(end.position - start.position)
                .putShort(cw.newUTF8(name)).putShort(cw.newUTF8(desc))
                .putShort(index)
        if (compute != NOTHING) {
            // updates max locals
            char c = desc.charAt(0)
            int n = index + (c == 'J' || c == 'D' ? 2 : 1)
            if (n > maxLocals) {
                maxLocals = n
            }
        }
    }*/

    override
    def visitLocalVariableAnnotation(typeRef: Int,
            typePath: TypePath, start: Array[Label], end: Array[Label], index: Array[Int],
            desc: String, visible: Boolean): AnnotationVisitor = ???/*{
        if (!ClassReader.ANNOTATIONS) {
            return null
        }
        ByteVector bv = new ByteVector()
        // write target_type and target_info
        bv.putByte(typeRef >>> 24).putShort(start.length)
        for (int i = 0 i < start.length ++i) {
            bv.putShort(start(i).position)
                    .putShort(end(i).position - start(i).position)
                    .putShort(index(i))
        }
        if (typePath == null) {
            bv.putByte(0)
        } else {
            int length = typePath.b[typePath.offset] * 2 + 1
            bv.putByteArray(typePath.b, typePath.offset, length)
        }
        // write type, and reserve space for values count
        bv.putShort(cw.newUTF8(desc)).putShort(0)
        AnnotationWriter aw = new AnnotationWriter(cw, true, bv, bv,
                bv.length - 2)
        if (visible) {
            aw.next = ctanns
            ctanns = aw
        } else {
            aw.next = ictanns
            ictanns = aw
        }
        return aw
    }*/

    override
    def visitLineNumber(line: Int, start: Label): Unit = {
        if (lineNumber == null) {
            lineNumber = new ByteVector()
        }
        lineNumberCount += 1
        lineNumber.putShort(start.position)
        lineNumber.putShort(line)
    }

    override
    def visitMaxs(maxStack: Int, maxLocals: Int): Unit = ???/*{
        if (resize) {
            // replaces the temporary jump opcodes introduced by Label.resolve.
            if (ClassReader.RESIZE) {
                resizeInstructions()
            } else {
                throw new RuntimeException("Method code too large!")
            }
        }
        if (ClassReader.FRAMES && compute == FRAMES) {
            // completes the control flow graph with exception handler blocks
            Handler handler = firstHandler
            while (handler != null) {
                Label l = handler.start.getFirst()
                Label h = handler.handler.getFirst()
                Label e = handler.end.getFirst()
                // computes the kind of the edges to 'h'
                String t = handler.desc == null ? "java/lang/Throwable"
                        : handler.desc
                int kind = Frame.OBJECT | cw.addType(t)
                // h is an exception handler
                h.status |= Label.TARGET
                // adds 'h' as a successor of labels between 'start' and 'end'
                while (l != e) {
                    // creates an edge to 'h'
                    Edge b = new Edge()
                    b.info = kind
                    b.successor = h
                    // adds it to the successors of 'l'
                    b.next = l.successors
                    l.successors = b
                    // goes to the next label
                    l = l.successor
                }
                handler = handler.next
            }

            // creates and visits the first (implicit) frame
            Frame f = labels.frame
            Type[] args = Type.getArgumentTypes(descriptor)
            f.initInputFrame(cw, access, args, this.maxLocals)
            visitFrame(f)

            /*
             * fix point algorithm: mark the first basic block as 'changed'
             * (i.e. put it in the 'changed' list) and, while there are changed
             * basic blocks, choose one, mark it as unchanged, and update its
             * successors (which can be changed in the process).
             */
            int max = 0
            Label changed = labels
            while (changed != null) {
                // removes a basic block from the list of changed basic blocks
                Label l = changed
                changed = changed.next
                l.next = null
                f = l.frame
                // a reachable jump target must be stored in the stack map
                if ((l.status & Label.TARGET) != 0) {
                    l.status |= Label.STORE
                }
                // all visited labels are reachable, by definition
                l.status |= Label.REACHABLE
                // updates the (absolute) maximum stack size
                int blockMax = f.inputStack.length + l.outputStackMax
                if (blockMax > max) {
                    max = blockMax
                }
                // updates the successors of the current basic block
                Edge e = l.successors
                while (e != null) {
                    Label n = e.successor.getFirst()
                    boolean change = f.merge(cw, n.frame, e.info)
                    if (change && n.next == null) {
                        // if n has changed and is not already in the 'changed'
                        // list, adds it to this list
                        n.next = changed
                        changed = n
                    }
                    e = e.next
                }
            }

            // visits all the frames that must be stored in the stack map
            Label l = labels
            while (l != null) {
                f = l.frame
                if ((l.status & Label.STORE) != 0) {
                    visitFrame(f)
                }
                if ((l.status & Label.REACHABLE) == 0) {
                    // finds start and end of dead basic block
                    Label k = l.successor
                    int start = l.position
                    int end = (k == null ? code.length : k.position) - 1
                    // if non empty basic block
                    if (end >= start) {
                        max = Math.max(max, 1)
                        // replaces instructions with NOP ... NOP ATHROW
                        for (int i = start i < end ++i) {
                            code.data(i) = Opcodes.NOP
                        }
                        code.data[end] = (byte) Opcodes.ATHROW
                        // emits a frame for this unreachable block
                        int frameIndex = startFrame(start, 0, 1)
                        frame[frameIndex] = Frame.OBJECT
                                | cw.addType("java/lang/Throwable")
                        endFrame()
                        // removes the start-end range from the exception
                        // handlers
                        firstHandler = Handler.remove(firstHandler, l, k)
                    }
                }
                l = l.successor
            }

            handler = firstHandler
            handlerCount = 0
            while (handler != null) {
                handlerCount += 1
                handler = handler.next
            }

            this.maxStack = max
        } else if (compute == MAXS) {
            // completes the control flow graph with exception handler blocks
            Handler handler = firstHandler
            while (handler != null) {
                Label l = handler.start
                Label h = handler.handler
                Label e = handler.end
                // adds 'h' as a successor of labels between 'start' and 'end'
                while (l != e) {
                    // creates an edge to 'h'
                    Edge b = new Edge()
                    b.info = Edge.EXCEPTION
                    b.successor = h
                    // adds it to the successors of 'l'
                    if ((l.status & Label.JSR) == 0) {
                        b.next = l.successors
                        l.successors = b
                    } else {
                        // if l is a JSR block, adds b after the first two edges
                        // to preserve the hypothesis about JSR block successors
                        // order (see {@link #visitJumpInsn})
                        b.next = l.successors.next.next
                        l.successors.next.next = b
                    }
                    // goes to the next label
                    l = l.successor
                }
                handler = handler.next
            }

            if (subroutines > 0) {
                // completes the control flow graph with the RET successors
                /*
                 * first step: finds the subroutines. This step determines, for
                 * each basic block, to which subroutine(s) it belongs.
                 */
                // finds the basic blocks that belong to the "main" subroutine
                int id = 0
                labels.visitSubroutine(null, 1, subroutines)
                // finds the basic blocks that belong to the real subroutines
                Label l = labels
                while (l != null) {
                    if ((l.status & Label.JSR) != 0) {
                        // the subroutine is defined by l's TARGET, not by l
                        Label subroutine = l.successors.next.successor
                        // if this subroutine has not been visited yet...
                        if ((subroutine.status & Label.VISITED) == 0) {
                            // ...assigns it a new id and finds its basic blocks
                            id += 1
                            subroutine.visitSubroutine(null, (id / 32L) << 32
                                    | (1L << (id % 32)), subroutines)
                        }
                    }
                    l = l.successor
                }
                // second step: finds the successors of RET blocks
                l = labels
                while (l != null) {
                    if ((l.status & Label.JSR) != 0) {
                        Label L = labels
                        while (L != null) {
                            L.status &= ~Label.VISITED2
                            L = L.successor
                        }
                        // the subroutine is defined by l's TARGET, not by l
                        Label subroutine = l.successors.next.successor
                        subroutine.visitSubroutine(l, 0, subroutines)
                    }
                    l = l.successor
                }
            }

            /*
             * control flow analysis algorithm: while the block stack is not
             * empty, pop a block from this stack, update the max stack size,
             * compute the true (non relative) begin stack size of the
             * successors of this block, and push these successors onto the
             * stack (unless they have already been pushed onto the stack).
             * Note: by hypothesis, the {@link Label#inputStackTop} of the
             * blocks in the block stack are the true (non relative) beginning
             * stack sizes of these blocks.
             */
            int max = 0
            Label stack = labels
            while (stack != null) {
                // pops a block from the stack
                Label l = stack
                stack = stack.next
                // computes the true (non relative) max stack size of this block
                int start = l.inputStackTop
                int blockMax = start + l.outputStackMax
                // updates the global max stack size
                if (blockMax > max) {
                    max = blockMax
                }
                // analyzes the successors of the block
                Edge b = l.successors
                if ((l.status & Label.JSR) != 0) {
                    // ignores the first edge of JSR blocks (virtual successor)
                    b = b.next
                }
                while (b != null) {
                    l = b.successor
                    // if this successor has not already been pushed...
                    if ((l.status & Label.PUSHED) == 0) {
                        // computes its true beginning stack size...
                        l.inputStackTop = b.info == Edge.EXCEPTION ? 1 : start
                                + b.info
                        // ...and pushes it onto the stack
                        l.status |= Label.PUSHED
                        l.next = stack
                        stack = l
                    }
                    b = b.next
                }
            }
            this.maxStack = Math.max(maxStack, max)
        } else {
            this.maxStack = maxStack
            this.maxLocals = maxLocals
        }
    }*/

    override
    def visitEnd(): Unit = ()

    private def addSuccessor(info: Int, successor: Label): Unit = ???/*{
        // creates and initializes an Edge object...
        Edge b = new Edge()
        b.info = info
        b.successor = successor
        // ...and adds it to the successor list of the currentBlock block
        b.next = currentBlock.successors
        currentBlock.successors = b
    }*/

    private def noSuccessor(): Unit = {
        if (compute == MethodWriter.FRAMES) {
            val l = new Label()
            l.frame = new Frame()
            l.frame.owner = l
            l.resolve(this, code.length, code.data)
            previousBlock.successor = l
            previousBlock = l
        } else {
            currentBlock.outputStackMax = maxStackSize
        }
        currentBlock = null
    }

    private def visitFrame(f: Frame): Unit = ???/*{
        int i, t
        int nTop = 0
        int nLocal = 0
        int nStack = 0
        int[] locals = f.inputLocals
        int[] stacks = f.inputStack
        // computes the number of locals (ignores TOP types that are just after
        // a LONG or a DOUBLE, and all trailing TOP types)
        for (i = 0 i < locals.length ++i) {
            t = locals(i)
            if (t == Frame.TOP) {
                ++nTop
            } else {
                nLocal += nTop + 1
                nTop = 0
            }
            if (t == Frame.LONG || t == Frame.DOUBLE) {
                ++i
            }
        }
        // computes the stack size (ignores TOP types that are just after
        // a LONG or a DOUBLE)
        for (i = 0 i < stacks.length ++i) {
            t = stacks(i)
            ++nStack
            if (t == Frame.LONG || t == Frame.DOUBLE) {
                ++i
            }
        }
        // visits the frame and its content
        int frameIndex = startFrame(f.owner.position, nLocal, nStack)
        for (i = 0 nLocal > 0 ++i, --nLocal) {
            t = locals(i)
            frame[frameIndex++] = t
            if (t == Frame.LONG || t == Frame.DOUBLE) {
                ++i
            }
        }
        for (i = 0 i < stacks.length ++i) {
            t = stacks(i)
            frame[frameIndex++] = t
            if (t == Frame.LONG || t == Frame.DOUBLE) {
                ++i
            }
        }
        endFrame()
    }*/

    private def visitImplicitFirstFrame(): Unit = ???/*{
        // There can be at most descriptor.length() + 1 locals
        int frameIndex = startFrame(0, descriptor.length() + 1, 0)
        if ((access & Opcodes.ACC_STATIC) == 0) {
            if ((access & ACC_CONSTRUCTOR) == 0) {
                frame[frameIndex++] = Frame.OBJECT | cw.addType(cw.thisName)
            } else {
                frame[frameIndex++] = 6 // Opcodes.UNINITIALIZED_THIS
            }
        }
        int i = 1
        loop: while (true) {
            int j = i
            switch (descriptor.charAt(i++)) {
            case 'Z':
            case 'C':
            case 'B':
            case 'S':
            case 'I':
                frame[frameIndex++] = 1 // Opcodes.INTEGER
                break
            case 'F':
                frame[frameIndex++] = 2 // Opcodes.FLOAT
                break
            case 'J':
                frame[frameIndex++] = 4 // Opcodes.LONG
                break
            case 'D':
                frame[frameIndex++] = 3 // Opcodes.DOUBLE
                break
            case '[':
                while (descriptor.charAt(i) == '[') {
                    ++i
                }
                if (descriptor.charAt(i) == 'L') {
                    ++i
                    while (descriptor.charAt(i) != '') {
                        ++i
                    }
                }
                frame[frameIndex++] = Frame.OBJECT
                        | cw.addType(descriptor.substring(j, ++i))
                break
            case 'L':
                while (descriptor.charAt(i) != '') {
                    ++i
                }
                frame[frameIndex++] = Frame.OBJECT
                        | cw.addType(descriptor.substring(j + 1, i++))
                break
            default:
                break loop
            }
        }
        frame[1] = frameIndex - 3
        endFrame()
    }*/

    private def startFrame(offset: Int, nLocal: Int, nStack: Int): Int = ???/*{
        int n = 3 + nLocal + nStack
        if (frame == null || frame.length < n) {
            frame = new int[n]
        }
        frame[0] = offset
        frame[1] = nLocal
        frame[2] = nStack
        return 3
    }*/

    private def endFrame(): Unit = ???/*{
        if (previousFrame != null) { // do not write the first frame
            if (stackMap == null) {
                stackMap = new ByteVector()
            }
            writeFrame()
            ++frameCount
        }
        previousFrame = frame
        frame = null
    }*/

    private def writeFrame(): Unit = ???/*{
        int clocalsSize = frame[1]
        int cstackSize = frame[2]
        if ((cw.version & 0xFFFF) < Opcodes.V1_6) {
            stackMap.putShort(frame[0]).putShort(clocalsSize)
            writeFrameTypes(3, 3 + clocalsSize)
            stackMap.putShort(cstackSize)
            writeFrameTypes(3 + clocalsSize, 3 + clocalsSize + cstackSize)
            return
        }
        int localsSize = previousFrame[1]
        int type = FULL_FRAME
        int k = 0
        int delta
        if (frameCount == 0) {
            delta = frame[0]
        } else {
            delta = frame[0] - previousFrame[0] - 1
        }
        if (cstackSize == 0) {
            k = clocalsSize - localsSize
            switch (k) {
            case -3:
            case -2:
            case -1:
                type = CHOP_FRAME
                localsSize = clocalsSize
                break
            case 0:
                type = delta < 64 ? SAME_FRAME : SAME_FRAME_EXTENDED
                break
            case 1:
            case 2:
            case 3:
                type = APPEND_FRAME
                break
            }
        } else if (clocalsSize == localsSize && cstackSize == 1) {
            type = delta < 63 ? SAME_LOCALS_1_STACK_ITEM_FRAME
                    : SAME_LOCALS_1_STACK_ITEM_FRAME_EXTENDED
        }
        if (type != FULL_FRAME) {
            // verify if locals are the same
            int l = 3
            for (int j = 0 j < localsSize j++) {
                if (frame[l] != previousFrame[l]) {
                    type = FULL_FRAME
                    break
                }
                l++
            }
        }
        switch (type) {
        case SAME_FRAME:
            stackMap.putByte(delta)
            break
        case SAME_LOCALS_1_STACK_ITEM_FRAME:
            stackMap.putByte(SAME_LOCALS_1_STACK_ITEM_FRAME + delta)
            writeFrameTypes(3 + clocalsSize, 4 + clocalsSize)
            break
        case SAME_LOCALS_1_STACK_ITEM_FRAME_EXTENDED:
            stackMap.putByte(SAME_LOCALS_1_STACK_ITEM_FRAME_EXTENDED).putShort(
                    delta)
            writeFrameTypes(3 + clocalsSize, 4 + clocalsSize)
            break
        case SAME_FRAME_EXTENDED:
            stackMap.putByte(SAME_FRAME_EXTENDED).putShort(delta)
            break
        case CHOP_FRAME:
            stackMap.putByte(SAME_FRAME_EXTENDED + k).putShort(delta)
            break
        case APPEND_FRAME:
            stackMap.putByte(SAME_FRAME_EXTENDED + k).putShort(delta)
            writeFrameTypes(3 + localsSize, 3 + clocalsSize)
            break
        // case FULL_FRAME:
        default:
            stackMap.putByte(FULL_FRAME).putShort(delta).putShort(clocalsSize)
            writeFrameTypes(3, 3 + clocalsSize)
            stackMap.putShort(cstackSize)
            writeFrameTypes(3 + clocalsSize, 3 + clocalsSize + cstackSize)
        }
    }*/

    private def writeFrameTypes(start: Int, end: Int): Unit = ???/*{
        for (int i = start i < end ++i) {
            int t = frame(i)
            int d = t & Frame.DIM
            if (d == 0) {
                int v = t & Frame.BASE_VALUE
                switch (t & Frame.BASE_KIND) {
                case Frame.OBJECT:
                    stackMap.putByte(7).putShort(
                            cw.newClass(cw.typeTable[v].strVal1))
                    break
                case Frame.UNINITIALIZED:
                    stackMap.putByte(8).putShort(cw.typeTable[v].intVal)
                    break
                default:
                    stackMap.putByte(v)
                }
            } else {
                StringBuilder sb = new StringBuilder()
                d >>= 28
                while (d-- > 0) {
                    sb.append('[')
                }
                if ((t & Frame.BASE_KIND) == Frame.OBJECT) {
                    sb.append('L')
                    sb.append(cw.typeTable[t & Frame.BASE_VALUE].strVal1)
                    sb.append('')
                } else {
                    switch (t & 0xF) {
                    case 1:
                        sb.append('I')
                        break
                    case 2:
                        sb.append('F')
                        break
                    case 3:
                        sb.append('D')
                        break
                    case 9:
                        sb.append('Z')
                        break
                    case 10:
                        sb.append('B')
                        break
                    case 11:
                        sb.append('C')
                        break
                    case 12:
                        sb.append('S')
                        break
                    default:
                        sb.append('J')
                    }
                }
                stackMap.putByte(7).putShort(cw.newClass(sb.toString()))
            }
        }
    }*/

    private def writeFrameType(type_ : Object): Unit = ???/*{
        if (type instanceof String) {
            stackMap.putByte(7).putShort(cw.newClass((String) type))
        } else if (type instanceof Integer) {
            stackMap.putByte(((Integer) type).intValue())
        } else {
            stackMap.putByte(8).putShort(((Label) type).position)
        }
    }*/

    def getSize(): Int = ??? /*{
        if (classReaderOffset != 0) {
            return 6 + classReaderLength
        }
        int size = 8
        if (code.length > 0) {
            if (code.length > 65535) {
                String nameString = ""
                Item nameItem = cw.findItemByIndex(name)
                if (nameItem != null) nameString = nameItem.strVal1 +"'s "
                throw new RuntimeException("Method "+ nameString +"code too large!")
            }
            cw.newUTF8("Code")
            size += 18 + code.length + 8 * handlerCount
            if (localVar != null) {
                cw.newUTF8("LocalVariableTable")
                size += 8 + localVar.length
            }
            if (localVarType != null) {
                cw.newUTF8("LocalVariableTypeTable")
                size += 8 + localVarType.length
            }
            if (lineNumber != null) {
                cw.newUTF8("LineNumberTable")
                size += 8 + lineNumber.length
            }
            if (stackMap != null) {
                boolean zip = (cw.version & 0xFFFF) >= Opcodes.V1_6
                cw.newUTF8(zip ? "StackMapTable" : "StackMap")
                size += 8 + stackMap.length
            }
            if (ClassReader.ANNOTATIONS && ctanns != null) {
                cw.newUTF8("RuntimeVisibleTypeAnnotations")
                size += 8 + ctanns.getSize()
            }
            if (ClassReader.ANNOTATIONS && ictanns != null) {
                cw.newUTF8("RuntimeInvisibleTypeAnnotations")
                size += 8 + ictanns.getSize()
            }
            if (cattrs != null) {
                size += cattrs.getSize(cw, code.data, code.length, maxStack,
                        maxLocals)
            }
        }
        if (exceptionCount > 0) {
            cw.newUTF8("Exceptions")
            size += 8 + 2 * exceptionCount
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
        if (ClassReader.SIGNATURES && signature != null) {
            cw.newUTF8("Signature")
            cw.newUTF8(signature)
            size += 8
        }
        if (methodParameters != null) {
            cw.newUTF8("MethodParameters")
            size += 7 + methodParameters.length
        }
        if (ClassReader.ANNOTATIONS && annd != null) {
            cw.newUTF8("AnnotationDefault")
            size += 6 + annd.length
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
        if (ClassReader.ANNOTATIONS && panns != null) {
            cw.newUTF8("RuntimeVisibleParameterAnnotations")
            size += 7 + 2 * (panns.length - synthetics)
            for (int i = panns.length - 1 i >= synthetics --i) {
                size += panns(i) == null ? 0 : panns(i).getSize()
            }
        }
        if (ClassReader.ANNOTATIONS && ipanns != null) {
            cw.newUTF8("RuntimeInvisibleParameterAnnotations")
            size += 7 + 2 * (ipanns.length - synthetics)
            for (int i = ipanns.length - 1 i >= synthetics --i) {
                size += ipanns(i) == null ? 0 : ipanns(i).getSize()
            }
        }
        if (attrs != null) {
            size += attrs.getSize(cw, null, 0, -1, -1)
        }
        return size
    }*/

    def put(out: ByteVector): Unit = ???/*{
        int FACTOR = ClassWriter.TO_ACC_SYNTHETIC
        int mask = ACC_CONSTRUCTOR | Opcodes.ACC_DEPRECATED
                | ClassWriter.ACC_SYNTHETIC_ATTRIBUTE
                | ((access & ClassWriter.ACC_SYNTHETIC_ATTRIBUTE) / FACTOR)
        out.putShort(access & ~mask).putShort(name).putShort(desc)
        if (classReaderOffset != 0) {
            out.putByteArray(cw.cr.b, classReaderOffset, classReaderLength)
            return
        }
        int attributeCount = 0
        if (code.length > 0) {
            ++attributeCount
        }
        if (exceptionCount > 0) {
            ++attributeCount
        }
        if ((access & Opcodes.ACC_SYNTHETIC) != 0) {
            if ((cw.version & 0xFFFF) < Opcodes.V1_5
                    || (access & ClassWriter.ACC_SYNTHETIC_ATTRIBUTE) != 0) {
                ++attributeCount
            }
        }
        if ((access & Opcodes.ACC_DEPRECATED) != 0) {
            ++attributeCount
        }
        if (ClassReader.SIGNATURES && signature != null) {
            ++attributeCount
        }
        if (methodParameters != null) {
            ++attributeCount
        }
        if (ClassReader.ANNOTATIONS && annd != null) {
            ++attributeCount
        }
        if (ClassReader.ANNOTATIONS && anns != null) {
            ++attributeCount
        }
        if (ClassReader.ANNOTATIONS && ianns != null) {
            ++attributeCount
        }
        if (ClassReader.ANNOTATIONS && tanns != null) {
            ++attributeCount
        }
        if (ClassReader.ANNOTATIONS && itanns != null) {
            ++attributeCount
        }
        if (ClassReader.ANNOTATIONS && panns != null) {
            ++attributeCount
        }
        if (ClassReader.ANNOTATIONS && ipanns != null) {
            ++attributeCount
        }
        if (attrs != null) {
            attributeCount += attrs.getCount()
        }
        out.putShort(attributeCount)
        if (code.length > 0) {
            int size = 12 + code.length + 8 * handlerCount
            if (localVar != null) {
                size += 8 + localVar.length
            }
            if (localVarType != null) {
                size += 8 + localVarType.length
            }
            if (lineNumber != null) {
                size += 8 + lineNumber.length
            }
            if (stackMap != null) {
                size += 8 + stackMap.length
            }
            if (ClassReader.ANNOTATIONS && ctanns != null) {
                size += 8 + ctanns.getSize()
            }
            if (ClassReader.ANNOTATIONS && ictanns != null) {
                size += 8 + ictanns.getSize()
            }
            if (cattrs != null) {
                size += cattrs.getSize(cw, code.data, code.length, maxStack,
                        maxLocals)
            }
            out.putShort(cw.newUTF8("Code")).putInt(size)
            out.putShort(maxStack).putShort(maxLocals)
            out.putInt(code.length).putByteArray(code.data, 0, code.length)
            out.putShort(handlerCount)
            if (handlerCount > 0) {
                Handler h = firstHandler
                while (h != null) {
                    out.putShort(h.start.position).putShort(h.end.position)
                            .putShort(h.handler.position).putShort(h.type)
                    h = h.next
                }
            }
            attributeCount = 0
            if (localVar != null) {
                ++attributeCount
            }
            if (localVarType != null) {
                ++attributeCount
            }
            if (lineNumber != null) {
                ++attributeCount
            }
            if (stackMap != null) {
                ++attributeCount
            }
            if (ClassReader.ANNOTATIONS && ctanns != null) {
                ++attributeCount
            }
            if (ClassReader.ANNOTATIONS && ictanns != null) {
                ++attributeCount
            }
            if (cattrs != null) {
                attributeCount += cattrs.getCount()
            }
            out.putShort(attributeCount)
            if (localVar != null) {
                out.putShort(cw.newUTF8("LocalVariableTable"))
                out.putInt(localVar.length + 2).putShort(localVarCount)
                out.putByteArray(localVar.data, 0, localVar.length)
            }
            if (localVarType != null) {
                out.putShort(cw.newUTF8("LocalVariableTypeTable"))
                out.putInt(localVarType.length + 2).putShort(localVarTypeCount)
                out.putByteArray(localVarType.data, 0, localVarType.length)
            }
            if (lineNumber != null) {
                out.putShort(cw.newUTF8("LineNumberTable"))
                out.putInt(lineNumber.length + 2).putShort(lineNumberCount)
                out.putByteArray(lineNumber.data, 0, lineNumber.length)
            }
            if (stackMap != null) {
                boolean zip = (cw.version & 0xFFFF) >= Opcodes.V1_6
                out.putShort(cw.newUTF8(zip ? "StackMapTable" : "StackMap"))
                out.putInt(stackMap.length + 2).putShort(frameCount)
                out.putByteArray(stackMap.data, 0, stackMap.length)
            }
            if (ClassReader.ANNOTATIONS && ctanns != null) {
                out.putShort(cw.newUTF8("RuntimeVisibleTypeAnnotations"))
                ctanns.put(out)
            }
            if (ClassReader.ANNOTATIONS && ictanns != null) {
                out.putShort(cw.newUTF8("RuntimeInvisibleTypeAnnotations"))
                ictanns.put(out)
            }
            if (cattrs != null) {
                cattrs.put(cw, code.data, code.length, maxLocals, maxStack, out)
            }
        }
        if (exceptionCount > 0) {
            out.putShort(cw.newUTF8("Exceptions")).putInt(
                    2 * exceptionCount + 2)
            out.putShort(exceptionCount)
            for (int i = 0 i < exceptionCount ++i) {
                out.putShort(exceptions(i))
            }
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
        if (ClassReader.SIGNATURES && signature != null) {
            out.putShort(cw.newUTF8("Signature")).putInt(2)
                    .putShort(cw.newUTF8(signature))
        }
        if (methodParameters != null) {
            out.putShort(cw.newUTF8("MethodParameters"))
            out.putInt(methodParameters.length + 1).putByte(
                    methodParametersCount)
            out.putByteArray(methodParameters.data, 0, methodParameters.length)
        }
        if (ClassReader.ANNOTATIONS && annd != null) {
            out.putShort(cw.newUTF8("AnnotationDefault"))
            out.putInt(annd.length)
            out.putByteArray(annd.data, 0, annd.length)
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
        if (ClassReader.ANNOTATIONS && panns != null) {
            out.putShort(cw.newUTF8("RuntimeVisibleParameterAnnotations"))
            AnnotationWriter.put(panns, synthetics, out)
        }
        if (ClassReader.ANNOTATIONS && ipanns != null) {
            out.putShort(cw.newUTF8("RuntimeInvisibleParameterAnnotations"))
            AnnotationWriter.put(ipanns, synthetics, out)
        }
        if (attrs != null) {
            attrs.put(cw, null, 0, -1, -1, out)
        }
    }*/

    private def resizeInstructions(): Unit = ???/*{
        byte[] b = code.data // bytecode of the method
        int u, v, label // indexes in b
        int i, j // loop indexes

        int[] allIndexes = new int[0] // copy of indexes
        int[] allSizes = new int[0] // copy of sizes
        boolean[] resize // instructions to be resized
        int newOffset // future offset of a jump instruction

        resize = new boolean[code.length]

        // 3 = loop again, 2 = loop ended, 1 = last pass, 0 = done
        int state = 3
        do {
            if (state == 3) {
                state = 2
            }
            u = 0
            while (u < b.length) {
                int opcode = b[u] & 0xFF // opcode of current instruction
                int insert = 0 // bytes to be added after this instruction

                switch (ClassWriter.TYPE[opcode]) {
                case ClassWriter.NOARG_INSN:
                case ClassWriter.IMPLVAR_INSN:
                    u += 1
                    break
                case ClassWriter.LABEL_INSN:
                    if (opcode > 201) {
                        // converts temporary opcodes 202 to 217, 218 and
                        // 219 to IFEQ ... JSR (inclusive), IFNULL and
                        // IFNONNULL
                        opcode = opcode < 218 ? opcode - 49 : opcode - 20
                        label = u + readUnsignedShort(b, u + 1)
                    } else {
                        label = u + readShort(b, u + 1)
                    }
                    newOffset = getNewOffset(allIndexes, allSizes, u, label)
                    if (newOffset < Short.MIN_VALUE
                            || newOffset > Short.MAX_VALUE) {
                        if (!resize[u]) {
                            if (opcode == Opcodes.GOTO || opcode == Opcodes.JSR) {
                                // two additional bytes will be required to
                                // replace this GOTO or JSR instruction with
                                // a GOTO_W or a JSR_W
                                insert = 2
                            } else {
                                // five additional bytes will be required to
                                // replace this IFxxx <l> instruction with
                                // IFNOTxxx <l'> GOTO_W <l>, where IFNOTxxx
                                // is the "opposite" opcode of IFxxx (i.e.,
                                // IFNE for IFEQ) and where <l'> designates
                                // the instruction just after the GOTO_W.
                                insert = 5
                            }
                            resize[u] = true
                        }
                    }
                    u += 3
                    break
                case ClassWriter.LABELW_INSN:
                    u += 5
                    break
                case ClassWriter.TABL_INSN:
                    if (state == 1) {
                        // true number of bytes to be added (or removed)
                        // from this instruction = (future number of padding
                        // bytes - current number of padding byte) -
                        // previously over estimated variation =
                        // = ((3 - newOffset%4) - (3 - u%4)) - u%4
                        // = (-newOffset%4 + u%4) - u%4
                        // = -(newOffset & 3)
                        newOffset = getNewOffset(allIndexes, allSizes, 0, u)
                        insert = -(newOffset & 3)
                    } else if (!resize[u]) {
                        // over estimation of the number of bytes to be
                        // added to this instruction = 3 - current number
                        // of padding bytes = 3 - (3 - u%4) = u%4 = u & 3
                        insert = u & 3
                        resize[u] = true
                    }
                    // skips instruction
                    u = u + 4 - (u & 3)
                    u += 4 * (readInt(b, u + 8) - readInt(b, u + 4) + 1) + 12
                    break
                case ClassWriter.LOOK_INSN:
                    if (state == 1) {
                        // like TABL_INSN
                        newOffset = getNewOffset(allIndexes, allSizes, 0, u)
                        insert = -(newOffset & 3)
                    } else if (!resize[u]) {
                        // like TABL_INSN
                        insert = u & 3
                        resize[u] = true
                    }
                    // skips instruction
                    u = u + 4 - (u & 3)
                    u += 8 * readInt(b, u + 4) + 8
                    break
                case ClassWriter.WIDE_INSN:
                    opcode = b[u + 1] & 0xFF
                    if (opcode == Opcodes.IINC) {
                        u += 6
                    } else {
                        u += 4
                    }
                    break
                case ClassWriter.VAR_INSN:
                case ClassWriter.SBYTE_INSN:
                case ClassWriter.LDC_INSN:
                    u += 2
                    break
                case ClassWriter.SHORT_INSN:
                case ClassWriter.LDCW_INSN:
                case ClassWriter.FIELDORMETH_INSN:
                case ClassWriter.TYPE_INSN:
                case ClassWriter.IINC_INSN:
                    u += 3
                    break
                case ClassWriter.ITFMETH_INSN:
                case ClassWriter.INDYMETH_INSN:
                    u += 5
                    break
                // case ClassWriter.MANA_INSN:
                default:
                    u += 4
                    break
                }
                if (insert != 0) {
                    // adds a new (u, insert) entry in the allIndexes and
                    // allSizes arrays
                    int[] newIndexes = new int[allIndexes.length + 1]
                    int[] newSizes = new int[allSizes.length + 1]
                    System.arraycopy(allIndexes, 0, newIndexes, 0,
                            allIndexes.length)
                    System.arraycopy(allSizes, 0, newSizes, 0, allSizes.length)
                    newIndexes[allIndexes.length] = u
                    newSizes[allSizes.length] = insert
                    allIndexes = newIndexes
                    allSizes = newSizes
                    if (insert > 0) {
                        state = 3
                    }
                }
            }
            if (state < 3) {
                --state
            }
        } while (state != 0)

        // 2nd step:
        // copies the bytecode of the method into a new bytevector, updates the
        // offsets, and inserts (or removes) bytes as requested.

        ByteVector newCode = new ByteVector(code.length)

        u = 0
        while (u < code.length) {
            int opcode = b[u] & 0xFF
            switch (ClassWriter.TYPE[opcode]) {
            case ClassWriter.NOARG_INSN:
            case ClassWriter.IMPLVAR_INSN:
                newCode.putByte(opcode)
                u += 1
                break
            case ClassWriter.LABEL_INSN:
                if (opcode > 201) {
                    // changes temporary opcodes 202 to 217 (inclusive), 218
                    // and 219 to IFEQ ... JSR (inclusive), IFNULL and
                    // IFNONNULL
                    opcode = opcode < 218 ? opcode - 49 : opcode - 20
                    label = u + readUnsignedShort(b, u + 1)
                } else {
                    label = u + readShort(b, u + 1)
                }
                newOffset = getNewOffset(allIndexes, allSizes, u, label)
                if (resize[u]) {
                    // replaces GOTO with GOTO_W, JSR with JSR_W and IFxxx
                    // <l> with IFNOTxxx <l'> GOTO_W <l>, where IFNOTxxx is
                    // the "opposite" opcode of IFxxx (i.e., IFNE for IFEQ)
                    // and where <l'> designates the instruction just after
                    // the GOTO_W.
                    if (opcode == Opcodes.GOTO) {
                        newCode.putByte(200) // GOTO_W
                    } else if (opcode == Opcodes.JSR) {
                        newCode.putByte(201) // JSR_W
                    } else {
                        newCode.putByte(opcode <= 166 ? ((opcode + 1) ^ 1) - 1
                                : opcode ^ 1)
                        newCode.putShort(8) // jump offset
                        newCode.putByte(200) // GOTO_W
                        // newOffset now computed from start of GOTO_W
                        newOffset -= 3
                    }
                    newCode.putInt(newOffset)
                } else {
                    newCode.putByte(opcode)
                    newCode.putShort(newOffset)
                }
                u += 3
                break
            case ClassWriter.LABELW_INSN:
                label = u + readInt(b, u + 1)
                newOffset = getNewOffset(allIndexes, allSizes, u, label)
                newCode.putByte(opcode)
                newCode.putInt(newOffset)
                u += 5
                break
            case ClassWriter.TABL_INSN:
                // skips 0 to 3 padding bytes
                v = u
                u = u + 4 - (v & 3)
                // reads and copies instruction
                newCode.putByte(Opcodes.TABLESWITCH)
                newCode.putByteArray(null, 0, (4 - newCode.length % 4) % 4)
                label = v + readInt(b, u)
                u += 4
                newOffset = getNewOffset(allIndexes, allSizes, v, label)
                newCode.putInt(newOffset)
                j = readInt(b, u)
                u += 4
                newCode.putInt(j)
                j = readInt(b, u) - j + 1
                u += 4
                newCode.putInt(readInt(b, u - 4))
                for ( j > 0 --j) {
                    label = v + readInt(b, u)
                    u += 4
                    newOffset = getNewOffset(allIndexes, allSizes, v, label)
                    newCode.putInt(newOffset)
                }
                break
            case ClassWriter.LOOK_INSN:
                // skips 0 to 3 padding bytes
                v = u
                u = u + 4 - (v & 3)
                // reads and copies instruction
                newCode.putByte(Opcodes.LOOKUPSWITCH)
                newCode.putByteArray(null, 0, (4 - newCode.length % 4) % 4)
                label = v + readInt(b, u)
                u += 4
                newOffset = getNewOffset(allIndexes, allSizes, v, label)
                newCode.putInt(newOffset)
                j = readInt(b, u)
                u += 4
                newCode.putInt(j)
                for ( j > 0 --j) {
                    newCode.putInt(readInt(b, u))
                    u += 4
                    label = v + readInt(b, u)
                    u += 4
                    newOffset = getNewOffset(allIndexes, allSizes, v, label)
                    newCode.putInt(newOffset)
                }
                break
            case ClassWriter.WIDE_INSN:
                opcode = b[u + 1] & 0xFF
                if (opcode == Opcodes.IINC) {
                    newCode.putByteArray(b, u, 6)
                    u += 6
                } else {
                    newCode.putByteArray(b, u, 4)
                    u += 4
                }
                break
            case ClassWriter.VAR_INSN:
            case ClassWriter.SBYTE_INSN:
            case ClassWriter.LDC_INSN:
                newCode.putByteArray(b, u, 2)
                u += 2
                break
            case ClassWriter.SHORT_INSN:
            case ClassWriter.LDCW_INSN:
            case ClassWriter.FIELDORMETH_INSN:
            case ClassWriter.TYPE_INSN:
            case ClassWriter.IINC_INSN:
                newCode.putByteArray(b, u, 3)
                u += 3
                break
            case ClassWriter.ITFMETH_INSN:
            case ClassWriter.INDYMETH_INSN:
                newCode.putByteArray(b, u, 5)
                u += 5
                break
            // case MANA_INSN:
            default:
                newCode.putByteArray(b, u, 4)
                u += 4
                break
            }
        }

        // updates the stack map frame labels
        if (compute == FRAMES) {
            Label l = labels
            while (l != null) {
                u = l.position - 3
                if (u >= 0 && resize[u]) {
                    l.status |= Label.TARGET
                }
                getNewOffset(allIndexes, allSizes, l)
                l = l.successor
            }
            // Update the offsets in the uninitialized types
            if (cw.typeTable != null) {
                for (i = 0 i < cw.typeTable.length ++i) {
                    Item item = cw.typeTable(i)
                    if (item != null && item.type == ClassWriter.TYPE_UNINIT) {
                        item.intVal = getNewOffset(allIndexes, allSizes, 0,
                                item.intVal)
                    }
                }
            }
            // The stack map frames are not serialized yet, so we don't need
            // to update them. They will be serialized in visitMaxs.
        } else if (frameCount > 0) {
            cw.invalidFrames = true
        }
        // updates the exception handler block labels
        Handler h = firstHandler
        while (h != null) {
            getNewOffset(allIndexes, allSizes, h.start)
            getNewOffset(allIndexes, allSizes, h.end)
            getNewOffset(allIndexes, allSizes, h.handler)
            h = h.next
        }
        // updates the instructions addresses in the
        // local var and line number tables
        for (i = 0 i < 2 ++i) {
            ByteVector bv = i == 0 ? localVar : localVarType
            if (bv != null) {
                b = bv.data
                u = 0
                while (u < bv.length) {
                    label = readUnsignedShort(b, u)
                    newOffset = getNewOffset(allIndexes, allSizes, 0, label)
                    writeShort(b, u, newOffset)
                    label += readUnsignedShort(b, u + 2)
                    newOffset = getNewOffset(allIndexes, allSizes, 0, label)
                            - newOffset
                    writeShort(b, u + 2, newOffset)
                    u += 10
                }
            }
        }
        if (lineNumber != null) {
            b = lineNumber.data
            u = 0
            while (u < lineNumber.length) {
                writeShort(
                        b,
                        u,
                        getNewOffset(allIndexes, allSizes, 0,
                                readUnsignedShort(b, u)))
                u += 4
            }
        }
        // updates the labels of the other attributes
        Attribute attr = cattrs
        while (attr != null) {
            Label[] labels = attr.getLabels()
            if (labels != null) {
                for (i = labels.length - 1 i >= 0 --i) {
                    getNewOffset(allIndexes, allSizes, labels(i))
                }
            }
            attr = attr.next
        }

        // replaces old bytecodes with new ones
        code = newCode
    }*/
}

object MethodWriter {
    final val ACC_CONSTRUCTOR = 0x80000
    final val SAME_FRAME = 0
    final val SAME_LOCALS_1_STACK_ITEM_FRAME = 64
    final val RESERVED = 128
    final val SAME_LOCALS_1_STACK_ITEM_FRAME_EXTENDED = 247
    final val CHOP_FRAME = 248
    final val SAME_FRAME_EXTENDED = 251
    final val APPEND_FRAME = 252
    final val FULL_FRAME = 255
    final val FRAMES = 0
    final val MAXS = 1
    final val NOTHING = 2

    def readUnsignedShort(b: Array[Byte], index: Int): Int =
        ((b(index) & 0xFF) << 8) | (b(index + 1) & 0xFF)

    def readShort(b: Array[Byte], index: Int): Short =
        (((b(index) & 0xFF) << 8) | (b(index + 1) & 0xFF)).toShort

    def readInt(b: Array[Byte], index: Int) =
        ((b(index) & 0xFF) << 24) | ((b(index + 1) & 0xFF) << 16) | ((b(index + 2) & 0xFF) << 8) | (b(index + 3) & 0xFF)

    def writeShort(b: Array[Byte], index: Int, s: Int): Unit = {
        b(index) = (s >>> 8).toByte
        b(index + 1) = s.toByte
    }

    def getNewOffset(indexes: Array[Int], sizes: Array[Int], begin: Int, end: Int): Int = {
        var offset = end - begin
        var i = 0
        while (i < indexes.length) {
            if (begin < indexes(i) && indexes(i) <= end) {
                // forward jump
                offset += sizes(i)
            } else if (end < indexes(i) && indexes(i) <= begin) {
                // backward jump
                offset -= sizes(i)
            }
            i += 1
        }
        offset
    }

    def getNewOffset(indexes: Array[Int], sizes: Array[Int], label: Label): Unit = {
        if ((label.status & Label.RESIZED) == 0) {
            label.position = getNewOffset(indexes, sizes, 0, label.position)
            label.status |= Label.RESIZED
        }
    }
}
