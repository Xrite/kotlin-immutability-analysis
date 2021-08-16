package test.test.dependencies

import org.jetbrains.kotlin.descriptors.ClassifierDescriptor
import org.jetbrains.kotlin.types.KotlinType
import test.test.Dependency
import test.test.ImmutabilityMap
import test.test.ImmutabilityProperty
import test.test.reasons.ConditionallyDeeplyImmutableReason
import test.test.reasons.MutableReason
import test.test.reasons.ShallowImmutableReason

data class Parent(
    val descriptor: ClassifierDescriptor,
    val type: KotlinType,
    val debug: List<Any?> = listOf()
) :
    Dependency() {
    companion object {
        fun fromKotlinType(kotlinType: KotlinType): Dependency =
            kotlinType.constructor.declarationDescriptor?.let { Parent(it, kotlinType) }
                ?: Error("Parent doesn't have ClassifierDescriptor, type: $kotlinType")
    }

    override fun recalculate(resolve: (KotlinType) -> ImmutabilityMap.Result): ImmutabilityProperty {
        val parent = descriptor.toString()
        return when (val status = resolve(type)) {
            is ImmutabilityMap.Result.ConditionallyDeeplyImmutable -> {
                val reason = when (status.reason) {
                    ImmutabilityMap.Result.Reason.ASSUMPTION -> ConditionallyDeeplyImmutableReason.ParentTypeConditionallyDeeplyImmutable(
                        true
                    )
                    ImmutabilityMap.Result.Reason.UNKNOWN -> throw IllegalArgumentException("ConditionallyDeeplyImmutable unknown")
                    ImmutabilityMap.Result.Reason.RESOLVED -> ConditionallyDeeplyImmutableReason.ParentTypeConditionallyDeeplyImmutable(
                        false
                    )
                }
                ImmutabilityProperty.ConditionallyDeeplyImmutable(status.conditions, reason)
            }
            is ImmutabilityMap.Result.Immutable -> ImmutabilityProperty.Immutable()
            is ImmutabilityMap.Result.Mutable -> {
                val reason = when (status.reason) {
                    ImmutabilityMap.Result.Reason.ASSUMPTION -> MutableReason.ParentType(
                        MutableReason.ParentType.Type.MUTABLE_BY_ASSUMPTION,
                        parent
                    )
                    ImmutabilityMap.Result.Reason.UNKNOWN -> MutableReason.ParentType(
                        MutableReason.ParentType.Type.UNKNOWN,
                        parent
                    )
                    ImmutabilityMap.Result.Reason.RESOLVED -> MutableReason.ParentType(
                        MutableReason.ParentType.Type.MUTABLE,
                        parent
                    )
                }
                ImmutabilityProperty.Mutable(reason)
            }
            is ImmutabilityMap.Result.ShallowImmutable -> {
                val reason = when (status.reason) {
                    ImmutabilityMap.Result.Reason.ASSUMPTION -> ShallowImmutableReason.ParentTypeShallowImmutable(
                        true,
                        parent
                    )
                    ImmutabilityMap.Result.Reason.UNKNOWN -> throw IllegalArgumentException("ShallowImmutable unknown")
                    ImmutabilityMap.Result.Reason.RESOLVED -> ShallowImmutableReason.ParentTypeShallowImmutable(
                        false,
                        parent
                    )
                }
                ImmutabilityProperty.ShallowImmutable(reason)
            }
        }
    }
}
