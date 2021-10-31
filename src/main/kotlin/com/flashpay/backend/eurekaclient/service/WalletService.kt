package com.flashpay.backend.eurekaclient.service

import com.flashpay.backend.eurekaclient.entity.UserWallet

interface WalletService {
    fun depositAmount(phoneNumber : Long, cardNumber : Long, amount : Double) : UserWallet?
    fun bankTransfer(phoneNumber : Long, cardNumber : Long, amount : Double) : UserWallet?
    fun payment(phoneNumber1 : Long, phoneNumber2 : Long, amount : Double) : UserWallet?
    fun sellInvestment(phoneNumber : Long, investmentAccountName : String, amount : Double) : UserWallet?
}