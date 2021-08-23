package test.test

import org.jetbrains.kotlin.descriptors.ClassifierDescriptor
import org.jetbrains.kotlin.descriptors.DeclarationDescriptor
import org.jetbrains.kotlin.descriptors.TypeParameterDescriptor
import org.jetbrains.kotlin.idea.refactoring.fqName.fqName
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameSafe
import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.kotlin.types.TypeProjection
import org.jetbrains.kotlin.types.Variance
import org.jetbrains.kotlin.types.typeUtil.asTypeProjection
import org.jetbrains.kotlin.types.typeUtil.isTypeParameter

class MutableImmutabilityMap(private val entities: List<Entity>, private vararg val assumptions: Assumptions) :
    Immutability {
    private val map: MutableMap<DeclarationDescriptor, ImmutabilityProperty> = entities.mapNotNull {
        when (it) {
            is ClassTemplate -> it.desc
            ErrorTemplate -> null
        }
    }.associateWith { ImmutabilityProperty.Immutable() }.toMutableMap()

    private fun checkAssumptions(name: String): ImmutabilityProperty? {
        val t = assumptions.mapNotNull { it.get(name) }
        if (t.isNotEmpty()) {
            return join(t)
        }
        return null
    }

    private fun mapWithAssumptions(descriptor: DeclarationDescriptor): ImmutabilityProperty? =
        checkAssumptions(descriptor.fqNameSafe.asString()) ?: map[descriptor]

    operator fun set(entity: Entity, newValue: ImmutabilityProperty): Boolean =
        when (entity) {
            is ClassTemplate -> set(entity.desc, newValue)
            ErrorTemplate -> false
        }

    operator fun set(descriptor: DeclarationDescriptor, newValue: ImmutabilityProperty): Boolean {
        val oldValue = map[descriptor]
        if (oldValue != null && oldValue hasSameStatus newValue) {
            return false
        }
        map[descriptor] = newValue
        return true
    }

    override operator fun get(descriptor: DeclarationDescriptor): ImmutabilityProperty? = mapWithAssumptions(descriptor)

    override operator fun get(entity: Entity): ImmutabilityProperty? = when (entity) {
        is ClassTemplate -> get(entity.desc)
        ErrorTemplate -> null
    }

    override fun toString(): String = map.map {
        it.key.fqNameSafe.asString() + " -> " + it.value.toString()
    }.joinToString(separator = "\n")

    override fun results(): List<Pair<Entity, ImmutabilityProperty>> =
        entities.mapNotNull { entity ->
            when (entity) {
                is ClassTemplate -> map[entity.desc]?.let { entity to it }
                ErrorTemplate -> null
            }
        }

    override fun resultsForEntities(): List<Pair<Entity, ImmutabilityProperty?>> =
        entities.map { entity ->
            when (entity) {
                is ClassTemplate -> entity to map[entity.desc]
                ErrorTemplate -> entity to null
            }
        }

    override fun unresolvedEntities(): List<Entity> =
        entities.filter {
            when (it) {
                is ClassTemplate -> map[it.desc] == null
                ErrorTemplate -> true
            }
        }

    inner class WithContext(private val context: List<TypeParameterDescriptor>) : Immutability by this,
        ImmutabilityWithContext {
        private val indices = context.associate { it.original.typeConstructor to it.index }

        private fun index(projection: TypeProjection) = indices[projection.type.constructor]

        override operator fun invoke(
            descriptor: ClassifierDescriptor,
            parameters: List<TypeProjection>
        ): ImmutabilityWithContext.Result = mapWithAssumptions(descriptor)?.let {
            when (it) {
                is ImmutabilityProperty.ConditionallyDeeplyImmutable -> {
                    join(it.conditions.map { i ->
                        when {
                            parameters[i].isStarProjection -> ImmutabilityWithContext.Result.Mutable()
                            parameters[i].projectionKind == Variance.OUT_VARIANCE -> ImmutabilityWithContext.Result.Mutable()
                            parameters[i].type.isTypeParameter() -> {
                                val idx = index(parameters[i])
                                if (idx != null) {
                                    ImmutabilityWithContext.Result.ConditionallyDeeplyImmutable(idx)
                                } else {
                                    throw IllegalArgumentException("Can't get index of ${parameters[i]} in ${descriptor.defaultType} with context $context") //TODO: fix (/Users/Anton.Bukov/work/dataset/InsertKoinIO#koin, /Users/Anton.Bukov/work/dataset/corda#corda)
                                }
                            }
                            else -> {
                                when (val t = invoke(parameters[i].type)) {
                                    is ImmutabilityWithContext.Result.ConditionallyDeeplyImmutable -> t
                                    is ImmutabilityWithContext.Result.Immutable -> t
                                    is ImmutabilityWithContext.Result.Mutable -> ImmutabilityWithContext.Result.ShallowImmutable()
                                    is ImmutabilityWithContext.Result.ShallowImmutable -> ImmutabilityWithContext.Result.ShallowImmutable()
                                }
                            }
                        }
                    })
                }
                is ImmutabilityProperty.Immutable -> ImmutabilityWithContext.Result.Immutable(if (it.isByAssumption()) ImmutabilityWithContext.Result.Immutable.Reason.ASSUMPTION else ImmutabilityWithContext.Result.Immutable.Reason.RESOLVED)
                is ImmutabilityProperty.Mutable -> ImmutabilityWithContext.Result.Mutable(if (it.isByAssumption()) ImmutabilityWithContext.Result.Mutable.Reason.ASSUMPTION else ImmutabilityWithContext.Result.Mutable.Reason.RESOLVED)
                is ImmutabilityProperty.ShallowImmutable -> ImmutabilityWithContext.Result.ShallowImmutable(if (it.isByAssumption()) ImmutabilityWithContext.Result.ShallowImmutable.Reason.ASSUMPTION else ImmutabilityWithContext.Result.ShallowImmutable.Reason.RESOLVED)
            }
        } ?: ImmutabilityWithContext.Result.Mutable(ImmutabilityWithContext.Result.Mutable.Reason.UNKNOWN)

        override operator fun invoke(
            type: KotlinType
        ): ImmutabilityWithContext.Result =
            if (type.isTypeParameter()) {
                val idx = index(type.asTypeProjection())
                if (idx != null) {
                    ImmutabilityWithContext.Result.ConditionallyDeeplyImmutable(idx)
                } else {
                    throw IllegalArgumentException("Can't get index of $type")
                }
            } else {
                null
            }
                ?: type.constructor.declarationDescriptor?.let {
                    try {
                        invoke(it, type.arguments)
                    } catch (e: IllegalArgumentException) {
                        throw IllegalArgumentException(
                            "Can't resolve type ${type} (${type.fqName}) in context $context",
                            e
                        )
                    }
                }
                ?: ImmutabilityWithContext.Result.Mutable(ImmutabilityWithContext.Result.Mutable.Reason.UNKNOWN)

    }


}
