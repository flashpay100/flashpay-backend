package com.flashpay.backend.eurekaclient.exception

open class CardException(errorMessage : String) : RuntimeException(errorMessage)

/***** Exception That Can Occur During Card Addition Process *****/
class InvalidCardInputException(errorMessage : String) : CardException(errorMessage)
class ExistingCardException(errorMessage : String) : CardException(errorMessage)

/***** Exceptions That Can Occur During Card Addition, Deposit Amount, Bank Transfer Process *****/
class ExpiredCardException(errorMessage : String) : CardException(errorMessage)

/***** Exception That Can Occur During Delete Card, Deposit Amount, Bank Transfer Process *****/
class CardNotFoundException(errorMessage : String) : CardException(errorMessage)

/***** Exception That Can Occur During Update Card, Delete Card For User *****/
class NoCardsFoundException(errorMessage : String) : CardException(errorMessage)

/***** Exception That Can Occur During Delete Card For User *****/
class CannotDeleteDefaultCardException(errorMessage : String) : CardException(errorMessage)

/***** Exception That Can Occur During Deposit Amount *****/
class InsufficientCardBalanceException(errorMessage : String) : CardException(errorMessage)