package com.jarekbank.domain.account

import com.jarekbank.domain.account.swap.SwapService
import com.jarekbank.domain.account.swap.SwapWithoutFeeServiceImpl
import com.jarekbank.domain.account.wallet.WalletService
import com.jarekbank.domain.account.wallet.WalletServiceImpl
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.math.BigDecimal

@Service
class AccountFacade: SwapService, WalletService {
    
    @Autowired
    private lateinit var swapService: SwapWithoutFeeServiceImpl
    @Autowired
    private lateinit var walletService: WalletServiceImpl

    override fun getWallet(pesel: String) =
        walletService.getWallet(pesel)

    override fun calculate(from: String, to: String, amount: BigDecimal) =
        swapService.calculate(from, to, amount)

    override fun swap(pesel: String, from: String, to: String, amount: BigDecimal) =
        swapService.swap(pesel, from, to, amount)
}


