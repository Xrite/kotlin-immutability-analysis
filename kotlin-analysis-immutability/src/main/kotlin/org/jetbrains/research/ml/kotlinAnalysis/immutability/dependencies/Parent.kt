package org.jetbrains.research.ml.kotlinAnalysis.immutability.dependencies

import org.jetbrains.kotlin.descriptors.ClassifierDescriptor
import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.research.ml.kotlinAnalysis.immutability.Dependency
import org.jetbrains.research.ml.kotlinAnalysis.immutability.ImmutabilityProperty
import org.jetbrains.research.ml.kotlinAnalysis.immutability.ImmutabilityWithContext
import org.jetbrains.research.ml.kotlinAnalysis.immutability.ImmutabilityWithContext.Result.*
import org.jetbrains.research.ml.kotlinAnalysis.immutability.reasons.conditionally_deeply_immutable.ParentTypeConditionallyDeeplyImmutable
import org.jetbrains.research.ml.kotlinAnalysis.immutability.reasons.mutable.ParentTypeMutable
import org.jetbrains.research.ml.kotlinAnalysis.immutability.reasons.shallow_immutable.ParentTypeShallowImmutable

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

    override fun recalculate(immutability: ImmutabilityWithContext): ImmutabilityProperty {
        val parentDescriptor = descriptor
        return when (val status = immutability.resolveType(type)) {
            is ConditionallyDeeplyImmutable -> {
                val reason = when (status.reason) {
                    ConditionallyDeeplyImmutable.Reason.ASSUMPTION -> ParentTypeConditionallyDeeplyImmutable(
                        true,
                        parentDescriptor
                    )
                    ConditionallyDeeplyImmutable.Reason.RESOLVED -> ParentTypeConditionallyDeeplyImmutable(
                        false,
                        parentDescriptor
                    )
                }
                ImmutabilityProperty.ConditionallyDeeplyImmutable(status.conditions, reason)
            }
            is Immutable -> ImmutabilityProperty.Immutable()
            is Mutable -> {
                val reason = when (status.reason) {
                    Mutable.Reason.ASSUMPTION -> ParentTypeMutable(
                        ParentTypeMutable.Type.MUTABLE_BY_ASSUMPTION,
                        descriptor
                    )
                    Mutable.Reason.UNKNOWN -> ParentTypeMutable(
                        ParentTypeMutable.Type.UNKNOWN,
                        descriptor
                    )
                    Mutable.Reason.RESOLVED -> ParentTypeMutable(
                        ParentTypeMutable.Type.MUTABLE,
                        descriptor
                    )
                }
                ImmutabilityProperty.Mutable(reason)
            }
            is ShallowImmutable -> {
                val reason = when (status.reason) {
                    ShallowImmutable.Reason.ASSUMPTION -> ParentTypeShallowImmutable(
                        parentDescriptor
                    )
                    ShallowImmutable.Reason.RESOLVED -> ParentTypeShallowImmutable(
                        parentDescriptor
                    )
                }
                ImmutabilityProperty.ShallowImmutable(reason)
            }
        }
    }
}
