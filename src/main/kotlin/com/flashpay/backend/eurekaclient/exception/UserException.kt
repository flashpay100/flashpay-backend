package com.flashpay.backend.eurekaclient.exception

import kotlin.RuntimeException

open class UserException(errorMessage : String) : RuntimeException(errorMessage) 

/***** Exceptions That Can Occur During SignUp Process *****/
class InvalidUserInputException(errorMessage : String) : UserException(errorMessage)
class ExistingEmailAddressException(errorMessage : String) : UserException(errorMessage)
class ExistingPhoneNumberException(errorMessage : String) : UserException(errorMessage)
class ExistingInvestmentAccountNameException(errorMessage : String) : UserException(errorMessage)
class ExistingServiceAccountNameException(errorMessage : String) : UserException(errorMessage)

/***** Exception That Can Occur During SignIn, Update/Delete User, Deposit/Withdraw/Transfer Amount Process *****/
class UserNotFoundException(errorMessage : String) : UserException(errorMessage)

/***** Exception That Can Occur During SignIn, Update Password Process ****/
class IncorrectPasswordException(errorMessage : String) : UserException(errorMessage)

/***** Exception That Can Occur During Update Process ****/
class NewNameSameException(errorMessage : String) : UserException(errorMessage)
class NewEmailSameException(errorMessage : String) : UserException(errorMessage)
class NewPasswordSameException(errorMessage : String) : UserException(errorMessage)
class NewPhoneSameException(errorMessage : String) : UserException(errorMessage)

/***** Exception That Can Occur During Deletion ****/
class DeleteAdminAccountException(errorMessage : String) : UserException(errorMessage)

/***** Exception That Can Occur During User Authentication ****/
class UnauthenticatedUserException(errorMessage : String) : UserException(errorMessage)

/***** Exception That Can Occur During Fetching Admin Account *****/
class AdminNotFoundException(errorMessage : String) : UserException(errorMessage)

/***** Exception That Can Occur During Fetching All Users *****/
class NoUsersFoundException(errorMessage : String) : UserException(errorMessage)

/***** Exception That Can Occur During Fetching Accounts *****/
class NoAccountsFoundException(errorMessage : String) : UserException(errorMessage)

/***** Exception That Can Occur During Admin SignIn *****/
class NonAdminAccountException(errorMessage : String) : UserException(errorMessage)