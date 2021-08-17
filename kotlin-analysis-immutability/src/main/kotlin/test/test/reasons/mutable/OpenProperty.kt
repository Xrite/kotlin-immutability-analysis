package test.test.reasons.mutable

import test.test.reasons.MutableReason

class OpenProperty(val name: String) : MutableReason() {
    override val csvData = object : CSVData {
        override val reason: String
            get() = "open property"
        override val info: String
            get() = name
    }
}
