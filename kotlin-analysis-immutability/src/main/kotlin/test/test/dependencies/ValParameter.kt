package test.test.dependencies

import org.jetbrains.kotlin.descriptors.ValueParameterDescriptor
import org.jetbrains.kotlin.types.KotlinType
import test.test.Dependency
import test.test.ImmutabilityMap
import test.test.ImmutabilityProperty
import test.test.reasons.ConditionallyDeeplyImmutableReason
import test.test.reasons.ShallowImmutableReason

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
                    ImmutabilityMap.Result.Reason.ASSUMPTION -> ConditionallyDeeplyImmutableReason.ValPropertyConditionallyDeeplyImmutable(
                        byAssumption = true,
                        isParameter = true
                    )
                    ImmutabilityMap.Result.Reason.UNKNOWN -> throw IllegalArgumentException("ConditionallyDeeplyImmutable unknown")
                    ImmutabilityMap.Result.Reason.RESOLVED -> ConditionallyDeeplyImmutableReason.ValPropertyConditionallyDeeplyImmutable(
                        byAssumption = false,
                        isParameter = true
                    )
                }
                ImmutabilityProperty.ConditionallyDeeplyImmutable(status.conditions, reason)
            }
            is ImmutabilityMap.Result.Immutable -> ImmutabilityProperty.Immutable()
            is ImmutabilityMap.Result.Mutable -> {
                val reason = when (status.reason) {
                    ImmutabilityMap.Result.Reason.ASSUMPTION -> ShallowImmutableReason.ValProperty(
                        ShallowImmutableReason.Type.MUTABLE_BY_ASSUMPTION,
                        true,
                        parameter
                    )
                    ImmutabilityMap.Result.Reason.UNKNOWN -> ShallowImmutableReason.ValProperty(
                        ShallowImmutableReason.Type.UNKNOWN,
                        true,
                        parameter
                    )
                    ImmutabilityMap.Result.Reason.RESOLVED -> ShallowImmutableReason.ValProperty(
                        ShallowImmutableReason.Type.MUTABLE,
                        true,
                        parameter
                    )
                }
                ImmutabilityProperty.ShallowImmutable(reason)
            }
            is ImmutabilityMap.Result.ShallowImmutable -> {
                val reason = when (status.reason) {
                    ImmutabilityMap.Result.Reason.ASSUMPTION -> ShallowImmutableReason.ValProperty(
                        ShallowImmutableReason.Type.SHALLOW_IMMUTABLE_BY_ASSUMPTION,
                        true,
                        parameter
                    )
                    ImmutabilityMap.Result.Reason.UNKNOWN -> throw IllegalArgumentException("ShallowImmutable unknown")
                    ImmutabilityMap.Result.Reason.RESOLVED -> ShallowImmutableReason.ValProperty(
                        ShallowImmutableReason.Type.SHALLOW_IMMUTABLE,
                        true,
                        parameter
                    )
                }
                ImmutabilityProperty.ShallowImmutable(reason)
            }
        }
    }
}
