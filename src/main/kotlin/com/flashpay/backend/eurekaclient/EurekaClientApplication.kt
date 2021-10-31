package com.flashpay.backend.eurekaclient

import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.info.Info
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration
import org.springframework.boot.runApplication
import org.springframework.cloud.netflix.eureka.EnableEurekaClient

@SpringBootApplication(exclude = [SecurityAutoConfiguration::class])
@EnableEurekaClient
@OpenAPIDefinition(info = Info(title = "FlashPay", version = "1.0", description = "Documentation For FlashPay Backend :- The E-Wallet & Payments App."))
class EurekaClientApplication

fun main(args: Array<String>) {
	runApplication<EurekaClientApplication>(*args)
}