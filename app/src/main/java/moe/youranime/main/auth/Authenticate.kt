package moe.youranime.main.auth

import CheckTokenQuery
import SigninUserMutation
import android.util.Log
import com.apollographql.apollo.ApolloCall
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.exception.ApolloException
import moe.youranime.main.config.Configuration
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.lang.Exception

class Authenticator(val callback: Callback) {
    private lateinit var apolloClient: ApolloClient;

    fun login(username: String, password: String) {
        setupClient(null)
        val mutation = SigninUserMutation(username, password)

        apolloClient.mutate(mutation).enqueue(object : ApolloCall.Callback<SigninUserMutation.Data>() {
            override fun onResponse(response: com.apollographql.apollo.api.Response<SigninUserMutation.Data>) {
                val fetchedUser = response.data?.signinUser?.user
                val user = User.fromGraphql(fetchedUser)
                if (user != null) {
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

    fun checkToken(token: String) {
        setupClient(token)
        val query = CheckTokenQuery()

        apolloClient.query(query).enqueue(object : ApolloCall.Callback<CheckTokenQuery.Data>() {
            override fun onResponse(response: com.apollographql.apollo.api.Response<CheckTokenQuery.Data>) {
                val fetchedUser = response.data?.currentUser
                val user = User.fromGraphql(fetchedUser)
                if (user != null) {
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
    override fun intercept(chain: Interceptor.Chain): Response {
        val request: Request = chain.request()

        val t1 = System.nanoTime()
        Log.i("Authenticate", String.format("Sending request %s on %s%n%s",
            request.url(), chain.connection(), request.headers()))

        val response: Response = chain.proceed(request)

        val t2 = System.nanoTime()
        Log.i("Authenticate", java.lang.String.format(
            "Received response for %s in %.1fms%n%s",
            response.request().url(), (t2 - t1) / 1e6, response.headers()))

        return response
    }
}

class RequestWithTokenInterceptor(val token: String) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request: Request = chain.request()
        val requestWithToken = request.newBuilder()
            .header("Authorization", token)
            .build()

        return chain.proceed(requestWithToken)
    }
}
