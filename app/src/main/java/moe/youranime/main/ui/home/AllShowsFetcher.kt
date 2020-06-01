package moe.youranime.main.ui.home

import AllShowsQuery
import android.util.Log
import com.apollographql.apollo.ApolloCall
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.exception.ApolloException
import moe.youranime.main.auth.RequestLoggerInterceptor
import moe.youranime.main.config.Configuration
import okhttp3.OkHttpClient

class AllShowsFetcher(val callback: Callback) {
    private lateinit var apolloClient: ApolloClient

    fun fetchAllShows() {
        setupClient()
        val query = AllShowsQuery()

        apolloClient.query(query).enqueue(object : ApolloCall.Callback<AllShowsQuery.Data>() {
            override fun onResponse(response: Response<AllShowsQuery.Data>) {
                val fetchedShowsData = response.data?.allShows
                if (fetchedShowsData != null) {
                    callback.onShowsFetched(fetchedShowsData.map { Show.create(it) }.filterNotNull())
                } else {
                    callback.onShowsFetched(listOf())
                }
            }

            override fun onFailure(e: ApolloException) {
                Log.i("AllShowsFetcher", e.message!!)
                callback.onShowsFetched(listOf())
            }
        })
    }

    private fun setupClient() {
        val okHttpClientBuilder = OkHttpClient.Builder()
            .addInterceptor(RequestLoggerInterceptor())

        val okHttpClient = okHttpClientBuilder.build()

        apolloClient = ApolloClient.builder()
            .serverUrl(Configuration.gqlHost)
            .okHttpClient(okHttpClient)
            .build()
    }
}
