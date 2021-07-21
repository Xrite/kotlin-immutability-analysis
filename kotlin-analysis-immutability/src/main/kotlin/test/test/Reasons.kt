package test.test

sealed class Reason {
    object None : Reason()

    data class Multiple(val reasons: List<Reason>) : Reason()

    data class Type(val type: ReasonType, val message: String) : Reason()
}

enum class ReasonType() {
    PARENT_TYPE_MUTABLE_ASSUMPTION,
    PARENT_TYPE_MUTABLE,
    VAR_FIELD,
    VAL_FIELD,
    PARENT_TYPE_UNKNOWN,
    PARENT_TYPE_SHALLOW_IMMUTABLE,
    VAL_FIELD_UNKNOWN,
    VAL_FIELD_MUTABLE,
    VAL_FIELD_MUTABLE_ASSUMPTION,
    VAL_FIELD_SHALLOW_MUTABLE,
    IMMUTABLE_ASSUMPTION
}

sealed class ImmutableReason : Reason() {

}

sealed class ShallowImmutableReason : Reason() {
    object ParentTypeShallowImmutable : ShallowImmutableReason()
    object ValFieldUnknown : ShallowImmutableReason()
    object ValFieldMutable : ShallowImmutableReason()
    object ValFieldMutableAssumption : ShallowImmutableReason()
    object ValFieldShallowImmutable : ShallowImmutableReason()
}

sealed class ConditionalDeeplyImmutableReason : Reason() {

}

sealed class MutableReason : Reason() {
    object ParentTypeMutableByAssumption : MutableReason()
    object ParentTypeMutable : MutableReason()
    object VarField : MutableReason()
    object ParentTypeUnknown : MutableReason()
}
