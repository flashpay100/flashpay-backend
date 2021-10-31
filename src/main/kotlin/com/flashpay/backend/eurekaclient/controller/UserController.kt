package com.flashpay.backend.eurekaclient.controller

import com.flashpay.backend.eurekaclient.dto.UserDto
import com.flashpay.backend.eurekaclient.entity.UserWallet
import com.flashpay.backend.eurekaclient.exception.InvalidUserInputException
import com.flashpay.backend.eurekaclient.exception.UnauthenticatedUserException
import com.flashpay.backend.eurekaclient.service.UserService
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.MalformedJwtException
import io.jsonwebtoken.SignatureAlgorithm
import io.swagger.v3.oas.annotations.Operation
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.BindingResult
import org.springframework.validation.FieldError
import org.springframework.web.bind.annotation.*
import java.util.*
import javax.servlet.http.Cookie
import javax.servlet.http.HttpServletResponse
import javax.validation.Valid

/************************************************************************************
 *    Author           Anush Raghavender
 *    Description      User Controller Class That Provides API Routes For The Following Methods
 *                     signUp, signIn, getUserDetails, updateUserName, updateAccountEmail,
 *                     updateAccountPassword, updateAccountPhone, deleteAccount, signOut
 *    Version          1.0
 *    Created Date     27-09-2021
************************************************************************************/
@RestController
@CrossOrigin(origins = ["https://flashpay-frontend.herokuapp.com"], allowCredentials = "true")
class UserController(val userService : UserService) {

    @Value("\${flashpay.app.jwtsecret}")
    val jwtSecret : String?  = null

    @Value("\${flashpay.app.jwtexpirydate}")
    val jwtExpiryDate : Long? = null

    object JWTConstants {
        const val emptyJWTToken = "User Not Authorized (Empty JWT Token)"
        const val malformedJWTToken = "User Not Authorized (Malformed JWT Token)"
        const val expiredJWTToken = "User Not Authorized (Expired JWT Token)"
    }

    /************************************* Test *************************************/
    @GetMapping("/test")
    fun test() : String = "Hello World"

    /************************************* SignUp/SignIn *************************************/
    @Operation(summary = "Create User Account")
    @PostMapping("/user/signup")
    fun signUp(@Valid @RequestBody user : UserDto, bindingResult : BindingResult) : ResponseEntity<Any> {
        val errorMessage = StringBuilder()
        if(bindingResult.hasErrors()) {
            val errors : List<FieldError> = bindingResult.fieldErrors
            for(error in errors) {
                errorMessage.append(error.defaultMessage + " ")
            }
            throw InvalidUserInputException(errorMessage.toString())
        }
        userService.createUser(user)
        return ResponseEntity<Any>("Signed Up", HttpStatus.CREATED)
    }

    @Operation(summary = "Authorize User")
    @PostMapping("/user/signin")
    fun signIn(@RequestParam phoneNumber : Long, @RequestParam password : String, response : HttpServletResponse) : ResponseEntity<Any> {
        val foundUser : UserWallet? = userService.authenticateUser(phoneNumber, password)
        val issuer = foundUser!!.id.toString()
        val expirationDate = Date(System.currentTimeMillis() + jwtExpiryDate!!)
        val secretKey = jwtSecret
        val jwt = Jwts.builder().setIssuer(issuer).setExpiration(expirationDate).signWith(SignatureAlgorithm.HS512, secretKey).compact()
        val cookie = Cookie("jwtToken", jwt)
        cookie.isHttpOnly = true
        cookie.path = "/"
        response.addCookie(cookie)
        return ResponseEntity<Any>(cookie, HttpStatus.OK)
    }

