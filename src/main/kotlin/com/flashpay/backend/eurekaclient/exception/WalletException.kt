package com.flashpay.backend.eurekaclient.exception

open class WalletException(errorMessage : String) : RuntimeException(errorMessage) 

/***** Exception That Can Occur During Bank Transfer, Payment *****/
class InsufficientWalletBalanceException(errorMessage : String) : WalletException(errorMessage)

/***** Exception That Can Occur During Payment *****/
class ReadOnlyAccountException(errorMessage : String) : WalletException(errorMessage)

/***** Exception That Can Occur During Payment To Admin *****/
class CannotPayToAdminException(errorMessage : String) : WalletException(errorMessage)

/***** Exception That Can Occur During Deposit Amount, Bank Transfer & Payment *****/
class InactiveAccountException(errorMessage : String) : WalletException(errorMessage)

/***** Exception That Can Occur During Sell Investment *****/
class InsufficientInvestmentBalanceException(errorMessage : String) : WalletException(errorMessage)