package org.jetbrains.research.ml.kotlinAnalysis.immutability.dependencies

import org.jetbrains.kotlin.descriptors.PropertyDescriptor
import org.jetbrains.kotlin.descriptors.VariableDescriptor
import org.jetbrains.kotlin.psi.KtPropertyDelegate
import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.research.ml.kotlinAnalysis.immutability.Dependency
import org.jetbrains.research.ml.kotlinAnalysis.immutability.ImmutabilityProperty
import org.jetbrains.research.ml.kotlinAnalysis.immutability.ImmutabilityWithContext
import org.jetbrains.research.ml.kotlinAnalysis.immutability.ImmutabilityWithContext.Result.*
import org.jetbrains.research.ml.kotlinAnalysis.immutability.reasons.conditionally_deeply_immutable.PropertyDelegateConditionallyDeeplyImmutable
import org.jetbrains.research.ml.kotlinAnalysis.immutability.reasons.mutable.PropertyDelegateMutable

data class DelegatedValProperty(
    val desc: VariableDescriptor,
    val type: KotlinType,
    val delegate: KtPropertyDelegate,
    val delegateType: KotlinType,
    val debug: List<Any?> = listOf()
) :
    Dependency() {
    companion object {
        fun fromDescriptor(
            desc: VariableDescriptor,
            delegate: KtPropertyDelegate,
            delegateType: KotlinType
        ): DelegatedValProperty =
            DelegatedValProperty(desc, desc.type, delegate, delegateType)
    }

    override fun recalculate(immutability: ImmutabilityWithContext): ImmutabilityProperty {
        val propertyDescriptor = desc as PropertyDescriptor
        return when (val result = immutability.resolveType(delegateType)) {
            is ConditionallyDeeplyImmutable -> {
                val reason = when (result.reason) {
                    ConditionallyDeeplyImmutable.Reason.ASSUMPTION -> PropertyDelegateConditionallyDeeplyImmutable(
                        PropertyDelegateConditionallyDeeplyImmutable.Type.CONDITIONALLY_DEEPLY_IMMUTABLE_BY_ASSUMPTION,
                        propertyDescriptor
                    )
                    ConditionallyDeeplyImmutable.Reason.RESOLVED -> PropertyDelegateConditionallyDeeplyImmutable(
                        PropertyDelegateConditionallyDeeplyImmutable.Type.CONDITIONALLY_DEEPLY_IMMUTABLE,
                        propertyDescriptor
                    )
                }
                ImmutabilityProperty.ConditionallyDeeplyImmutable(result.conditions, reason)
            }
            is Immutable -> ImmutabilityProperty.Immutable()
            is Mutable -> {
                val reason = when (result.reason) {
                    Mutable.Reason.ASSUMPTION -> PropertyDelegateMutable(
                        PropertyDelegateMutable.Type.MUTABLE_BY_ASSUMPTION,
                        propertyDescriptor
                    )
                    Mutable.Reason.UNKNOWN -> PropertyDelegateMutable(
                        PropertyDelegateMutable.Type.UNKNOWN,
                        propertyDescriptor
                    )
                    Mutable.Reason.RESOLVED -> PropertyDelegateMutable(
                        PropertyDelegateMutable.Type.MUTABLE,
                        propertyDescriptor
                    )
                }
                ImmutabilityProperty.Mutable(reason)
            }
            is ShallowImmutable -> {
                val reason = when (result.reason) {
                    ShallowImmutable.Reason.ASSUMPTION -> PropertyDelegateMutable(
                        PropertyDelegateMutable.Type.SHALLOW_IMMUTABLE_BY_ASSUMPTION,
                        propertyDescriptor
                    )
                    ShallowImmutable.Reason.RESOLVED -> PropertyDelegateMutable(
                        PropertyDelegateMutable.Type.SHALLOW_IMMUTABLE,
                        propertyDescriptor
                    )
                }
                ImmutabilityProperty.Mutable(reason)
            }
        }
    }
}
