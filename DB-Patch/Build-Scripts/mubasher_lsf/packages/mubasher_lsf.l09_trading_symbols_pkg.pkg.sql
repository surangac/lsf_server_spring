-- Start of DDL Script for Package MUBASHER_LSF.L09_TRADING_SYMBOLS_PKG
-- Generated 17-Nov-2025 10:52:51 from MUBASHER_LSF@Mubasher_UAT

CREATE OR REPLACE
PACKAGE mubasher_lsf.l09_trading_symbols_pkg
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

   PROCEDURE l09_add_edit
    ( pkey                               OUT NUMBER,
    pL09_L06_TRADING_ACC_ID VARCHAR2,
    pL09_L08_SYMBOL_CODE  VARCHAR2,
    pL09_L08_EXCHANGE     VARCHAR2,
    pL09_COLLAT_QTY       NUMBER default 0,
    pL09_CONTRIBUTION_TO_COLLAT NUMBER default 0,
    pL09_TRANSFERRED_QTY  NUMBER default 0,
    pL09_BLOCK_REFERENCE    varchar2 default null,
    pL09_STATUS             number default 0,
    pl09_l01_app_id         number default 0,
    pl09_available_qty                NUMBER DEFAULT 0,
    pl09_close_price                  NUMBER DEFAULT 0,
    pl09_ltp                          NUMBER DEFAULT 0
    );
    procedure l09_get_account_symbols(pview OUT refcursor,
   pL09_L06_TRADING_ACC_ID VARCHAR2,
   pl09_l01_app_id         number);

   procedure l09_update_symbol_status(
        pkey                          OUT NUMBER,
        pl09_l06_trading_acc_id       VARCHAR2,
        pl09_l01_app_id               NUMBER,
        pl09_l08_symbol_code              VARCHAR2,
        pl09_status                       NUMBER DEFAULT 0);

END;
/

-- Grants for Package
GRANT EXECUTE ON mubasher_lsf.l09_trading_symbols_pkg TO mubasher_readonly_role
/
GRANT EXECUTE ON mubasher_lsf.l09_trading_symbols_pkg TO mubasher_debug_role
/
GRANT DEBUG ON mubasher_lsf.l09_trading_symbols_pkg TO mubasher_debug_role
/
GRANT EXECUTE ON mubasher_lsf.l09_trading_symbols_pkg TO mubasher_lsf_role
/
GRANT DEBUG ON mubasher_lsf.l09_trading_symbols_pkg TO mubasher_lsf_role
/

