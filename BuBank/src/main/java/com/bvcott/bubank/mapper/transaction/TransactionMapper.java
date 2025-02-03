package com.bvcott.bubank.mapper.transaction;

import com.bvcott.bubank.dto.TransactionDTO;
import com.bvcott.bubank.model.transaction.Transaction;
import com.bvcott.bubank.model.transaction.transfer.TransferTransaction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface TransactionMapper {

    TransactionMapper INSTANCE = Mappers.getMapper(TransactionMapper.class);

    // Map TransactionDTO -> Transaction
    // Note: We’ll ignore the auto-generated ID and set timestamp later (or you can map them if you like).
    @Mapping(target = "transactionId", ignore = true)
    @Mapping(target = "timestamp", ignore = true)
    @Mapping(target = "transactionNumber", ignore = true)
    Transaction toTransactionEntity(TransactionDTO dto);

    // Map TransactionDTO -> TransferTransaction
    // Similarly, we’ll ignore fields you want to manage manually in the service.
    @Mapping(target = "transactionId", ignore = true)
    @Mapping(target = "timestamp", ignore = true)
    @Mapping(target = "transactionNumber", ignore = true)
    @Mapping(target = "linkedTransactionId", ignore = true)
    @Mapping(target = "transferDirection", ignore = true)
    @Mapping(target = "senderAccountNumber", ignore = true)
    TransferTransaction toTransferTransactionEntity(TransactionDTO dto);
}
