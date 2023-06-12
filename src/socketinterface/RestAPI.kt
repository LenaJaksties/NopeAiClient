package socketinterface

import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse.BodyHandlers
import com.google.gson.Gson

/*
 * Http Requests - REST API code
 */

class RestApi {
    /**
     * registers a new user
     */
    fun registerUser(uName: String, uPassword:String, firstName:String, lastName:String): Boolean {

        val newUser = Register(uName,uPassword,firstName,lastName)
        var registerSuccess = false

        val client = HttpClient.newHttpClient()
        val gson = Gson()
        val jsonRequest = gson.toJson(newUser)

        val request = HttpRequest.newBuilder()
            .uri(URI("https://nope-server.azurewebsites.net/api/auth/register"))
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(jsonRequest))
            .build()

        val postResponse = client.send(request, BodyHandlers.ofString())
        println("Register Response: ")
        println(postResponse.body())
        if (postResponse.statusCode() == 200) {
            print("User was registered correctly")
            registerSuccess = true
        }
        return registerSuccess
    }

    /**
     * logs the user in to the server
     */
    fun userLogin(uName: String, uPassword:String): AccessToken? {
        // create Login Object with data and convert to Json with gson
        val loginOb = Login(uName, uPassword)
        val gson = Gson()
        val jsonLogRequest = gson.toJson(loginOb)
        val client = HttpClient.newHttpClient()

        //Send a Http Request with jsonLogRequest to server
        val request = HttpRequest.newBuilder()
            .uri(URI("https://nope-server.azurewebsites.net/api/auth/login"))
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(jsonLogRequest))
            .build()

        // store the access token if Login was valid and return accessToken
        val loginResponse = client.send(request, BodyHandlers.ofString())
        if (loginResponse.statusCode() == 200) {
            val responseBody = loginResponse.body()
            val accessToken = extractAccessToken(responseBody)
            //store the token into class Token
            val token = AccessToken(accessToken)
            println("Login Response: ")
            println(loginResponse.body())
            println(accessToken)
            return token

        }
        return null
    }

    fun connect(accessToken: AccessToken){
        // create Login Object with data and convert to Json with gson
        val token = Token(accessToken.accessToken.toString())
        val gson = Gson()
        val jsonAuthentication = gson.toJson(token)
        val client = HttpClient.newHttpClient()

        //Send a Http Request with jsonLogRequest to server
        val request = HttpRequest.newBuilder()
            .uri(URI("https://nope-server.azurewebsites.net/api/verify-token"))
            .header("Content-Type", "application/json")
            .header("Authorization", "Bearer " + token.token)
            .POST(HttpRequest.BodyPublishers.ofString(jsonAuthentication))
            .build()

        // store the access token if Login was valid and return accessToken
        val authenticationResponse = client.send(request, BodyHandlers.ofString())
        if (authenticationResponse.statusCode() == 200) {
            val responseBody = authenticationResponse.body()
            println("Authentication response: ")
            println(responseBody)
        }

    }

    /**
     * extract token from the original response body via regex matching
     */
    private fun extractAccessToken(responseBody: String): String? {
        val regex = "\"accessToken\":\"(.*?)\"".toRegex()
        val matchResult = regex.find(responseBody)
        return matchResult?.groupValues?.getOrNull(1)
    }

}
