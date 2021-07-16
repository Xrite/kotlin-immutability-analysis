package org.jetbrains.research.ml.kotlinAnalysis

import test.test.TestAnalysisExecutor

object KotlinImmutabilityAnalysisRunner : KotlinAnalysisRunner<IORunnerArgs, IORunnerArgsParser>
    ("kotlin-immutability-analysis", IORunnerArgsParser) {
    override fun run(args: IORunnerArgs) {
        TestAnalysisExecutor(args.outputDir).execute(args.inputDir)
    }
}
