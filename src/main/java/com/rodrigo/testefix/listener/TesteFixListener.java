package com.rodrigo.testefix.listener;

import io.allune.quickfixj.spring.boot.starter.model.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class TesteFixListener {

    @EventListener
    public void handleFromAdmin1(FromAdmin fromAdmin) {
        log.info("fromAdmin: Message={}, SessionId={}", fromAdmin.getMessage(), fromAdmin.getSessionId());
    }

    @EventListener
    public void handleFromAdmin2(FromAdmin fromAdmin) {
        log.info("fromAdmin: Message={}, SessionId={}", fromAdmin.getMessage(), fromAdmin.getSessionId());
    }

    @EventListener
    public void handleFromApp(FromApp fromApp) {
        log.info("fromAdmin: Message={}, SessionId={}", fromApp.getMessage(), fromApp.getSessionId());
    }

    @EventListener
    public void handleCreate(Create create) {
        log.info("onCreate: SessionId={}", create.getSessionId());
    }

    @EventListener
    public void handleLogon(Logon logon) {
        log.info("onLogon: SessionId={}", logon.getSessionId());
    }

    @EventListener
    public void handleLogout(Logout logout) {
        log.info("onLogout: SessionId={}", logout.getSessionId());
    }

    @EventListener
    public void handleToAdmin(ToAdmin toAdmin) {
        log.info("toAdmin: SessionId={}", toAdmin.getSessionId());
    }

    @EventListener
    public void handleToApp(ToApp toApp) {
        log.info("toApp: SessionId={}", toApp.getSessionId());
    }
}
