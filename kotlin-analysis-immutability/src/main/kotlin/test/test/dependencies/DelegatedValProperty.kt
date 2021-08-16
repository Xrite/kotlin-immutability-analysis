package test.test.dependencies

import org.jetbrains.kotlin.descriptors.VariableDescriptor
import org.jetbrains.kotlin.psi.KtPropertyDelegate
import org.jetbrains.kotlin.types.KotlinType
import test.test.Dependency
import test.test.ImmutabilityMap
import test.test.ImmutabilityProperty
import test.test.reasons.MutableReason

data class DelegatedValProperty(
    val desc: VariableDescriptor,
    val type: KotlinType,
    val delegate: KtPropertyDelegate,
    val debug: List<Any?> = listOf()
) :
    Dependency() {
    companion object {
        fun fromDescriptor(desc: VariableDescriptor, delegate: KtPropertyDelegate): Dependency =
            DelegatedValProperty(desc, desc.type, delegate)
    }

    override fun recalculate(resolve: (KotlinType) -> ImmutabilityMap.Result): ImmutabilityProperty =
        ImmutabilityProperty.Mutable(MutableReason.DelegatedProperty(desc.toString()))
}
