package test.test.reasons.mutable

import com.beust.klaxon.json
import org.jetbrains.kotlin.descriptors.DeclarationDescriptor
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameSafe
import test.test.reasons.MutableReason

class OuterClassMutable(val type: Type, val outerDescriptor: DeclarationDescriptor) : MutableReason() {
    override val csvData = object : CSVData {
        override val reason: String
            get() = when(type) {
                Type.MUTABLE_BY_ASSUMPTION -> "Outer class mutable (assumption)"
                Type.MUTABLE -> "Outer class mutable"
            }
        override val info: String
            get() = json {
                obj(
                    "descriptor" to outerDescriptor.toString(),
                    "fqName" to outerDescriptor.fqNameSafe.asString()
                )
            }.toJsonString(true)
    }
    enum class Type {
        MUTABLE_BY_ASSUMPTION,
        MUTABLE,
    }
}
