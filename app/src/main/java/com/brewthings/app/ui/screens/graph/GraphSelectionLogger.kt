package com.brewthings.app.ui.screens.graph

import com.brewthings.app.util.Logger

object GraphSelectionLogger {
    private val logger = Logger("GraphSelection")

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
