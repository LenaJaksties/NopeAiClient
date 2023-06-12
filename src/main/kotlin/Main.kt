
import guiview.GUIMain
import socketinterface.*
import java.util.*

/**
 * Main to start the Card Game Nope
 */

fun main() {


    val scanner = Scanner(System.`in`)
    //Server URL
    val serverURL = "https://nope-server.azurewebsites.net/"
    val restapi = RestApi()

    var token: AccessToken? = null
    var loggedIn = false
    var username = ""

    // user login via console
    while(!loggedIn){
        println("Welcome to Nope - You need to log in before you can play:")
        println("1: Register \n2: Login")
        val choice = scanner.nextInt()
        when(choice){
            1 -> {
                println("Registration:")
                print("Enter username: ")
                username = scanner.next()
                print("Enter password: ")
                val password = scanner.next()
                print("Enter first name: ")
                val firstName = scanner.next()
                print("Enter last name: ")
                val lastName = scanner.next()

                val registerSuccess = restapi.registerUser(username, password, firstName, lastName)

                if (registerSuccess) {
                    println("Registration was successful.")
                } else {
                    println("Registration failed.")
                }
            }
            2 -> {
                println("Login:")
                print("Enter username: ")
                username = scanner.next()
                print("Enter password: ")
                val password = scanner.next()

                token = restapi.userLogin(username, password)

                if (token != null) {
                    println("Login successful.")
                    loggedIn = true
                } else {
                    println("Login failed.")
                }
            }
            else ->{
                println("Invalid choice. Please try again")
            }
        }
    }

    // Create an instance of the StartMenu class
    val menu = GUIMain()

    //Socket init
    if(token != null){
        val mSocket = socketInit(serverURL, token, menu, username)
        if(mSocket!=  null){
            println("SocketInit was successful")
        }
        connect()
        restapi.connect(token)
    }else{
        println("Your login/ was not valid")
    }

}


