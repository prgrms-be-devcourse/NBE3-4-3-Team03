package com.programmers.pcquotation.domain.seller.dto

import com.programmers.pcquotation.domain.seller.entitiy.Seller
import lombok.AllArgsConstructor
import lombok.Builder
import lombok.Getter

@Getter
@AllArgsConstructor
@Builder
class SellerSignupResponse{
    var id: Long?
    var username: String?
    var companyName: String?
    var email: String?
    var message: String?

    constructor(seller:Seller,message:String) {
        this.id = seller.id
        this.username = seller.username
        this.companyName = seller.companyName
        this.email = seller.email
        this.message = message;
    }
}