package org.jetbrains.research.ml.kotlinAnalysis.immutability

abstract class Dependency {
    abstract fun recalculate(immutability: ImmutabilityWithContext): ImmutabilityProperty
}

typealias Dependencies = List<Dependency>
