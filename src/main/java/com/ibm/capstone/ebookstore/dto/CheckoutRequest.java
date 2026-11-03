package com.ibm.capstone.ebookstore.dto;

import com.ibm.capstone.ebookstore.entity.PaymentMethod;

public class CheckoutRequest {
    private Long addressId;
    private PaymentMethod paymentMethod = PaymentMethod.CREDIT_CARD;
    private String cardNumber;
    private String cardExpiry;
    private String cvv;
    private Integer redeemPoints = 0;

    public CheckoutRequest() {}

    public Long getAddressId() { return addressId; }
    public void setAddressId(Long addressId) { this.addressId = addressId; }

    public PaymentMethod getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(PaymentMethod paymentMethod) { this.paymentMethod = paymentMethod; }

    public String getCardNumber() { return cardNumber; }
    public void setCardNumber(String cardNumber) { this.cardNumber = cardNumber; }

    public String getCardExpiry() { return cardExpiry; }
    public void setCardExpiry(String cardExpiry) { this.cardExpiry = cardExpiry; }

    public String getCvv() { return cvv; }
    public void setCvv(String cvv) { this.cvv = cvv; }

    public Integer getRedeemPoints() { return redeemPoints; }
    public void setRedeemPoints(Integer redeemPoints) { this.redeemPoints = redeemPoints; }
}
