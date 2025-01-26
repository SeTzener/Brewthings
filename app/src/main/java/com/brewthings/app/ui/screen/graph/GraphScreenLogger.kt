package com.brewthings.app.ui.screen.graph

import com.brewthings.app.util.Logger

object GraphScreenLogger {
    private val logger = Logger("GraphScreen")

    fun logGraphSelect(index: Int?) {
        logger.info("GraphSelect: index=$index")
    }

    fun logPagerSelect(index: Int) {
        logger.info("PagerSelect: index=$index")
    }

    fun logGraph(index: Int?, animated: Boolean) {
        logger.info("Graph: index=$index animated=$animated")
    }

    fun logPager(index: Int?, animated: Boolean) {
        logger.info("Pager: index=$index animated=$animated")
    }
}
