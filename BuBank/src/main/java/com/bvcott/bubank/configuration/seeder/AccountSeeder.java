package com.bvcott.bubank.configuration.seeder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.bvcott.bubank.dto.CreateAccountDTO;
import com.bvcott.bubank.dto.CreateAccountDTO.CreateAccountDTOBuilder;
import com.bvcott.bubank.dto.CreateAccountRequestDTO;
import com.bvcott.bubank.dto.account.creationrequest.AdminCommentDTO;
import com.bvcott.bubank.model.account.creationrequest.AccountCreationRequest;
import com.bvcott.bubank.model.account.creationrequest.RequestStatus;
import com.bvcott.bubank.service.AccountService;
import com.bvcott.bubank.service.account.creationrequest.AccountCreationRequestService;

@Component
public class AccountSeeder {
    private final Logger log = LoggerFactory.getLogger(AccountSeeder.class);
    private AccountService accountService;
    private AccountCreationRequestService acrService;

    public AccountSeeder(AccountService accountService, AccountCreationRequestService acrService) {
        this.accountService = accountService;
        this.acrService = acrService;
    }

    public void seed() {
        List<AccountCreationRequest> requests = createAccountCreationRequests();
        createComments(requests);
        acceptAndDenyRequests(requests);
    }

    private void acceptAndDenyRequests(List<AccountCreationRequest> requests) {
        log.debug("[SEED] - Seeding acceptance and denial of requests.");
        for(int i = 0; i < requests.size(); i += 2) {
            String adminUsername = randomizedAdminUsername();
            AccountCreationRequest request = requests.get(i);
            acceptRequest(request, adminUsername);
            log.debug("[SEED] - Accepted request with details - Admin: {} - request {}", adminUsername, request);
        }

        for(int i = 1; i < requests.size(); i += 2) {
            String adminUsername = randomizedAdminUsername();
            AccountCreationRequest request = requests.get(i);
            denyRequest(request, adminUsername);
            log.debug("[SEED] - Denied request with details - Admin: {} - request {}", adminUsername, request);
        }
    }

    private void acceptRequest(AccountCreationRequest request, String username) {
        log.debug("[SEED] - Accepting request with details - Admin: {} - request: {}", username, request);
        CreateAccountDTO acrDTO = null;

        CreateAccountDTOBuilder acrBuilder = CreateAccountDTO
        .builder()
        .accountType(request.getAccountType())
        .initialBalance(BigDecimal.valueOf(Math.random() * 15000));

        switch(request.getAccountType().toLowerCase()) {
            case "business":
                acrDTO = acrBuilder
                    .creditLimit(BigDecimal.valueOf(Math.random() * 500_000))
                    .build();
                break;
            case "checking":
                acrDTO = acrBuilder
                    .overdraftLimit(BigDecimal.valueOf(Math.random() * 15_000))
                    .build();
                break;
            case "savings":
                acrDTO = acrBuilder
                    .interestRate(BigDecimal.valueOf(Math.random() * 35))
                    .build();
                break;
            default:
                break;
        }

        acrService.updateAccountCreationRequestStatus(
            request.getRequestId(), 
            username, 
            RequestStatus.APPROVED, 
            acrDTO
            );
    }

    private void denyRequest(AccountCreationRequest request, String username) {
        log.debug("[SEED] - Denying request with details - Admin: {} - request: {}", username, request);
        acrService.updateAccountCreationRequestStatus(
            request.getRequestId(), 
            username, 
            RequestStatus.REJECTED, 
            CreateAccountDTO
                .builder()
                .accountType(request.getAccountType())
                .initialBalance(BigDecimal.valueOf(Math.random() * 15000))
                .build()
            );
    }

    private void createComments(List<AccountCreationRequest> requests) {
        log.debug("[SEED] - Generating comments...");
        for(AccountCreationRequest request : requests) {
            
            long numberOfCommentsForRequest = Math.round(Math.random() * 6);

            for(int i = 0; i < numberOfCommentsForRequest; i++) {
                String username = randomizedAdminUsername();

                createComment(request, 
                new AdminCommentDTO(
                    username, 
                    "Dummy Comment", 
                    LocalDateTime.now()),
                    username);
            }
        }
    }

    private String randomizedAdminUsername() {
        long number = Math.round((Math.random() * 3) + 1);
        return "admin" + number;
    }

    private void createComment(AccountCreationRequest request, AdminCommentDTO commentDTO, String username) {
        log.debug("[SEED] - Generating comment - request: {} - comment: {} - admin: {}", request, commentDTO, username);
        acrService.addCommentToCreationRequest(request.getRequestId(), commentDTO, username);
    }

    private List<AccountCreationRequest> createAccountCreationRequests() {
        AccountCreationRequest request1 = createAccountCreationRequest(CreateAccountRequestDTO
            .builder()
            .accountType("checking")
            .build(),
            "customer1");
        AccountCreationRequest request2 = createAccountCreationRequest(CreateAccountRequestDTO
            .builder()
            .accountType("checking")
            .build(),
            "customer1");
        AccountCreationRequest request3 =createAccountCreationRequest(CreateAccountRequestDTO
            .builder()
            .accountType("savings")
            .build(),
            "customer1");
        AccountCreationRequest request4 =createAccountCreationRequest(CreateAccountRequestDTO
            .builder()
            .accountType("business")
            .build(),
            "customer1");
        AccountCreationRequest request5 = createAccountCreationRequest(CreateAccountRequestDTO
            .builder()
            .accountType("checking")
            .build(),
            "customer2");
        AccountCreationRequest request6 =createAccountCreationRequest(CreateAccountRequestDTO
            .builder()
            .accountType("savings")
            .build(),
            "customer3");
        AccountCreationRequest request7 =createAccountCreationRequest(CreateAccountRequestDTO
            .builder()
            .accountType("checking")
            .build(),
            "customer3");
        AccountCreationRequest request8 =createAccountCreationRequest(CreateAccountRequestDTO
            .builder()
            .accountType("checking")
            .build(),
            "customer4");
        List<AccountCreationRequest> accountCreationRequests = new ArrayList<>(Arrays.asList(request1, request2, request3, request4,
            request5, request6, request7, request8));
        return accountCreationRequests;
    }

    private AccountCreationRequest createAccountCreationRequest(CreateAccountRequestDTO dto, String username) {
        log.debug("[SEED] - Creating account creation request - request: {} - username: {}", dto, username);
        return accountService.createAccountRequest(dto, username);
    }
}
