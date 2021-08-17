package test.test.dependencies

import org.jetbrains.kotlin.descriptors.ValueParameterDescriptor
import org.jetbrains.kotlin.types.KotlinType
import test.test.Dependency
import test.test.ImmutabilityMap
import test.test.ImmutabilityProperty
import test.test.reasons.shallow_immutable.ValPropertyShallowImmutable
import test.test.reasons.conditionally_deeply_immutable.ValPropertyConditionallyDeeplyImmutable

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

    override fun recalculate(resolve: (KotlinType) -> ImmutabilityMap.Result): ImmutabilityProperty {
        val parameter = desc.toString()
        return when (val status = resolve(type)) {
            is ImmutabilityMap.Result.ConditionallyDeeplyImmutable -> {
                val reason = when (status.reason) {
                    ImmutabilityMap.Result.ConditionallyDeeplyImmutable.Reason.ASSUMPTION -> ValPropertyConditionallyDeeplyImmutable(
                        byAssumption = true,
                        isParameter = true
                    )
                    ImmutabilityMap.Result.ConditionallyDeeplyImmutable.Reason.RESOLVED -> ValPropertyConditionallyDeeplyImmutable(
                        byAssumption = false,
                        isParameter = true
                    )
                }
                ImmutabilityProperty.ConditionallyDeeplyImmutable(status.conditions, reason)
            }
            is ImmutabilityMap.Result.Immutable -> ImmutabilityProperty.Immutable()
            is ImmutabilityMap.Result.Mutable -> {
                val reason = when (status.reason) {
                    ImmutabilityMap.Result.Mutable.Reason.ASSUMPTION -> ValPropertyShallowImmutable(
                        ValPropertyShallowImmutable.Type.MUTABLE_BY_ASSUMPTION,
                        true,
                        parameter
                    )
                    ImmutabilityMap.Result.Mutable.Reason.UNKNOWN -> ValPropertyShallowImmutable(
                        ValPropertyShallowImmutable.Type.UNKNOWN,
                        true,
                        parameter
                    )
                    ImmutabilityMap.Result.Mutable.Reason.RESOLVED -> ValPropertyShallowImmutable(
                        ValPropertyShallowImmutable.Type.MUTABLE,
                        true,
                        parameter
                    )
                }
                ImmutabilityProperty.ShallowImmutable(reason)
            }
            is ImmutabilityMap.Result.ShallowImmutable -> {
                val reason = when (status.reason) {
                    ImmutabilityMap.Result.ShallowImmutable.Reason.ASSUMPTION -> ValPropertyShallowImmutable(
                        ValPropertyShallowImmutable.Type.SHALLOW_IMMUTABLE_BY_ASSUMPTION,
                        true,
                        parameter
                    )
                    ImmutabilityMap.Result.ShallowImmutable.Reason.RESOLVED -> ValPropertyShallowImmutable(
                        ValPropertyShallowImmutable.Type.SHALLOW_IMMUTABLE,
                        true,
                        parameter
                    )
                }
                ImmutabilityProperty.ShallowImmutable(reason)
            }
        }
    }
}
