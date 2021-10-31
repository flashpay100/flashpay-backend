package com.flashpay.backend.eurekaclient.test

import com.flashpay.backend.eurekaclient.dao.UserWalletDao
import com.flashpay.backend.eurekaclient.dto.UserDto
import com.flashpay.backend.eurekaclient.service.UserService
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

@SpringBootTest
class UserTest(@Autowired val userService: UserService, @Autowired val userWalletDao: UserWalletDao) {

    private val user : UserDto = UserDto(
        "6381342954",
        "Anush Raghavender",
        "anushraghavender3@gmail.com",
        "Personal",
        "anush12345$"
    )
    private val testUser = user

    private val user1 : UserDto = UserDto(
        "7358440894",
        "Shri Ram",
        "shriram@gmail.com",
        "Personal",
        "shriram12345$"
    )
    private val testUser1 = user1

    private val admin : UserDto = UserDto(
        "9999999999",
        "Flash Pay",
        "admin@flashpay.com",
        "Admin",
        "flashpay12345$"
    )
    val testAdmin = admin

    /********************* Create User Test *********************/
    @Test
    fun testCreateUser() {
        val createdUser = userService.createUser(testUser)
        Assertions.assertEquals(6381342954, createdUser?.id)
    }

    /********************* Get User Test *********************/
    @Test
    fun testGetUser() {
        userService.createUser(testUser)
        val retrievedUser = userService.getUser(6381342954)
        Assertions.assertEquals(6381342954, retrievedUser?.id)
    }

    /********************* Authenticate User Test *********************/
    @Test
    fun testAuthenticateUser() {
        userService.createUser(testUser)
        val retrievedUser = userService.authenticateUser(6381342954, "anush12345$")
        val passwordEncoder = BCryptPasswordEncoder()
        Assertions.assertEquals(6381342954, retrievedUser?.id)
        Assertions.assertTrue(passwordEncoder.matches("anush12345$", retrievedUser?.password))
    }

    /********************* Update User Name Test *********************/
    @Test
    fun testUpdateUserName() {
        userService.createUser(testUser)
        val updatedUser = userService.updateUserName(6381342954, "Hari Om")
        Assertions.assertEquals("Hari Om", updatedUser?.userName)
    }

    /********************* Update User Email Test *********************/
    @Test
    fun testUpdateUserEmail() {
        userService.createUser(testUser)
        val updatedUser = userService.updateUserEmail(6381342954, "anush@gmail.com")
        Assertions.assertEquals("anush@gmail.com", updatedUser?.emailAddress)
    }

    /********************* Update User Password Test *********************/
    @Test
    fun testUpdateUserPassword() {
        val createdUser = userService.createUser(testUser)
        val updatedUser = userService.updateUserPassword(6381342954, "anush12345$", "hariom12345$")
        val passwordEncoder = BCryptPasswordEncoder()
        Assertions.assertTrue(passwordEncoder.matches("anush12345$", createdUser?.password))
        Assertions.assertTrue(passwordEncoder.matches("hariom12345$", updatedUser?.password))
    }

    /********************* Update User Phone Test *********************/
    @Test
    fun testUpdateUserPhone() {
        userService.createUser(testUser)
        val updatedUser = userService.updateUserPhone(6381342954, "anush12345$", 7358440894)
        Assertions.assertEquals(7358440894, updatedUser?.id)
    }

    /********************* Delete User Test *********************/
    @Test
    fun testDeleteUser() {
        userService.createUser(testUser)
        val deletedUser = userService.deleteUser(6381342954, "anush12345$")
        Assertions.assertEquals("Inactive", deletedUser?.accountStatus)
    }

    /********************* Delete Admin Test *********************/
    @Test
    fun testDeleteAdmin() {
        userService.createUser(testAdmin)
        val deletedUser = userService.deleteUser(9999999999, "flashpay12345$")
        Assertions.assertEquals("Admin", deletedUser?.accountType)
        Assertions.assertEquals("Inactive", deletedUser?.accountStatus)
    }

    /******************** Existing Email Test ******************/
    @Test
    fun testExistingEmail() {
        userService.createUser(testAdmin)
        val emailAddress = "admin@flashpay.com"
        val existingUser = userWalletDao.findEmailAddress(emailAddress)
        testUser.emailAddress = "admin@flashpay.com"
        val createdUser = userService.createUser(testUser)
        Assertions.assertEquals(existingUser?.emailAddress, createdUser?.emailAddress)
    }

