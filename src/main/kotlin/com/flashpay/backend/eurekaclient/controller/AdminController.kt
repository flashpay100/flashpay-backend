package com.flashpay.backend.eurekaclient.controller

import com.flashpay.backend.eurekaclient.entity.UserWallet
import com.flashpay.backend.eurekaclient.exception.NonAdminAccountException
import com.flashpay.backend.eurekaclient.exception.UnauthenticatedUserException
import com.flashpay.backend.eurekaclient.service.UserService
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.MalformedJwtException
import io.swagger.v3.oas.annotations.Operation
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

/************************************************************************************
 *    Author           Anush Raghavender
 *    Description      Admin Controller Class That Provides API Routes For The Following Methods
 *                     getAllUsers
 *    Version          1.0
 *    Created Date     30-09-2021
************************************************************************************/
@RestController
@CrossOrigin(origins = ["https://flashpay-frontend.herokuapp.com"], allowCredentials = "true")
class AdminController(val userService : UserService) {

    @Value("\${flashpay.app.jwtsecret}")
    val jwtSecret : String? = null

    /************************************* Get All Users *************************************/
    @Operation(summary = "Get All Users With Their Details")
    @GetMapping("/users")
    fun getAllUsers(@RequestParam jwtToken : String?) : ResponseEntity<List<UserWallet>> {
        try {
            if(jwtToken == null) {
                throw UnauthenticatedUserException("User Not Authorized (Empty JWT Token)")
            }
            val jwtBody = Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(jwtToken).body
            val retrievedUser : UserWallet? = userService.getUser(jwtBody.issuer.toLong())
            if(!retrievedUser?.accountType.equals("Admin")) {
                throw NonAdminAccountException("Account Type Is Not Admin")
            }
            val foundUsers : List<UserWallet>? = userService.getAllUsers()
            return ResponseEntity<List<UserWallet>>(foundUsers, HttpStatus.OK)
        }
        catch(e : MalformedJwtException) {
            throw UnauthenticatedUserException("User Not Authorized (Malformed JWT Token)")
        }
        catch(e : ExpiredJwtException) {
            throw UnauthenticatedUserException("User Not Authorized (Expired JWT Token)")
        }
    }
}