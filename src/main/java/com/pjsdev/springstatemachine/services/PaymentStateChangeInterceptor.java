package com.pjsdev.springstatemachine.services;

import com.pjsdev.springstatemachine.domain.Payment;
import com.pjsdev.springstatemachine.domain.PaymentEvent;
import com.pjsdev.springstatemachine.domain.PaymentState;
import com.pjsdev.springstatemachine.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.state.State;
import org.springframework.statemachine.support.StateMachineInterceptorAdapter;
import org.springframework.statemachine.transition.Transition;
import org.springframework.stereotype.Component;

import java.util.Optional;

@RequiredArgsConstructor
@Component
public class PaymentStateChangeInterceptor extends StateMachineInterceptorAdapter<PaymentState, PaymentEvent> {

    private final PaymentRepository paymentRepository;

    @Override
    public void preStateChange(State<PaymentState, PaymentEvent> state, Message<PaymentEvent> message,
                               Transition<PaymentState, PaymentEvent> transition, StateMachine<PaymentState, PaymentEvent> stateMachine,
                               StateMachine<PaymentState, PaymentEvent> rootStateMachine) {

        Optional.ofNullable(message).flatMap(msg -> Optional.ofNullable((Long) msg.getHeaders()
                .getOrDefault(PaymentServiceImpl.PAYMENT_ID_HEADER, -1L))).ifPresent(paymentId -> {
            Payment payment = paymentRepository.getById(paymentId);
            payment.setState(state.getId());
            paymentRepository.save(payment);
        });
    }
}
