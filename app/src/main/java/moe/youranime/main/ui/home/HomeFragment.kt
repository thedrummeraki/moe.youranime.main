package moe.youranime.main.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import moe.youranime.main.R

class HomeFragment : Fragment(), Callback {

    private lateinit var showsfetcher: AllShowsFetcher

    private lateinit var swipeRefreshView: SwipeRefreshLayout
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var progressBar: ProgressBar
    private lateinit var recyclerView: RecyclerView
    private lateinit var shows: MutableList<Show>
    private lateinit var adapter: ShowsAdapter

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        showsfetcher = AllShowsFetcher(this)
        showsfetcher.fetchAllShows()

        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_home, container, false)

        swipeRefreshView = root.findViewById(R.id.fragment_home_swipe_layout)
        progressBar = root.findViewById(R.id.fragment_home_progress_bar)
        recyclerView = root.findViewById(R.id.fragment_home_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(context)
        shows = mutableListOf()
        adapter = ShowsAdapter(requireContext(), shows)
        recyclerView.adapter = adapter

        swipeRefreshView.setOnRefreshListener {
            onShowsRefetchRequest()
        }

        return root
    }

    override fun onShowsRefetchRequest() {
        showsfetcher.fetchAllShows()
    }

    override fun onShowsFetched(shows: List<Show>) {
        Log.i("HomeFragment", shows.toString())
        requireActivity().runOnUiThread {
            swipeRefreshView.isRefreshing = false
            progressBar.visibility = View.GONE
            this.shows.clear()
            this.shows.addAll(shows)

            adapter = ShowsAdapter(requireContext(), shows)
            recyclerView.adapter = adapter
            recyclerView.invalidate()
        }
    }
}
