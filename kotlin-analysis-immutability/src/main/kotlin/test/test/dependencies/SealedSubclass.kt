package test.test.dependencies

import org.jetbrains.kotlin.descriptors.ClassifierDescriptor
import org.jetbrains.kotlin.types.KotlinType
import test.test.Dependency
import test.test.ImmutabilityProperty
import test.test.ImmutabilityWithContext
import test.test.ImmutabilityWithContext.*
import test.test.ImmutabilityWithContext.Result.*
import test.test.reasons.shallow_immutable.SealedSubclassShallowImmutable
import test.test.reasons.conditionally_deeply_immutable.SealedSubclassConditionallyDeeplyImmutable
import test.test.reasons.mutable.SealedSubclassMutable

class SealedSubclass(
    val descriptor: ClassifierDescriptor,
    val type: KotlinType,
    val debug: List<Any?> = listOf()
) : Dependency() {
    companion object {
        fun fromDescriptor(desc: ClassifierDescriptor): Dependency =
            SealedSubclass(desc, desc.defaultType)
    }

    override fun recalculate(immutability: ImmutabilityWithContext): ImmutabilityProperty {
        return when (val result = immutability.resolveDescriptor(descriptor)) {
            is ConditionallyDeeplyImmutable -> {
                val reason = when (result.reason) {
                    ConditionallyDeeplyImmutable.Reason.ASSUMPTION -> SealedSubclassConditionallyDeeplyImmutable(
                        SealedSubclassConditionallyDeeplyImmutable.Type.CONDITIONALLY_DEEPLY_IMMUTABLE_BY_ASSUMPTION, descriptor)
                    ConditionallyDeeplyImmutable.Reason.RESOLVED -> SealedSubclassConditionallyDeeplyImmutable(
                        SealedSubclassConditionallyDeeplyImmutable.Type.CONDITIONALLY_DEEPLY_IMMUTABLE, descriptor)
                }
                return ImmutabilityProperty.ConditionallyDeeplyImmutable(result.conditions, reason)
            }
            is Immutable -> ImmutabilityProperty.Immutable()
            is Mutable -> {
                val reason = when (result.reason) {
                    Mutable.Reason.ASSUMPTION -> SealedSubclassMutable(SealedSubclassMutable.Type.MUTABLE_BY_ASSUMPTION, descriptor)
                    Mutable.Reason.RESOLVED -> SealedSubclassMutable(SealedSubclassMutable.Type.MUTABLE, descriptor)
                    Mutable.Reason.UNKNOWN -> SealedSubclassMutable(SealedSubclassMutable.Type.UNKNOWN, descriptor)
                }
                return ImmutabilityProperty.Mutable(reason)
            }
            is ShallowImmutable -> {
                val reason = when (result.reason) {
                    ShallowImmutable.Reason.ASSUMPTION -> SealedSubclassShallowImmutable(
                        SealedSubclassShallowImmutable.Type.SHALLOW_IMMUTABLE_BY_ASSUMPTION, descriptor)
                    ShallowImmutable.Reason.RESOLVED -> SealedSubclassShallowImmutable(
                        SealedSubclassShallowImmutable.Type.SHALLOW_IMMUTABLE, descriptor)
                }
                return ImmutabilityProperty.ShallowImmutable(reason)
            }
        }
    }
}