    /************************************* Get User Details *************************************/
    @Operation(summary = "Get User's Details")
    @GetMapping("/user")
    fun getUserDetails(@RequestParam jwtToken : String?) : ResponseEntity<Any> {
        try {
            if(jwtToken == null) {
                throw UnauthenticatedUserException(JWTConstants.emptyJWTToken)
            }
            val jwtBody = Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(jwtToken).body
            val authenticatedUser : UserWallet? = userService.getUser(jwtBody.issuer.toLong())
            return ResponseEntity<Any>(authenticatedUser, HttpStatus.OK)
        }
        catch(e : MalformedJwtException) {
            throw UnauthenticatedUserException(JWTConstants.malformedJWTToken)
        }
        catch(e : ExpiredJwtException) {
            throw UnauthenticatedUserException(JWTConstants.expiredJWTToken)
        }
    }

    /************************************* Update Account *************************************/
    @Operation(summary = "Update User's Name")
    @PatchMapping("/user/name")
    fun updateUserName(@RequestParam jwtToken : String?, @RequestParam newUserName : String) : ResponseEntity<UserWallet> {
        val regex = Regex("([A-Za-z]+)|([A-Za-z]+[ ][A-Za-z]+)|([A-Za-z]+[ ][A-Za-z]+[ ][A-Za-z]+)")
        if(newUserName.isEmpty()) {
            throw InvalidUserInputException("New User Name Cannot Be Empty")
        }
        if(newUserName.length > 30 || newUserName.length < 3) {
            throw InvalidUserInputException("User Name Should Be Between 3 & 30 Characters.")
        }
        if(!regex.matches(newUserName)) {
            throw InvalidUserInputException("User Name Can Have Only Alphabets & Maximum Of 3 Words.")
        }
        try {
            if(jwtToken == null) {
                throw UnauthenticatedUserException(JWTConstants.emptyJWTToken)
            }
            val jwtBody = Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(jwtToken).body
            val phoneNumber =  jwtBody.issuer.toLong()
            val updatedUserName : UserWallet? = userService.updateUserName(phoneNumber, newUserName)
            return ResponseEntity<UserWallet>(updatedUserName, HttpStatus.ACCEPTED)
        }
        catch(e : MalformedJwtException) {
            throw UnauthenticatedUserException(JWTConstants.malformedJWTToken)
        }
        catch(e : ExpiredJwtException) {
            throw UnauthenticatedUserException(JWTConstants.expiredJWTToken)
        }
    }

    @Operation(summary = "Update User's Email Address")
    @PatchMapping("/user/email")
    fun updateAccountEmail(@RequestParam jwtToken : String?, @RequestParam newEmailAddress : String) : ResponseEntity<UserWallet> {
        val regex = Regex("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")
        if(newEmailAddress.isEmpty()) {
            throw InvalidUserInputException("New Email Address Cannot Be Empty.")
        }
        if(!regex.matches(newEmailAddress)) {
            throw InvalidUserInputException("Enter A Valid Email.")
        }
        try {
            if(jwtToken == null) {
                throw UnauthenticatedUserException(JWTConstants.emptyJWTToken)
            }
            val jwtBody = Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(jwtToken).body
            val phoneNumber =  jwtBody.issuer.toLong()
            val updatedUserEmail : UserWallet? = userService.updateUserEmail(phoneNumber, newEmailAddress)
            return ResponseEntity<UserWallet>(updatedUserEmail, HttpStatus.ACCEPTED)
        }
        catch(e : MalformedJwtException) {
            throw UnauthenticatedUserException(JWTConstants.malformedJWTToken)
        }
        catch(e : ExpiredJwtException) {
            throw UnauthenticatedUserException(JWTConstants.expiredJWTToken)
        }
    }

    @Operation(summary = "Update User's Password")
    @PatchMapping("/user/password")
    fun updateAccountPassword(@RequestParam jwtToken : String?, @RequestParam password : String, @RequestParam newPassword : String) : ResponseEntity<UserWallet> {
        val regex = Regex("^(?=.{8,}\$)(?=.*[A-Za-z])(?=.*[0-9])(?=.*\\W).*\$")
        if(newPassword.isEmpty()) {
            throw InvalidUserInputException("New Password Cannot Be Empty.")
        }
        if(!regex.matches(newPassword)) {
            throw InvalidUserInputException("Length Of Password Should Be Atleast 8 & Contain 1 Digit, 1 Alphabet & 1 Special Character.")
        }
        try {
            if(jwtToken == null) {
                throw UnauthenticatedUserException(JWTConstants.emptyJWTToken)
            }
            val jwtBody = Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(jwtToken).body
            val phoneNumber =  jwtBody.issuer.toLong()
            val updatedUserPassword : UserWallet? = userService.updateUserPassword(phoneNumber, password, newPassword)
            return ResponseEntity<UserWallet>(updatedUserPassword, HttpStatus.ACCEPTED)
        }
        catch(e : MalformedJwtException) {
            throw UnauthenticatedUserException(JWTConstants.malformedJWTToken)
        }
        catch(e : ExpiredJwtException) {
            throw UnauthenticatedUserException(JWTConstants.expiredJWTToken)
        }
    }

