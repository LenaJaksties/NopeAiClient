package socketinterface

/**
 * Data classes necessary for rest api and socket-io
 */
data class AccessToken(
    var accessToken: String?
)
data class Register(
    var username: String,
    var password: String,
    var firstname: String,
    var lastname: String
)
data class Login(
    var username: String,
    var password: String
)
data class Token(
    var token: String
)
