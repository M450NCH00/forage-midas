package com.jpmc.midascore.kafka;

import com.jpmc.midascore.component.DatabaseConduit;
import com.jpmc.midascore.entity.TransactionRecord;
import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.foundation.Balance;
import com.jpmc.midascore.foundation.Transaction;
import com.jpmc.midascore.repository.TransactionRepository;
import com.jpmc.midascore.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import static com.jpmc.midascore.constants.MidasConstants.INCENTIVE_PATH;

@Component
public class KafkaConsumer {

    private final UserRepository allUsers;
    private final TransactionRepository allTransactions;
    private final DatabaseConduit databaseConduit;
    private final RestTemplate restTemplate;

    public KafkaConsumer(UserRepository allUsers, TransactionRepository allTransactions, DatabaseConduit databaseConduit, RestTemplate restTemplate) {
        this.allUsers = allUsers;
        this.allTransactions = allTransactions;
        this.databaseConduit = databaseConduit;
        this.restTemplate = restTemplate;
    }

    @KafkaListener(topics = "${general.kafka-topic}", groupId = "midas-core-tasks")
    public void listenTransaction(Transaction transaction) {
        System.out.println("Received transaction: " + transaction);
        UserRecord sender = allUsers.findById(transaction.getSenderId());
        UserRecord recipient = allUsers.findById(transaction.getRecipientId());

        if (sender == null) {
            System.out.println("Sender with not found!");
            return;
        }
        if (recipient == null) {
            System.out.println("Recipient with not found!");
            return;
        }
        if (sender.getBalance() < transaction.getAmount()) {
            System.out.println("Sender doesn't have enough balance!");
            return;
        }

        ResponseEntity<Balance> response = restTemplate.postForEntity(INCENTIVE_PATH, transaction, Balance.class);
        Balance incentive = response.getBody();

        if (incentive == null) {
            System.out.println("Incentive API returned incentive as null!");
            return;
        }

        TransactionRecord validTransactionRecord = new TransactionRecord(transaction, incentive.getAmount());
        allTransactions.save(validTransactionRecord);

        System.out.println("Transaction is valid and saved!");

        sender.setBalance(sender.getBalance() - transaction.getAmount());
        recipient.setBalance(recipient.getBalance() + transaction.getAmount() + incentive.getAmount());
        databaseConduit.save(sender);
        databaseConduit.save(recipient);

        System.out.println("Incentive:");
        System.out.printf("Amount = %f\n", incentive.getAmount());
        System.out.println("New Balances:");
        System.out.printf("%s: %f\n", sender.getName(), sender.getBalance());
        System.out.printf("%s: %f\n\n", recipient.getName(), recipient.getBalance());
    }
}