    @Operation(summary = "Update User's Phone Number")
    @PatchMapping("/user/phone")
    fun updateAccountPhone(@RequestParam jwtToken : String?, @RequestParam password : String, @RequestParam newPhoneNumber : String, response : HttpServletResponse) : ResponseEntity<Any> {
        val regex = Regex("[0-9]{10}")
        if(newPhoneNumber.isEmpty()) {
            throw InvalidUserInputException("New Phone Number Cannot Be Empty.")
        }
        if(!regex.matches(newPhoneNumber)) {
            throw InvalidUserInputException("Phone Number Should Have 10 Digits & Only Numbers Are Allowed.")
        }
        try {
            if(jwtToken == null) {
                throw UnauthenticatedUserException(JWTConstants.emptyJWTToken)
            }
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(jwtToken).body
            val newJwt = Jwts.builder().setIssuer(newPhoneNumber).setExpiration(Date(System.currentTimeMillis() + jwtExpiryDate!!)).signWith(SignatureAlgorithm.HS512, jwtSecret).compact()
            val cookie = Cookie("jwtToken", newJwt)
            cookie.isHttpOnly = true
            cookie.path = "/"
            response.addCookie(cookie)
            val jwtBody = Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(jwtToken).body
            val phoneNumber =  jwtBody.issuer.toLong()
            userService.updateUserPhone(phoneNumber, password, newPhoneNumber.toLong())
            return ResponseEntity<Any>(cookie, HttpStatus.ACCEPTED)
        }
        catch(e : MalformedJwtException) {
            throw UnauthenticatedUserException(JWTConstants.malformedJWTToken)
        }
        catch(e : ExpiredJwtException) {
            throw UnauthenticatedUserException(JWTConstants.expiredJWTToken)
        }
    }

    /************************************* Delete Account *************************************/
    @Operation(summary = "Delete User Account")
    @DeleteMapping("/user")
    fun deleteAccount(@RequestParam jwtToken : String?, @RequestParam password : String, response : HttpServletResponse) : ResponseEntity<Any> {
        try {
            if(jwtToken == null) {
                throw UnauthenticatedUserException(JWTConstants.emptyJWTToken)
            }
            val jwtBody = Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(jwtToken).body
            val phoneNumber =  jwtBody.issuer.toLong()
            userService.deleteUser(phoneNumber, password)
            val cookie = Cookie("jwtToken", "")
            cookie.maxAge = 0
            cookie.isHttpOnly = true
            cookie.path = "/"
            response.addCookie(cookie)
            return ResponseEntity<Any>("Account Deleted", HttpStatus.ACCEPTED)
        }
        catch(e : MalformedJwtException) {
            throw UnauthenticatedUserException(JWTConstants.malformedJWTToken)
        }
        catch(e : ExpiredJwtException) {
            throw UnauthenticatedUserException(JWTConstants.expiredJWTToken)
        }
    }

    /************************************* SignOut *************************************/
    @Operation(summary = "Exit Application")
    @PostMapping("/user/signout")
    fun signOut(response : HttpServletResponse) : ResponseEntity<Any> {
        val cookie = Cookie("jwtToken", "")
        cookie.maxAge = 0
        cookie.isHttpOnly = true
        cookie.path = "/"
        response.addCookie(cookie)
        return ResponseEntity<Any>("Signed Out", HttpStatus.ACCEPTED)
    }
}