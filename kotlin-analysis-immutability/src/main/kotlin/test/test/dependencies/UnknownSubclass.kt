package test.test.dependencies

import org.jetbrains.kotlin.descriptors.ClassifierDescriptor
import test.test.Dependency
import test.test.ImmutabilityProperty
import test.test.ImmutabilityWithContext
import test.test.reasons.mutable.MayHaveMutableSubclass

class UnknownSubclass(val desc: ClassifierDescriptor) : Dependency() {
    override fun recalculate(immutability: ImmutabilityWithContext): ImmutabilityProperty {
        return ImmutabilityProperty.Mutable(MayHaveMutableSubclass(desc))
    }
}
