-- Start of DDL Script for Package MUBASHER_LSF.N04_MESSAGE_OUT_PKG
-- Generated 11/27/2025 9:33:12 AM from MUBASHER_LSF@(DESCRIPTION =(ADDRESS_LIST =(ADDRESS = (PROTOCOL = TCP)(HOST = 192.168.14.243)(PORT = 1529)))(CONNECT_DATA =(SERVICE_NAME = ABICQA)))

CREATE OR REPLACE
PACKAGE n04_message_out_pkg
/* Formatted on 29-Mar-2016 00:16:20 (QP5 v5.206) */
IS
    --
    -- To modify this template, edit file PKGSPEC.TXT in TEMPLATE
    -- directory of SQL Navigator
    --
    -- Purpose: Briefly explain the functionality of the package
    --
    -- MODIFICATION HISTORY
    -- Person      Date    Comments
    -- ---------   ------  ------------------------------------------
    -- Enter package declarations as shown below

    TYPE refcursor IS REF CURSOR;

    PROCEDURE n04_add_message_out (pkey                       OUT NUMBER,
                                   p04_uid                 IN     NUMBER,
                                   p04_user_id             IN     VARCHAR2,
                                   p04_notification_type   IN     VARCHAR2,
                                   p04_language            IN     VARCHAR2,
                                   p04_attachment          IN     NUMBER,
                                   p04_mobile_no           IN     VARCHAR2,
                                   p04_from_address        IN     VARCHAR2,
                                   p04_to_addresses        IN     VARCHAR2,
                                   p04_cc_addresses        IN     VARCHAR2,
                                   p04_bcc_addresses       IN     VARCHAR2,
                                   p04_message             IN     VARCHAR2,
                                   p04_subject             IN     VARCHAR2,
                                   p04_status              IN     VARCHAR2,
                                   p04_is_custom           IN     NUMBER,
                                   p04_sent_by             IN     VARCHAR2,
                                   pn04_tp_sms             IN     VARCHAR2,
                                   pn04_tp_email           IN     VARCHAR2);

    PROCEDURE n04_update_status_message_out (pkey            OUT NUMBER,
                                             p04_uid      IN     NUMBER,
                                             p04_status   IN     NUMBER);

    PROCEDURE n04_get_custom_message_history (pview OUT refcursor);

    PROCEDURE n04_get_message_history (pview OUT refcursor,
                                   fromdate   IN     VARCHAR2,
                                   todate     IN     VARCHAR2);


END;
/



-- End of DDL Script for Package MUBASHER_LSF.N04_MESSAGE_OUT_PKG




-- Start of DDL Script for Package Body MUBASHER_LSF.N04_MESSAGE_OUT_PKG
-- Generated 11/27/2025 9:32:17 AM from MUBASHER_LSF@(DESCRIPTION =(ADDRESS_LIST =(ADDRESS = (PROTOCOL = TCP)(HOST = 192.168.14.243)(PORT = 1529)))(CONNECT_DATA =(SERVICE_NAME = ABICQA)))

CREATE OR REPLACE
PACKAGE BODY n04_message_out_pkg
/* Formatted on 4/11/2016 12:16:39 PM (QP5 v5.206) */
IS
    --
    -- To modify this template, edit file PKGBODY.TXT in TEMPLATE
    -- directory of SQL Navigator
    --
    -- Purpose: Briefly explain the functionality of the package body
    --
    -- MODIFICATION HISTORY
    -- Person      Date    Comments
    -- ---------   ------  ------------------------------------------
    -- Enter procedure, function bodies as shown below


    PROCEDURE n04_add_message_out (pkey                       OUT NUMBER,
                                   p04_uid                 IN     NUMBER,
                                   p04_user_id             IN     VARCHAR2,
                                   p04_notification_type   IN     VARCHAR2,
                                   p04_language            IN     VARCHAR2,
                                   p04_attachment          IN     NUMBER,
                                   p04_mobile_no           IN     VARCHAR2,
                                   p04_from_address        IN     VARCHAR2,
                                   p04_to_addresses        IN     VARCHAR2,
                                   p04_cc_addresses        IN     VARCHAR2,
                                   p04_bcc_addresses       IN     VARCHAR2,
                                   p04_message             IN     VARCHAR2,
                                   p04_subject             IN     VARCHAR2,
                                   p04_status              IN     VARCHAR2,
                                   p04_is_custom           IN     NUMBER,
                                   p04_sent_by             IN     VARCHAR2,
                                   pn04_tp_sms             IN     VARCHAR2,
                                   pn04_tp_email           IN     VARCHAR2)
    IS
    BEGIN
        INSERT INTO n04_message_out (n04_uid,
                                     n04_time,
                                     n04_user_id,
                                     n04_notification_type,
                                     n04_language,
                                     n04_attachment,
                                     n04_mobile_no,
                                     n04_from_address,
                                     n04_to_addresses,
                                     n04_cc_addresses,
                                     n04_bcc_addresses,
                                     n04_message,
                                     n04_subject,
                                     n04_status,
                                     n04_is_custom,
                                     n04_sent_by,
                                     n04_tp_sms,
                                     n04_tp_email)
             VALUES (p04_uid,
                     SYSDATE,
                     p04_user_id,
                     p04_notification_type,
                     p04_language,
                     p04_attachment,
                     p04_mobile_no,
                     p04_from_address,
                     p04_to_addresses,
                     p04_cc_addresses,
                     p04_bcc_addresses,
                     p04_message,
                     p04_subject,
                     p04_status,
                     p04_is_custom,
                     p04_sent_by,
                     pn04_tp_sms,
                     pn04_tp_email);

        pkey := 1;
    END;


    PROCEDURE n04_update_status_message_out (pkey            OUT NUMBER,
                                             p04_uid      IN     NUMBER,
                                             p04_status   IN     NUMBER)
    IS
    BEGIN
        UPDATE n04_message_out
           SET n04_status = p04_status
         WHERE n04_uid = p04_uid;

        pkey := 1;
    END;

    PROCEDURE n04_get_custom_message_history (pview OUT refcursor)
    IS
    BEGIN
        OPEN pview FOR
              SELECT *
                FROM n04_message_out
               WHERE n04_is_custom = 1
            ORDER BY n04_time;
    END;

    PROCEDURE n04_get_message_history (pview OUT refcursor,
                                   fromdate   IN     VARCHAR2,
                                   todate     IN     VARCHAR2)
    IS
    BEGIN
        OPEN pview FOR
              SELECT *
                FROM n04_message_out WHERE n04_time BETWEEN TO_DATE (fromdate, 'yyyy-mm-dd') AND TO_DATE (todate, 'yyyy-mm-dd') + 1
            ORDER BY n04_time DESC;
    END;
END;
/



-- End of DDL Script for Package Body MUBASHER_LSF.N04_MESSAGE_OUT_PKG

