package com.flashpay.backend.eurekaclient.advice

import com.flashpay.backend.eurekaclient.exception.*
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@ControllerAdvice
class CardControllerAdvice {

    /***** Handle Exception That Can Occur During Card Addition Process *****/
    @ExceptionHandler
    fun handleExistingCardException(existingCardException : ExistingCardException) : ResponseEntity<String> {
        return ResponseEntity<String>("Entered Card Number Already Exists", HttpStatus.BAD_REQUEST)
    }
    @ExceptionHandler
    fun handleInvalidCardInputException(invalidCardInputException: InvalidCardInputException) : ResponseEntity<String> {
        return ResponseEntity<String>(invalidCardInputException.message, HttpStatus.BAD_REQUEST)
    }

    /***** Exceptions That Can Occur During Card Addition, Deposit Amount, Bank Transfer Process *****/
    @ExceptionHandler
    fun handleExpiredCardException(expiredCardException : ExpiredCardException) : ResponseEntity<String> {
        return ResponseEntity<String>("Card Is Expired", HttpStatus.BAD_REQUEST)
    }

    /***** Exception That Can Occur During Delete Card, Deposit Amount, Bank Transfer Process *****/
    @ExceptionHandler
    fun handleCardNotFoundException(cardNotFoundException : CardNotFoundException) : ResponseEntity<String> {
        return ResponseEntity<String>("Entered Card Number Not Found", HttpStatus.NOT_FOUND)
    }

    /***** Handle Exception That Can Occur During Update Card, Delete Card For User *****/
    @ExceptionHandler
    fun handleNoCardsFoundException(noCardsFoundException : NoCardsFoundException) : ResponseEntity<String> {
        return ResponseEntity<String>("No Cards Found For User", HttpStatus.NOT_FOUND)
    }

    /***** Handle Exception That Can Occur During Delete Card For User *****/
    @ExceptionHandler
    fun handleCannotDeleteDefaultCardException(cannotDeleteDefaultCardException : CannotDeleteDefaultCardException) : ResponseEntity<String> {
        return ResponseEntity<String>("Cannot Delete Default Card", HttpStatus.BAD_REQUEST)
    }

    /***** Handle Exception That Can Occur During Deposit Amount *****/
    @ExceptionHandler
    fun handleInsufficientCardBalanceException(insufficientCardBalanceException : InsufficientCardBalanceException) : ResponseEntity<String> {
        return ResponseEntity<String>("Insufficient Card Balance", HttpStatus.BAD_REQUEST)
    }
}