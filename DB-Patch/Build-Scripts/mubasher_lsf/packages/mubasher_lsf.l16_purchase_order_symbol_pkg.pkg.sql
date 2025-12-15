-- Start of DDL Script for Package MUBASHER_LSF.L16_PURCHASE_ORDER_SYMBOL_PKG
-- Generated 17-Nov-2025 10:52:52 from MUBASHER_LSF@Mubasher_UAT

CREATE OR REPLACE
PACKAGE mubasher_lsf.l16_purchase_order_symbol_pkg
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

   PROCEDURE l16_add_edit
    ( pkey                               OUT NUMBER,
    pL16_L08_SYMBOL_CODE  VARCHAR,
    pL16_L08_EXCHANGE     VARCHAR,
    pL16_L14_PURCHASE_ORD_ID VARCHAR,
    pL16_PERCENTAGE       NUMBER);

    procedure l16_get_po_symbols(pview OUT refcursor,
   pL22_PURCHASE_ORD_ID  VARCHAR);

END;
/

-- Grants for Package
GRANT EXECUTE ON mubasher_lsf.l16_purchase_order_symbol_pkg TO mubasher_readonly_role
/
GRANT EXECUTE ON mubasher_lsf.l16_purchase_order_symbol_pkg TO mubasher_debug_role
/
GRANT DEBUG ON mubasher_lsf.l16_purchase_order_symbol_pkg TO mubasher_debug_role
/
GRANT EXECUTE ON mubasher_lsf.l16_purchase_order_symbol_pkg TO mubasher_lsf_role
/
GRANT DEBUG ON mubasher_lsf.l16_purchase_order_symbol_pkg TO mubasher_lsf_role
/

CREATE OR REPLACE
PACKAGE BODY mubasher_lsf.l16_purchase_order_symbol_pkg
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

   PROCEDURE l16_add_edit
    ( pkey                               OUT NUMBER,
    pL16_L08_SYMBOL_CODE  VARCHAR,
    pL16_L08_EXCHANGE     VARCHAR,
    pL16_L14_PURCHASE_ORD_ID VARCHAR,
    pL16_PERCENTAGE       NUMBER)
    IS
   BEGIN
        insert INTO L16_PURCHASE_ORDER_SYMBOL(L16_L08_SYMBOL_CODE,L16_L08_EXCHANGE,L16_L14_PURCHASE_ORD_ID,L16_PERCENTAGE)
        values(pL16_L08_SYMBOL_CODE,pL16_L08_EXCHANGE,pL16_L14_PURCHASE_ORD_ID,pL16_PERCENTAGE);

        pkey:=1;
   END;
   procedure l16_get_po_symbols(pview OUT refcursor,
   pL22_PURCHASE_ORD_ID  VARCHAR)
   is
   begin
   open pview FOR
        select * from L16_PURCHASE_ORDER_SYMBOL l16,l08_symbol l08
        where l16.l16_l08_symbol_code=l08.l08_symbol_code
        and l16.l16_l08_exchange=l08.l08_exchange
        and l16.l16_l14_purchase_ord_id=pL22_PURCHASE_ORD_ID;
   end;

   -- Enter further code below as specified in the Package spec.
END;
/


-- End of DDL Script for Package MUBASHER_LSF.L16_PURCHASE_ORDER_SYMBOL_PKG

