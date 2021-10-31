package com.flashpay.backend.eurekaclient.service

import com.flashpay.backend.eurekaclient.dto.UserDto
import com.flashpay.backend.eurekaclient.entity.UserWallet

interface UserService {
    fun createUser(userDto : UserDto) : UserWallet?
    fun getUser (phoneNumber : Long) : UserWallet?
    fun authenticateUser(phoneNumber : Long, password : String) : UserWallet?
    fun updateUserName(phoneNumber : Long, newUserName : String) : UserWallet?
    fun updateUserEmail(phoneNumber : Long, newEmailAddress : String) : UserWallet?
    fun updateUserPassword(phoneNumber : Long, password : String, newPassword : String) : UserWallet?
    fun updateUserPhone(phoneNumber : Long, password : String, newPhoneNumber: Long) : UserWallet?
    fun deleteUser(phoneNumber : Long, password : String) : UserWallet?
    fun getAllUsers() : List<UserWallet>?
    fun getAccounts(phoneNumber : Long) : List<UserDto>?
}