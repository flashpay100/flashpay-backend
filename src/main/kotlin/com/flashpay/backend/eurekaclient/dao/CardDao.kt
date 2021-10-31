package com.flashpay.backend.eurekaclient.dao

import com.flashpay.backend.eurekaclient.entity.Card
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface CardDao : MongoRepository<Card, Long>