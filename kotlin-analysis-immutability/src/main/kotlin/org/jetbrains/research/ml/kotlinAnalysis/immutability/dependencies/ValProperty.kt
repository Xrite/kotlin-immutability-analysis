package org.jetbrains.research.ml.kotlinAnalysis.immutability.dependencies

import org.jetbrains.kotlin.descriptors.PropertyDescriptor
import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.research.ml.kotlinAnalysis.immutability.Dependency
import org.jetbrains.research.ml.kotlinAnalysis.immutability.ImmutabilityProperty
import org.jetbrains.research.ml.kotlinAnalysis.immutability.ImmutabilityWithContext
import org.jetbrains.research.ml.kotlinAnalysis.immutability.ImmutabilityWithContext.Result.*
import org.jetbrains.research.ml.kotlinAnalysis.immutability.reasons.shallow_immutable.ValPropertyShallowImmutable
import org.jetbrains.research.ml.kotlinAnalysis.immutability.reasons.conditionally_deeply_immutable.ValPropertyConditionallyDeeplyImmutable

data class ValProperty(
    val desc: PropertyDescriptor,
    val type: KotlinType,
    val debug: List<Any?> = listOf()
) :
    Dependency() {
    companion object {
        fun fromDescriptor(desc: PropertyDescriptor): Dependency =
            ValProperty(desc, desc.type, listOf(desc.type.arguments))
    }

    override fun recalculate(immutability: ImmutabilityWithContext): ImmutabilityProperty {
        val property = desc
        return when (val status = immutability.resolveType(type)) {
            is ConditionallyDeeplyImmutable -> {
                val reason = when (status.reason) {
                    ConditionallyDeeplyImmutable.Reason.ASSUMPTION -> ValPropertyConditionallyDeeplyImmutable(
                        byAssumption = true,
                        property
                    )
                    ConditionallyDeeplyImmutable.Reason.RESOLVED -> ValPropertyConditionallyDeeplyImmutable(
                        byAssumption = false,
                        property
                    )
                }
                ImmutabilityProperty.ConditionallyDeeplyImmutable(status.conditions, reason)
            }
            is Immutable -> ImmutabilityProperty.Immutable()
            is Mutable -> {
                val reason = when (status.reason) {
                    Mutable.Reason.ASSUMPTION -> ValPropertyShallowImmutable(
                        ValPropertyShallowImmutable.Type.MUTABLE_BY_ASSUMPTION,
                        property
                    )
                    Mutable.Reason.UNKNOWN -> ValPropertyShallowImmutable(
                        ValPropertyShallowImmutable.Type.UNKNOWN,
                        property
                    )
                    Mutable.Reason.RESOLVED -> ValPropertyShallowImmutable(
                        ValPropertyShallowImmutable.Type.MUTABLE,
                        property
                    )
                }
                ImmutabilityProperty.ShallowImmutable(reason)
            }
            is ShallowImmutable -> {
                val reason = when (status.reason) {
                    ShallowImmutable.Reason.ASSUMPTION -> ValPropertyShallowImmutable(
                        ValPropertyShallowImmutable.Type.SHALLOW_IMMUTABLE_BY_ASSUMPTION,
                        property
                    )
                    ShallowImmutable.Reason.RESOLVED -> ValPropertyShallowImmutable(
                        ValPropertyShallowImmutable.Type.SHALLOW_IMMUTABLE,
                        property
                    )
                }
                ImmutabilityProperty.ShallowImmutable(reason)
            }
        }
    }
}
