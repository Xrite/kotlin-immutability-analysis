package test.test.reasons.mutable

import com.beust.klaxon.json
import org.jetbrains.kotlin.descriptors.ClassifierDescriptor
import test.test.reasons.MutableReason
import test.test.reasons.basicInfo

class MayHaveMutableSubclass(val classifierDescriptor: ClassifierDescriptor) : MutableReason() {
    override val csvData: CSVData
        get() = object : CSVData {
            override val reason: String
                get() = "May have mutable subclass"
            override val info: String
                get() = json {
                    obj(*classifierDescriptor.basicInfo)
                }.toJsonString(true)
        }
}
