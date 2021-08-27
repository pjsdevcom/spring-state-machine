package com.pjsdev.springstatemachine.repository;

import com.pjsdev.springstatemachine.domain.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
}
