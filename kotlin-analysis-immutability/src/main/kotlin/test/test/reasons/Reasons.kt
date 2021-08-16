package test.test.reasons

import org.jetbrains.kotlin.descriptors.PropertyDescriptor
import org.jetbrains.kotlin.idea.util.string.collapseSpaces

sealed class Reason {
    open fun isByAssumption(): Boolean = false
    open val csvData = object : CSVData {
        override val reason = "None"
        override val info = "None"
    }

    interface CSVData {
        val reason: String
        val info: String
            get() = ""
    }
}

sealed class ImmutableReason : Reason() {
    object ByAssumption : ImmutableReason() {
        override fun isByAssumption() = true
        override val csvData = object : CSVData {
            override val reason = "Assumption"
        }
    }

    object Initial : ImmutableReason() {
        override val csvData = object : CSVData {
            override val reason = "Initial"
        }
    }
}

sealed class ShallowImmutableReason : Reason() {
    class ParentTypeShallowImmutable(val byAssumption: Boolean, val parentType: String) : ShallowImmutableReason() {
        override val csvData = object : CSVData {
            override val reason = "Parent type shallow immutable"
            override val info: String
                get() = parentType
        }
    }
    class OuterClassShallowImmutable(val byAssumption: Boolean, val outerClass: String) : ShallowImmutableReason() {
        override val csvData = object : CSVData {
            override val reason = "Outer class shallow immutable"
            override val info: String
                get() = outerClass
        }
    }
    class ValProperty(val type: Type, val isParameter: Boolean, val property: String) : ShallowImmutableReason() {
        override val csvData = object : CSVData {
            override val reason: String
                get() {
                    val valType = if (isParameter) "(parameter)" else ""
                    return when (type) {
                        Type.MUTABLE_BY_ASSUMPTION -> "val $valType refers to mutable type (assumption)"
                        Type.SHALLOW_IMMUTABLE_BY_ASSUMPTION -> "val $valType refers to shallow immutable type (assumption)"
                        Type.UNKNOWN -> "val $valType refers to unknown type"
                        Type.MUTABLE -> "val $valType refers to mutable type"
                        Type.SHALLOW_IMMUTABLE -> "val $valType refers to shallow immutable type"
                    }.collapseSpaces()
                }
            override val info: String
                get() = property
        }
    }

    object ByAssumption : ShallowImmutableReason() {
        override fun isByAssumption(): Boolean = true
        override val csvData = object : CSVData {
            override val reason = "Assumption"
        }
    }

    enum class Type {
        MUTABLE_BY_ASSUMPTION,
        SHALLOW_IMMUTABLE_BY_ASSUMPTION,
        UNKNOWN,
        MUTABLE,
        SHALLOW_IMMUTABLE
    }
}

sealed class ConditionallyDeeplyImmutableReason : Reason() {
    class ParentTypeConditionallyDeeplyImmutable(val byAssumption: Boolean) : ConditionallyDeeplyImmutableReason() {
        override val csvData = object : CSVData {
            override val reason: String
                get() {
                    val assumption = if (byAssumption) " (assumption)" else ""
                    return "Parent type conditionally deeply immutable $assumption".collapseSpaces()
                }
        }
    }
    class OuterClassTypeConditionallyDeeplyImmutable(val byAssumption: Boolean) :
        ConditionallyDeeplyImmutableReason() {
        override val csvData = object : CSVData {
            override val reason: String
                get() {
                    val assumption = if (byAssumption) " (assumption)" else ""
                    return "Outer class conditionally deeply immutable $assumption".collapseSpaces()
                }
        }
    }

    class ValPropertyConditionallyDeeplyImmutable(val byAssumption: Boolean, val isParameter: Boolean) :
        ConditionallyDeeplyImmutableReason() {
        override val csvData = object : CSVData {
            override val reason: String
                get() {
                    val valType = if (isParameter) "(parameter)" else ""
                    val assumption = if (byAssumption) "(assumption)" else ""
                    return "val $valType refers to conditionally deeply immutable type $assumption".collapseSpaces()
                }
        }
    }

    object ByAssumption : ConditionallyDeeplyImmutableReason() {
        override fun isByAssumption(): Boolean = true
    }
}

sealed class MutableReason : Reason() {
    class ParentType(val type: Type, val parentType: String) : MutableReason() {
        override val csvData = object : CSVData {
            override val reason: String
                get() = when (type) {
                    Type.MUTABLE_BY_ASSUMPTION -> "Parent type mutable (assumption)"
                    Type.UNKNOWN -> "Parent type unknown"
                    Type.MUTABLE -> "Parent type mutable"
                }
            override val info: String
                get() = parentType
        }
        enum class Type {
            MUTABLE_BY_ASSUMPTION,
            UNKNOWN,
            MUTABLE,
        }
    }
    class OuterClass(val type: Type, val outerClass: String) : MutableReason() {
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

    class VarProperty(val isParameter: Boolean, val d: String) : MutableReason() {
        override val csvData = object : CSVData {
            override val reason: String
                get() {
                    val varType = if (isParameter) "(parameter)" else ""
                    return "var $varType".collapseSpaces()
                }
            override val info: String
                get() = d
        }
    }
    object Error : MutableReason() {
        override val csvData = object : CSVData {
            override val reason = "Error"
        }
    }

    class DelegatedProperty(val property: String) : MutableReason() {
        override val csvData = object : CSVData {
            override val reason = "Delegated property"
            override val info: String
                get() = property
        }
    }
    object PropertyWithGetter : MutableReason() {
        override val csvData = object : CSVData {
            override val reason = "Property with getter"
        }
    }
    object ByAssumption : MutableReason() {
        override fun isByAssumption(): Boolean = true
        override val csvData = object : CSVData {
            override val reason = "Assumption"
        }
    }
}
