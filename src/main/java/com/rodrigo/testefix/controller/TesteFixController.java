package com.rodrigo.testefix.controller;

import io.allune.quickfixj.spring.boot.starter.template.QuickFixJTemplate;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import quickfix.*;
import quickfix.field.*;
import quickfix.fix44.ExecutionReport;

import java.util.HashMap;
import java.util.Map;

import static java.util.UUID.randomUUID;
import static quickfix.FixVersions.*;

@RestController
@RequestMapping("/fix")
@AllArgsConstructor
public class TesteFixController {

    private static final Map<String, Map<String, Message>> messageMap = createMessageMap();
    private final QuickFixJTemplate serverQuickFixJTemplate;
    private final Acceptor serverAcceptor;
    private final QuickFixJTemplate clientQuickFixJTemplate;
    private final Initiator clientInitiator;


    //http://localhost:8089/fix/send-client-message?fixVersion=FIX.4.1&messageType=OrderCancelRequest
    @GetMapping("/send-client-message")
    public ResponseEntity<?> sendMessageToClient(@RequestParam String fixVersion, @RequestParam String messageType) {

        Map<String, Message> stringMessageMap = messageMap.get(fixVersion);
        Message message = stringMessageMap.get(messageType);
        message.setField(new StringField(Text.FIELD, "Text: " + randomUUID().toString()));

        SessionID sessionID = serverAcceptor.getSessions().stream()
                .filter(id -> id.getBeginString().equals(fixVersion))
                .findFirst()
                .orElseThrow(RuntimeException::new);
        serverQuickFixJTemplate.send(message, sessionID);

        return ResponseEntity.ok("OK");
    }

    //http://localhost:8089/fix/send-server-message?fixVersion=FIX.4.1&messageType=OrderCancelRequest
    @GetMapping("/send-server-message")
    public ResponseEntity<?> sendMessageToServer(@RequestParam String fixVersion, @RequestParam String messageType) {

        Map<String, Message> stringMessageMap = messageMap.get(fixVersion);
        Message message = stringMessageMap.get(messageType);
        message.setField(new StringField(Text.FIELD, "Text: " + randomUUID().toString()));

        SessionID sessionID = clientInitiator.getSessions().stream()
                .filter(id -> id.getBeginString().equals(fixVersion))
                .findFirst()
                .orElseThrow(RuntimeException::new);
        clientQuickFixJTemplate.send(message, sessionID);

        return ResponseEntity.ok("OK");
    }
    //http://localhost:8089/fix/execution-report
    @GetMapping(path = "/execution-report")
    public ResponseEntity<?> executionReport() throws SessionNotFound {
        ExecutionReport executionReport = new ExecutionReport(new OrderID("3-2-805331618T-0-0"), new ExecID("3-2-805331618T-0-0"), new ExecType(ExecType.TRADE),
                new OrdStatus(OrdStatus.FILLED), new Side(Side.BUY), new LeavesQty(0), new CumQty(1000000), new AvgPx(5.57765));
        SessionID sessionID = new SessionID(BEGINSTRING_FIX44, "EXEC", "BANZAI");
        OnBehalfOfCompID onBehalfOfCompId = new OnBehalfOfCompID("FX");
        executionReport.set(new TransactTime());
        executionReport.set(new SettlType(SettlType.REGULAR_FX_SPOT_SETTLEMENT));
        executionReport.set(new SettlDate("20201016"));
        executionReport.set(new OrderQty(1000000));
        executionReport.set(new Spread(0));
        executionReport.set(new OrdType(OrdType.MARKET));
        executionReport.set(new Product(Product.CURRENCY));
//		executionReport.set(new TradePublishIndicator(TradePublishIndicator.DO_NOT_PUBLISH_TRADE));
        executionReport.set(new ClOrdID("3-2-805331618T-0-0"));
//		executionReport.set(new OfferPx(5.57765));
        executionReport.set(new Symbol("USD/BRL"));

        Session.sendToTarget(executionReport, sessionID);

        return ResponseEntity.ok("OK");
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
