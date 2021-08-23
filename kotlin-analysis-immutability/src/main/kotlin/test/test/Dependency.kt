package test.test

import org.jetbrains.kotlin.types.KotlinType

abstract class Dependency {
    abstract fun recalculate(immutability: ImmutabilityWithContext): ImmutabilityProperty
}

typealias Dependencies = List<Dependency>
