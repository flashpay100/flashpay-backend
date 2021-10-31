package com.flashpay.backend.eurekaclient.dao

import com.flashpay.backend.eurekaclient.entity.Transaction
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface TransactionDao : MongoRepository<Transaction, Long>