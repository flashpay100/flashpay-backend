package com.flashpay.backend.eurekaclient.dao

import com.flashpay.backend.eurekaclient.entity.UserWallet
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.mongodb.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface UserWalletDao : MongoRepository<UserWallet, Long>  {
    @Query("{'_id' :  ?0}")
    fun findPhoneNumber(phoneNumber : Long): UserWallet?

    @Query("{'emailAddress' :  ?0}")
    fun findEmailAddress(emailAddress : String): UserWallet?

    @Query("{'userName' :  ?0}")
    fun findInvestmentAccount(userName : String): UserWallet?

    @Query("{'userName' :  ?0, 'accountType' : ?1}")
    fun findExistingAccountName(userName : String, accountType : String): UserWallet?

    @Query("{'accountType' :  ?0}")
    fun findAdminAccount(adminAccount : String = "Admin") : UserWallet?

    @Query("{'accountType' :  ?0}")
    fun findAccountByType(accountType : String) : List<UserWallet>?
}