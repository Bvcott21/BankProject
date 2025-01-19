package com.bvcott.bubank.model.account.creationrequest;

import com.bvcott.bubank.model.user.Admin;
import com.bvcott.bubank.model.user.Customer;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountCreationRequest {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long requestId;

    @Column(nullable = false)
    private String accountType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="REQUESTED_BY", nullable = false)
    private Customer requestedBy;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RequestStatus status = RequestStatus.PENDING;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "REVIEWED_BY")
    private Admin reviewedBy;

    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime reviewedAt;

    @OneToMany(mappedBy = "request", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AdminComment> adminComments = new ArrayList<>();
}
