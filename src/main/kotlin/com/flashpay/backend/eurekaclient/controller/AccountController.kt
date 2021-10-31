package com.flashpay.backend.eurekaclient.controller

import com.flashpay.backend.eurekaclient.dto.UserDto
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
 *    Description      Account Controller Class That Provides API Routes For The Following Methods
 *                     getAccounts
 *    Version          1.0
 *    Created Date     01-10-2021
 ************************************************************************************/
@RestController
@CrossOrigin(origins = ["https://flashpay-frontend.herokuapp.com"], allowCredentials = "true")
class AccountController(val userService : UserService) {
    @Value("\${flashpay.app.jwtsecret}")
    val jwtSecret : String?  = null

    /************************************* Get Accounts *************************************/
    @Operation(summary = "Get Accounts")
    @GetMapping("/accounts")
    fun getAccounts(@RequestParam jwtToken : String?) : ResponseEntity<List<UserDto>> {
        try {
            if(jwtToken == null) {
                throw UnauthenticatedUserException("User Not Authorized (Empty JWT Token)")
            }
            val jwtBody = Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(jwtToken).body
            val phoneNumber =  jwtBody.issuer.toLong()
            val retrievedAccounts : List<UserDto>? = userService.getAccounts(phoneNumber)
            return ResponseEntity<List<UserDto>>(retrievedAccounts, HttpStatus.OK)
        }
        catch(e : MalformedJwtException) {
            throw UnauthenticatedUserException("User Not Authorized (Malformed JWT Token)")
        }
        catch(e : ExpiredJwtException) {
            throw UnauthenticatedUserException("User Not Authorized (Expired JWT Token)")
        }
    }
}