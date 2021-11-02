package com.flashpay.backend.eurekaclient.entity

data class Transaction (
    var transactionDateTime : String,
    var fromAccountId : Long,
    var fromAccount : String,
    var toAccountId : Long,
    var toAccount : String,
    var transactionAmount : Double,
    var transactionType : String?,
    var transactionReward : String,
    var transactionStatus : String
)
