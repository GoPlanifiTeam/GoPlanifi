package com.example.travelplanner.domain.service

import com.example.travelplanner.domain.model.User

interface AuthenticationService {
    fun login(user: User): Boolean
    fun logout(user: User): Boolean
    fun resetPassword(user: User): Boolean
}

/*
package com.example.travelplanner.domain.model

data class Authentication(
    var userId: User,
    var loginErrors: Int,
)
{
    fun login(user: User): Boolean {
        var userEmail = user.email
        var userPassword = user.password
        //@TODO: Implementar Login, no cogemos email y password, si no el objeto de un Usuario
        return true
    }
    fun logout(user: User): Boolean {
        var userEmail = user.email
        var userPassword = user.password
        //@TODO: Implementar LogOut
        return true
    }
    fun resetPassword(user: User): Boolean {
        var userEmail = user.email
        //@TODO: Implementar ResetPassword
        return true
    }
}

 */