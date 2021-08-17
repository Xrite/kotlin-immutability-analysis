package test.test.dependencies

import org.jetbrains.kotlin.descriptors.VariableDescriptor
import org.jetbrains.kotlin.types.KotlinType
import test.test.Dependency
import test.test.ImmutabilityMap
import test.test.ImmutabilityProperty
import test.test.reasons.shallow_immutable.ValPropertyShallowImmutable
import test.test.reasons.conditionally_deeply_immutable.ValPropertyConditionallyDeeplyImmutable

data class ValProperty(
    val desc: VariableDescriptor,
    val type: KotlinType,
    val debug: List<Any?> = listOf()
) :
    Dependency() {
    companion object {
        fun fromDescriptor(desc: VariableDescriptor): Dependency =
            ValProperty(desc, desc.type, listOf(desc.type.arguments))
    }

    override fun recalculate(resolve: (KotlinType) -> ImmutabilityMap.Result): ImmutabilityProperty {
        val property = desc.toString()
        return when (val status = resolve(type)) {
            is ImmutabilityMap.Result.ConditionallyDeeplyImmutable -> {
                val reason = when (status.reason) {
                    ImmutabilityMap.Result.Reason.ASSUMPTION -> ValPropertyConditionallyDeeplyImmutable(
                        byAssumption = true,
                        isParameter = false
                    )
                    ImmutabilityMap.Result.Reason.UNKNOWN -> throw IllegalArgumentException("ConditionallyDeeplyImmutable unknown")
                    ImmutabilityMap.Result.Reason.RESOLVED -> ValPropertyConditionallyDeeplyImmutable(
                        byAssumption = false,
                        isParameter = false
                    )
                }
                ImmutabilityProperty.ConditionallyDeeplyImmutable(status.conditions, reason)
            }
            is ImmutabilityMap.Result.Immutable -> ImmutabilityProperty.Immutable()
            is ImmutabilityMap.Result.Mutable -> {
                val reason = when (status.reason) {
                    ImmutabilityMap.Result.Reason.ASSUMPTION -> ValPropertyShallowImmutable(
                        ValPropertyShallowImmutable.Type.MUTABLE_BY_ASSUMPTION,
                        false,
                        property
                    )
                    ImmutabilityMap.Result.Reason.UNKNOWN -> ValPropertyShallowImmutable(
                        ValPropertyShallowImmutable.Type.UNKNOWN,
                        false,
                        property
                    )
                    ImmutabilityMap.Result.Reason.RESOLVED -> ValPropertyShallowImmutable(
                        ValPropertyShallowImmutable.Type.MUTABLE,
                        false,
                        property
                    )
                }
                ImmutabilityProperty.ShallowImmutable(reason)
            }
            is ImmutabilityMap.Result.ShallowImmutable -> {
                val reason = when (status.reason) {
                    ImmutabilityMap.Result.Reason.ASSUMPTION -> ValPropertyShallowImmutable(
                        ValPropertyShallowImmutable.Type.SHALLOW_IMMUTABLE_BY_ASSUMPTION,
                        false,
                        property
                    )
                    ImmutabilityMap.Result.Reason.UNKNOWN -> throw IllegalArgumentException("ShallowImmutable unknown")
                    ImmutabilityMap.Result.Reason.RESOLVED -> ValPropertyShallowImmutable(
                        ValPropertyShallowImmutable.Type.SHALLOW_IMMUTABLE,
                        false,
                        property
                    )
                }
                ImmutabilityProperty.ShallowImmutable(reason)
            }
        }
    }
}
