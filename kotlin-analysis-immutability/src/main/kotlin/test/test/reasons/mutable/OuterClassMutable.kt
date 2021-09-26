package test.test.reasons.mutable

import org.jetbrains.kotlin.descriptors.DeclarationDescriptor
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameSafe
import test.test.reasons.MutableReason
import test.test.reasons.basicInfo

class OuterClassMutable(val type: Type, val outerDescriptor: DeclarationDescriptor) : MutableReason() {
    override val csvData = object : CSVData {
        override val reason: String
            get() = when (type) {
                Type.MUTABLE_BY_ASSUMPTION -> "Outer class mutable (assumption)"
                Type.MUTABLE -> "Outer class mutable"
            }
        override val info: Map<String, Any?>
            get() =
                mapOf(
                    "fqName" to outerDescriptor.fqNameSafe.asString(),
                    *outerDescriptor.basicInfo
                )
    }

    enum class Type {
        MUTABLE_BY_ASSUMPTION,
        MUTABLE,
    }
}
