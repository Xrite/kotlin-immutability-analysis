package org.jetbrains.research.ml.kotlinAnalysis.immutability.dependencies

import org.jetbrains.kotlin.descriptors.VariableDescriptor
import org.jetbrains.kotlin.psi.KtPropertyDelegate
import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.research.ml.kotlinAnalysis.immutability.Dependency
import org.jetbrains.research.ml.kotlinAnalysis.immutability.ImmutabilityProperty
import org.jetbrains.research.ml.kotlinAnalysis.immutability.ImmutabilityWithContext
import org.jetbrains.research.ml.kotlinAnalysis.immutability.reasons.mutable.DelegatedProperty

data class DelegatedValPropertyIsMutable(
    val desc: VariableDescriptor,
    val type: KotlinType,
    val delegate: KtPropertyDelegate,
    val debug: List<Any?> = listOf()
) :
    Dependency() {
    companion object {
        fun fromDescriptor(desc: VariableDescriptor, delegate: KtPropertyDelegate): DelegatedValPropertyIsMutable =
            DelegatedValPropertyIsMutable(desc, desc.type, delegate)
    }

    override fun recalculate(immutability: ImmutabilityWithContext): ImmutabilityProperty =
        ImmutabilityProperty.Mutable(DelegatedProperty(desc))
}
