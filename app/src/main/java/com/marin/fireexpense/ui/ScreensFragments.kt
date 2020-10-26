package com.marin.fireexpense.ui

import androidx.navigation.NavDirections
import com.marin.fireexpense.data.model.Expense

class FeedingFragment : BaseFragment() {

    override var type: String = "Alimentación"

    override fun openChart(itemsList: Array<Expense>): NavDirections =
        FeedingFragmentDirections.actionNavFeedingToChartFragment(itemsList)
}

class WearFragment : BaseFragment() {

    override var type: String = "Vestuario"

    override fun openChart(itemsList: Array<Expense>): NavDirections =
        WearFragmentDirections.actionNavWearToChartFragment(itemsList)
}

class LeisureFragment : BaseFragment() {

    override var type: String = "Ocio"

    override fun openChart(itemsList: Array<Expense>): NavDirections =
        LeisureFragmentDirections.actionNavLeisureToChartFragment(itemsList)
}

class TravelsFragment : BaseFragment() {

    override var type: String = "Viajes"

    override fun openChart(itemsList: Array<Expense>): NavDirections  =
        TravelsFragmentDirections.actionNavTravelsToChartFragment(itemsList)
}

class HomeFragment : BaseFragment() {

    override var type: String = "Hogar"

    override fun openChart(itemsList: Array<Expense>): NavDirections =
        HomeFragmentDirections.actionNavHomeToChartFragment(itemsList)
}