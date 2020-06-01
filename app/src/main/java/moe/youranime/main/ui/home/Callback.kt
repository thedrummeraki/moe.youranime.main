package moe.youranime.main.ui.home

interface Callback {
    fun onShowsFetched(shows: List<Show>)
    fun onShowsRefetchRequest()
}
