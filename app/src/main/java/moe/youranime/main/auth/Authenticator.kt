package moe.youranime.main.auth

import CheckTokenQuery
import SigninUserMutation
import android.content.Context
import android.util.Log
import com.apollographql.apollo.ApolloCall
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.exception.ApolloException
import com.apollographql.apollo.api.Response
import moe.youranime.main.config.Configuration
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import java.lang.Exception

typealias OkResponse = okhttp3.Response

class Authenticator(val callback: Callback, val context: Context) {
    private lateinit var apolloClient: ApolloClient

    fun login(username: String, password: String) {
        setupClient(null)
        val mutation = SigninUserMutation(username, password)

        apolloClient.mutate(mutation).enqueue(object : ApolloCall.Callback<SigninUserMutation.Data>() {
            override fun onResponse(response: Response<SigninUserMutation.Data>) {
                val fetchedUser = response.data?.signinUser?.user
                val fetchedToken = response.data?.signinUser?.token
                val user = User.fromGraphql(fetchedUser)
                if (user != null) {
                    AuthenticationTokenContract.save(context, fetchedToken)
                    User.Singleton.setCurrentUser(currentUser = user)
                    callback.onLoginSuccessful(user)
                } else {
                    callback.onLoginFailed(Exception("Invalid user."))
                }
            }

            override fun onFailure(e: ApolloException) {
                callback.onLoginFailed(e)
            }
        })
    }

    fun checkToken() {
        val token = AuthenticationTokenContract.getCurrentToken()
        setupClient(token)
        val query = CheckTokenQuery()

        apolloClient.query(query).enqueue(object : ApolloCall.Callback<CheckTokenQuery.Data>() {
            override fun onResponse(response: Response<CheckTokenQuery.Data>) {
                val fetchedUser = response.data?.currentUser
                val user = User.fromGraphql(fetchedUser)
                if (user != null) {
                    User.Singleton.setCurrentUser(currentUser = user)
                    callback.onTokenCheckSuccess(user)
                } else {
                    callback.onTokenCheckFailed(Exception("Invalid user."))
                }
            }

            override fun onFailure(e: ApolloException) {
                callback.onTokenCheckFailed(e)
            }
        })
    }

    private fun setupClient(token: String?) {
        val okHttpClientBuilder = OkHttpClient.Builder()
            .addInterceptor(RequestLoggerInterceptor())

        if (token != null) {
            okHttpClientBuilder.addInterceptor(RequestWithTokenInterceptor(token))
        }

        val okHttpClient = okHttpClientBuilder.build()

        apolloClient = ApolloClient.builder()
            .serverUrl(Configuration.gqlHost)
            .okHttpClient(okHttpClient)
            .build()
    }
}

class Credentials

class RequestLoggerInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): OkResponse {
        val request: Request = chain.request()

        val t1 = System.nanoTime()
        Log.i("Authenticate", String.format("Sending request %s on %s%n%s",
            request.url(), chain.connection(), request.headers()))

        val response: OkResponse = chain.proceed(request)

        val t2 = System.nanoTime()
        Log.i("Authenticate", java.lang.String.format(
            "Received response for %s in %.1fms%n%s",
            response.request().url(), (t2 - t1) / 1e6, response.headers()))

        return response
    }
}

class RequestWithTokenInterceptor(val token: String) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): OkResponse {
        val request: Request = chain.request()
        val requestWithToken = request.newBuilder()
            .header("Authorization", token)
            .build()

        return chain.proceed(requestWithToken)
    }
}
