package test.test.dependencies

import org.jetbrains.kotlin.descriptors.ClassifierDescriptor
import org.jetbrains.kotlin.types.KotlinType
import test.test.Dependency
import test.test.ImmutabilityMap
import test.test.ImmutabilityProperty
import test.test.reasons.ConditionallyDeeplyImmutableReason
import test.test.reasons.MutableReason
import test.test.reasons.ShallowImmutableReason

data class Outer(
    val descriptor: ClassifierDescriptor,
    val debug: List<Any?> = listOf()
) : Dependency() {
    override fun recalculate(resolve: (KotlinType) -> ImmutabilityMap.Result): ImmutabilityProperty {
        val outer = descriptor.toString()
        return when (val status = resolve(descriptor.defaultType)) {
            is ImmutabilityMap.Result.ConditionallyDeeplyImmutable -> {
                val reason = when (status.reason) {
                    ImmutabilityMap.Result.Reason.ASSUMPTION -> ConditionallyDeeplyImmutableReason.OuterClassTypeConditionallyDeeplyImmutable(
                        true
                    )
                    ImmutabilityMap.Result.Reason.UNKNOWN -> throw IllegalArgumentException("ConditionallyDeeplyImmutable unknown")
                    ImmutabilityMap.Result.Reason.RESOLVED -> ConditionallyDeeplyImmutableReason.OuterClassTypeConditionallyDeeplyImmutable(
                        false
                    )
                }
                ImmutabilityProperty.ConditionallyDeeplyImmutable(status.conditions, reason)
            }
            is ImmutabilityMap.Result.Immutable -> ImmutabilityProperty.Immutable()
            is ImmutabilityMap.Result.Mutable -> {
                val reason = when (status.reason) {
                    ImmutabilityMap.Result.Reason.ASSUMPTION -> MutableReason.OuterClass(
                        MutableReason.OuterClass.Type.MUTABLE_BY_ASSUMPTION,
                        outer
                    )
                    ImmutabilityMap.Result.Reason.UNKNOWN -> throw IllegalArgumentException("Outer class unknown")
                    ImmutabilityMap.Result.Reason.RESOLVED -> MutableReason.OuterClass(
                        MutableReason.OuterClass.Type.MUTABLE,
                        outer
                    )
                }
                ImmutabilityProperty.Mutable(reason)
            }
            is ImmutabilityMap.Result.ShallowImmutable -> {
                val reason = when (status.reason) {
                    ImmutabilityMap.Result.Reason.ASSUMPTION -> ShallowImmutableReason.OuterClassShallowImmutable(
                        true,
                        outer
                    )
                    ImmutabilityMap.Result.Reason.UNKNOWN -> throw IllegalArgumentException("ShallowImmutable unknown")
                    ImmutabilityMap.Result.Reason.RESOLVED -> ShallowImmutableReason.OuterClassShallowImmutable(
                        false,
                        outer
                    )
                }
                ImmutabilityProperty.ShallowImmutable(reason)
            }
        }
    }
}
