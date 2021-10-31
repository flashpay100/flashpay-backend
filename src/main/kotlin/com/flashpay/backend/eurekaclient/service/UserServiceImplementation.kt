package com.flashpay.backend.eurekaclient.service

import com.flashpay.backend.eurekaclient.dao.UserWalletDao
import com.flashpay.backend.eurekaclient.dto.UserDto
import com.flashpay.backend.eurekaclient.entity.Log
import com.flashpay.backend.eurekaclient.entity.UserWallet
import com.flashpay.backend.eurekaclient.exception.*
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

/************************************************************************************
 *    Author           Anush Raghavender
 *    Description      User Service Class That Provides Services For The Following Methods
 *                     createUser, getUser, authenticateUser, updateUserName, updateUserEmail,
 *                     updateUserPassword, updateUserPhone, deleteUser, getAllUsers
 *    Version          1.0
 *    Created Date     27-09-2021
************************************************************************************/
@Service
class UserServiceImplementation(val userWalletDao : UserWalletDao) : UserService {

    @Value("\${admin.initialbalance}")
    val adminAccountBalance : Double = 0.0

    object DateTimeConstants {
        const val zoneId = "Asia/Kolkata"
        const val pattern = "dd-MM-YY, HH:mm:ss"
    }

    object PasswordConstant {
        const val passwordNotMatching = "Entered Password Does Not Match With User Password"
    }

    /************************************* Create User *************************************/
    override fun createUser(userDto : UserDto) : UserWallet? {
        val newEmailAddress = userDto.emailAddress
        val newPhoneNumber = userDto.phoneNumber.toLong()
        if(userWalletDao.findEmailAddress(newEmailAddress) != null) {
            throw ExistingEmailAddressException("User With Email Address \"$newEmailAddress\" Already Exists")
        }
        else if(userWalletDao.findPhoneNumber(newPhoneNumber) != null) {
            throw ExistingPhoneNumberException("User With Phone Number \"$newPhoneNumber\" Already Exists")
        }
        else {
            val id = userDto.phoneNumber.toLong()
            val userName = userDto.userName.toLowerCase().split(" ").joinToString(" "){it.capitalize()}.trimEnd()
            val emailAddress = userDto.emailAddress
            val accountType = userDto.accountType.toLowerCase().capitalize()

            if(accountType == "Investment" && userWalletDao.findExistingAccountName(userName, "Investment") != null) {
                throw ExistingInvestmentAccountNameException("Investment Account Name Already Taken")
            }

            if(accountType == "Service" && userWalletDao.findExistingAccountName(userName, "Service") != null) {
                throw ExistingServiceAccountNameException("Service Account Name Already Taken")
            }

            val accountBalance : Double
            val rewards : Double
            if(accountType == "Admin") {
                accountBalance = adminAccountBalance
                rewards = 0.0
            }
            else {
                accountBalance = 0.0
                rewards = 0.0
            }

            val zonedDateTime = ZonedDateTime.now(ZoneId.of(DateTimeConstants.zoneId))
            val formattingPattern = DateTimeFormatter.ofPattern(DateTimeConstants.pattern)
            val creationDateTime = formattingPattern.format(zonedDateTime)

            val passwordEncoder = BCryptPasswordEncoder()
            val password = passwordEncoder.encode(userDto.password)

            val user = UserWallet(
                id,
                userName,
                emailAddress,
                password,
                accountType,
                "Active",
                accountBalance,
                rewards,
                0.0,
                0.0,
                0.0,
                0.0,
                0.0,
                0.0,
                creationDateTime,
                "",
                "",
                null,
                null,
                null,
                null,
                null
            )

            val log = Log(
                creationDateTime,
                "Account Creation",
                "Success"
            )
            user.logs = mutableSetOf(log)

            user.transactions = mutableSetOf()
            user.cards = mutableSetOf()
            user.donations = mutableMapOf()
            user.investments = mutableMapOf()

            return userWalletDao.save(user)
        }
    }

    /************************************* Get User *************************************/
    override fun getUser(phoneNumber : Long) : UserWallet? {
        return userWalletDao.findPhoneNumber(phoneNumber)
    }

    /************************************* Authenticate User *************************************/
    override fun authenticateUser(phoneNumber : Long, password : String) : UserWallet? {
        val retrievedUser : UserWallet? = userWalletDao.findPhoneNumber(phoneNumber)
        val passwordEncoder = BCryptPasswordEncoder()
        if(retrievedUser == null) {
            throw UserNotFoundException("User With Phone Number \"$phoneNumber\" Not Found")
        }
        else if(!passwordEncoder.matches(password, retrievedUser.password)) {
            throw IncorrectPasswordException("Incorrect Password Entered")
        }
        else {
            if(retrievedUser.accountStatus == "Inactive") {
                val activatedUser : UserWallet = retrievedUser
                userWalletDao.delete(retrievedUser)

                activatedUser.accountStatus = "Active"
                activatedUser.deletionDateTime = ""

                val zonedDateTime = ZonedDateTime.now(ZoneId.of(DateTimeConstants.zoneId))
                val formattingPattern = DateTimeFormatter.ofPattern(DateTimeConstants.pattern)
                val activationDateTime = formattingPattern.format(zonedDateTime)
                activatedUser.activationDateTime = activationDateTime

                val log = Log(
                    activationDateTime,
                    "Account Activation",
                    "Success"
                )

                activatedUser.logs?.add(log)

                return userWalletDao.save(activatedUser)
            }
            return retrievedUser
        }
    }

