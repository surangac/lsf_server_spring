-- Start of DDL Script for View MUBASHER_LSF.VW_L14_PURCHASE_ORDER
-- Generated 27-Nov-2025 08:43:59 from MUBASHER_LSF@Mubasher_UAT

CREATE OR REPLACE VIEW mubasher_lsf.vw_l14_purchase_order (
   nuofposymbols,
   noofcollaterals,
   l14_purchase_ord_id,
   l14_customer_id,
   l14_ord_value,
   l14_ord_settlement_amount,
   l14_settlement_date,
   l14_trading_account,
   l14_exchange,
   l14_settlement_account,
   l14_is_one_time_settlement,
   l14_installment_frequency,
   l14_set_duration_months,
   l14_approval_status,
   l14_created_date,
   l14_l15_tenor_id,
   l14_app_id,
   l14_profit_amount,
   l14_sibour_amount,
   l14_libour_amount,
   l14_profit_percentage,
   l14_approved_by_id,
   l14_approved_by_name,
   l14_approved_date,
   l14_ord_status,
   l14_ord_completed_value,
   l14_customer_approve_state,
   l14_customer_approve_date,
   l14_customer_comment,
   l14_no_of_calling_attempts,
   l14_last_called_time,
   l14_settlement_status,
   l14_liquidation_date,
   l14_liquidation_status,
   l14_accepted_client_ip,
   l14_accepted_date,
   l14_settled_date,
   finance_type,
   m01_sima_charges,
   l14_vat_amount_admin_fee )
AS
select COALESCE(l16.nuOfPoSymbols,l34.nuOfPoSymbols) AS nuOfPoSymbols,l09.noOfcollaterals, "L14_PURCHASE_ORD_ID","L14_CUSTOMER_ID","L14_ORD_VALUE","L14_ORD_SETTLEMENT_AMOUNT","L14_SETTLEMENT_DATE",
"L14_TRADING_ACCOUNT","L14_EXCHANGE","L14_SETTLEMENT_ACCOUNT","L14_IS_ONE_TIME_SETTLEMENT","L14_INSTALLMENT_FREQUENCY",
"L14_SET_DURATION_MONTHS","L14_APPROVAL_STATUS","L14_CREATED_DATE","L14_L15_TENOR_ID","L14_APP_ID","L14_PROFIT_AMOUNT",
"L14_SIBOUR_AMOUNT","L14_LIBOUR_AMOUNT","L14_PROFIT_PERCENTAGE","L14_APPROVED_BY_ID","L14_APPROVED_BY_NAME","L14_APPROVED_DATE",
"L14_ORD_STATUS","L14_ORD_COMPLETED_VALUE","L14_CUSTOMER_APPROVE_STATE","L14_CUSTOMER_APPROVE_DATE","L14_CUSTOMER_COMMENT",
"L14_NO_OF_CALLING_ATTEMPTS","L14_LAST_CALLED_TIME","L14_SETTLEMENT_STATUS","L14_LIQUIDATION_DATE","L14_LIQUIDATION_STATUS",
"L14_ACCEPTED_CLIENT_IP","L14_ACCEPTED_DATE","L14_SETTLED_DATE",
CASE
        WHEN la.L01_FINANCE_METHOD = 1 THEN 'SHARE'
        WHEN la.L01_FINANCE_METHOD = 2 AND la.L01_ROLLOVER_APP_ID > 0 THEN 'ROLLOVER'
        WHEN la.L01_FINANCE_METHOD = 2 AND la.L01_ROLLOVER_APP_ID IS NULL THEN 'COMM'
        ELSE 'N/A'
    END AS finance_type, l14_sima_charges AS m01_sima_charges,l14_vat_amount_admin_fee
from l14_purchase_order l14,
(select count(*) as noOfcollaterals,l09_l01_app_id from l09_trading_symbols
where l09_block_reference>0
group by l09_l01_app_id) l09,
(select count(*) nuOfPoSymbols,l16_l14_purchase_ord_id
from l16_purchase_order_symbol
group by  l16_l14_purchase_ord_id) l16,
(SELECT count(*) nuOfPoSymbols,L34_L16_PURCHASE_ORD_ID FROM MUBASHER_LSF.L34_PURCHASE_ORDER_COMMODITIES
GROUP BY L34_L16_PURCHASE_ORD_ID ) l34,

L01_APPLICATION la
where l14.l14_app_id=l09.l09_l01_app_id(+)
and l14.l14_purchase_ord_id=l16.l16_l14_purchase_ord_id(+)
AND l14.l14_purchase_ord_id = l34.L34_L16_PURCHASE_ORD_ID(+)
AND l14.l14_app_id=la.L01_APP_ID
/

-- End of DDL Script for View MUBASHER_LSF.VW_L14_PURCHASE_ORDER

