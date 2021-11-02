package com.flashpay.backend.eurekaclient.service

import com.flashpay.backend.eurekaclient.dao.UserWalletDao
import com.flashpay.backend.eurekaclient.entity.Card
import com.flashpay.backend.eurekaclient.entity.Transaction
import com.flashpay.backend.eurekaclient.entity.UserWallet
import com.flashpay.backend.eurekaclient.exception.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.stereotype.Service
import java.text.SimpleDateFormat
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.*

/************************************************************************************
 *    Author           Anush Raghavender
 *    Description      Wallet Service Class That Provides Services For The Following Methods
 *                     depositAmount, bankTransfer, payment
 *    Version          1.0
 *    Created Date     29-09-2021
 ************************************************************************************/
@Service
class WalletServiceImplementation(val userWalletDao : UserWalletDao) : WalletService {

    @Value("\${admin.donationamount}")
    val donationAmount : Double = 0.0

    @Value("\${wallet.transactioncost}")
    val transactionCost : Double = 0.0

    @Value("\${wallet.rewardpercentage}")
    val rewardPercentage : Double = 0.0

    @Autowired
    val mailSender : JavaMailSender? = null

    object DateTimeConstants {
        const val zoneId = "Asia/Kolkata"
        const val pattern = "dd-MM-YY, HH:mm:ss"
    }