    /************************************* Update User *************************************/
    override fun updateUserName(phoneNumber : Long, newUserName : String) : UserWallet? {
        val retrievedUser : UserWallet? = userWalletDao.findPhoneNumber(phoneNumber)
        if(retrievedUser == null) {
            throw UserNotFoundException("User With Phone Number \"$phoneNumber\" Not Found")
        }
        else if(retrievedUser.accountStatus == "Inactive") {
            throw InactiveAccountException("User Account With \"${retrievedUser.id}\" Is Inactive")
        }
        else if(newUserName.equals(retrievedUser.userName, ignoreCase = true)) {
            throw NewNameSameException("New User Name Same As Old User Name")
        }
        else {
            val updatedUser : UserWallet = retrievedUser
            userWalletDao.delete(retrievedUser)
            updatedUser.userName = newUserName.toLowerCase()
            updatedUser.userName = updatedUser.userName.split(" ").joinToString(" ") { it.capitalize() }.trimEnd()

            val zonedDateTime = ZonedDateTime.now(ZoneId.of(DateTimeConstants.zoneId))
            val formattingPattern = DateTimeFormatter.ofPattern(DateTimeConstants.pattern)
            val updationDateTime = formattingPattern.format(zonedDateTime)

            val log = Log(
                updationDateTime,
                "User Name Updation",
                "Success"
            )

            updatedUser.logs?.add(log)

            return userWalletDao.save(updatedUser)
        }
    }

    override fun updateUserEmail(phoneNumber : Long, newEmailAddress : String) : UserWallet? {
        val retrievedUser : UserWallet? = userWalletDao.findPhoneNumber(phoneNumber)
        if(retrievedUser == null) {
            throw UserNotFoundException("User With Phone Number \"$phoneNumber\" Not Found")
        }
        else if(retrievedUser.accountStatus == "Inactive") {
            throw InactiveAccountException("User Account With \"${retrievedUser.id}\" Is Inactive")
        }
        else if(newEmailAddress == retrievedUser.emailAddress) {
            throw NewEmailSameException("New Email Address Same As Old Email Address")
        }
        else if(userWalletDao.findEmailAddress(newEmailAddress) != null) {
            throw ExistingEmailAddressException("User With Email Address \"$newEmailAddress\" Already Exists")
        }
        else {
            val updatedUser : UserWallet = retrievedUser
            userWalletDao.delete(retrievedUser)
            updatedUser.emailAddress = newEmailAddress

            val zonedDateTime = ZonedDateTime.now(ZoneId.of(DateTimeConstants.zoneId))
            val formattingPattern = DateTimeFormatter.ofPattern(DateTimeConstants.pattern)
            val updationDateTime = formattingPattern.format(zonedDateTime)

            val log = Log(
                updationDateTime,
                "Email Address Updated To $newEmailAddress",
                "Success"
            )

            updatedUser.logs?.add(log)

            return userWalletDao.save(updatedUser)
        }
    }

    override fun updateUserPassword(phoneNumber : Long, password : String, newPassword : String) : UserWallet? {
        val retrievedUser : UserWallet? = userWalletDao.findPhoneNumber(phoneNumber)
        val passwordEncoder = BCryptPasswordEncoder()
        if(retrievedUser == null) {
            throw UserNotFoundException("User With Phone Number \"$phoneNumber\" Not Found")
        }
        else if(retrievedUser.accountStatus == "Inactive") {
            throw InactiveAccountException("User Account With \"${retrievedUser.id}\" Is Inactive")
        }
        else if(!passwordEncoder.matches(password, retrievedUser.password)) {
            throw IncorrectPasswordException(PasswordConstant.passwordNotMatching)
        }
        else if(passwordEncoder.matches(newPassword, retrievedUser.password)) {
            throw NewPasswordSameException("New Password Same As Old Password")
        }
        else {
            val updatedUser : UserWallet = retrievedUser
            userWalletDao.delete(retrievedUser)
            updatedUser.password = passwordEncoder.encode(newPassword)

            val zonedDateTime = ZonedDateTime.now(ZoneId.of(DateTimeConstants.zoneId))
            val formattingPattern = DateTimeFormatter.ofPattern(DateTimeConstants.pattern)
            val updationDateTime = formattingPattern.format(zonedDateTime)

            val log = Log(
                updationDateTime,
                "Password Updation",
                "Success"
            )

            updatedUser.logs?.add(log)

            return userWalletDao.save(updatedUser)
        }
    }

