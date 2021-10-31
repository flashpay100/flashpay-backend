package com.flashpay.backend.eurekaclient.dto

import javax.validation.constraints.*

data class CardDto (
        @field:NotEmpty(message = "Card Number Cannot Be Empty.")
        @field:Pattern(regexp = "[0-9]{16}", message = "Card Number Should Have 16 Digits & Only Numbers Are Allowed.")
        var cardNumber : String,

        @field:NotEmpty(message = "Card Name Cannot Be Empty.")
        @field:Size(min = 3, max = 15, message = "Card Name Should Be Between 3 & 15 Characters.")
        @field:Pattern(regexp = "([A-Za-z]+)|([A-Za-z]+[ ][A-Za-z]+)", message = "Only Alphabets & Spaces Allowed In Card Name.")
        var cardName : String,

        @field:NotEmpty(message = "Expiry Month Cannot Be Empty.")
        @field:Pattern(regexp = "01|02|03|04|05|06|07|08|09|10|11|12", message = "Expiry Month Should Be Between 01-12.")
        var expiryMonth : String,

        @field:NotEmpty(message = "Expiry Year Cannot Be Empty.")
        @field:Pattern(regexp = "[0-9]{2}", message = "Expiry Year Should Have 2 Digits & Only Numbers Are Allowed.")
        var expiryYear : String,

        @field:NotEmpty(message = "CVV Cannot Be Empty.")
        @field:Pattern(regexp = "[0-9]{3}", message = "CVV Should Have 3 Digits & Only Numbers Are Allowed.")
        var cvv : String
)