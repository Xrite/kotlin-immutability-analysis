package test.test.reasons.mutable

import com.beust.klaxon.json
import org.jetbrains.kotlin.descriptors.ClassifierDescriptor
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameSafe
import test.test.reasons.MutableReason

class ParentTypeMutable(val type: Type, val parentDescriptor: ClassifierDescriptor) : MutableReason() {
    override val csvData = object : CSVData {
        override val reason: String
            get() = when (type) {
                Type.MUTABLE_BY_ASSUMPTION -> "Parent type mutable (assumption)"
                Type.UNKNOWN -> "Parent type unknown"
                Type.MUTABLE -> "Parent type mutable"
            }
        override val info: String
            get() = json {
                obj(
                    "descriptor" to parentDescriptor.toString(),
                    "source" to parentDescriptor.source.containingFile.name,
                    "fqName" to parentDescriptor.fqNameSafe.asString()
                )
            }.toJsonString(true)
    }
    enum class Type {
        MUTABLE_BY_ASSUMPTION,
        UNKNOWN,
        MUTABLE,
    }
}
