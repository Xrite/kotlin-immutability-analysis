package test.test.reasons.mutable

import com.beust.klaxon.json
import org.jetbrains.kotlin.descriptors.ValueParameterDescriptor
import test.test.reasons.MutableReason
import test.test.reasons.basicInfo

class VarParameter(val variableDescriptor: ValueParameterDescriptor) : MutableReason() {
    override val csvData = object : CSVData {
        override val reason: String
            get() = "var (parameter)"
        override val info: String
            get() = json {
                obj(*variableDescriptor.basicInfo)
            }.toJsonString(true)
    }
}
