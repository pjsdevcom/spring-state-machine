package com.pjsdev.springstatemachine.domain;

import lombok.*;

import javax.persistence.*;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Payment {

    @Id
    @GeneratedValue
    private Long id;

    @Enumerated(EnumType.STRING)
    private PaymentState state;

    private BigDecimal amount;
}
