package org.jetbrains.research.ml.kotlinAnalysis.immutability

import org.jetbrains.kotlin.descriptors.DeclarationDescriptor

/**
 * Maps entities in project to immutability property.
 * [get] can return null when such entity is unknown or immutability property was can not be calculated. This does NOT
 * mean that entity is mutable.
 */
interface Immutability {
    operator fun get(descriptor: DeclarationDescriptor): ImmutabilityProperty?

    operator fun get(entity: Entity): ImmutabilityProperty?
    fun results(): List<Pair<Entity, ImmutabilityProperty>>
    fun resultsForEntities(): List<Pair<Entity, ImmutabilityProperty?>>
    fun unresolvedEntities(): List<Entity>
}
