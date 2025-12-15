-- Start of DDL Script for Package MUBASHER_LSF.L34_PO_COMMODITIES_PKG
-- Generated 17-Nov-2025 10:52:55 from MUBASHER_LSF@Mubasher_UAT

CREATE OR REPLACE 
PACKAGE              mubasher_lsf.l34_po_commodities_pkg
IS
    TYPE refcursor IS REF CURSOR;

    PROCEDURE l34_add_edit (pkey                          OUT NUMBER,
                            pl34_m12_commodity_code    IN     VARCHAR,
                            pl34_m12_exchange          IN     VARCHAR,
                            pl34_l16_purchase_ord_id   IN     VARCHAR,
                            pl34_percentage            IN     NUMBER,
                            pl34_sold_amnt             IN     NUMBER,
    						pl34_bought_amnt		   IN	  NUMBER);


    PROCEDURE l34_get_po_commodities (
        pview                         OUT refcursor,
        pl34_l16_purchase_ord_id   IN     VARCHAR);
END;
/


CREATE OR REPLACE 
PACKAGE BODY              mubasher_lsf.l34_po_commodities_pkg
IS
    PROCEDURE l34_add_edit (pkey                          OUT NUMBER,
                            pl34_m12_commodity_code    IN     VARCHAR,
                            pl34_m12_exchange          IN     VARCHAR,
                            pl34_l16_purchase_ord_id   IN     VARCHAR,
                            pl34_percentage            IN     NUMBER,
                            pl34_sold_amnt             IN     NUMBER,
                            pl34_bought_amnt		   IN	  NUMBER)
    IS
        icount           NUMBER;
        il34_sold_amnt   NUMBER;
    BEGIN
        SELECT COUNT (l34_m12_commodity_code)
          INTO icount
          FROM l34_purchase_order_commodities
         WHERE     l34_l16_purchase_ord_id = pl34_l16_purchase_ord_id
               AND l34_m12_commodity_code = pl34_m12_commodity_code;

        IF (icount = 0)
        THEN
            INSERT
              INTO l34_purchase_order_commodities (l34_m12_commodity_code,
                                                   l34_m12_exchange,
                                                   l34_l16_purchase_ord_id,
                                                   l34_percentage,
                                                   l34_sold_amnt,
                                                   l34_bought_amnt)
            VALUES (pl34_m12_commodity_code,
                    pl34_m12_exchange,
                    pl34_l16_purchase_ord_id,
                    pl34_percentage,
                    pl34_sold_amnt,
        pl34_bought_amnt);

            pkey := 1;
        ELSE
            SELECT l34_sold_amnt
              INTO il34_sold_amnt
              FROM l34_purchase_order_commodities
             WHERE     l34_l16_purchase_ord_id = pl34_l16_purchase_ord_id
                   AND l34_m12_commodity_code = pl34_m12_commodity_code;

            UPDATE l34_purchase_order_commodities
               SET l34_sold_amnt = pl34_sold_amnt,
                   l34_percentage = pl34_percentage,
                   l34_m12_exchange = pl34_m12_exchange,
                   l34_bought_amnt = pl34_bought_amnt
             WHERE     l34_l16_purchase_ord_id = pl34_l16_purchase_ord_id
                   AND l34_m12_commodity_code = pl34_m12_commodity_code;

            IF (il34_sold_amnt != pl34_sold_amnt)
            THEN
                pkey := 999;
            END IF;
        END IF;
    END;

    PROCEDURE l34_get_po_commodities (
        pview                         OUT refcursor,
        pl34_l16_purchase_ord_id   IN     VARCHAR)
    IS
    BEGIN
        OPEN pview FOR
            SELECT *
              FROM l34_purchase_order_commodities l34, m12_commodities m12
             WHERE     l34.l34_m12_commodity_code = m12.m12_commodity_code
                   AND l34.l34_m12_exchange = m12.m12_exchange
                   AND l34.l34_l16_purchase_ord_id = pl34_l16_purchase_ord_id;
    END;
END;
/


-- End of DDL Script for Package MUBASHER_LSF.L34_PO_COMMODITIES_PKG

