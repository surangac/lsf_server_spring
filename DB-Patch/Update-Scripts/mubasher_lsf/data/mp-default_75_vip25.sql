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
        3     AS grp_id,
        'TDWL' AS exch
    FROM dual
)
SELECT 
    s.symbol,
    p.exch,
    p.grp_id,
    p.margin_perc
FROM (
	SELECT '4336'  AS symbol FROM dual UNION ALL
    SELECT '4339'  FROM dual UNION ALL
    SELECT '4141'  FROM dual UNION ALL
    SELECT '8180'  FROM dual UNION ALL
    SELECT '4331'  FROM dual UNION ALL
    SELECT '4333'  FROM dual UNION ALL
    SELECT '4337'  FROM dual UNION ALL
    SELECT '4342'  FROM dual UNION ALL
    SELECT '4340'  FROM dual UNION ALL
    SELECT '4346'  FROM dual UNION ALL
    SELECT '4348'  FROM dual UNION ALL
    SELECT '4321'  FROM dual UNION ALL
    SELECT '8100'  FROM dual UNION ALL
    SELECT '4334'  FROM dual UNION ALL
    SELECT '2380'  FROM dual UNION ALL
    SELECT '4349'  FROM dual UNION ALL
    SELECT '4061'  FROM dual UNION ALL
    SELECT '6002'  FROM dual UNION ALL
    SELECT '6060'  FROM dual UNION ALL
    SELECT '4250'  FROM dual UNION ALL
    SELECT '2340'  FROM dual UNION ALL
    SELECT '4040'  FROM dual UNION ALL
    SELECT '2090'  FROM dual UNION ALL
    SELECT '6090'  FROM dual UNION ALL
    SELECT '4080'  FROM dual UNION ALL
    SELECT '4310'  FROM dual UNION ALL
    SELECT '4110'  FROM dual UNION ALL
    SELECT '4170'  FROM dual UNION ALL
    SELECT '4010'  FROM dual UNION ALL
    SELECT '2100'  FROM dual UNION ALL
    SELECT '4330'  FROM dual UNION ALL
    SELECT '4335'  FROM dual UNION ALL
    SELECT '4338'  FROM dual UNION ALL
    SELECT '4345'  FROM dual UNION ALL
    SELECT '4347'  FROM dual UNION ALL
    SELECT '8300'  FROM dual UNION ALL
    SELECT '7201'  FROM dual UNION ALL
    SELECT '8170'  FROM dual UNION ALL
    SELECT '6013'  FROM dual UNION ALL
    SELECT '7204'  FROM dual UNION ALL
    SELECT '4350'  FROM dual UNION ALL
    SELECT '6016'  FROM dual UNION ALL
    SELECT '4324'  FROM dual UNION ALL
    SELECT '6018'  FROM dual UNION ALL
    SELECT '4019'  FROM dual
    ) s
CROSS JOIN params p;





























































































