package test.test

import org.jetbrains.kotlin.descriptors.DeclarationDescriptor

interface Immutability {
    operator fun get(descriptor: DeclarationDescriptor): ImmutabilityProperty?

    operator fun get(entity: Entity): ImmutabilityProperty?
    fun results(): List<Pair<Entity, ImmutabilityProperty>>
    fun resultsForEntities(): List<Pair<Entity, ImmutabilityProperty?>>
    fun unresolvedEntities(): List<Entity>
}
