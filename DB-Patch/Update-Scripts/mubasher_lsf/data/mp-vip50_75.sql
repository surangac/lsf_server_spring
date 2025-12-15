INSERT INTO MUBASHER_LSF.l35_symbol_marginability_perc
(
    l35_l08_symbol_code,
    l35_l08_exchange,
    l35_l11_marginability_grp_id,
    l35_marginability_percentage
)
WITH params AS (
    SELECT 
        75    AS margin_perc,
        2     AS grp_id,
        'TDWL' AS exch
    FROM dual
)
SELECT 
    s.symbol,
    p.exch,
    p.grp_id,
    p.margin_perc
FROM (
	SELECT '4220'AS symbol FROM DUAL UNION ALL
	SELECT '2350' FROM DUAL UNION ALL
	SELECT '8150' FROM DUAL UNION ALL
	SELECT '6050' FROM DUAL UNION ALL
	SELECT '2140' FROM DUAL UNION ALL
	SELECT '2130' FROM DUAL UNION ALL
	SELECT '4130' FROM DUAL UNION ALL
	SELECT '2220' FROM DUAL UNION ALL
	SELECT '4332' FROM DUAL UNION ALL
	SELECT '4144' FROM DUAL UNION ALL
	SELECT '7211' FROM DUAL UNION ALL
	SELECT '4145' FROM DUAL UNION ALL
	SELECT '4021' FROM DUAL
    ) s
CROSS JOIN params p;




































































































