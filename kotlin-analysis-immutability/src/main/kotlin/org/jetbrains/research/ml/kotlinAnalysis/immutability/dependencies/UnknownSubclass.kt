package org.jetbrains.research.ml.kotlinAnalysis.immutability.dependencies

import org.jetbrains.kotlin.descriptors.ClassifierDescriptor
import org.jetbrains.research.ml.kotlinAnalysis.immutability.Dependency
import org.jetbrains.research.ml.kotlinAnalysis.immutability.ImmutabilityProperty
import org.jetbrains.research.ml.kotlinAnalysis.immutability.ImmutabilityWithContext
import org.jetbrains.research.ml.kotlinAnalysis.immutability.reasons.mutable.MayHaveMutableSubclass

class UnknownSubclass(val desc: ClassifierDescriptor) : Dependency() {
    override fun recalculate(immutability: ImmutabilityWithContext): ImmutabilityProperty {
        return ImmutabilityProperty.Mutable(MayHaveMutableSubclass(desc))
    }
}
