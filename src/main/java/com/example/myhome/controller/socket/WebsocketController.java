package com.example.myhome.controller.socket;

import com.example.myhome.dto.CashBoxDTO;
import com.example.myhome.model.ApartmentAccount;
import com.example.myhome.model.CashBox;
import com.example.myhome.model.Invoice;
import com.example.myhome.model.Message;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@Slf4j
public class WebsocketController {

    @Autowired
    private SimpMessagingTemplate template;

    @MessageMapping("/cashbox")
    @SendTo("/topic/cashbox")
    public CashBoxDTO cashboxItemMessage(CashBoxDTO dto) throws Exception {
        Thread.sleep(1000);
        return dto;
    }

    @MessageMapping("/invoices")
    @SendTo("/topic/invoices")
    public Invoice invoiceItemMessage(Invoice dto) throws Exception {
        Thread.sleep(1000);
        return dto;
    }

    @MessageMapping("/accounts")
    @SendTo("/topic/accounts")
    public ApartmentAccount accountItemMessage(ApartmentAccount dto) throws Exception {
        Thread.sleep(1000);
        return dto;
    }

    @MessageMapping("/messages")
    @SendTo("/topic/messages")
    public Message messagesItemMessage(Message msg) throws Exception {
        Thread.sleep(1000);
        return msg;
    }
    @MessageMapping("/delMessages")
    @SendTo("/topic/delMessages")
    public Message messagesItemMessageDel(Message msg) throws Exception {
        Thread.sleep(1000);
        return msg;
    }

    public void sendCashboxItem(CashBox item) {
        log.info("Trying to send new cashbox item to the client...");
        this.template.convertAndSend("/topic/cashbox", "ping");
    }

    public void sendInvoiceItem(Invoice invoice) {
        log.info("Trying to send new invoice item to the client...");
        this.template.convertAndSend("/topic/invoices", "ping");
    }

    public void sendAccountItem(ApartmentAccount account){
        log.info("Trying to send new account item to the client...");
        this.template.convertAndSend("/topic/accounts", "ping");
    }

    public void sendMessagesItem(Message item) {
        log.info("Trying to send new message item to the client...");
        System.out.println("catch");
        this.template.convertAndSend("/topic/messages", item);
    }
    public void deleteMessagesItem(Message item) {
        log.info("Trying to send new message item to the client...");
        System.out.println("catch");
        this.template.convertAndSend("/topic/delMessages", item);
    }

}
