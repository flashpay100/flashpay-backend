package com.flashpay.backend.eurekaclient.service

import com.flashpay.backend.eurekaclient.dto.CardDto
import com.flashpay.backend.eurekaclient.entity.UserWallet

interface CardService {
    fun addCard(phoneNumber : Long, cardDto : CardDto) : UserWallet?
    fun updateCard(phoneNumber : Long, cardNumber : Long) : UserWallet?
    fun deleteCard(phoneNumber : Long, cardNumber : Long) : UserWallet?
}