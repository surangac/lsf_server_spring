-- Start of DDL Script for Package MUBASHER_LSF.M02_APP_STATE_FLOW_PKG
-- Generated 17-Nov-2025 10:52:55 from MUBASHER_LSF@Mubasher_UAT

CREATE OR REPLACE 
PACKAGE              mubasher_lsf.m02_app_state_flow_pkg
/* Formatted on 11/2/2015 3:09:31 PM (QP5 v5.206) */
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

    -- variable_name   datatype;
    TYPE refcursor IS REF CURSOR;

    PROCEDURE m02_approve_application (
        pkey                            OUT VARCHAR2,
        approve_state                IN     NUMBER,
        pl01_app_id                  IN     NUMBER,
        pl02_message                 IN     VARCHAR2,
        pl02_sts_changed_user_id     IN     VARCHAR2,
        pl02_sts_changed_user_name   IN     VARCHAR2,
        pl02_status_changed_ip       IN     VARCHAR2 DEFAULT '127.0.0.1');

    PROCEDURE l01_update_application_level (pl01_current_level    IN NUMBER,
                                            pl01_overall_status   IN NUMBER,
                                            pl01_app_id           IN NUMBER);

    PROCEDURE l02_add_update_state (
        pl02_l01_app_id              IN NUMBER,
        pl02_level_id                IN NUMBER,
        pl02_description             IN VARCHAR2,
        pl02_message                 IN VARCHAR2,
        pl02_sts_changed_user_id     IN VARCHAR2,
        pl02_status_id               IN NUMBER,
        pl02_sts_changed_user_name   IN VARCHAR2,
        pl02_status_changed_ip       IN VARCHAR2 DEFAULT '127.0.0.1');

    PROCEDURE m02_get_app_state_flow (pview OUT refcursor);

    PROCEDURE m02_set_liquidate_state (
        pkey                            OUT VARCHAR2,
        pl01_app_id                  IN     NUMBER,
        pl02_message                 IN     VARCHAR2,
        pl02_sts_changed_user_id     IN     VARCHAR2,
        pl02_sts_changed_user_name   IN     VARCHAR2);

PROCEDURE m02_set_closed_state_system (pkey             OUT VARCHAR2,
                                           pl01_app_id   IN     NUMBER);

    PROCEDURE m02_set_closed_state (
        pkey                            OUT VARCHAR2,
        pl01_app_id                  IN     NUMBER,
        pl02_message                 IN     VARCHAR2,
        pl02_sts_changed_user_id     IN     VARCHAR2,
        pl02_sts_changed_user_name   IN     VARCHAR2,
        pl02_order_id           number default 0);

    PROCEDURE m02_close_app( pkey                            OUT VARCHAR2,
        pl01_app_id                  IN     NUMBER);
END;
/

-- Grants for Package
GRANT EXECUTE ON mubasher_lsf.m02_app_state_flow_pkg TO mubasher_readonly_role
/
GRANT EXECUTE ON mubasher_lsf.m02_app_state_flow_pkg TO mubasher_debug_role
/
GRANT DEBUG ON mubasher_lsf.m02_app_state_flow_pkg TO mubasher_debug_role
/
GRANT EXECUTE ON mubasher_lsf.m02_app_state_flow_pkg TO mubasher_lsf_role
/
GRANT DEBUG ON mubasher_lsf.m02_app_state_flow_pkg TO mubasher_lsf_role
/

