package test.test.dependencies

import org.jetbrains.kotlin.descriptors.ClassifierDescriptor
import org.jetbrains.kotlin.types.KotlinType
import test.test.Dependency
import test.test.ImmutabilityMap
import test.test.ImmutabilityProperty
import test.test.reasons.shallow_immutable.OuterClassShallowImmutable
import test.test.reasons.mutable.OuterClassMutable
import test.test.reasons.conditionally_deeply_immutable.OuterClassTypeConditionallyDeeplyImmutable

data class Outer(
    val descriptor: ClassifierDescriptor,
    val debug: List<Any?> = listOf()
) : Dependency() {
    override fun recalculate(resolve: (KotlinType) -> ImmutabilityMap.Result): ImmutabilityProperty {
        val outer = descriptor.toString()
        return when (val status = resolve(descriptor.defaultType)) {
            is ImmutabilityMap.Result.ConditionallyDeeplyImmutable -> {
                val reason = when (status.reason) {
                    ImmutabilityMap.Result.ConditionallyDeeplyImmutable.Reason.ASSUMPTION -> OuterClassTypeConditionallyDeeplyImmutable(
                        true
                    )
                    ImmutabilityMap.Result.ConditionallyDeeplyImmutable.Reason.RESOLVED -> OuterClassTypeConditionallyDeeplyImmutable(
                        false
                    )
                }
                ImmutabilityProperty.ConditionallyDeeplyImmutable(status.conditions, reason)
            }
            is ImmutabilityMap.Result.Immutable -> ImmutabilityProperty.Immutable()
            is ImmutabilityMap.Result.Mutable -> {
                val reason = when (status.reason) {
                    ImmutabilityMap.Result.Mutable.Reason.ASSUMPTION -> OuterClassMutable(
                        OuterClassMutable.Type.MUTABLE_BY_ASSUMPTION,
                        outer
                    )
                    ImmutabilityMap.Result.Mutable.Reason.UNKNOWN -> throw IllegalArgumentException("Outer class unknown")
                    ImmutabilityMap.Result.Mutable.Reason.RESOLVED -> OuterClassMutable(
                        OuterClassMutable.Type.MUTABLE,
                        outer
                    )
                }
                ImmutabilityProperty.Mutable(reason)
            }
            is ImmutabilityMap.Result.ShallowImmutable -> {
                val reason = when (status.reason) {
                    ImmutabilityMap.Result.ShallowImmutable.Reason.ASSUMPTION -> OuterClassShallowImmutable(
                        true,
                        outer
                    )
                    ImmutabilityMap.Result.ShallowImmutable.Reason.RESOLVED -> OuterClassShallowImmutable(
                        false,
                        outer
                    )
                }
                ImmutabilityProperty.ShallowImmutable(reason)
            }
        }
    }
}
