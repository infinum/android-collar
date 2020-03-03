package co.infinum.generator.utils

import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class SynchronousExecutor {

    private val executor: ExecutorService = Executors.newSingleThreadExecutor()
    private val progress = Progress()

    fun start(task: Runnable) {
        executor.execute(task)
        progress.start()
    }

    fun await() {
        executor.shutdown()
        executor.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS)
        stop()
    }

    private fun stop() {
        hideProgress()
        println()
        System.out.flush()
    }

    private fun hideProgress() {
        progress.showProgress = false
    }

    private class Progress : Thread() {

        private val PROGRESS_PERIOD = 200L // ms

        var showProgress = true

        override fun run() {
            while(showProgress) {
                print(".")
                System.out.flush()
                Thread.sleep(PROGRESS_PERIOD)
            }
        }
    }
}