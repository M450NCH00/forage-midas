package com.jpmc.midascore.entity;

import com.jpmc.midascore.foundation.Transaction;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

@Entity
public class TransactionRecord {

    @Id
    @GeneratedValue()
    private long id;

    @Column(nullable = false)
    private long senderId;

    @Column(nullable = false)
    private long recipientId;

    @Column(nullable = false)
    private float amount;

    @Column(nullable = false)
    private float incentive;

    public TransactionRecord(Transaction transaction, float incentive) {
        this.senderId = transaction.getSenderId();
        this.recipientId = transaction.getRecipientId();
        this.amount = transaction.getAmount();
        this.incentive = incentive;
    }

    public TransactionRecord(long senderId, long recipientId, float amount, float incentive) {
        this.senderId = senderId;
        this.recipientId = recipientId;
        this.amount = amount;
        this.incentive = incentive;
    }

    public Long getId() {
        return id;
    }

    public Long getSenderId() {
        return senderId;
    }

    public Long getRecipientId() {
        return recipientId;
    }

    public float getAmount() {
        return amount;
    }

    public float getIncentive() {
        return incentive;
    }

    public void setSenderId(long senderId) {
        this.senderId = senderId;
    }

    public void setRecipientId(long recipientId) {
        this.recipientId = recipientId;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    public void setIncentive(float incentive) {
        this.incentive = incentive;
    }
}
