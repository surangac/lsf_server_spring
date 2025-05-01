package com.dfn.lsf.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.dfn.lsf.jms.LsfJmsListener;
import com.dfn.lsf.model.responseMsg.OMSQueueResponse;
import org.springframework.http.ResponseEntity;
import lombok.RequiredArgsConstructor;
@RestController
@RequiredArgsConstructor
public class JbossListnerController {

    private final LsfJmsListener lsfJmsListener;

    @PostMapping("/jboss/listener")
    public ResponseEntity<OMSQueueResponse> handleJbossListener(@RequestBody String request) {
        System.out.println("Received request: " + request);
        OMSQueueResponse response = lsfJmsListener.handleJmsMessage(request);
        return ResponseEntity.ok(response);
    }
}
