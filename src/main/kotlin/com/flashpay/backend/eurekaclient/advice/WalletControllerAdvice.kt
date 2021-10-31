package com.flashpay.backend.eurekaclient.advice

import com.flashpay.backend.eurekaclient.exception.*
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@ControllerAdvice
class WalletControllerAdvice {

    /***** Handle Exception That Can Occur During Bank Transfer, Payment *****/
    @ExceptionHandler
    fun handleInsufficientWalletBalanceException(insufficientWalletBalanceException : InsufficientWalletBalanceException) : ResponseEntity<String> {
        return ResponseEntity<String>("Insufficient Wallet Balance", HttpStatus.BAD_REQUEST)
    }

    /***** Handle Exception That Can Occur During Payment *****/
    @ExceptionHandler
    fun handleReadOnlyAccountException(readOnlyAccountException : ReadOnlyAccountException) : ResponseEntity<String> {
        return ResponseEntity<String>("Account Is Read Only", HttpStatus.BAD_REQUEST)
    }

    /***** Exception That Can Occur During Payment To Admin *****/
    @ExceptionHandler
    fun handleCannotPayToAdminException(cannotPayToAdminException : CannotPayToAdminException) : ResponseEntity<String> {
        return ResponseEntity<String>("Cannot Pay Amount To Admin", HttpStatus.BAD_REQUEST)
    }

    /***** Handle Exception That Can Occur During Deposit Amount, Bank Transfer & Payment *****/
    @ExceptionHandler
    fun handleInactiveAccountException(inactiveAccountException : InactiveAccountException) : ResponseEntity<String> {
        return ResponseEntity<String>("Account Is Inactive", HttpStatus.BAD_REQUEST)
    }

    /***** Handle Exception That Can Occur During Sell Investment *****/
    @ExceptionHandler
    fun handleInsufficientInvestmentBalanceException(insufficientInvestmentBalanceException : InsufficientInvestmentBalanceException) : ResponseEntity<String> {
        return ResponseEntity<String>("Insufficient Investment Balance", HttpStatus.BAD_REQUEST)
    }
}