package com.flashpay.backend.eurekaclient.entity

import com.fasterxml.jackson.annotation.JsonProperty
import com.flashpay.backend.eurekaclient.entity.Card
import com.flashpay.backend.eurekaclient.entity.Log
import com.flashpay.backend.eurekaclient.entity.Transaction
import org.springframework.data.mongodb.core.mapping.Document
import javax.persistence.Id

@Document(collection = "UserWallet")
data class UserWallet(
    @Id
    var id : Long, //Phone Number
    var userName : String,
    var emailAddress : String,
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    var password : String,
    var accountType : String,
    var accountStatus : String,
    var accountBalance : Double,
    var rewards : Double,
    var totalTransfers : Double,
    var totalPayments : Double,
    var totalBills : Double,
    var totalServices : Double,
    var totalInvestments : Double,
    var totalDonations : Double,
    var creationDateTime : String,
    var activationDateTime : String,
    var deletionDateTime : String,
    var cards : MutableSet<Card>?,
    var transactions : MutableSet<Transaction>?,
    var logs : MutableSet<Log>?,
    var donations : MutableMap<String, Double>?,
    var investments : MutableMap<String, Double>?
)