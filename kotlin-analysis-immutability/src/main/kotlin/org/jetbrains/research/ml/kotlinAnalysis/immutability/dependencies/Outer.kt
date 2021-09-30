package org.jetbrains.research.ml.kotlinAnalysis.immutability.dependencies

import org.jetbrains.kotlin.descriptors.ClassifierDescriptor
import org.jetbrains.research.ml.kotlinAnalysis.immutability.Dependency
import org.jetbrains.research.ml.kotlinAnalysis.immutability.ImmutabilityProperty
import org.jetbrains.research.ml.kotlinAnalysis.immutability.ImmutabilityWithContext
import org.jetbrains.research.ml.kotlinAnalysis.immutability.ImmutabilityWithContext.Result.*
import org.jetbrains.research.ml.kotlinAnalysis.immutability.ImmutabilityWithContext.Result.ConditionallyDeeplyImmutable.*
import org.jetbrains.research.ml.kotlinAnalysis.immutability.reasons.shallow_immutable.OuterClassShallowImmutable
import org.jetbrains.research.ml.kotlinAnalysis.immutability.reasons.mutable.OuterClassMutable
import org.jetbrains.research.ml.kotlinAnalysis.immutability.reasons.conditionally_deeply_immutable.OuterClassTypeConditionallyDeeplyImmutable

data class Outer(
    val descriptor: ClassifierDescriptor,
    val debug: List<Any?> = listOf()
) : Dependency() {
    override fun recalculate(immutability: ImmutabilityWithContext): ImmutabilityProperty {
        val classifierDescriptor = descriptor
        return when (val status = immutability.resolveDescriptor(descriptor)) {
            is ConditionallyDeeplyImmutable -> {
                val reason = when (status.reason) {
                    Reason.ASSUMPTION -> OuterClassTypeConditionallyDeeplyImmutable(
                        true,
                        classifierDescriptor
                    )
                    Reason.RESOLVED -> OuterClassTypeConditionallyDeeplyImmutable(
                        false,
                        classifierDescriptor
                    )
                }
                ImmutabilityProperty.ConditionallyDeeplyImmutable(status.conditions, reason)
            }
            is Immutable -> ImmutabilityProperty.Immutable()
            is Mutable -> {
                val reason = when (status.reason) {
                    Mutable.Reason.ASSUMPTION -> OuterClassMutable(
                        OuterClassMutable.Type.MUTABLE_BY_ASSUMPTION,
                        classifierDescriptor
                    )
                    Mutable.Reason.UNKNOWN -> throw IllegalArgumentException("Outer class unknown")
                    Mutable.Reason.RESOLVED -> OuterClassMutable(
                        OuterClassMutable.Type.MUTABLE,
                        classifierDescriptor
                    )
                }
                ImmutabilityProperty.Mutable(reason)
            }
            is ShallowImmutable -> {
                val reason = when (status.reason) {
                    ShallowImmutable.Reason.ASSUMPTION -> OuterClassShallowImmutable(
                        classifierDescriptor
                    )
                    ShallowImmutable.Reason.RESOLVED -> OuterClassShallowImmutable(
                        classifierDescriptor
                    )
                }
                ImmutabilityProperty.ShallowImmutable(reason)
            }
        }
    }
}
