package org.jetbrains.research.ml.kotlinAnalysis

import test.test.ImmutabilityAnalysisExecutor

object KotlinImmutabilityAnalysisRunner : KotlinAnalysisRunner<IORunnerArgs, IORunnerArgsParser>
    ("kotlin-immutability-analysis", IORunnerArgsParser) {
    override fun run(args: IORunnerArgs) {
        ImmutabilityAnalysisExecutor(args.outputDir).execute(args.inputDir)
    }
}
