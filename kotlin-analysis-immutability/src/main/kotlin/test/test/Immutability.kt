package test.test

import org.jetbrains.kotlin.descriptors.ClassifierDescriptor
import org.jetbrains.kotlin.descriptors.DeclarationDescriptor
import org.jetbrains.kotlin.idea.refactoring.fqName.fqName
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameSafe
import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.kotlin.types.TypeProjection
import org.jetbrains.kotlin.types.typeUtil.isTypeParameter

sealed class ImmutabilityStatus() {
    object Immutable : ImmutabilityStatus() {
        override fun toString(): String {
            return "Immutable"
        }
    }

    object ShallowImmutable : ImmutabilityStatus() {
        override fun toString(): String {
            return "ShallowImmutable"
        }
    }

    data class ConditionallyDeeplyImmutable(val conditions: Set<Int>) : ImmutabilityStatus()
    object Mutable : ImmutabilityStatus() {
        override fun toString(): String {
            return "Mutable"
        }
    }
}

fun join(statuses: List<ImmutabilityStatus>): ImmutabilityStatus = when {
    ImmutabilityStatus.Mutable in statuses -> ImmutabilityStatus.Mutable
    ImmutabilityStatus.ShallowImmutable in statuses -> ImmutabilityStatus.ShallowImmutable
    statuses.any { it is ImmutabilityStatus.ConditionallyDeeplyImmutable } -> {
        val conditions = statuses.mapNotNull {
            when (it) {
                is ImmutabilityStatus.ConditionallyDeeplyImmutable -> it.conditions
                else -> null
            }
        }.flatten().toSet()
        ImmutabilityStatus.ConditionallyDeeplyImmutable(conditions)
    }
    else -> ImmutabilityStatus.Immutable
}

fun join(vararg statuses: ImmutabilityStatus): ImmutabilityStatus = join(statuses.toList())

class Immutability(entities: List<Entity>, vararg assumptions: Assumptions) {
    private val map: MutableMap<DeclarationDescriptor, ImmutabilityStatus> = entities.mapNotNull {
        when (it) {
            is ClassTemplate -> it.desc
            ErrorTemplate -> null
            is ObjectTemplate -> it.desc
            //is ParameterizedClassTemplate -> it.desc
        }
    }.associateWith { ImmutabilityStatus.Immutable }.toMutableMap()
    private val assumptions = assumptions.toList()


    operator fun get(
        descriptor: ClassifierDescriptor,
        parameters: List<TypeProjection> = listOf()
    ): ImmutabilityStatus = checkAssumptions(descriptor.fqNameSafe.asString())
        ?: map[descriptor]?.let {
            when (it) {
                is ImmutabilityStatus.ConditionallyDeeplyImmutable -> {
                    join(it.conditions.map { i ->
                        if (parameters[i].type.isTypeParameter()) {
                            ImmutabilityStatus.ConditionallyDeeplyImmutable(setOf(i))
                        } else {
                            when (val t = get(parameters[i].type)) {
                                is ImmutabilityStatus.ConditionallyDeeplyImmutable -> t
                                ImmutabilityStatus.Immutable -> t
                                ImmutabilityStatus.Mutable -> ImmutabilityStatus.ShallowImmutable
                                ImmutabilityStatus.ShallowImmutable -> ImmutabilityStatus.ShallowImmutable
                            }
                        }
                    })
                }
                else -> it
            }
        } ?: ImmutabilityStatus.Mutable

    operator fun get(
        type: KotlinType
    ): ImmutabilityStatus = type.fqName?.asString()?.let { checkAssumptions(it) }
        ?: type.constructor.declarationDescriptor?.let { get(it, type.arguments) }
        ?: ImmutabilityStatus.Mutable

    fun checkAssumptions(name: String): ImmutabilityStatus? {
        val t = assumptions.mapNotNull { it.get(name) }
        if (t.isNotEmpty()) {
            return join(t)
        }
        return null
    }

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
