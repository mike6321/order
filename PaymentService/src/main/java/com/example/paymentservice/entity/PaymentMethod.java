package com.example.paymentservice.entity;

import com.example.paymentservice.entity.enums.PaymentMethodType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
@Table(indexes = {@Index(name = "idx_userId", columnList = "userId")})
public class PaymentMethod {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    public Long userId;

    private PaymentMethodType paymentMethodType;

    private String creditCardNumber;

    public PaymentMethod(Long userId, PaymentMethodType paymentMethodType, String creditCardNumber) {
        this.userId = userId;
        this.paymentMethodType = paymentMethodType;
        this.creditCardNumber = creditCardNumber;
    }

}
