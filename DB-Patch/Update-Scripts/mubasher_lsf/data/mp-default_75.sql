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
        1     AS grp_id,
        'TDWL' AS exch
    FROM dual
)
SELECT 
    s.symbol,
    p.exch,
    p.grp_id,
    p.margin_perc
FROM (
	SELECT '4344' AS symbol FROM dual UNION ALL
    SELECT '1832' FROM dual UNION ALL
    SELECT '2282' FROM dual UNION ALL
    SELECT '3007' FROM dual UNION ALL
    SELECT '8280' FROM dual UNION ALL
    SELECT '8040' FROM dual UNION ALL
    SELECT '8050' FROM dual UNION ALL
    SELECT '8060' FROM dual UNION ALL
    SELECT '8020' FROM dual UNION ALL
    SELECT '4081' FROM dual UNION ALL
    SELECT '6015' FROM dual UNION ALL
    SELECT '4192' FROM dual UNION ALL
    SELECT '4082' FROM dual UNION ALL
    SELECT '2283' FROM dual UNION ALL
    SELECT '3008' FROM dual UNION ALL
    SELECT '2160' FROM dual UNION ALL
    SELECT '4008' FROM dual UNION ALL
    SELECT '4006' FROM dual UNION ALL
    SELECT '4100' FROM dual UNION ALL
    SELECT '6001' FROM dual UNION ALL
    SELECT '2240' FROM dual UNION ALL
    SELECT '2330' FROM dual UNION ALL
    SELECT '2030' FROM dual UNION ALL
    SELECT '4290' FROM dual UNION ALL
    SELECT '3091' FROM dual UNION ALL
    SELECT '2180' FROM dual UNION ALL
    SELECT '2210' FROM dual UNION ALL
    SELECT '6020' FROM dual UNION ALL
    SELECT '2170' FROM dual UNION ALL
    SELECT '2070' FROM dual UNION ALL
    SELECT '1202' FROM dual UNION ALL
    SELECT '4300' FROM dual UNION ALL
    SELECT '2381' FROM dual UNION ALL
    SELECT '4072' FROM dual UNION ALL
    SELECT '2084' FROM dual UNION ALL
    SELECT '1834' FROM dual UNION ALL
    SELECT '4143' FROM dual UNION ALL
    SELECT '2286' FROM dual UNION ALL
    SELECT '4083' FROM dual UNION ALL
    SELECT '1835' FROM dual UNION ALL
    SELECT '4165' FROM dual UNION ALL
    SELECT '2285' FROM dual UNION ALL
    SELECT '6017' FROM dual UNION ALL
    SELECT '4264' FROM dual UNION ALL
    SELECT '1323' FROM dual UNION ALL
    SELECT '4326' FROM dual UNION ALL
    SELECT '4194' FROM dual
    ) s
CROSS JOIN params p;











































































































