package test.test.reasons

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
}

sealed class ShallowImmutableReason : Reason() {
    class ParentTypeShallowImmutable(val byAssumption: Boolean) : ShallowImmutableReason() {
        override val csvData = object : CSVData {
            override val reason = "Parent type shallow immutable"
        }
    }
    class OuterClassShallowImmutable(val byAssumption: Boolean) : ShallowImmutableReason() {
        override val csvData = object : CSVData {
            override val reason = "Outer class shallow immutable"
        }
    }
    class ValProperty(val type: Type, val isParameter: Boolean) : ShallowImmutableReason() {
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
    class ParentType(val type: Type) : MutableReason() {
        override val csvData = object : CSVData {
            override val reason: String
                get() = when (type) {
                    Type.MUTABLE_BY_ASSUMPTION -> "Parent type mutable (assumption)"
                    Type.UNKNOWN -> "Parent type unknown"
                    Type.MUTABLE -> "Parent type mutable"
                }
        }
        enum class Type {
            MUTABLE_BY_ASSUMPTION,
            UNKNOWN,
            MUTABLE,
        }
    }
    class OuterClass(val type: Type) : MutableReason() {
        override val csvData = object : CSVData {
            override val reason: String
                get() = when(type) {
                    Type.MUTABLE_BY_ASSUMPTION -> "Outer class mutable (assumption)"
                    Type.MUTABLE -> "Outer class mutable"
                }
        }
        enum class Type {
            MUTABLE_BY_ASSUMPTION,
            MUTABLE,
        }
    }

    class VarProperty(val isParameter: Boolean) : MutableReason() {
        override val csvData = object : CSVData {
            override val reason: String
                get() {
                    val varType = if (isParameter) "(parameter)" else ""
                    return "var $varType".collapseSpaces()
                }
        }
    }
    object Error : MutableReason() {
        override val csvData = object : CSVData {
            override val reason = "Error"
        }
    }
    object DelegatedProperty : MutableReason() {
        override val csvData = object : CSVData {
            override val reason = "Delegated property"
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
