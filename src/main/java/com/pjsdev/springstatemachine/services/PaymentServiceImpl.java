package com.pjsdev.springstatemachine.services;

import com.pjsdev.springstatemachine.domain.Payment;
import com.pjsdev.springstatemachine.domain.PaymentEvent;
import com.pjsdev.springstatemachine.domain.PaymentState;
import com.pjsdev.springstatemachine.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.support.DefaultStateMachineContext;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class PaymentServiceImpl implements PaymentService {

    public static final String PAYMENT_ID_HEADER = "payment_id";

    private final PaymentRepository paymentRepository;

    private final StateMachineFactory<PaymentState, PaymentEvent> stateMachineFactory;

    private final PaymentStateChangeInterceptor paymentStateChangeInterceptor;

    @Override
    public Payment newPayment(Payment payment) {
        payment.setState(PaymentState.NEW);
        return paymentRepository.save(payment);
    }

    @Override
    public StateMachine<PaymentState, PaymentEvent> preAuth(Long paymentId) {
        StateMachine<PaymentState, PaymentEvent> sm = build(paymentId);

        sendEvent(paymentId, sm, PaymentEvent.PRE_AUTHORISE);

        return null;
    }

    @Override
    public StateMachine<PaymentState, PaymentEvent> authorisePayment(Long paymentId) {
        StateMachine<PaymentState, PaymentEvent> sm = build(paymentId);

        sendEvent(paymentId, sm, PaymentEvent.AUTH_APPROVED);

        return null;
    }

    @Override
    public StateMachine<PaymentState, PaymentEvent> declineAuth(Long paymentId) {
        StateMachine<PaymentState, PaymentEvent> sm = build(paymentId);

        sendEvent(paymentId, sm, PaymentEvent.AUTH_DECLINED);

        return null;
    }

    private void sendEvent(Long paymentId, StateMachine<PaymentState, PaymentEvent> sm, PaymentEvent event) {
        Message msg = MessageBuilder.withPayload(event).setHeader(PAYMENT_ID_HEADER, paymentId).build();

        sm.sendEvent(msg);
    }

    //to initialize SM from database
    private StateMachine<PaymentState, PaymentEvent> build(Long paymentId) {
        Payment payment = paymentRepository.getById(paymentId);

        StateMachine<PaymentState, PaymentEvent> sm = stateMachineFactory.getStateMachine(Long.toString(payment.getId()));

        sm.stop();

        sm.getStateMachineAccessor()
                .doWithAllRegions(sma -> {
                    sma.addStateMachineInterceptor(paymentStateChangeInterceptor);
                    sma.resetStateMachine(new DefaultStateMachineContext<>(payment.getState(),
                            null, null, null));
                });

        sm.start();

        return sm;
    }
}
