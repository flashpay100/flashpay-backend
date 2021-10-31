package com.flashpay.backend.eurekaclient.service

import com.flashpay.backend.eurekaclient.dao.UserWalletDao
import com.flashpay.backend.eurekaclient.dto.CardDto
import com.flashpay.backend.eurekaclient.entity.Card
import com.flashpay.backend.eurekaclient.entity.Log
import com.flashpay.backend.eurekaclient.entity.UserWallet
import com.flashpay.backend.eurekaclient.exception.*
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import java.text.SimpleDateFormat
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.*

/************************************************************************************
 *    Author           Anush Raghavender
 *    Description      Card Service Class That Provides Services For The Following Methods
 *                     createCard, deleteCard
 *    Version          1.0
 *    Created Date     28-09-2021
************************************************************************************/
@Service
class CardServiceImplementation(val userWalletDao : UserWalletDao) : CardService {

    @Value("\${card.initialbalance}")
    val cardBalance : Double = 0.0

    object DateTimeConstants {
        const val zoneId = "Asia/Kolkata"
        const val pattern = "dd-MM-YY, HH:mm:ss"
    }

    /************************************* Create Card *************************************/
    override fun addCard(phoneNumber : Long, cardDto : CardDto) : UserWallet? {
        val retrievedUser : UserWallet? = userWalletDao.findPhoneNumber(phoneNumber)
        if(retrievedUser == null) {
            throw UserNotFoundException("User With Phone Number \"$phoneNumber\" Not Found")
        }
        else if(retrievedUser.accountType == "Admin" || retrievedUser.accountType == "Business" || retrievedUser.accountType == "Utility" || retrievedUser.accountType == "Charity" || retrievedUser.accountType == "Investment") {
            throw ReadOnlyAccountException("User Account With \"$phoneNumber\" Is Read Only")
        }
        else {
            val id = cardDto.cardNumber.toLong()

            val retrievedCards = retrievedUser.cards
            if (retrievedCards != null) {
                for (card in retrievedCards) {
                    if(card.id == id) {
                        throw ExistingCardException("Card Number \"$id\" Already Exists")
                    }
                }
            }

            val cardExpiryDate = "${cardDto.expiryMonth}/${cardDto.expiryYear}"
            val simpleDateFormat = SimpleDateFormat("MM/yy")
            val expiryDate = simpleDateFormat.parse(cardExpiryDate)
            if(expiryDate.before(Date())) {
                throw ExpiredCardException("Card With Date \"${cardExpiryDate}\" Is Expired")
            }
            else {
                var cardName = cardDto.cardName.toLowerCase().split(" ").joinToString(" ") { it.capitalize() }.trimEnd()
                if(!cardName.contains("bank", ignoreCase = true)) {
                    cardName = "$cardName Bank"
                }

                val cvvEncoder = BCryptPasswordEncoder()
                val cvv = cvvEncoder.encode(cardDto.cvv)

                var defaultCard = false
                if(retrievedUser.cards?.isEmpty() == true) {
                    defaultCard = true
                }

                val card = Card(
                    id,
                    cardName,
                    cardBalance,
                    cardExpiryDate,
                    cvv,
                    defaultCard
                )

                retrievedUser.cards?.add(card)

                val zonedDateTime = ZonedDateTime.now(ZoneId.of(DateTimeConstants.zoneId))
                val formattingPattern = DateTimeFormatter.ofPattern(DateTimeConstants.pattern)
                val cardCreationDateTime = formattingPattern.format(zonedDateTime)

                val log = Log(
                    cardCreationDateTime,
                    "Card Addition",
                    "Success"
                )

                retrievedUser.logs?.add(log)

                return userWalletDao.save(retrievedUser)
            }
        }
    }

    /************************************* Update Card *************************************/
    override fun updateCard(phoneNumber : Long, cardNumber : Long) : UserWallet? {
        val retrievedUser : UserWallet? = userWalletDao.findPhoneNumber(phoneNumber)
        if(retrievedUser == null) {
            throw UserNotFoundException("User With Phone Number \"$phoneNumber\" Not Found")
        }
        else {
            val retrievedCards = retrievedUser.cards
            if (retrievedCards == null) {
                throw NoCardsFoundException("No Cards Found In Database")
            }
            else {
                var retrievedCard : Card? = null
                for (card in retrievedCards) {
                    if(card.id == cardNumber) {
                        retrievedCard = card
                        retrievedCard.defaultCard = true
                        break
                    }
                }

                if(retrievedCard == null) {
                    throw CardNotFoundException("Card Number \"$cardNumber\" Not Found")
                }
                else {
                    for (card in retrievedCards) {
                        if(card.id != cardNumber) {
                            card.defaultCard = false
                        }
                    }

                    val zonedDateTime = ZonedDateTime.now(ZoneId.of(DateTimeConstants.zoneId))
                    val formattingPattern = DateTimeFormatter.ofPattern(DateTimeConstants.pattern)
                    val cardDeletionDateTime = formattingPattern.format(zonedDateTime)

                    val log = Log(
                        cardDeletionDateTime,
                        "Default Card Updation",
                        "Success"
                    )

                    retrievedUser.logs?.add(log)

                    return userWalletDao.save(retrievedUser)
                }
            }
        }
    }

    /************************************* Delete Card *************************************/
    override fun deleteCard(phoneNumber : Long, cardNumber : Long) : UserWallet? {
        val retrievedUser : UserWallet? = userWalletDao.findPhoneNumber(phoneNumber)
        if(retrievedUser == null) {
            throw UserNotFoundException("User With Phone Number \"$phoneNumber\" Not Found")
        }
        else {
            val retrievedCards = retrievedUser.cards
            if (retrievedCards == null) {
                throw NoCardsFoundException("No Cards Found In Database")
            }
            else {
                var retrievedCard : Card? = null
                for (card in retrievedCards) {
                    if(card.id == cardNumber) {
                        retrievedCard = card
                        break
                    }
                }

                if(retrievedCard == null) {
                    throw CardNotFoundException("Card Number \"$cardNumber\" Not Found")
                }
                else if(retrievedCard.defaultCard) {
                    throw CannotDeleteDefaultCardException("Default Card \"$cardNumber\" Cannot Be Deleted")
                }
                else {
                    retrievedUser.cards!!.remove(retrievedCard)

                    val zonedDateTime = ZonedDateTime.now(ZoneId.of(DateTimeConstants.zoneId))
                    val formattingPattern = DateTimeFormatter.ofPattern(DateTimeConstants.pattern)
                    val cardDeletionDateTime = formattingPattern.format(zonedDateTime)

                    val log = Log(
                        cardDeletionDateTime,
                        "Card Deletion",
                        "Success"
                    )

                    retrievedUser.logs?.add(log)

                    return userWalletDao.save(retrievedUser)
                }
            }
        }
    }
}