package test.test.reasons.mutable

import test.test.reasons.MutableReason

class OuterClassMutable(val type: Type, val outerClass: String) : MutableReason() {
    override val csvData = object : CSVData {
        override val reason: String
            get() = when(type) {
                Type.MUTABLE_BY_ASSUMPTION -> "Outer class mutable (assumption)"
                Type.MUTABLE -> "Outer class mutable"
            }
        override val info: String
            get() = outerClass
    }
    enum class Type {
        MUTABLE_BY_ASSUMPTION,
        MUTABLE,
    }
}
