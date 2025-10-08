package tz.co.flex.payment.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tz.co.flex.payment.exception.UnauthorizedException;
import tz.co.flex.payment.model.Account;
import tz.co.flex.payment.repository.AccountRepository;

import java.util.Optional;

@Service
public class ApiTokenService {
    private final AccountRepository accountRepository;

    @Autowired
    public ApiTokenService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public Account validateApiToken(String apiToken) {
        if (apiToken == null || apiToken.trim().isEmpty()) {
            throw new UnauthorizedException("API token is required");
        }

        Optional<Account> accountOpt = accountRepository.findByApiToken(apiToken);
        if (accountOpt.isEmpty() || !accountOpt.get().isActive()) {
            throw new UnauthorizedException("Invalid or inactive API token");
        }

        return accountOpt.get();
    }

    public boolean isIpAllowed(Account account, String clientIp) {
        if (account.getAllowedIps() == null || account.getAllowedIps().trim().isEmpty()) {
            return true; // No IP restrictions
        }
        
        String[] allowedIps = account.getAllowedIps().split(",");
        for (String ip : allowedIps) {
            if (ip.trim().equals(clientIp)) {
                return true;
            }
        }
        return false;
    }
}
