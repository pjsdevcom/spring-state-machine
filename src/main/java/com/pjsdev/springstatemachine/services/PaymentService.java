package com.pjsdev.springstatemachine.services;

import com.pjsdev.springstatemachine.domain.Payment;
import com.pjsdev.springstatemachine.domain.PaymentEvent;
import com.pjsdev.springstatemachine.domain.PaymentState;
import org.springframework.statemachine.StateMachine;

public interface PaymentService {

    Payment newPayment(Payment payment);

    StateMachine<PaymentState, PaymentEvent> preAuth(Long paymentId);

    StateMachine<PaymentState, PaymentEvent> authorisePayment(Long paymentId);

    StateMachine<PaymentState, PaymentEvent> declineAuth(Long paymentId);
}
