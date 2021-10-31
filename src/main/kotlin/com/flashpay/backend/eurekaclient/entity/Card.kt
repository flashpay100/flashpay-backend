package com.flashpay.backend.eurekaclient.entity

import com.fasterxml.jackson.annotation.JsonProperty
import javax.persistence.Id

data class Card (
    @Id
    var id : Long, //Card Number
    var cardName : String,
    var cardBalance : Double,
    var expiryDate : String,
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    var cvv : String,
    var defaultCard : Boolean
)