CREATE OR REPLACE 
PACKAGE BODY              mubasher_lsf.m02_app_state_flow_pkg
/* Formatted on 5/8/2016 5:02:50 PM (QP5 v5.206) */
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

    PROCEDURE m02_approve_application (
        pkey                            OUT VARCHAR2,
        approve_state                IN     NUMBER,
        pl01_app_id                  IN     NUMBER,
        pl02_message                 IN     VARCHAR2,
        pl02_sts_changed_user_id     IN     VARCHAR2,
        pl02_sts_changed_user_name   IN     VARCHAR2,
        pl02_status_changed_ip       IN     VARCHAR2 DEFAULT '127.0.0.1')
    IS
        current_app_level            NUMBER;
        current_app_status           NUMBER;
        app_reject_status            NUMBER;
        nxt_app_level                NUMBER;
        previous_state_description   VARCHAR (500);
        approve_status               NUMBER;
    	app_type					 NUMBER;
    BEGIN
        SELECT l01_current_level, l01_overall_status,decode(l01_rollover_app_id,null,0,1) AS appType
          INTO current_app_level, current_app_status, app_type
          FROM l01_application a
         WHERE a.l01_app_id = pl01_app_id;

        IF (current_app_status != -999)
        THEN
            SELECT m02_state,
                   m02_state_description,
                   m02_approve_status,
                   m02_reject_status
              INTO nxt_app_level,
                   previous_state_description,
                   approve_status,
                   app_reject_status
              FROM m02_app_state_flow
             WHERE m02_parent_state = current_app_level AND M02_APP_TYPE=app_type;

            IF (approve_state > 0)
            THEN
                l01_update_application_level (nxt_app_level,
                                              approve_status,
                                              pl01_app_id);

                l02_add_update_state (pl01_app_id,
                                      current_app_level,
                                      previous_state_description,
                                      pl02_message,
                                      pl02_sts_changed_user_id,
                                      approve_status,
                                      pl02_sts_changed_user_name,
                                      pl02_status_changed_ip);
                pkey :=
                       TO_CHAR (current_app_level)
                    || '|'
                    || TO_CHAR (approve_status);
            ELSIF (approve_state = -1)
            THEN
                l01_update_application_level (nxt_app_level,
                                              app_reject_status,
                                              pl01_app_id);
                l02_add_update_state (pl01_app_id,
                                      current_app_level,
                                      previous_state_description,
                                      pl02_message,
                                      pl02_sts_changed_user_id,
                                      app_reject_status,
                                      pl02_sts_changed_user_name,
                                      pl02_status_changed_ip);
                pkey := current_app_level || '|' || app_reject_status;
            ELSE
                l01_update_application_level (nxt_app_level,
                                              -999,
                                              pl01_app_id);
                l02_add_update_state (pl01_app_id,
                                      current_app_level,
                                      previous_state_description,
                                      pl02_message,
                                      pl02_sts_changed_user_id,
                                      -999,
                                      pl02_sts_changed_user_name,
                                      pl02_status_changed_ip);
                pkey := current_app_level || '|' || -999;
            END IF;
        ELSE
            pkey := -999;
        END IF;
    END;

    PROCEDURE l01_update_application_level (pl01_current_level    IN NUMBER,
                                            pl01_overall_status   IN NUMBER,
                                            pl01_app_id           IN NUMBER)
    IS
    BEGIN
        UPDATE l01_application
           SET l01_current_level = pl01_current_level,
               l01_overall_status = pl01_overall_status
         WHERE l01_app_id = pl01_app_id;
    END;

    PROCEDURE l02_add_update_state (
        pl02_l01_app_id              IN NUMBER,
        pl02_level_id                IN NUMBER,
        pl02_description             IN VARCHAR2,
        pl02_message                 IN VARCHAR2,
        pl02_sts_changed_user_id     IN VARCHAR2,
        pl02_status_id               IN NUMBER,
        pl02_sts_changed_user_name   IN VARCHAR2,
        pl02_status_changed_ip       IN VARCHAR2 DEFAULT '127.0.0.1')
    IS
    BEGIN
        INSERT INTO l02_app_state (l02_l01_app_id,
                                   l02_level_id,
                                   l02_description,
                                   l02_message,
                                   l02_sts_changed_user_id,
                                   l02_sts_changed_user_name,
                                   l02_sts_changed_date,
                                   l02_notify_type,
                                   l02_status_id,
                                   l02_status_changed_ip)
             VALUES (pl02_l01_app_id,
                     pl02_level_id,
                     pl02_description,
                     pl02_message,
                     pl02_sts_changed_user_id,
                     pl02_sts_changed_user_name,
                     SYSDATE,
                     '2',
                     pl02_status_id,
                     pl02_status_changed_ip);
    END;

    PROCEDURE m02_get_app_state_flow (pview OUT refcursor)
    IS
    BEGIN
        OPEN pview FOR
              SELECT *
                FROM m02_app_state_flow
               WHERE m02_state != 1 AND M02_APP_TYPE = 0
            ORDER BY m02_state;
    END;

    PROCEDURE m02_set_liquidate_state (
        pkey                            OUT VARCHAR2,
        pl01_app_id                  IN     NUMBER,
        pl02_message                 IN     VARCHAR2,
        pl02_sts_changed_user_id     IN     VARCHAR2,
        pl02_sts_changed_user_name   IN     VARCHAR2)
    IS
        current_app_level            NUMBER;
        current_app_status           NUMBER;
        app_reject_status            NUMBER;
        nxt_app_level                NUMBER;
        previous_state_description   VARCHAR (500);
        approve_status               NUMBER;
    	app_type					 NUMBER;
    BEGIN
        SELECT l01_current_level, l01_overall_status,decode(l01_rollover_app_id,null,0,1) AS appType
          INTO current_app_level, current_app_status,app_type
          FROM l01_application a
         WHERE a.l01_app_id = pl01_app_id;

        pkey := -1;

        IF (current_app_status != -999)
        THEN
            SELECT m02_state,
                   m02_state_description,
                   m02_approve_status,
                   m02_reject_status
              INTO nxt_app_level,
                   previous_state_description,
                   approve_status,
                   app_reject_status
              FROM m02_app_state_flow
             WHERE m02_level_type = 1 AND M02_APP_TYPE=app_type;

            l01_update_application_level (nxt_app_level,
                                          approve_status,
                                          pl01_app_id);

            l02_add_update_state (pl01_app_id,
                                  current_app_level,
                                  previous_state_description,
                                  pl02_message,
                                  pl02_sts_changed_user_id,
                                  approve_status,
                                  pl02_sts_changed_user_name,
                                  '127.0.0.1');
            pkey :=
                   TO_CHAR (current_app_level)
                || '|'
                || TO_CHAR (approve_status);
        END IF;
    END;

    PROCEDURE m02_set_closed_state (
        pkey                            OUT VARCHAR2,
        pl01_app_id                  IN     NUMBER,
        pl02_message                 IN     VARCHAR2,
        pl02_sts_changed_user_id     IN     VARCHAR2,
        pl02_sts_changed_user_name   IN     VARCHAR2,
        pl02_order_id                       NUMBER DEFAULT 0)
    IS
        current_app_level            NUMBER;
        current_app_status           NUMBER;
        app_reject_status            NUMBER;
        nxt_app_level                NUMBER;
        previous_state_description   VARCHAR (500);
        approve_status               NUMBER;
        v_ismultipleorderallowed     NUMBER := 0;
        v_minimumodervalue           NUMBER := 0;
    	app_type					 NUMBER;
    BEGIN
        pkey := -1;

        -- set individual order as settle in all the cases
        IF (pl02_order_id > 0)
        THEN
            l14_purchase_order_pkg.l14_update_to_settle_state (pl02_order_id);
        END IF;

        SELECT a.m01_is_multiple_order_allowed, a.m01_minimum_order_value
          INTO v_ismultipleorderallowed, v_minimumodervalue
          FROM m01_sys_paras a;

        SELECT l01_current_level, l01_overall_status,decode(l01_rollover_app_id,null,0,1) AS appType
          INTO current_app_level, current_app_status,app_type
          FROM l01_application a
         WHERE a.l01_app_id = pl01_app_id;

        pkey := TO_CHAR (current_app_level) || '|' || TO_CHAR (approve_status);

        -- in multiple order method application is not set to completed state
        IF (current_app_status != -999 AND v_ismultipleorderallowed = 0)
        THEN
            SELECT m02_state,
                   m02_state_description,
                   m02_approve_status,
                   m02_reject_status
              INTO nxt_app_level,
                   previous_state_description,
                   approve_status,
                   app_reject_status
              FROM m02_app_state_flow
             WHERE m02_level_type = 2 AND M02_APP_TYPE=app_type;

            l01_update_application_level (nxt_app_level,
                                          approve_status,
                                          pl01_app_id);

            l02_add_update_state (pl01_app_id,
                                  current_app_level,
                                  previous_state_description,
                                  pl02_message,
                                  pl02_sts_changed_user_id,
                                  approve_status,
                                  pl02_sts_changed_user_name,
                                  '127.0.0.1');
            pkey :=
                   TO_CHAR (current_app_level)
                || '|'
                || TO_CHAR (approve_status);
        END IF;
    END;

   PROCEDURE m02_set_closed_state_system (pkey             OUT VARCHAR2,
                                           pl01_app_id   IN     NUMBER)
    IS
    v_app_close_lvel NUMBER;
    v_app_close_state number;
    BEGIN

    SELECT a.m01_app_completed_state
          INTO v_app_close_lvel
          FROM m01_sys_paras a;

            l01_update_application_level (v_app_close_lvel,
                                          v_app_close_lvel,
                                          pl01_app_id);

            l02_add_update_state (pl01_app_id,
                                  v_app_close_lvel,
                                  'Liquidated & Closed due to PO not acceptance',
                                  'Liquidated & Closed due to PO not acceptance',
                                  '0001',
                                  '-15',
                                  'System',
                                  '127.0.0.1');
          pkey := 1;
    END;


    PROCEDURE m02_close_app (pkey OUT VARCHAR2, pl01_app_id IN NUMBER)
    IS
     v_app_close_state NUMBER;
    BEGIN
         SELECT a.m01_app_completed_state
          INTO v_app_close_state
          FROM m01_sys_paras a;
            l01_update_application_level (v_app_close_state,
                                          v_app_close_state - 1,
                                          pl01_app_id);
    END;
-- Enter further code below as specified in the Package spec.
END;
/


-- End of DDL Script for Package MUBASHER_LSF.M02_APP_STATE_FLOW_PKG

