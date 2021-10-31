package com.flashpay.backend.eurekaclient.test

import com.flashpay.backend.eurekaclient.dao.UserWalletDao
import com.flashpay.backend.eurekaclient.dto.CardDto
import com.flashpay.backend.eurekaclient.dto.UserDto
import com.flashpay.backend.eurekaclient.service.CardService
import com.flashpay.backend.eurekaclient.service.UserService
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.text.SimpleDateFormat
import java.util.*

@SpringBootTest
class CardTest(
    @Autowired val cardService : CardService,
    @Autowired val userService: UserService,
    @Autowired val userWalletDao : UserWalletDao
) {

    private val user : UserDto = UserDto(
        "6381342954",
        "Anush Raghavender",
        "anushraghavender3@gmail.com",
        "Personal",
        "anush12345$"
    )
    val testUser = user

    private val card : CardDto = CardDto(
        "4486770098076615",
        "Axis Bank",
        "12",
        "21",
        "777"
    )
    val testCard = card

    private val card1 : CardDto = CardDto(
        "4486770098076615",
        "ICICI Bank",
        "10",
        "22",
        "999"
    )
    val testCard1 = card1

    /********************* Add Card Test *********************/
    @Test
    fun testAddCard() {
        userService.createUser(testUser)
        cardService.addCard(6381342954, testCard)
        val user = userWalletDao.findPhoneNumber(6381342954)
        val itr = user?.cards?.iterator()
        while(itr!!.hasNext()) {
            Assertions.assertEquals(20374794190, itr.next().id)
        }
    }

    /********************* Update Card Test *********************/
    @Test
    fun testUpdateCard() {
        userService.createUser(testUser)
        cardService.addCard(6381342954, testCard)
        val user = userWalletDao.findPhoneNumber(6381342954)
        val itr = user?.cards?.iterator()
        while(itr!!.hasNext()) {
            Assertions.assertEquals(true, itr.next().defaultCard)
        }
    }

    /********************* Delete Card Test *********************/
    @Test
    fun testDeleteCard() {
        userService.createUser(testUser)
        cardService.addCard(6381342954, testCard)
        cardService.deleteCard(6381342954, 4486770098076615)
        val user = userWalletDao.findPhoneNumber(6381342954)
        Assertions.assertTrue(user?.cards!!.isEmpty())
    }

    /********************* Delete Default Card Test *********************/
    @Test
    fun testDeleteDefaultCard() {
        userService.createUser(testUser)
        cardService.addCard(6381342954, testCard)
        cardService.deleteCard(6381342954, 4486770098076615)
        val user = userWalletDao.findPhoneNumber(6381342954)
        val itr = user?.cards?.iterator()
        while(itr!!.hasNext()) {
            Assertions.assertEquals(true, itr.next().defaultCard)
        }
    }

    /********************* Expired Card Test *********************/
    @Test
    fun testExpiredCard() {
        userService.createUser(testUser)
        testCard.expiryMonth = "04"
        testCard.expiryYear = "20"
        cardService.addCard(6381342954, testCard)
        val user = userWalletDao.findPhoneNumber(6381342954)
        val itr = user?.cards?.iterator()
        while(itr!!.hasNext()) {
            val cardExpiryDate = itr.next().expiryDate
            val simpleDateFormat = SimpleDateFormat("MM/yy")
            val expiryDate = simpleDateFormat.parse(cardExpiryDate)
            Assertions.assertTrue(expiryDate.before(Date()))
        }
    }

    /********************* No Cards Test *********************/
    @Test
    fun testNoCards() {
        userService.createUser(testUser)
        val user = userWalletDao.findPhoneNumber(6381342954)
        Assertions.assertTrue(user?.cards!!.isEmpty())
    }

    /********************* Existing Card Test *********************/
    @Test
    fun testExistingCard() {
        userService.createUser(testUser)
        cardService.addCard(6381342954, testCard)
        var user = userWalletDao.findPhoneNumber(6381342954)
        val itr = user?.cards?.iterator()
        var card : Long = 0
        while(itr!!.hasNext()) {
            card = itr.next().id
            break
        }
        cardService.addCard(6381342954, testCard1)
        user = userWalletDao.findPhoneNumber(6381342954)
        val itr1 = user?.cards?.iterator()
        var card1 : Long = 0
        while(itr1!!.hasNext()) {
            card1 = itr1.next().id
        }
        Assertions.assertEquals(card, card1)
    }
}