    override fun updateUserPhone(phoneNumber : Long, password : String, newPhoneNumber: Long) : UserWallet? {
        val retrievedUser : UserWallet? = userWalletDao.findPhoneNumber(phoneNumber)
        val passwordEncoder = BCryptPasswordEncoder()
        if(retrievedUser == null) {
            throw UserNotFoundException("User With Phone Number \"$phoneNumber\" Not Found")
        }
        else if(retrievedUser.accountStatus == "Inactive") {
            throw InactiveAccountException("User Account With \"${retrievedUser.id}\" Is Inactive")
        }
        else if(!passwordEncoder.matches(password, retrievedUser.password)) {
            throw IncorrectPasswordException(PasswordConstant.passwordNotMatching)
        }
        else if(newPhoneNumber == retrievedUser.id) {
            throw NewPhoneSameException("New Phone Number Same As Old Phone Number")
        }
        else if(userWalletDao.findPhoneNumber(newPhoneNumber) != null) {
            throw ExistingPhoneNumberException("User With Phone Number \"$newPhoneNumber\" Already Exists")
        }
        else {
            val updatedUser : UserWallet = retrievedUser
            userWalletDao.delete(retrievedUser)
            updatedUser.id = newPhoneNumber

            val zonedDateTime = ZonedDateTime.now(ZoneId.of(DateTimeConstants.zoneId))
            val formattingPattern = DateTimeFormatter.ofPattern(DateTimeConstants.pattern)
            val updationDateTime = formattingPattern.format(zonedDateTime)

            val log = Log(
                updationDateTime,
                "Phone Number Updated To $newPhoneNumber",
                "Success"
            )

            updatedUser.logs?.add(log)
            
            return userWalletDao.save(updatedUser)
        }
    }

    /************************************* Delete User *************************************/
    override fun deleteUser(phoneNumber : Long, password : String) : UserWallet? {
        val retrievedUser : UserWallet? = userWalletDao.findPhoneNumber(phoneNumber)
        val passwordEncoder = BCryptPasswordEncoder()
        if(retrievedUser == null) {
            throw UserNotFoundException("User With Phone Number \"$phoneNumber\" Not Found")
        }
        else if(retrievedUser.accountStatus == "Inactive") {
            throw InactiveAccountException("User Account With \"${retrievedUser.id}\" Is Inactive")
        }
        else if(!passwordEncoder.matches(password, retrievedUser.password)) {
            throw IncorrectPasswordException(PasswordConstant.passwordNotMatching)
        }
        else if(retrievedUser.accountType == "Admin") {
            throw DeleteAdminAccountException("Cannot Delete Admin Account")
        }
        else {
            val deletedUser : UserWallet = retrievedUser
            userWalletDao.delete(retrievedUser)
            deletedUser.accountStatus = "Inactive"
            val zonedDateTime = ZonedDateTime.now(ZoneId.of(DateTimeConstants.zoneId))
            val formattingPattern = DateTimeFormatter.ofPattern(DateTimeConstants.pattern)
            val deletionDateTime = formattingPattern.format(zonedDateTime)
            deletedUser.deletionDateTime = deletionDateTime
            deletedUser.activationDateTime = ""

            val log = Log(
                deletionDateTime,
                "Account Deletion",
                "Success"
            )

            deletedUser.logs?.add(log)

            return userWalletDao.save(deletedUser)
        }
    }

    /************************************* Get Users *************************************/
    override fun getAllUsers() : List<UserWallet>? {
        if(userWalletDao.findAll().isEmpty()) {
            throw NoUsersFoundException("No Users Found In Database")
        }
        else {
            return userWalletDao.findAll()
        }
    }

    /************************************* Get Accounts *************************************/
    override fun getAccounts(phoneNumber : Long) : List<UserDto>? {
        if(userWalletDao.findAll().isEmpty()) {
            throw NoUsersFoundException("No Users Found In Database")
        }
        else {
            var retrievedAccountsOfType : List<UserWallet>? = userWalletDao.findAll()
            if(retrievedAccountsOfType?.isEmpty() == true) {
                throw NoAccountsFoundException("No Accounts Found")
            }
            retrievedAccountsOfType = retrievedAccountsOfType?.filter{it.id != phoneNumber}
            retrievedAccountsOfType = retrievedAccountsOfType?.filter{it.accountType != "Admin"}
            val retrievedAccounts = mutableListOf<UserDto>()
            for(account in retrievedAccountsOfType!!) {
                val retrievedAccount = UserDto(
                    account.id.toString(),
                    account.userName,
                    account.emailAddress,
                    account.accountType,
                    ""
                )
                retrievedAccounts.add(retrievedAccount)
            }
            return retrievedAccounts
        }
    }
}