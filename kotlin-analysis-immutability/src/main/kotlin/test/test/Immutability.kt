package test.test

import org.jetbrains.kotlin.descriptors.ClassifierDescriptor
import org.jetbrains.kotlin.descriptors.DeclarationDescriptor
import org.jetbrains.kotlin.descriptors.TypeParameterDescriptor
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameSafe
import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.kotlin.types.TypeProjection
import org.jetbrains.kotlin.types.typeUtil.asTypeProjection
import org.jetbrains.kotlin.types.typeUtil.isTypeParameter

class Immutability(entities: List<Entity>, vararg assumptions: Assumptions) {
    sealed class Status {
        object Immutable : Status() {
            override fun toString(): String {
                return "Immutable"
            }
        }

        object ShallowImmutable : Status() {
            override fun toString(): String {
                return "ShallowImmutable"
            }
        }

        data class ConditionallyDeeplyImmutable(val conditions: Set<TypeProjection>) : Status()
        object Mutable : Status() {
            override fun toString(): String {
                return "Mutable"
            }
        }
    }

    fun join(statuses: List<Status>): Status = when {
        Status.Mutable in statuses -> Status.Mutable
        Status.ShallowImmutable in statuses -> Status.ShallowImmutable
        statuses.any { it is Status.ConditionallyDeeplyImmutable } -> {
            val conditions = statuses.mapNotNull {
                when (it) {
                    is Status.ConditionallyDeeplyImmutable -> it.conditions
                    else -> null
                }
            }.flatten().toSet()
            Status.ConditionallyDeeplyImmutable(conditions)
        }
        else -> Status.Immutable
    }

    private val map: MutableMap<DeclarationDescriptor, ImmutabilityStatus> = entities.mapNotNull {
        when (it) {
            is ClassTemplate -> it.desc
            ErrorTemplate -> null
            is ObjectTemplate -> it.desc
            //is ParameterizedClassTemplate -> it.desc
        }
    }.associateWith { ImmutabilityStatus.Immutable }.toMutableMap()
    private val assumptions = assumptions.toList()

    private fun getStatus(
        descriptor: ClassifierDescriptor,
        parameters: List<TypeProjection> = listOf()
    ): Status = mapWithAssumptions(descriptor)?.let {
            when (it) {
                is ImmutabilityStatus.ConditionallyDeeplyImmutable -> {
                    join(it.conditions.map { i ->
                        if (parameters[i].type.isTypeParameter()) {
                            val t = parameters[i]
                            Status.ConditionallyDeeplyImmutable(setOf(t))
                        } else {
                            when (val t = getStatus(parameters[i].type)) {
                                is Status.ConditionallyDeeplyImmutable -> t
                                Status.Immutable -> t
                                Status.Mutable -> Status.ShallowImmutable
                                Status.ShallowImmutable -> Status.ShallowImmutable
                            }
                        }
                    })
                }
                ImmutabilityStatus.Immutable -> Status.Immutable
                ImmutabilityStatus.Mutable -> Status.Mutable
                ImmutabilityStatus.ShallowImmutable -> Status.ShallowImmutable
            }
        } ?: Status.Mutable

    operator fun get(
        descriptor: ClassifierDescriptor,
        parameters: List<TypeProjection> = listOf()
    ): ImmutabilityStatus = when(val status = getStatus(descriptor, parameters)) {
            is Status.ConditionallyDeeplyImmutable ->ImmutabilityStatus.ConditionallyDeeplyImmutable(status.conditions.map {
                (it.type.constructor.declarationDescriptor as TypeParameterDescriptor).index
            }.toSet())
            Status.Immutable -> ImmutabilityStatus.Immutable
            Status.Mutable -> ImmutabilityStatus.Mutable
            Status.ShallowImmutable -> ImmutabilityStatus.ShallowImmutable
        }


    private fun getStatus(
        type: KotlinType
    ): Status = if (type.isTypeParameter()) {
            Status.ConditionallyDeeplyImmutable(setOf(type.asTypeProjection()))
        } else { null }
        ?: type.constructor.declarationDescriptor?.let { getStatus(it, type.arguments) }
        ?: Status.Mutable

    operator fun get(
        type: KotlinType
    ): ImmutabilityStatus = when(val status = getStatus(type)) {
        is Status.ConditionallyDeeplyImmutable ->ImmutabilityStatus.ConditionallyDeeplyImmutable(status.conditions.map {
            (it.type.constructor.declarationDescriptor as TypeParameterDescriptor).index
        }.toSet())
        Status.Immutable -> ImmutabilityStatus.Immutable
        Status.Mutable -> ImmutabilityStatus.Mutable
        Status.ShallowImmutable -> ImmutabilityStatus.ShallowImmutable
    }

    fun checkAssumptions(name: String): ImmutabilityStatus? {
        val t = assumptions.mapNotNull { it.get(name) }
        if (t.isNotEmpty()) {
            return join(t)
        }
        return null
    }

    fun mapWithAssumptions(descriptor: DeclarationDescriptor): ImmutabilityStatus? =
        checkAssumptions(descriptor.fqNameSafe.asString()) ?: map[descriptor]

    fun update(entity: Entity, newValue: ImmutabilityStatus): Boolean =
        when (entity) {
            is ClassTemplate -> update(entity.desc, newValue)
            ErrorTemplate -> false
            is ObjectTemplate -> update(entity.desc, newValue)
        }

    fun update(descriptor: DeclarationDescriptor, newValue: ImmutabilityStatus): Boolean {
        if (map[descriptor] == newValue) {
            return false
        }
        map[descriptor] = newValue
        return true
    }

    override fun toString(): String {
        return map.map {
            it.key.fqNameSafe.asString() + " -> " + it.value.toString()
        }.joinToString(separator = "\n")
    }
}
