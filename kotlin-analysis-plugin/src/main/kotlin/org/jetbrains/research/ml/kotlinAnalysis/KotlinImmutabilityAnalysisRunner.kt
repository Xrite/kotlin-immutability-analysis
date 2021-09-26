package org.jetbrains.research.ml.kotlinAnalysis

import org.jetbrains.research.ml.kotlinAnalysis.util.RepositoryOpenerUtil
import org.jetbrains.research.pluginUtilities.runners.BaseRunner
import org.jetbrains.research.pluginUtilities.runners.IORunnerArgs
import org.jetbrains.research.pluginUtilities.runners.IORunnerArgsParser
import test.test.ImmutabilityAnalysisExecutor
import test.test.loadConfig


object KotlinImmutabilityAnalysisRunner : BaseRunner<IORunnerArgs, IORunnerArgsParser>
    ("kotlin-immutability-analysis", IORunnerArgsParser) {
    override fun run(args: IORunnerArgs) {
        val config = loadConfig(args.inputDir)
        val executors = config.tasks.map {
            ImmutabilityAnalysisExecutor(args.outputDir, it)
        }
        MultipleAnalysisExecutor(executors).execute(args.inputDir, RepositoryOpenerUtil::openReloadRepositoryOpener)
    }
}
