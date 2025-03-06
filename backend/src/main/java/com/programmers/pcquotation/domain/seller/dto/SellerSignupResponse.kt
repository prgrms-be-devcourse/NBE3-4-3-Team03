package com.programmers.pcquotation.domain.seller.dto

import com.programmers.pcquotation.domain.seller.entitiy.Seller
import lombok.AllArgsConstructor
import lombok.Builder
import lombok.Getter

@Getter
@AllArgsConstructor
@Builder
class SellerSignupResponse{
    var id: Long? = null
    var username: String? = null
    var companyName: String? = null
    var email: String? = null
    var message: String? = null

    constructor(seller:Seller,message:String) {
        this.id = seller.id
        this.username = seller.username
        this.companyName = seller.companyName
        this.email = seller.email
        this.message = message;
    }
}