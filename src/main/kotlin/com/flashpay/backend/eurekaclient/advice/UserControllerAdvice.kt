package com.flashpay.backend.eurekaclient.advice

import com.flashpay.backend.eurekaclient.exception.*
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@ControllerAdvice
class UserControllerAdvice {

    /***** Handle Exceptions That Can Occur During SignUp Process *****/
    @ExceptionHandler
    fun handleInvalidUserInputException(invalidUserInputException: InvalidUserInputException) : ResponseEntity<String> {
        return ResponseEntity<String>(invalidUserInputException.message, HttpStatus.BAD_REQUEST)
    }
    @ExceptionHandler
    fun handleExistingEmailAddressException(emailAddressAlreadyExistsException : ExistingEmailAddressException) : ResponseEntity<String> {
        return ResponseEntity<String>("User With Entered Email Address Already Exists", HttpStatus.BAD_REQUEST)
    }
    @ExceptionHandler
    fun handleExistingPhoneNumberException(phoneNumberAlreadyExistsException : ExistingPhoneNumberException) : ResponseEntity<String> {
        return ResponseEntity<String>("User With Entered Phone Number Already Exists", HttpStatus.BAD_REQUEST)
    }
    @ExceptionHandler
    fun handleExistingInvestmentAccountNameException(existingInvestmentAccountNameException : ExistingInvestmentAccountNameException) : ResponseEntity<String> {
        return ResponseEntity<String>("Investment Account Name Already Taken", HttpStatus.BAD_REQUEST)
    }
    @ExceptionHandler
    fun handleExistingServiceAccountNameException(existingServiceAccountNameException : ExistingServiceAccountNameException) : ResponseEntity<String> {
        return ResponseEntity<String>("Service Account Name Already Taken", HttpStatus.BAD_REQUEST)
    }

    /***** Handle Exception That Can Occur During SignIn, Update/Delete User, Deposit/Withdraw/Transfer Amount Process *****/
    @ExceptionHandler
    fun handleUserNotFoundException(userNotFoundException : UserNotFoundException) : ResponseEntity<String> {
        return ResponseEntity<String>("User Not Found", HttpStatus.NOT_FOUND)
    }

    /***** Handle Exception That Can Occur During SignIn, Update Password Process *****/
    @ExceptionHandler
    fun handleIncorrectPasswordException(incorrectPasswordException : IncorrectPasswordException) : ResponseEntity<String> {
        return ResponseEntity<String>("Incorrect Password Entered", HttpStatus.UNAUTHORIZED)
    }

    /***** Handle Exception That Can Occur During Update Process ****/
    @ExceptionHandler
    fun handleNewNameSameException(newNameSameException : NewNameSameException) : ResponseEntity<String> {
        return ResponseEntity<String>("New User Name Cannot Be Equal To Old User Name", HttpStatus.BAD_REQUEST)
    }
    @ExceptionHandler
    fun handleNewEmailSameException(newEmailSameException : NewEmailSameException) : ResponseEntity<String> {
        return ResponseEntity<String>("New Email Address Cannot Be Equal To Old Email Address", HttpStatus.BAD_REQUEST)
    }
    @ExceptionHandler
    fun handleNewPasswordSameException(newPasswordSameException : NewPasswordSameException) : ResponseEntity<String> {
        return ResponseEntity<String>("New Password Cannot Be Equal To Old Password", HttpStatus.BAD_REQUEST)
    }
    @ExceptionHandler
    fun handleNewPhoneSameException(newPhoneSameException : NewPhoneSameException) : ResponseEntity<String> {
        return ResponseEntity<String>("New Phone Number Cannot Be Equal To Old Phone Number", HttpStatus.BAD_REQUEST)
    }

    /***** Handle Exception That Can Occur During Deletion ****/
    @ExceptionHandler
    fun handleDeleteAdminAccountException(deleteAdminAccountException : DeleteAdminAccountException) : ResponseEntity<String> {
        return ResponseEntity<String>("Cannot Delete Admin Account", HttpStatus.BAD_REQUEST)
    }

    /***** Handle Exception That Can Occur During User Authentication ****/
    @ExceptionHandler
    fun handleUnauthenticatedUserException(unauthenticatedUserException : UnauthenticatedUserException) : ResponseEntity<String> {
        return ResponseEntity<String>(unauthenticatedUserException.message, HttpStatus.UNAUTHORIZED)
    }

    /***** Handle Exception That Can Occur During Fetching Admin Account *****/
    @ExceptionHandler
    fun handleAdminNotFoundException(adminNotFoundException : AdminNotFoundException) : ResponseEntity<String> {
        return ResponseEntity<String>("Admin Account Not Found", HttpStatus.NOT_FOUND)
    }

    /***** Handle Exception That Can Occur During Fetching All Users *****/
    @ExceptionHandler
    fun handleNoUsersFoundException(noUsersFoundException : NoUsersFoundException) : ResponseEntity<String> {
        return ResponseEntity<String>("No Users Found", HttpStatus.NOT_FOUND)
    }

    /***** Handle Exception That Can Occur During Fetching Accounts Process *****/
    @ExceptionHandler
    fun handleNoAccountsFoundException(noAccountsFoundException : NoAccountsFoundException) : ResponseEntity<String> {
        return ResponseEntity<String>("No Accounts Found", HttpStatus.BAD_REQUEST)
    }

    /***** Handle Exception That Can Occur During Admin SignIn *****/
    @ExceptionHandler
    fun handleNonAdminAccountException(nonAdminAccountException : NonAdminAccountException) : ResponseEntity<String> {
        return ResponseEntity<String>("Account Does Not Have Admin Privilege", HttpStatus.UNAUTHORIZED)
    }
}