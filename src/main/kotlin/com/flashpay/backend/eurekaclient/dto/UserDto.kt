package com.flashpay.backend.eurekaclient.dto

import com.fasterxml.jackson.annotation.JsonProperty
import javax.validation.constraints.*

data class UserDto (
    @field:NotEmpty(message = "Phone Number Cannot Be Empty.")
    @field:Pattern(regexp = "[0-9]{10}", message = "Phone Number Should Have 10 Digits & Only Numbers Are Allowed.")
    var phoneNumber : String,

    @field:NotEmpty(message = "User Name Cannot Be Empty.")
    @field:Size(min = 3, max = 30, message = "User Name Should Be Between 3 & 30 Characters.")
    @field:Pattern(regexp = "([A-Za-z]+)|([A-Za-z]+[ ][A-Za-z]+)|([A-Za-z]+[ ][A-Za-z]+[ ][A-Za-z]+)", message = "User Name Can Have Only Alphabets & Maximum Of 3 Words.")
    var userName : String,

    @field:NotEmpty(message = "Email Cannot Be Empty.")
    @field:Pattern(regexp = "^[^@\\s]+@[^@\\s]+\\.[^@\\s]+\$", message = "Enter A Valid Email.")
    var emailAddress : String,

    @field:NotEmpty(message = "Account Type Cannot Be Empty.")
    @field:Pattern(regexp = "Personal|Business|Utility|Service|Charity|Investment|Admin", flags = [Pattern.Flag.CASE_INSENSITIVE], message = "Account Type Has To Be Personal/Business/Utility/Service/Charity/Investment/Admin.")
    var accountType : String,

    @field:NotEmpty(message = "Password Cannot Be Empty.")
    @field:Pattern(regexp = "^(?=.{8,}\$)(?=.*[A-Za-z])(?=.*[0-9])(?=.*\\W).*\$", message = "Length Of Password Should Be Atleast 8 & Contain 1 Digit, 1 Alphabet & 1 Special Character.")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    var password : String
)