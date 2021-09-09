package test.test.dependencies

import org.jetbrains.kotlin.descriptors.ValueParameterDescriptor
import org.jetbrains.kotlin.types.KotlinType
import test.test.Dependency
import test.test.ImmutabilityProperty
import test.test.ImmutabilityWithContext
import test.test.ImmutabilityWithContext.Result.*
import test.test.reasons.conditionally_deeply_immutable.ValParameterConditionallyDeeplyImmutable
import test.test.reasons.shallow_immutable.ValParameterShallowImmutable

data class ValParameter(
    val desc: ValueParameterDescriptor,
    val type: KotlinType,
    val debug: List<Any?> = listOf()
) :
    Dependency() {
    companion object {
        fun fromDescriptor(desc: ValueParameterDescriptor): Dependency =
            ValParameter(desc, desc.type, listOf(desc.type.arguments))
    }

    override fun recalculate(immutability: ImmutabilityWithContext): ImmutabilityProperty {
        val parameter = desc
        return when (val status = immutability.resolveType(type)) {
            is ConditionallyDeeplyImmutable -> {
                val reason = when (status.reason) {
                    ConditionallyDeeplyImmutable.Reason.ASSUMPTION -> ValParameterConditionallyDeeplyImmutable(
                        byAssumption = true,
                        parameter
                    )
                    ConditionallyDeeplyImmutable.Reason.RESOLVED -> ValParameterConditionallyDeeplyImmutable(
                        byAssumption = false,
                        parameter
                    )
                }
                ImmutabilityProperty.ConditionallyDeeplyImmutable(status.conditions, reason)
            }
            is Immutable -> ImmutabilityProperty.Immutable()
            is Mutable -> {
                val reason = when (status.reason) {
                    Mutable.Reason.ASSUMPTION -> ValParameterShallowImmutable(
                        ValParameterShallowImmutable.Type.MUTABLE_BY_ASSUMPTION,
                        parameter
                    )
                    Mutable.Reason.UNKNOWN -> ValParameterShallowImmutable(
                        ValParameterShallowImmutable.Type.UNKNOWN,
                        parameter
                    )
                    Mutable.Reason.RESOLVED -> ValParameterShallowImmutable(
                        ValParameterShallowImmutable.Type.MUTABLE,
                        parameter
                    )
                }
                ImmutabilityProperty.ShallowImmutable(reason)
            }
            is ShallowImmutable -> {
                val reason = when (status.reason) {
                    ShallowImmutable.Reason.ASSUMPTION -> ValParameterShallowImmutable(
                        ValParameterShallowImmutable.Type.SHALLOW_IMMUTABLE_BY_ASSUMPTION,
                        parameter
                    )
                    ShallowImmutable.Reason.RESOLVED -> ValParameterShallowImmutable(
                        ValParameterShallowImmutable.Type.SHALLOW_IMMUTABLE,
                        parameter
                    )
                }
                ImmutabilityProperty.ShallowImmutable(reason)
            }
        }
    }
}
