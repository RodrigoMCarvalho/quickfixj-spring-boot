package com.rodrigo.testefix.controller;

import io.allune.quickfixj.spring.boot.starter.template.QuickFixJTemplate;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import quickfix.*;
import quickfix.field.*;

import java.util.HashMap;
import java.util.Map;

import static java.util.UUID.randomUUID;
import static org.springframework.http.HttpStatus.OK;
import static quickfix.FixVersions.BEGINSTRING_FIX41;
import static quickfix.FixVersions.BEGINSTRING_FIXT11;

@RestController
@AllArgsConstructor
public class TesteFixController {

    private static final Map<String, Map<String, Message>> messageMap = createMessageMap();
    private final QuickFixJTemplate serverQuickFixJTemplate;
    private final Acceptor serverAcceptor;
    private final QuickFixJTemplate clientQuickFixJTemplate;
    private final Initiator clientInitiator;


    //http://localhost:8089/send-client-message?fixVersion=FIX.4.1&messageType=OrderCancelRequest
    @RequestMapping("/send-client-message")
    @ResponseStatus(OK)
    public void sendMessageToClient(@RequestParam String fixVersion, @RequestParam String messageType) {

        Map<String, Message> stringMessageMap = messageMap.get(fixVersion);
        Message message = stringMessageMap.get(messageType);
        message.setField(new StringField(Text.FIELD, "Text: " + randomUUID().toString()));

        SessionID sessionID = serverAcceptor.getSessions().stream()
                .filter(id -> id.getBeginString().equals(fixVersion))
                .findFirst()
                .orElseThrow(RuntimeException::new);
        serverQuickFixJTemplate.send(message, sessionID);
    }

    //http://localhost:8089/send-server-message?fixVersion=FIX.4.1&messageType=OrderCancelRequest
    @RequestMapping("/send-server-message")
    @ResponseStatus(OK)
    public void sendMessageToServer(@RequestParam String fixVersion, @RequestParam String messageType) {

        Map<String, Message> stringMessageMap = messageMap.get(fixVersion);
        Message message = stringMessageMap.get(messageType);
        message.setField(new StringField(Text.FIELD, "Text: " + randomUUID().toString()));

        SessionID sessionID = clientInitiator.getSessions().stream()
                .filter(id -> id.getBeginString().equals(fixVersion))
                .findFirst()
                .orElseThrow(RuntimeException::new);
        clientQuickFixJTemplate.send(message, sessionID);
    }

    private static HashMap<String, Map<String, Message>> createMessageMap() {
        HashMap<String, Map<String, Message>> stringMapHashMap = new HashMap<>();
        stringMapHashMap.put(BEGINSTRING_FIX41, initialiseFix41MessageMap());
        stringMapHashMap.put(BEGINSTRING_FIXT11, initialiseFix50MessageMap());
        return stringMapHashMap;
    }

    private static Map<String, Message> initialiseFix41MessageMap() {
        Map<String, Message> messageMap = new HashMap<>();
        messageMap.put("OrderCancelRequest", new quickfix.fix41.OrderCancelRequest(
                new OrigClOrdID("123"),
                new ClOrdID("321"),
                new Symbol("LNUX"),
                new Side(Side.BUY)));
        return messageMap;
    }

    private static Map<String, Message> initialiseFix50MessageMap() {
        Map<String, Message> messageMap = new HashMap<>();
        messageMap.put("Quote", new quickfix.fix50.Quote(new QuoteID("123")));
        return messageMap;
    }
}
