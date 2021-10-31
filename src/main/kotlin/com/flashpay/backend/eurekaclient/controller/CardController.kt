package com.flashpay.backend.eurekaclient.controller

import com.flashpay.backend.eurekaclient.dto.CardDto
import com.flashpay.backend.eurekaclient.entity.UserWallet
import com.flashpay.backend.eurekaclient.exception.InvalidCardInputException
import com.flashpay.backend.eurekaclient.exception.UnauthenticatedUserException
import com.flashpay.backend.eurekaclient.service.CardService
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.MalformedJwtException
import io.swagger.v3.oas.annotations.Operation
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.BindingResult
import org.springframework.validation.FieldError
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

/************************************************************************************
 *    Author           Anush Raghavender
 *    Description      Card Controller Class That Provides API Routes For The Following Methods
 *                     addCard, deleteCard
 *    Version          1.0
 *    Created Date     28-09-2021
************************************************************************************/
@RestController
@CrossOrigin(origins = ["https://flashpay-frontend.herokuapp.com"], allowCredentials = "true")
class CardController(val cardService : CardService) {

    @Value("\${flashpay.app.jwtsecret}")
    val jwtSecret : String? = null

    object JWTConstants {
        const val emptyJWTToken = "User Not Authorized (Empty JWT Token)"
        const val malformedJWTToken = "User Not Authorized (Malformed JWT Token)"
        const val expiredJWTToken = "User Not Authorized (Expired JWT Token)"
    }

    /************************************* Add Card *************************************/
    @Operation(summary = "Add Card To User Account")
    @PostMapping("/card/{jwtToken}")
    fun addCard(@PathVariable jwtToken : String?, @Valid @RequestBody cardDto : CardDto, bindingResult : BindingResult) : ResponseEntity<UserWallet?> {
        val errorMessage = StringBuilder()
        if(bindingResult.hasErrors()) {
            val errors : List<FieldError> = bindingResult.fieldErrors
            for(error in errors) {
                errorMessage.append(error.defaultMessage + " ")
            }
            throw InvalidCardInputException(errorMessage.toString())
        }
        try {
            if(jwtToken == null) {
                throw UnauthenticatedUserException(JWTConstants.emptyJWTToken)
            }
            val jwtBody = Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(jwtToken).body
            val createdCard : UserWallet? = cardService.addCard(jwtBody.issuer.toLong(), cardDto)
            return ResponseEntity<UserWallet?>(createdCard, HttpStatus.CREATED)
        }
        catch(e : MalformedJwtException) {
            throw UnauthenticatedUserException(JWTConstants.malformedJWTToken)
        }
        catch(e : ExpiredJwtException) {
            throw UnauthenticatedUserException(JWTConstants.expiredJWTToken)
        }
    }

    /************************************* Update Card *************************************/
    @Operation(summary = "Update Default Card")
    @PatchMapping("/card")
    fun updateDefaultCard(@RequestParam jwtToken : String?, @RequestParam cardNumber : Long) : ResponseEntity<UserWallet?> {
        try {
            if(jwtToken == null) {
                throw UnauthenticatedUserException(JWTConstants.emptyJWTToken)
            }
            val jwtBody = Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(jwtToken).body
            val phoneNumber =  jwtBody.issuer.toLong()
            val updatedCard : UserWallet? = cardService.updateCard(phoneNumber, cardNumber)
            return ResponseEntity<UserWallet?>(updatedCard, HttpStatus.ACCEPTED)
        }
        catch(e : MalformedJwtException) {
            throw UnauthenticatedUserException(JWTConstants.malformedJWTToken)
        }
        catch(e : ExpiredJwtException) {
            throw UnauthenticatedUserException(JWTConstants.expiredJWTToken)
        }
    }

    /************************************* Delete Card *************************************/
    @Operation(summary = "Delete Card From User Account")
    @DeleteMapping("/card")
    fun deleteCard(@RequestParam jwtToken : String?, @RequestParam cardNumber : Long) : ResponseEntity<UserWallet?> {
        try {
            if(jwtToken == null) {
                throw UnauthenticatedUserException(JWTConstants.emptyJWTToken)
            }
            val jwtBody = Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(jwtToken).body
            val phoneNumber =  jwtBody.issuer.toLong()
            val deletedCard : UserWallet? = cardService.deleteCard(phoneNumber, cardNumber)
            return ResponseEntity<UserWallet?>(deletedCard, HttpStatus.ACCEPTED)
        }
        catch(e : MalformedJwtException) {
            throw UnauthenticatedUserException(JWTConstants.malformedJWTToken)
        }
        catch(e : ExpiredJwtException) {
            throw UnauthenticatedUserException(JWTConstants.expiredJWTToken)
        }
    }
}