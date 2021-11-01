package com.flashpay.backend.eurekaclient.controller

import com.flashpay.backend.eurekaclient.entity.UserWallet
import com.flashpay.backend.eurekaclient.exception.UnauthenticatedUserException
import com.flashpay.backend.eurekaclient.service.WalletService
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
 *    Description      Wallet Controller Class That Provides API Routes For The Following Methods
 *                     depositAmount, bankTransfer, payment
 *    Version          1.0
 *    Created Date     29-09-2021
************************************************************************************/
@RestController
@CrossOrigin(origins = ["https://flashpay-frontend.herokuapp.com"], allowCredentials = "true")
class WalletController(val walletService : WalletService) {

    @Value("\${flashpay.app.jwtsecret}")
    val jwtSecret : String? = null

    object JWTConstants {
        const val emptyJWTToken = "User Not Authorized (Empty JWT Token)"
        const val malformedJWTToken = "User Not Authorized (Malformed JWT Token)"
        const val expiredJWTToken = "User Not Authorized (Expired JWT Token)"
    }

    /************************************* Deposit Amount *************************************/
    @Operation(summary = "Transfer Amount From Bank To Wallet")
    @PutMapping("/wallet/depositamount")
    fun depositAmount(@RequestParam jwtToken : String?, @RequestParam cardNumber : Long, @RequestParam amount : Double) : ResponseEntity<UserWallet> {
        try {
            if(jwtToken == null) {
                throw UnauthenticatedUserException(JWTConstants.emptyJWTToken)
            }
            val jwtBody = Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(jwtToken).body
            val phoneNumber =  jwtBody.issuer.toLong()
            val updatedUserWallet : UserWallet? = walletService.depositAmount(phoneNumber, cardNumber, amount)
            return ResponseEntity<UserWallet>(updatedUserWallet, HttpStatus.ACCEPTED)
        }
        catch(e : MalformedJwtException) {
            throw UnauthenticatedUserException(JWTConstants.malformedJWTToken)
        }
        catch(e : ExpiredJwtException) {
            throw UnauthenticatedUserException(JWTConstants.expiredJWTToken)
        }
    }

    /************************************* Bank Transfer *************************************/
    @Operation(summary = "Transfer Amount From Wallet To Bank")
    @PutMapping("/wallet/banktransfer")
    fun bankTransfer(@RequestParam jwtToken : String?, @RequestParam cardNumber : Long, @RequestParam amount : Double) : ResponseEntity<UserWallet> {
        try {
            if(jwtToken == null) {
                throw UnauthenticatedUserException(JWTConstants.emptyJWTToken)
            }
            val jwtBody = Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(jwtToken).body
            val phoneNumber =  jwtBody.issuer.toLong()
            val updatedUserWallet : UserWallet? = walletService.bankTransfer(phoneNumber, cardNumber, amount)
            return ResponseEntity<UserWallet>(updatedUserWallet, HttpStatus.ACCEPTED)
        }
        catch(e : MalformedJwtException) {
            throw UnauthenticatedUserException(JWTConstants.malformedJWTToken)
        }
        catch(e : ExpiredJwtException) {
            throw UnauthenticatedUserException(JWTConstants.expiredJWTToken)
        }
    }

    /************************************* Payment *************************************/
    @Operation(summary = "Transfer Amount From 1 Account To Another")
    @PutMapping("/wallet/payment")
    fun payment(@RequestParam jwtToken : String?, @RequestParam phoneNumber : Long, @RequestParam amount : Double) : ResponseEntity<UserWallet> {
        try {
            if(jwtToken == null) {
                throw UnauthenticatedUserException(JWTConstants.emptyJWTToken)
            }
            val jwtBody = Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(jwtToken).body
            val phoneNumber1 =  jwtBody.issuer.toLong()
            val phoneNumber2 : Long = phoneNumber
            val updatedUserWallet : UserWallet? = walletService.payment(phoneNumber1, phoneNumber2, amount)
            return ResponseEntity<UserWallet>(updatedUserWallet, HttpStatus.ACCEPTED)
        }
        catch(e : MalformedJwtException) {
            throw UnauthenticatedUserException(JWTConstants.malformedJWTToken)
        }
        catch(e : ExpiredJwtException) {
            throw UnauthenticatedUserException(JWTConstants.expiredJWTToken)
        }
    }

    /************************************* Sell Investment *************************************/
    @Operation(summary = "Sell Investment")
    @PutMapping("/wallet/sellinvestment")
    fun sellInvestment(@RequestParam jwtToken : String?, @RequestParam investmentAccountName : String, @RequestParam amount : Double) : ResponseEntity<UserWallet> {
        try {
            if(jwtToken == null) {
                throw UnauthenticatedUserException(JWTConstants.emptyJWTToken)
            }
            val jwtBody = Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(jwtToken).body
            val phoneNumber =  jwtBody.issuer.toLong()
            val updatedUserWallet : UserWallet? = walletService.sellInvestment(phoneNumber, investmentAccountName, amount)
            return ResponseEntity<UserWallet>(updatedUserWallet, HttpStatus.ACCEPTED)
        }
        catch(e : MalformedJwtException) {
            throw UnauthenticatedUserException(JWTConstants.malformedJWTToken)
        }
        catch(e : ExpiredJwtException) {
            throw UnauthenticatedUserException(JWTConstants.expiredJWTToken)
        }
    }
}
