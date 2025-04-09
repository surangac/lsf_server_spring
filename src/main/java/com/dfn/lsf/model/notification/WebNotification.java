package com.dfn.lsf.model.notification;

/**
 * Created by Atchuthan on 6/10/2015.
 */

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class WebNotification {
    private String messageId;
    private String applicationId;
    private String subject;
    private String body;
    private String reference;
    private String date;
    private int status;
}