    /******************** Existing Phone Test ******************/
    @Test
    fun testExistingPhone() {
        userService.createUser(testAdmin)
        val phoneNumber = 9999999999
        val existingUser = userWalletDao.findPhoneNumber(phoneNumber)
        testUser.phoneNumber = "9999999999"
        val createdUser = userService.createUser(testUser)
        Assertions.assertEquals(existingUser?.id, createdUser?.id)
    }

    /******************** User Not Found Test ******************/
    @Test
    fun testUserNotFound() {
        val phoneNumber = 9999999999
        val existingUser = userWalletDao.findPhoneNumber(phoneNumber)
        val createdUser = userService.createUser(testUser)
        Assertions.assertNotEquals(existingUser?.id, createdUser?.id)
    }

    /******************** Incorrect Password Test ******************/
    @Test
    fun testIncorrectPassword() {
        val createdUser = userService.createUser(testUser)
        userService.authenticateUser(6381342954, "hariom12345$")
        val passwordEncoder = BCryptPasswordEncoder()
        Assertions.assertFalse(passwordEncoder.matches("hariom12345$", createdUser?.password))
    }

    /******************** New Password Same As Old Password Test ******************/
    @Test
    fun testNewPasswordSameAsOldPassword() {
        userService.createUser(testUser)
        val updatedUser = userService.updateUserPassword(6381342954, "anush12345$", "anush12345$")
        val passwordEncoder = BCryptPasswordEncoder()
        Assertions.assertTrue(passwordEncoder.matches("anush12345$", updatedUser?.password))
    }

    /********************* Get All Users Test *********************/
    @Test
    fun testGetAllUsers() {
        userService.createUser(testUser)
        val retrievedUsers = userService.getAllUsers()
        Assertions.assertNotNull(retrievedUsers)
    }

    /********************* No Users Test *********************/
    @Test
    fun testNoUsers() {
        val retrievedUsers = userService.getAllUsers()
        if (retrievedUsers != null) {
            Assertions.assertTrue(retrievedUsers.isEmpty())
        }
    }

    /********************* Get Accounts Test *********************/
    @Test
    fun testGetAccounts() {
        userService.createUser(testUser)
        userService.createUser(testUser1)
        val retrievedUsers = userService.getAccounts(6381342954)
        val itr = retrievedUsers?.iterator()
        while(itr!!.hasNext()) {
            Assertions.assertEquals("Personal", itr.next().accountType)
        }
    }

    /********************* No Accounts Test *********************/
    @Test
    fun testNoAccounts() {
        userService.createUser(testUser)
        userService.createUser(testUser1)
        val retrievedUsers = userService.getAccounts(6381342954)
        Assertions.assertTrue(retrievedUsers?.isEmpty() == true)
    }

    /********************* Existing Investment Account Name Test *********************/
    @Test
    fun testExistingInvestmentAccountName() {
        testUser.accountType = "Investment"
        userService.createUser(testUser)
        testUser1.userName = "Anush Raghavender"
        testUser1.accountType = "Investment"
        userService.createUser(testUser1)
        val retrievedUser1 = userService.getUser(6381342954)
        val retrievedUser2 = userService.getUser(7358440894)
        Assertions.assertEquals("Investment", retrievedUser1?.accountType)
        Assertions.assertEquals("Investment", retrievedUser2?.accountType)
        Assertions.assertEquals(retrievedUser1?.userName, retrievedUser2?.userName)
    }

    /********************* Existing Service Account Name Test *********************/
    @Test
    fun testExistingServiceAccountName() {
        testUser.accountType = "Service"
        userService.createUser(testUser)
        testUser1.userName = "Anush Raghavender"
        testUser1.accountType = "Service"
        userService.createUser(testUser1)
        val retrievedUser1 = userService.getUser(6381342954)
        val retrievedUser2 = userService.getUser(7358440894)
        Assertions.assertEquals("Service", retrievedUser1?.accountType)
        Assertions.assertEquals("Service", retrievedUser2?.accountType)
        Assertions.assertEquals(retrievedUser1?.userName, retrievedUser2?.userName)
    }
}