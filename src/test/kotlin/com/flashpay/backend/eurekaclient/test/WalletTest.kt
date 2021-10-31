package com.flashpay.backend.eurekaclient.test

import com.flashpay.backend.eurekaclient.dao.CardDao
import com.flashpay.backend.eurekaclient.dao.TransactionDao
import com.flashpay.backend.eurekaclient.dao.UserWalletDao
import com.flashpay.backend.eurekaclient.dto.CardDto
import com.flashpay.backend.eurekaclient.dto.UserDto
import com.flashpay.backend.eurekaclient.service.CardService
import com.flashpay.backend.eurekaclient.service.UserService
import com.flashpay.backend.eurekaclient.service.WalletService
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class WalletTest(
    @Autowired val userService: UserService,
    @Autowired val cardService: CardService,
    @Autowired val walletService: WalletService,
    @Autowired val userWalletDao : UserWalletDao,
    @Autowired val cardDao : CardDao,
    @Autowired val transactionDao: TransactionDao
) {
    private val admin : UserDto = UserDto(
        "9999999999",
        "Flash Pay",
        "admin@flashpay.com",
        "Admin",
        "flashpay12345$"
    )
    val testAdmin = admin

    private val wallet : UserDto = UserDto(
        "6381342954",
        "Anush Raghavender",
        "anushraghavender3@gmail.com",
        "Personal",
        "anush12345$"
    )
    val testWallet = wallet

    private val wallet1 : UserDto = UserDto(
        "7358440894",
        "Sriman Narayana",
        "srimannarayana@gmail.com",
        "Business",
        "narayana12345$"
    )
    val testWallet1 = wallet1

    private val card : CardDto = CardDto(
        "4486770098076615",
        "Axis Bank",
        "12",
        "21",
        "777"
    )
    val testCard = card

    /********************* Deposit Amount Test *********************/
    @Test
    fun testDepositAmount() {
        userService.createUser(testAdmin)
        userService.createUser(testWallet)
        cardService.addCard(6381342954, testCard)
        val updatedWallet = walletService.depositAmount(6381342954, 4486770098076615, 500.0)
        val transaction = transactionDao.findAll()
        val card = cardDao.findAll()
        Assertions.assertEquals(500.0, updatedWallet?.accountBalance)
        val itr1 = card.iterator()
        while(itr1.hasNext()) {
            Assertions.assertEquals(4500.0, itr1.next()?.cardBalance)
        }
        val itr2 = transaction.iterator()
        while(itr2.hasNext()) {
            Assertions.assertEquals(6381342954, itr2.next()?.toAccountId)
        }
    }

    /********************* Withdraw Amount Test *********************/
    @Test
    fun testWithdrawAmount() {
        userService.createUser(testAdmin)
        userService.createUser(testWallet)
        cardService.addCard(6381342954, testCard)
        walletService.depositAmount(6381342954, 4486770098076615, 500.0)
        val updatedWallet = walletService.bankTransfer(6381342954, 4486770098076615, 200.0)
        val transaction = transactionDao.findAll()
        val card = cardDao.findAll()
        Assertions.assertEquals(300.0, updatedWallet?.accountBalance)
        val itr1 = card.iterator()
        while(itr1.hasNext()) {
            Assertions.assertEquals(5200.0, itr1.next()?.cardBalance)
        }
        val itr2 = transaction.iterator()
        while(itr2.hasNext()) {
            Assertions.assertEquals(20374794190, itr2.next()?.toAccountId)
        }
    }

    /********************* Transfer Amount Test *********************/
    @Test
    fun testTransferAmount() {
        userService.createUser(testAdmin)
        userService.createUser(testWallet)
        userService.createUser(testWallet1)
        cardService.addCard(6381342954, testCard)
        walletService.depositAmount(6381342954, 4486770098076615, 500.0)
        walletService.payment(6381342954, 7358440894, 100.0)
        val user1 = userWalletDao.findPhoneNumber(6381342954)
        val user2 = userWalletDao.findPhoneNumber(7358440894)
        Assertions.assertEquals(395.0, user1?.accountBalance)
        Assertions.assertEquals(100.0, user2?.accountBalance)
    }

    /********************* Read Only Test *********************/
    @Test
    fun testReadOnlyAccount() {
        userService.createUser(testAdmin)
        userService.createUser(testWallet)
        userService.createUser(testWallet1)
        walletService.payment(7358440894, 6381342954, 100.0)
        val user1 = userWalletDao.findPhoneNumber(7358440894)
        Assertions.assertEquals("Business", user1?.accountType)
    }

    /********************* Inactive Account Test *********************/
    @Test
    fun testInactiveAccount() {
        userService.createUser(testAdmin)
        userService.createUser(testWallet)
        userService.createUser(testWallet1)
        userService.deleteUser(7358440894, "narayana12345$")
        walletService.payment(6381342954, 7358440894, 100.0)
        val user1 = userWalletDao.findPhoneNumber(7358440894)
        Assertions.assertEquals("Inactive", user1?.accountStatus)
    }

    /********************* Admin Account Transfer Test *********************/
    @Test
    fun testAdminAccountTransfer() {
        userService.createUser(testWallet)
        val user2 = userService.createUser(testAdmin)
        walletService.payment(6381342954, 9999999999, 100.0)
        Assertions.assertEquals("Admin", user2?.accountType)
    }

    /********************* Insufficient Wallet Balance Test *********************/
    @Test
    fun testInsufficientWalletBalance() {
        userService.createUser(testAdmin)
        val user1 = userService.createUser(testWallet)
        userService.createUser(testWallet1)
        walletService.payment(6381342954, 7358440894, 100000.0)
        Assertions.assertTrue(100000.0 > user1?.accountBalance!!)
    }

    /********************* Insufficient Card Balance Test *********************/
    @Test
    fun testInsufficientCardBalance() {
        userService.createUser(testWallet)
        cardService.addCard(6381342954, testCard)
        walletService.depositAmount(6381342954, 4486770098076615, 100000.0)
        val user = userWalletDao.findPhoneNumber(6381342954)
        val itr = user?.cards?.iterator()
        while(itr!!.hasNext()) {
            Assertions.assertTrue(100000.0 > itr.next().cardBalance)
        }
    }

    /********************* Insufficient Investment Balance Test *********************/
    @Test
    fun testInsufficientInvestmentBalance() {
        userService.createUser(testWallet)
        cardService.addCard(6381342954, testCard)
        walletService.depositAmount(6381342954, 4486770098076615, 500.0)
        testWallet1.accountType = "Investment"
        userService.createUser(testWallet1)
        val user = walletService.payment(6381342954, 7358440894, 100.0)
        walletService.sellInvestment(6381342954, "Sriman Narayana", 500.0)
        Assertions.assertTrue(500.0 > user?.investments?.get("Sriman Narayana")!!)
    }
}