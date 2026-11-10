
package com.example.securebank.core.data.remote

import com.example.securebank.core.data.remote.dto.AccountsResponseDto
import retrofit2.http.GET

interface BankApiService {

    @GET("accounts")
    suspend fun getAccounts(): AccountsResponseDto
}
