package co.infinum.generator.utils

import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class SynchronousExecutor {

    private val executor: ExecutorService = Executors.newSingleThreadExecutor()

    fun start(task: Runnable) {
        executor.execute(task)
    }

    fun await() {
        executor.shutdown()
        executor.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS)
        stop()
    }

    private fun stop() {
        println()
        System.out.flush()
    }
}