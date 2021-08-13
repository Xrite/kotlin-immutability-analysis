package test.test

import org.jetbrains.kotlin.descriptors.ClassifierDescriptor
import org.jetbrains.kotlin.descriptors.ValueParameterDescriptor
import org.jetbrains.kotlin.descriptors.VariableDescriptor
import org.jetbrains.kotlin.psi.KtPropertyDelegate
import org.jetbrains.kotlin.types.KotlinType
import test.test.reasons.ConditionallyDeeplyImmutableReason
import test.test.reasons.MutableReason
import test.test.reasons.ShallowImmutableReason

sealed class Dependency {
    data class DebugType(val debug: List<Any?>) : Dependency() {
        override fun recalculate(resolve: (KotlinType) -> Immutability.Result): ImmutabilityStatus =
            ImmutabilityStatus.Immutable()
    }

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

        override fun recalculate(resolve: (KotlinType) -> Immutability.Result): ImmutabilityStatus =
            when (val status = resolve(type)) {
                is Immutability.Result.ConditionallyDeeplyImmutable -> {
                    val reason = when (status.reason) {
                        Immutability.Result.Reason.ASSUMPTION -> ConditionallyDeeplyImmutableReason.ValPropertyConditionallyDeeplyImmutable(
                            byAssumption = true,
                            isParameter = true
                        )
                        Immutability.Result.Reason.UNKNOWN -> throw IllegalArgumentException("ConditionallyDeeplyImmutable unknown")
                        Immutability.Result.Reason.RESOLVED -> ConditionallyDeeplyImmutableReason.ValPropertyConditionallyDeeplyImmutable(
                            byAssumption = false,
                            isParameter = true
                        )
                    }
                    ImmutabilityStatus.ConditionallyDeeplyImmutable(status.conditions, reason)
                }
                is Immutability.Result.Immutable -> ImmutabilityStatus.Immutable()
                is Immutability.Result.Mutable -> {
                    val reason = when (status.reason) {
                        Immutability.Result.Reason.ASSUMPTION -> ShallowImmutableReason.ValProperty(
                            ShallowImmutableReason.Type.MUTABLE_BY_ASSUMPTION,
                            true
                        )
                        Immutability.Result.Reason.UNKNOWN -> ShallowImmutableReason.ValProperty(
                            ShallowImmutableReason.Type.UNKNOWN,
                            true
                        )
                        Immutability.Result.Reason.RESOLVED -> ShallowImmutableReason.ValProperty(
                            ShallowImmutableReason.Type.MUTABLE,
                            true
                        )
                    }
                    ImmutabilityStatus.ShallowImmutable(reason)
                }
                is Immutability.Result.ShallowImmutable -> {
                    val reason = when (status.reason) {
                        Immutability.Result.Reason.ASSUMPTION -> ShallowImmutableReason.ValProperty(
                            ShallowImmutableReason.Type.SHALLOW_IMMUTABLE_BY_ASSUMPTION,
                            true
                        )
                        Immutability.Result.Reason.UNKNOWN -> throw IllegalArgumentException("ShallowImmutable unknown")
                        Immutability.Result.Reason.RESOLVED -> ShallowImmutableReason.ValProperty(
                            ShallowImmutableReason.Type.SHALLOW_IMMUTABLE,
                            true
                        )
                    }
                    ImmutabilityStatus.ShallowImmutable(reason)
                }
            }
    }

    data class VarParameter(
        val desc: ValueParameterDescriptor,
        val type: KotlinType,
        val debug: List<Any?> = listOf()
    ) :
        Dependency() {
        companion object {
            fun fromDescriptor(desc: ValueParameterDescriptor): Dependency =
                VarParameter(desc, desc.type, listOf(desc.type.arguments))
        }

        override fun recalculate(resolve: (KotlinType) -> Immutability.Result): ImmutabilityStatus =
            ImmutabilityStatus.Mutable(MutableReason.VarProperty(true))
    }

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

        override fun recalculate(resolve: (KotlinType) -> Immutability.Result): ImmutabilityStatus =
            when (val status = resolve(type)) {
                is Immutability.Result.ConditionallyDeeplyImmutable -> {
                    val reason = when (status.reason) {
                        Immutability.Result.Reason.ASSUMPTION -> ConditionallyDeeplyImmutableReason.ValPropertyConditionallyDeeplyImmutable(
                            byAssumption = true,
                            isParameter = false
                        )
                        Immutability.Result.Reason.UNKNOWN -> throw IllegalArgumentException("ConditionallyDeeplyImmutable unknown")
                        Immutability.Result.Reason.RESOLVED -> ConditionallyDeeplyImmutableReason.ValPropertyConditionallyDeeplyImmutable(
                            byAssumption = false,
                            isParameter = false
                        )
                    }
                    ImmutabilityStatus.ConditionallyDeeplyImmutable(status.conditions, reason)
                }
                is Immutability.Result.Immutable -> ImmutabilityStatus.Immutable()
                is Immutability.Result.Mutable -> {
                    val reason = when (status.reason) {
                        Immutability.Result.Reason.ASSUMPTION -> ShallowImmutableReason.ValProperty(
                            ShallowImmutableReason.Type.MUTABLE_BY_ASSUMPTION,
                            false
                        )
                        Immutability.Result.Reason.UNKNOWN -> ShallowImmutableReason.ValProperty(
                            ShallowImmutableReason.Type.UNKNOWN,
                            false
                        )
                        Immutability.Result.Reason.RESOLVED -> ShallowImmutableReason.ValProperty(
                            ShallowImmutableReason.Type.MUTABLE,
                            false
                        )
                    }
                    ImmutabilityStatus.ShallowImmutable(reason)
                }
                is Immutability.Result.ShallowImmutable -> {
                    val reason = when (status.reason) {
                        Immutability.Result.Reason.ASSUMPTION -> ShallowImmutableReason.ValProperty(
                            ShallowImmutableReason.Type.SHALLOW_IMMUTABLE_BY_ASSUMPTION,
                            false
                        )
                        Immutability.Result.Reason.UNKNOWN -> throw IllegalArgumentException("ShallowImmutable unknown")
                        Immutability.Result.Reason.RESOLVED -> ShallowImmutableReason.ValProperty(
                            ShallowImmutableReason.Type.SHALLOW_IMMUTABLE,
                            false
                        )
                    }
                    ImmutabilityStatus.ShallowImmutable(reason)
                }
            }
    }

    data class VarProperty(
        val desc: VariableDescriptor,
        val type: KotlinType,
        val debug: List<Any?> = listOf()
    ) :
        Dependency() {
        companion object {
            fun fromDescriptor(desc: VariableDescriptor): Dependency =
                VarProperty(desc, desc.type, listOf(desc.type.arguments))
        }

        override fun recalculate(resolve: (KotlinType) -> Immutability.Result): ImmutabilityStatus =
            ImmutabilityStatus.Mutable(MutableReason.VarProperty(false))
    }

    data class PropertyWithGetter(
        val desc: VariableDescriptor,
        val type: KotlinType,
        val debug: List<Any?> = listOf()
    ) :
        Dependency() {
        companion object {
            fun fromDescriptor(desc: VariableDescriptor): Dependency =
                PropertyWithGetter(desc, desc.type)
        }

        override fun recalculate(resolve: (KotlinType) -> Immutability.Result): ImmutabilityStatus =
            ImmutabilityStatus.Mutable(MutableReason.PropertyWithGetter)
    }

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

        override fun recalculate(resolve: (KotlinType) -> Immutability.Result): ImmutabilityStatus =
            ImmutabilityStatus.Mutable(MutableReason.DelegatedProperty)
    }

    data class Parent(
        val parent: ClassifierDescriptor,
        val type: KotlinType,
        val debug: List<Any?> = listOf()
    ) :
        Dependency() {
        companion object {
            fun fromKotlinType(kotlinType: KotlinType): Dependency =
                kotlinType.constructor.declarationDescriptor?.let { Parent(it, kotlinType) }
                    ?: Error("Parent doesn't have ClassifierDescriptor, type: $kotlinType")
        }

        override fun recalculate(resolve: (KotlinType) -> Immutability.Result): ImmutabilityStatus =
            when (val status = resolve(type)) {
                is Immutability.Result.ConditionallyDeeplyImmutable -> {
                    val reason = when (status.reason) {
                        Immutability.Result.Reason.ASSUMPTION -> ConditionallyDeeplyImmutableReason.ParentTypeConditionallyDeeplyImmutable(
                            true
                        )
                        Immutability.Result.Reason.UNKNOWN -> throw IllegalArgumentException("ConditionallyDeeplyImmutable unknown")
                        Immutability.Result.Reason.RESOLVED -> ConditionallyDeeplyImmutableReason.ParentTypeConditionallyDeeplyImmutable(
                            false
                        )
                    }
                    ImmutabilityStatus.ConditionallyDeeplyImmutable(status.conditions, reason)
                }
                is Immutability.Result.Immutable -> ImmutabilityStatus.Immutable()
                is Immutability.Result.Mutable -> {
                    val reason = when (status.reason) {
                        Immutability.Result.Reason.ASSUMPTION -> MutableReason.ParentType(MutableReason.ParentType.Type.MUTABLE_BY_ASSUMPTION)
                        Immutability.Result.Reason.UNKNOWN -> MutableReason.ParentType(MutableReason.ParentType.Type.UNKNOWN)
                        Immutability.Result.Reason.RESOLVED -> MutableReason.ParentType(MutableReason.ParentType.Type.MUTABLE)
                    }
                    ImmutabilityStatus.Mutable(reason)
                }
                is Immutability.Result.ShallowImmutable -> {
                    val reason = when (status.reason) {
                        Immutability.Result.Reason.ASSUMPTION -> ShallowImmutableReason.ParentTypeShallowImmutable(true)
                        Immutability.Result.Reason.UNKNOWN -> throw IllegalArgumentException("ShallowImmutable unknown")
                        Immutability.Result.Reason.RESOLVED -> ShallowImmutableReason.ParentTypeShallowImmutable(false)
                    }
                    ImmutabilityStatus.ShallowImmutable(reason)
                }
            }
    }

    data class Outer(
        val outer: ClassifierDescriptor,
        val debug: List<Any?> = listOf()
    ) : Dependency() {
        override fun recalculate(resolve: (KotlinType) -> Immutability.Result): ImmutabilityStatus =
            when (val status = resolve(outer.defaultType)) {
                is Immutability.Result.ConditionallyDeeplyImmutable -> {
                    val reason = when (status.reason) {
                        Immutability.Result.Reason.ASSUMPTION -> ConditionallyDeeplyImmutableReason.OuterClassTypeConditionallyDeeplyImmutable(
                            true
                        )
                        Immutability.Result.Reason.UNKNOWN -> throw IllegalArgumentException("ConditionallyDeeplyImmutable unknown")
                        Immutability.Result.Reason.RESOLVED -> ConditionallyDeeplyImmutableReason.OuterClassTypeConditionallyDeeplyImmutable(
                            false
                        )
                    }
                    ImmutabilityStatus.ConditionallyDeeplyImmutable(status.conditions, reason)
                }
                is Immutability.Result.Immutable -> ImmutabilityStatus.Immutable()
                is Immutability.Result.Mutable -> {
                    val reason = when (status.reason) {
                        Immutability.Result.Reason.ASSUMPTION -> MutableReason.OuterClass(MutableReason.OuterClass.Type.MUTABLE_BY_ASSUMPTION)
                        Immutability.Result.Reason.UNKNOWN -> throw IllegalArgumentException("Outer class unknown")
                        Immutability.Result.Reason.RESOLVED -> MutableReason.OuterClass(MutableReason.OuterClass.Type.MUTABLE)
                    }
                    ImmutabilityStatus.Mutable(reason)
                }
                is Immutability.Result.ShallowImmutable -> {
                    val reason = when (status.reason) {
                        Immutability.Result.Reason.ASSUMPTION -> ShallowImmutableReason.OuterClassShallowImmutable(true)
                        Immutability.Result.Reason.UNKNOWN -> throw IllegalArgumentException("ShallowImmutable unknown")
                        Immutability.Result.Reason.RESOLVED -> ShallowImmutableReason.OuterClassShallowImmutable(false)
                    }
                    ImmutabilityStatus.ShallowImmutable(reason)
                }
            }
    }

    data class Error(
        val reason: Any?,
        val debug: List<Any?> = listOf()
    ) : Dependency() {
        override fun recalculate(resolve: (KotlinType) -> Immutability.Result): ImmutabilityStatus =
            ImmutabilityStatus.Mutable(MutableReason.Error)
    }

    abstract fun recalculate(resolve: (KotlinType) -> Immutability.Result): ImmutabilityStatus

}


typealias Dependencies = List<Dependency>
