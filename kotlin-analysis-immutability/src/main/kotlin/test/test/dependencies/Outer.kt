package test.test.dependencies

import org.jetbrains.kotlin.descriptors.ClassifierDescriptor
import test.test.Dependency
import test.test.ImmutabilityProperty
import test.test.ImmutabilityWithContext
import test.test.ImmutabilityWithContext.Result.*
import test.test.ImmutabilityWithContext.Result.ConditionallyDeeplyImmutable.*
import test.test.reasons.shallow_immutable.OuterClassShallowImmutable
import test.test.reasons.mutable.OuterClassMutable
import test.test.reasons.conditionally_deeply_immutable.OuterClassTypeConditionallyDeeplyImmutable

data class Outer(
    val descriptor: ClassifierDescriptor,
    val debug: List<Any?> = listOf()
) : Dependency() {
    override fun recalculate(immutability: ImmutabilityWithContext): ImmutabilityProperty {
        val outer = descriptor.toString()
        return when (val status = immutability(descriptor.defaultType)) {
            is ConditionallyDeeplyImmutable -> {
                val reason = when (status.reason) {
                    Reason.ASSUMPTION -> OuterClassTypeConditionallyDeeplyImmutable(
                        true
                    )
                    Reason.RESOLVED -> OuterClassTypeConditionallyDeeplyImmutable(
                        false
                    )
                }
                ImmutabilityProperty.ConditionallyDeeplyImmutable(status.conditions, reason)
            }
            is Immutable -> ImmutabilityProperty.Immutable()
            is Mutable -> {
                val reason = when (status.reason) {
                    Mutable.Reason.ASSUMPTION -> OuterClassMutable(
                        OuterClassMutable.Type.MUTABLE_BY_ASSUMPTION,
                        outer
                    )
                    Mutable.Reason.UNKNOWN -> throw IllegalArgumentException("Outer class unknown")
                    Mutable.Reason.RESOLVED -> OuterClassMutable(
                        OuterClassMutable.Type.MUTABLE,
                        outer
                    )
                }
                ImmutabilityProperty.Mutable(reason)
            }
            is ShallowImmutable -> {
                val reason = when (status.reason) {
                    ShallowImmutable.Reason.ASSUMPTION -> OuterClassShallowImmutable(
                        true,
                        outer
                    )
                    ShallowImmutable.Reason.RESOLVED -> OuterClassShallowImmutable(
                        false,
                        outer
                    )
                }
                ImmutabilityProperty.ShallowImmutable(reason)
            }
        }
    }
}
