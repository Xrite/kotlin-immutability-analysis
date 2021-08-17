package test.test.reasons.mutable

import test.test.reasons.MutableReason

class DelegatedProperty(val property: String) : MutableReason() {
    override val csvData = object : CSVData {
        override val reason = "Delegated property"
        override val info: String
            get() = property
    }
}
