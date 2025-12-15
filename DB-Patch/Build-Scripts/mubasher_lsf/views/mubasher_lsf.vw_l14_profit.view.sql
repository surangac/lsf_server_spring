-- Start of DDL Script for View MUBASHER_LSF.VW_L14_PROFIT
-- Generated 27-Nov-2025 08:43:29 from MUBASHER_LSF@Mubasher_UAT

CREATE OR REPLACE VIEW mubasher_lsf.vw_l14_profit (
   l14_app_id,
   l14_customer_id,
   l14_purchase_ord_id,
   l14_settlement_status,
   l14_profit_amount,
   l23_profit_amt,
   l23_cum_profit_amt,
   m01_sima_charges,
   m01_transfer_charges,
   l14_customer_approve_state,
   l23_date,
   l14_l15_tenor_id,
   lsf_type_cash_acc,
   non_lsf_type_cash_acc,
   l14_customer_approve_date,
   l14_settled_date,
   l14_vat_amount_admin_fee,
   nuofposymbols,
   noofcollaterals,
   finance_type,
   m01_institution_invest_acc )
AS
SELECT l14_app_id,
           l14_customer_id,
           l14_purchase_ord_id,
           l14_settlement_status,
           l14_profit_amount,
           l23_profit_amt,
           l23_cum_profit_amt,
           l14_sima_charges AS m01_sima_charges,
           m01_transfer_charges,
           l14_customer_approve_state,
           l23_date,
           l14_l15_tenor_id,
           l07.lsf_type_cash_acc,
           l07.non_lsf_type_cash_acc,
           l14_customer_approve_date,
           l14_settled_date,
           l14_vat_amount_admin_fee,
          COALESCE(l16.nuOfPoSymbols,l34.nuOfPoSymbols) AS nuofposymbols,
           NVL (l09.noofcollaterals, 0) noofcollaterals,
           CASE
        WHEN la.L01_FINANCE_METHOD = 1 THEN 'SHARE'
        WHEN la.L01_FINANCE_METHOD = 2 AND la.L01_ROLLOVER_APP_ID > 0 THEN 'ROLLOVER'
        WHEN la.L01_FINANCE_METHOD = 2 AND la.L01_ROLLOVER_APP_ID IS NULL THEN 'COMM'
        ELSE 'N/A'
    END AS finance_type,
    M01_INSTITUTION_INVEST_ACC
      FROM l14_purchase_order l14,
           m01_sys_paras,
           (SELECT l23_date,
                   l23_application_id,
                   l23_profit_amt,
                   l23_cum_profit_amt
              FROM l23_order_profit_log),
           vw_l07_cash_acc l07,
           (SELECT COUNT (*) AS noofcollaterals, l09_l01_app_id
              FROM l09_trading_symbols
             WHERE l09_block_reference > 0
            GROUP BY l09_l01_app_id) l09,

           (SELECT COUNT (*) nuofposymbols, l16_l14_purchase_ord_id
              FROM l16_purchase_order_symbol
            GROUP BY l16_l14_purchase_ord_id) l16,
            (SELECT count(*) nuOfPoSymbols,L34_L16_PURCHASE_ORD_ID FROM MUBASHER_LSF.L34_PURCHASE_ORDER_COMMODITIES
GROUP BY L34_L16_PURCHASE_ORD_ID ) l34,
L01_APPLICATION la

     WHERE     l14.l14_app_id = l09.l09_l01_app_id(+)
           AND l14.l14_purchase_ord_id = l16.l16_l14_purchase_ord_id(+)
           AND l14.l14_purchase_ord_id = l34.L34_L16_PURCHASE_ORD_ID(+)
           AND l14_app_id = l07.l07_l01_app_id
           AND l14_app_id = l23_application_id(+)
           AND l14.l14_app_id=la.L01_APP_ID
/


-- End of DDL Script for View MUBASHER_LSF.VW_L14_PROFIT