    /************************************* Deposit Amount *************************************/
    override fun depositAmount(phoneNumber : Long, cardNumber : Long, amount : Double) : UserWallet? {
        val retrievedWallet : UserWallet? = userWalletDao.findPhoneNumber(phoneNumber)
        if(retrievedWallet == null) {
            throw UserNotFoundException("User With Phone Number \"$phoneNumber\" Not Found")
        }
        else if(retrievedWallet.accountType == "Admin" || retrievedWallet.accountType == "Business" || retrievedWallet.accountType == "Service" || retrievedWallet.accountType == "Utility" || retrievedWallet.accountType == "Charity" || retrievedWallet.accountType == "Investment") {
            print(retrievedWallet.accountType)
            throw ReadOnlyAccountException("User Account With \"$phoneNumber\" Is Read Only")
        }
        else if(retrievedWallet.accountStatus == "Inactive") {
            throw InactiveAccountException("User Account With \"${retrievedWallet.id}\" Is Inactive")
        }
        else {
            val retrievedCards = retrievedWallet.cards
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
                else {
                    if(amount > retrievedCard.cardBalance) {
                        failedTransaction(retrievedCard.id, retrievedCard.cardName, retrievedWallet.id, retrievedWallet.userName, amount, "Amount To Deposit Greater Than Card Balance")
                        throw InsufficientCardBalanceException("Amount To Deposit Greater Than Card Balance")
                    }

                    val cardExpiryDate = retrievedCard.expiryDate
                    val simpleDateFormat = SimpleDateFormat("MM/yy")
                    val expiryDate = simpleDateFormat.parse(cardExpiryDate)
                    if(expiryDate.before(Date())) {
                        failedTransaction(retrievedCard.id, retrievedCard.cardName, retrievedWallet.id, retrievedWallet.userName, amount, "Card With Date \"${cardExpiryDate}\" Is Expired")
                        throw ExpiredCardException("Card With Date \"${cardExpiryDate}\" Is Expired")
                    }

                    val updatedWallet : UserWallet = retrievedWallet
                    val updatedCard = retrievedCard

                    updatedWallet.accountBalance += amount
                    updatedCard.cardBalance -= amount

                    val zonedDateTime = ZonedDateTime.now(ZoneId.of(DateTimeConstants.zoneId))
                    val formattingPattern = DateTimeFormatter.ofPattern(DateTimeConstants.pattern)
                    val transactionDateTime = formattingPattern.format(zonedDateTime)

                    val bill = Transaction(
                        transactionDateTime,
                        updatedCard.id,
                        updatedCard.cardName,
                        updatedWallet.id,
                        updatedWallet.userName,
                        amount,
                        "Credited",
                        "-",
                        "Success"

                    )

                    retrievedWallet.transactions?.add(bill)

                    userWalletDao.delete(retrievedWallet)
                    userWalletDao.save(updatedWallet)
                    return updatedWallet
                }
            }
        }
    }

    /************************************* Bank Transfer *************************************/
    override fun bankTransfer(phoneNumber : Long, cardNumber : Long, amount : Double) : UserWallet? {
        val retrievedWallet : UserWallet? = userWalletDao.findPhoneNumber(phoneNumber)
        if(retrievedWallet == null) {
            throw UserNotFoundException("User With Phone Number \"$phoneNumber\" Not Found")
        }
        else if(retrievedWallet.accountType == "Admin" || retrievedWallet.accountType == "Business" || retrievedWallet.accountType == "Service" || retrievedWallet.accountType == "Utility" || retrievedWallet.accountType == "Charity" || retrievedWallet.accountType == "Investment") {
            throw ReadOnlyAccountException("User Account With \"$phoneNumber\" Is Read Only")
        }
        else if(retrievedWallet.accountStatus == "Inactive") {
            throw InactiveAccountException("User Account With \"${retrievedWallet.id}\" Is Inactive")
        }
        else {
            val retrievedCards = retrievedWallet.cards
            if (retrievedCards == null) {
                throw NoCardsFoundException("No Cards Found In Database")
            }
            else {
                var retrievedCard: Card? = null
                for (card in retrievedCards) {
                    if (card.id == cardNumber) {
                        retrievedCard = card
                        break
                    }
                }
                if (retrievedCard == null) {
                    throw CardNotFoundException("Card Number \"$cardNumber\" Not Found")
                }
                else {
                    if(amount > retrievedWallet.accountBalance) {
                        failedTransaction(retrievedWallet.id, retrievedWallet.userName, retrievedCard.id, retrievedCard.cardName, amount, "Amount To Transfer Greater Than Wallet Balance")
                        throw InsufficientWalletBalanceException("Amount To Transfer Greater Than Wallet Balance")
                    }

                    val cardExpiryDate = retrievedCard.expiryDate
                    val simpleDateFormat = SimpleDateFormat("MM/yy")
                    val expiryDate = simpleDateFormat.parse(cardExpiryDate)
                    if (expiryDate.before(Date())) {
                        failedTransaction(retrievedWallet.id, retrievedWallet.userName, retrievedCard.id, retrievedCard.cardName, amount, "Card With Date \"${cardExpiryDate}\" Is Expired")
                        throw ExpiredCardException("Card With Date \"${cardExpiryDate}\" Is Expired")
                    }

                    val updatedWallet : UserWallet = retrievedWallet
                    val updatedCard = retrievedCard

                    updatedWallet.accountBalance -= amount
                    updatedCard.cardBalance += amount

                    val zonedDateTime = ZonedDateTime.now(ZoneId.of(DateTimeConstants.zoneId))
                    val formattingPattern = DateTimeFormatter.ofPattern(DateTimeConstants.pattern)
                    val transactionDateTime = formattingPattern.format(zonedDateTime)

                    val bill = Transaction(
                        transactionDateTime,
                        updatedWallet.id,
                        updatedWallet.userName,
                        updatedCard.id,
                        updatedCard.cardName,
                        amount,
                        "Debited",
                        "-",
                        "Success"
                    )

                    retrievedWallet.transactions?.add(bill)

                    userWalletDao.delete(retrievedWallet)
                    userWalletDao.save(updatedWallet)
                    return updatedWallet
                }
            }
        }
    }

    /************************************* Payment *************************************/
    @Suppress("EqualsBetweenInconvertibleTypes")
    override fun payment(phoneNumber1 : Long, phoneNumber2 : Long, amount : Double) : UserWallet? {
        val adminAccount : UserWallet? = userWalletDao.findAdminAccount()
        val userAccount1 : UserWallet? = userWalletDao.findPhoneNumber(phoneNumber1)
        val userAccount2 : UserWallet? = userWalletDao.findPhoneNumber(phoneNumber2)
        if(adminAccount == null) {
            throw AdminNotFoundException("Admin Account Not Found")
        }
        if(userAccount1 == null) {
            throw UserNotFoundException("User Account With \"$phoneNumber1\" Not Found")
        }
        else if(userAccount2 == null) {
            throw UserNotFoundException("User Account With \"$phoneNumber2\" Not Found")
        }
        else if(userAccount1.accountStatus == "Inactive") {
            throw InactiveAccountException("User Account With \"$phoneNumber1\" Is Inactive")
        }
        else if(userAccount2.accountStatus == "Inactive") {
            failedTransaction(userAccount1.id, userAccount1.userName, userAccount2.id, userAccount2.userName, amount, "User Account With \"$phoneNumber2\" Is Inactive")
            throw InactiveAccountException("User Account With \"$phoneNumber2\" Is Inactive")
        }
        else if(userAccount1.accountType == "Admin" || userAccount1.accountType == "Business" || userAccount1.accountType == "Service" || userAccount1.accountType == "Utility" || userAccount1.accountType == "Charity" || userAccount1.accountType == "Investment") {
            throw ReadOnlyAccountException("User Account With \"$phoneNumber1\" Is Read Only")
        }
        else if(userAccount1.accountBalance < amount) {
            failedTransaction(userAccount1.id, userAccount1.userName, userAccount2.id, userAccount2.userName, amount, "Amount To Pay Greater Than Wallet Balance")
            throw InsufficientWalletBalanceException("Amount To Pay Greater Than Wallet Balance")
        }
        else if(userAccount2.accountType == "Admin") {
            throw CannotPayToAdminException("Amount Cannot Be Payed To Admin")
        }
        else {
            val updatedUserAccount1 : UserWallet = userAccount1
            val updatedUserAccount2 : UserWallet  = userAccount2
            val updatedAdminAccount : UserWallet = adminAccount

            updatedUserAccount1.accountBalance -= amount + transactionCost
            updatedUserAccount2.accountBalance += amount
            updatedAdminAccount.accountBalance += transactionCost

            val zonedDateTime = ZonedDateTime.now(ZoneId.of(DateTimeConstants.zoneId))
            val formattingPattern = DateTimeFormatter.ofPattern(DateTimeConstants.pattern)
            val transactionDateTime = formattingPattern.format(zonedDateTime)

            var userTransactionReward = "-"
            var businessTransactionReward = "-"

            if(amount >= 500) {
                updatedUserAccount1.accountBalance += rewardPercentage * amount
                updatedAdminAccount.accountBalance -= rewardPercentage * amount
                updatedUserAccount1.rewards += rewardPercentage * amount
                updatedAdminAccount.rewards += rewardPercentage * amount
                userTransactionReward = String.format("%.2f", (rewardPercentage * amount))
            }

            var transactionToAccountType : String? = null
            when (userAccount2.accountType) {
                "Business" -> {
                    if(amount >= 1000) {
                        updatedUserAccount2.accountBalance += rewardPercentage * amount
                        updatedAdminAccount.accountBalance -= rewardPercentage * amount
                        updatedUserAccount2.rewards += rewardPercentage * amount
                        updatedAdminAccount.rewards += rewardPercentage * amount
                        businessTransactionReward = (rewardPercentage * amount).toString()
                    }
                    updatedUserAccount1.totalPayments += amount
                    transactionToAccountType = "Payment"
                }
                "Charity" -> {
                    updatedUserAccount2.accountBalance += donationAmount
                    updatedAdminAccount.accountBalance -= donationAmount
                    updatedUserAccount1.totalDonations += amount
                    updatedUserAccount2.rewards += donationAmount
                    updatedAdminAccount.totalDonations += donationAmount
                    transactionToAccountType = "Donation"
                    var donationAmount = amount
                    if(updatedUserAccount1.donations?.get(updatedUserAccount2.userName)?.equals(0) == false) {
                        donationAmount += updatedUserAccount1.donations?.get(updatedUserAccount2.userName)!!
                    }
                    updatedUserAccount1.donations?.put(updatedUserAccount2.userName, donationAmount)
                }
                "Utility" -> {
                    updatedUserAccount1.totalBills += amount
                    transactionToAccountType = "Bill"
                }
                "Service" -> {
                    updatedUserAccount1.totalServices += amount
                    transactionToAccountType = "Service"
                }
                "Investment" -> {
                    updatedUserAccount1.totalInvestments += amount
                    transactionToAccountType = "Investment"
                    var investmentAmount = amount
                    if(updatedUserAccount1.investments?.get(updatedUserAccount2.userName)?.equals(0) == false) {
                        investmentAmount += updatedUserAccount1.investments?.get(updatedUserAccount2.userName)!!
                    }
                    updatedUserAccount1.investments?.put(updatedUserAccount2.userName, investmentAmount)
                }
                "Personal" -> {
                    updatedUserAccount1.totalTransfers += amount
                    transactionToAccountType = "Transfer"
                }
            }

            val bill1 = Transaction(
                transactionDateTime,
                updatedUserAccount1.id,
                updatedUserAccount1.userName,
                updatedUserAccount2.id,
                updatedUserAccount2.userName,
                amount,
                "Debited ($transactionToAccountType)",
                userTransactionReward,
                "Success"
            )
            val bill2 = Transaction(
                transactionDateTime,
                updatedUserAccount1.id,
                updatedUserAccount1.userName,
                updatedUserAccount2.id,
                updatedUserAccount2.userName,
                amount,
                "Credited (Transfer)",
                businessTransactionReward,
                "Success"
            )

            sendTransactionEmail1(updatedUserAccount1.emailAddress, "Payment Transaction", bill1)
            sendTransactionEmail2(updatedUserAccount2.emailAddress, "Payment Transaction", bill2)

            userAccount1.transactions?.add(bill1)
            userAccount2.transactions?.add(bill2)

            userWalletDao.delete(userAccount1)
            userWalletDao.delete(userAccount2)
            userWalletDao.delete(adminAccount)

            userWalletDao.save(updatedUserAccount1)
            userWalletDao.save(updatedUserAccount2)
            userWalletDao.save(updatedAdminAccount)

            return updatedUserAccount1
        }
    }

    /************************************* Sell Investment *************************************/
    override fun sellInvestment(phoneNumber : Long, investmentAccountName : String, amount : Double) : UserWallet? {
        val userAccount1 : UserWallet? = userWalletDao.findPhoneNumber(phoneNumber)
        val userAccount2 : UserWallet? = userWalletDao.findInvestmentAccount(investmentAccountName.toLowerCase().split(" ").joinToString(" "){it.capitalize()}.trimEnd())
        if(userAccount1 == null) {
            throw UserNotFoundException("User Account With \"$phoneNumber\" Not Found")
        }
        else if(userAccount2 == null) {
            throw UserNotFoundException("User Account With \"$investmentAccountName\" Not Found")
        }
        else if(userAccount1.accountStatus == "Inactive") {
            throw InactiveAccountException("User Account With \"$phoneNumber\" Is Inactive")
        }
        else if(userAccount2.accountStatus == "Inactive") {
            throw InactiveAccountException("User Account With \"$investmentAccountName\" Is Inactive")
        }
        else if(userAccount1.accountType == "Admin" || userAccount1.accountType == "Business" || userAccount1.accountType == "Service" || userAccount1.accountType == "Utility" || userAccount1.accountType == "Charity" || userAccount1.accountType == "Investment") {
            throw ReadOnlyAccountException("User Account With \"$phoneNumber\" Is Read Only")
        }
        else {
            val updatedUserAccount1 : UserWallet = userAccount1
            val updatedUserAccount2 : UserWallet  = userAccount2

            if(updatedUserAccount1.investments?.get(updatedUserAccount2.userName)!! < amount) {
                failedTransaction(userAccount1.id, userAccount1.userName, userAccount2.id, userAccount2.userName, amount, "Investment Sale Amount Greater Than Investment Balance")
                throw InsufficientInvestmentBalanceException("Investment Sale Amount Greater Than Investment Balance")
            }
            else {
                updatedUserAccount1.accountBalance += amount
                updatedUserAccount1.totalInvestments -= amount
                updatedUserAccount2.accountBalance -= amount
                val investmentBalance = updatedUserAccount1.investments?.get(updatedUserAccount2.userName)!! - amount
                updatedUserAccount1.investments?.put(updatedUserAccount2.userName, investmentBalance)
            }

            val zonedDateTime = ZonedDateTime.now(ZoneId.of(DateTimeConstants.zoneId))
            val formattingPattern = DateTimeFormatter.ofPattern(DateTimeConstants.pattern)
            val transactionDateTime = formattingPattern.format(zonedDateTime)

            val bill1 = Transaction(
                transactionDateTime,
                updatedUserAccount2.id,
                updatedUserAccount2.userName,
                updatedUserAccount1.id,
                updatedUserAccount1.userName,
                amount,
                "Credited (Investment Sale)",
                "-",
                "Success"
            )
            val bill2 = Transaction(
                transactionDateTime,
                updatedUserAccount2.id,
                updatedUserAccount2.userName,
                updatedUserAccount1.id,
                updatedUserAccount1.userName,
                amount,
                "Debited (Investment Sale)",
                "-",
                "Success"
            )

            sendTransactionEmail1(updatedUserAccount2.emailAddress, "Investment Transaction", bill1)
            sendTransactionEmail2(updatedUserAccount1.emailAddress, "Investment Transaction", bill2)

            userAccount1.transactions?.add(bill1)
            userAccount2.transactions?.add(bill2)

            userWalletDao.delete(userAccount1)
            userWalletDao.delete(userAccount2)

            userWalletDao.save(updatedUserAccount1)
            userWalletDao.save(updatedUserAccount2)

            return updatedUserAccount1
        }
    }

    fun failedTransaction(
        fromAccountId : Long,
        fromAccount : String,
        toAccountId : Long,
        toAccount : String,
        transactionAmount : Double,
        message : String
    ) {
        val zonedDateTime = ZonedDateTime.now(ZoneId.of(DateTimeConstants.zoneId))
        val formattingPattern = DateTimeFormatter.ofPattern(DateTimeConstants.pattern)
        val transactionDateTime = formattingPattern.format(zonedDateTime)

        val bill = Transaction(
            transactionDateTime,
            fromAccountId,
            fromAccount,
            toAccountId,
            toAccount,
            transactionAmount,
            "-",
            "-",
            "Failure ($message)"
        )

        if(fromAccountId.toString().length == 10) {
            val retrievedWallet : UserWallet = userWalletDao.findPhoneNumber(fromAccountId)!!
            retrievedWallet.transactions?.add(bill)
            userWalletDao.save(retrievedWallet)
        }
        else {
            val retrievedWallet : UserWallet = userWalletDao.findPhoneNumber(toAccountId)!!
            retrievedWallet.transactions?.add(bill)
            userWalletDao.save(retrievedWallet)
        }
    }

    fun sendTransactionEmail1(toEmail : String, subject : String, bill : Transaction) {
        val body =
            "Hi ${bill.fromAccount}, \n\n" +
                    "You have transferred ₹${bill.transactionAmount} to ${bill.toAccount} on ${bill.transactionDateTime}. \n\n" +
                    "Thanks & Regards, \n" +
                    "Team FlashPay."

        val message = SimpleMailMessage()
        message.setFrom("flashpay100@gmail.com")
        message.setTo(toEmail)
        message.setText(body)
        message.setSubject(subject)

        mailSender?.send(message)
    }

    fun sendTransactionEmail2(toEmail : String, subject : String, bill : Transaction) {
        val body =
            "Hi ${bill.toAccount}, \n\n" +
                    "You have received ₹${bill.transactionAmount} from ${bill.fromAccount} on ${bill.transactionDateTime}. \n\n" +
                    "Thanks & Regards, \n" +
                    "Team FlashPay."

        val message = SimpleMailMessage()
        message.setFrom("flashpay100@gmail.com")
        message.setTo(toEmail)
        message.setText(body)
        message.setSubject(subject)

        mailSender?.send(message)
    }
}
