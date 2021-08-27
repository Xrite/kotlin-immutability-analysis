package test.test.reasons.mutable

import com.beust.klaxon.JsonObject
import com.beust.klaxon.Klaxon
import com.beust.klaxon.json
import org.jetbrains.kotlin.descriptors.VariableDescriptor
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameSafe
import test.test.reasons.MutableReason
import test.test.reasons.basicInfo

class DelegatedProperty(val descriptor: VariableDescriptor) : MutableReason() {
    override val csvData = object : CSVData {
        override val reason = "Delegated property"
        override val info: String
            get() =
                json {
                    obj(*descriptor.basicInfo)
                }.toJsonString(true)
    }
}