CREATE OR REPLACE
PACKAGE BODY mubasher_lsf.l09_trading_symbols_pkg
/* Formatted on 11/15/2016 4:47:39 PM (QP5 v5.206) */
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

    PROCEDURE l09_add_edit (
        pkey                          OUT NUMBER,
        pl09_l06_trading_acc_id           VARCHAR2,
        pl09_l08_symbol_code              VARCHAR2,
        pl09_l08_exchange                 VARCHAR2,
        pl09_collat_qty                   NUMBER DEFAULT 0,
        pl09_contribution_to_collat       NUMBER DEFAULT 0,
        pl09_transferred_qty              NUMBER DEFAULT 0,
        pl09_block_reference              VARCHAR2 DEFAULT NULL,
        pl09_status                       NUMBER DEFAULT 0,
        pl09_l01_app_id                   NUMBER DEFAULT 0,
        pl09_available_qty                NUMBER DEFAULT 0,
        pl09_close_price                  NUMBER DEFAULT 0,
        pl09_ltp                          NUMBER DEFAULT 0)
    IS
        v_rec_count   NUMBER := 0;
        v_symbol_status number:=0;
        v_newSymbolStatus number:=0;
    BEGIN
        SELECT COUNT (*)
          INTO v_rec_count
          FROM l09_trading_symbols
         WHERE     l09_l06_trading_acc_id = pl09_l06_trading_acc_id
               AND l09_l08_symbol_code = pl09_l08_symbol_code
               AND l09_l08_exchange = pl09_l08_exchange
               AND l09_l01_app_id = pl09_l01_app_id;

        IF (v_rec_count > 0)
        THEN

        SELECT l09_status
          INTO v_symbol_status
          FROM l09_trading_symbols
         WHERE     l09_l06_trading_acc_id = pl09_l06_trading_acc_id
               AND l09_l08_symbol_code = pl09_l08_symbol_code
               AND l09_l08_exchange = pl09_l08_exchange
               AND l09_l01_app_id = pl09_l01_app_id;

         if(v_symbol_status<0) then
            v_newSymbolStatus:=v_symbol_status;
         else
            v_newSymbolStatus:=pl09_status;
         end if;


            UPDATE l09_trading_symbols
               SET l09_collat_qty = pl09_collat_qty,
                   l09_contribution_to_collat = pl09_contribution_to_collat,
                   l09_transferred_qty = pl09_transferred_qty,
                   l09_block_reference = pl09_block_reference,
                   l09_status = v_newSymbolStatus,
                   l09_available_qty=pl09_available_qty,
                   l09_close_price=pl09_close_price,
                   l09_ltp=pl09_ltp
             WHERE     l09_l06_trading_acc_id = pl09_l06_trading_acc_id
                   AND l09_l08_symbol_code = pl09_l08_symbol_code
                   AND l09_l08_exchange = pl09_l08_exchange
                   AND l09_l01_app_id = pl09_l01_app_id;
        ELSE
            INSERT INTO l09_trading_symbols (l09_l06_trading_acc_id,
                                             l09_l08_symbol_code,
                                             l09_l08_exchange,
                                             l09_collat_qty,
                                             l09_contribution_to_collat,
                                             l09_transferred_qty,
                                             l09_block_reference,
                                             l09_status,
                                             l09_l01_app_id,
                                             l09_available_qty,
                                             l09_close_price,
                                             l09_ltp)
                 VALUES (pl09_l06_trading_acc_id,
                         pl09_l08_symbol_code,
                         pl09_l08_exchange,
                         pl09_collat_qty,
                         pl09_contribution_to_collat,
                         pl09_transferred_qty,
                         pl09_block_reference,
                         pl09_status,
                         pl09_l01_app_id,
                         pl09_available_qty,
                         pl09_close_price,
                         pl09_ltp);
        END IF;

        pkey := 1;
    END;

    PROCEDURE l09_get_account_symbols (
        pview                     OUT refcursor,
        pl09_l06_trading_acc_id       VARCHAR2,
        pl09_l01_app_id               NUMBER)
    IS
    BEGIN
        OPEN pview FOR
            SELECT l09.*,
                   l08.*,
                   l10.l10_liquid_id,
                   l10.l10_liquid_name
              FROM l09_trading_symbols l09,
                   l08_symbol l08,
                   l10_liquidity_type l10
             WHERE     l09.l09_l08_symbol_code = l08.l08_symbol_code
                   AND l09.l09_l08_exchange = l08.l08_exchange
                   AND l08.l08_l10_liquid_id = l10.l10_liquid_id(+)
                   AND l09.l09_l06_trading_acc_id = pl09_l06_trading_acc_id
                   AND l09_l01_app_id = pl09_l01_app_id;
    END;

    procedure l09_update_symbol_status(
        pkey                          OUT NUMBER,
        pl09_l06_trading_acc_id       VARCHAR2,
        pl09_l01_app_id               NUMBER,
        pl09_l08_symbol_code              VARCHAR2,
        pl09_status                       NUMBER DEFAULT 0)
    is
    v_transferedQty number:=0;
    v_collateralQty number:=0;
    begin

        select l09_collat_qty,l09_transferred_qty
        into v_collateralQty,v_transferedQty
        from l09_trading_symbols
        WHERE     l09_l06_trading_acc_id = pl09_l06_trading_acc_id
                   AND l09_l08_symbol_code = pl09_l08_symbol_code
                   AND l09_l01_app_id = pl09_l01_app_id;
     if pl09_status <0 THEN
        if v_transferedQty >0 then
            v_collateralQty :=v_collateralQty+v_transferedQty;
            v_transferedQty :=0;
        end if;
     end if;


        UPDATE l09_trading_symbols
               SET l09_status = pl09_status,
               l09_collat_qty=v_collateralQty,l09_transferred_qty=v_transferedQty
             WHERE     l09_l06_trading_acc_id = pl09_l06_trading_acc_id
                   AND l09_l08_symbol_code = pl09_l08_symbol_code
                   AND l09_l01_app_id = pl09_l01_app_id;
       pkey:=1;
    exception
        when others then
        pkey:=-1;

    end;

-- Enter further code below as specified in the Package spec.
END;
/


-- End of DDL Script for Package MUBASHER_LSF.L09_TRADING_SYMBOLS_PKG

