-- Start of DDL Script for Package MUBASHER_LSF.L35_SYMBOL_MARGINABILITY_PKG
-- Generated 17-Nov-2025 10:52:55 from MUBASHER_LSF@Mubasher_UAT

CREATE OR REPLACE 
PACKAGE              mubasher_lsf.l35_symbol_marginability_pkg
IS

    TYPE refcursor IS REF CURSOR;

    PROCEDURE l35_get_symbol_margin_perc(appId IN NUMBER, pview OUT SYS_REFCURSOR);

    PROCEDURE l35_add_symbol_margin_perc(pkey OUT NUMBER,
                                         pl35_marginability_grp_id NUMBER,
                                         pl35_symbol_code VARCHAR2,
                                         pl35_exchange VARCHAR2,
                                         pl35_marginability_percentage NUMBER,
                                         pl35_is_marginable NUMBER);

    PROCEDURE l35_get_symbol_groups(pl35_symbol_code IN VARCHAR2, pl35_exchange IN VARCHAR2, pview OUT SYS_REFCURSOR);

    PROCEDURE l35_update_symbol_groups(pkey OUT NUMBER,
                                     pl35_marginability_grp_id NUMBER,
                                     pl35_symbol_code VARCHAR2,
                                     pl35_exchange VARCHAR2,
                                     pl35_marginability_percentage NUMBER);

    PROCEDURE l35_get_margin_perc_by_group(groupId IN NUMBER, pview OUT SYS_REFCURSOR);
    
    PROCEDURE l35_remove_frm_margin_grp(pkey OUT NUMBER,
                                     pl35_marginability_grp_id NUMBER,
                                     pl35_symbol_code VARCHAR2,
                                     pl35_exchange VARCHAR2);

END;
/


CREATE OR REPLACE 
PACKAGE BODY              mubasher_lsf.l35_symbol_marginability_pkg
IS

    PROCEDURE l35_get_symbol_margin_perc(appId IN NUMBER, pview OUT SYS_REFCURSOR)
    IS
    	default_percentage   NUMBER := 0;
    BEGIN
	    
	    SELECT l11.L11_GLOBAL_MARGINABILITY_PERC INTO default_percentage
	    from L11_MARGINABILITY_GROUP l11 WHERE l11.L11_IS_DEFAULT =1;
	    
	    IF appId = -1 THEN
	     OPEN pview FOR
	     
	     			SELECT
					    l08.L08_SYMBOL_CODE AS "symbol_code",
					    default_percentage AS "marginability_percentage",
					    l08.L08_SHORT_DESC AS "security_name",
					    l08.L08_SHORT_DESC_AR AS "security_name_ar"
					FROM
					    vw_l08_symbol_base l08
					WHERE
						decode(l08.L08_SECURITY_TYPE,NULL,'CS',l08.L08_SECURITY_TYPE) NOT in('OPT', 'FUT');
	    
	    else
	        OPEN pview FOR
	              WITH l35_data AS (
					    SELECT 
					        l35.L35_L08_SYMBOL_CODE,
					        l35.L35_MARGINABILITY_PERCENTAGE,
					        l35.L35_L08_EXCHANGE
					    FROM 
					        L35_SYMBOL_MARGINABILITY_PERC l35
					    INNER JOIN 
					        L01_APPLICATION l01
					        ON l35.L35_L11_MARGINABILITY_GRP_ID = l01.L01_L11_MARGINABILITY_GRP_ID
					    WHERE 
					        l01.L01_APP_ID = appId
					)
					-- First Query: Get Marginability Data
					SELECT
					    l35.L35_L08_SYMBOL_CODE AS "symbol_code",
					    l35.L35_MARGINABILITY_PERCENTAGE AS "marginability_percentage",
					    l08.L08_SHORT_DESC AS "security_name",
					    l08.L08_SHORT_DESC_AR AS "security_name_ar"
					FROM
					    l35_data l35
					INNER JOIN 
					    vw_l08_symbol_base l08
					    ON l35.L35_L08_SYMBOL_CODE = l08.L08_SYMBOL_CODE
					    AND l35.L35_L08_EXCHANGE = l08.L08_EXCHANGE
					    AND l08.L08_SECURITY_TYPE NOT IN ('OPT', 'FUT')
					
					UNION ALL
					
					-- Second Query: Default Marginability = 100, Exclude Symbols from First Query
					SELECT
					    l08.L08_SYMBOL_CODE AS "symbol_code",
					    default_percentage AS "marginability_percentage",
					    l08.L08_SHORT_DESC AS "security_name",
					    l08.L08_SHORT_DESC_AR AS "security_name_ar"
					FROM
					    vw_l08_symbol_base l08
					WHERE
					    l08.L08_SECURITY_TYPE NOT IN ('OPT', 'FUT')
					    AND NOT EXISTS (
					        SELECT 1
					        FROM l35_data l35
					        WHERE l35.L35_L08_SYMBOL_CODE = l08.L08_SYMBOL_CODE
					          AND l35.L35_L08_EXCHANGE = l08.L08_EXCHANGE
					    );

	    END IF;
	    
    END;

    PROCEDURE l35_add_symbol_margin_perc(pkey OUT NUMBER,
                                     pl35_marginability_grp_id NUMBER,
                                     pl35_symbol_code VARCHAR2,
                                     pl35_exchange VARCHAR2,
                                     pl35_marginability_percentage NUMBER,
                                     pl35_is_marginable NUMBER)
    IS
        preccount   NUMBER := 0;
    BEGIN
            SELECT COUNT(*) INTO preccount
                           FROM L35_SYMBOL_MARGINABILITY_PERC
                           WHERE
                               L35_L11_MARGINABILITY_GRP_ID=pl35_marginability_grp_id
                                AND L35_L08_SYMBOL_CODE=pl35_symbol_code
                                AND L35_L08_EXCHANGE=pl35_exchange;

            IF(preccount > 0)
                THEN
                    UPDATE L35_SYMBOL_MARGINABILITY_PERC
                        SET
                            L35_MARGINABILITY_PERCENTAGE=pl35_marginability_percentage,
                            L35_IS_MARGINABLE=pl35_is_marginable
                        WHERE
                            L35_L11_MARGINABILITY_GRP_ID=pl35_marginability_grp_id
                                AND L35_L08_SYMBOL_CODE=pl35_symbol_code
                                AND L35_L08_EXCHANGE=pl35_exchange;

                    pkey := 1;
                ELSE
                    INSERT INTO L35_SYMBOL_MARGINABILITY_PERC(
                                                              L35_L08_SYMBOL_CODE,
                                                              L35_L08_EXCHANGE,
                                                              L35_L11_MARGINABILITY_GRP_ID,
                                                              L35_MARGINABILITY_PERCENTAGE,
                                                              L35_IS_MARGINABLE)
                    VALUES (
                            pl35_symbol_code,
                            pl35_exchange,
                            pl35_marginability_grp_id,
                            pl35_marginability_percentage,
                            pl35_is_marginable);

                    pkey := 2;
            END IF;
    END;


    PROCEDURE l35_get_symbol_groups(pl35_symbol_code IN VARCHAR2, pl35_exchange IN VARCHAR2, pview OUT SYS_REFCURSOR)
    IS
    BEGIN
        OPEN pview FOR
            SELECT
                l35."symbol_code" AS "symbol_code",
                l35."marginability_percentage" AS "marginability_percentage",
                l35."security_name" AS "security_name",
                L11_MARGINABILITY_GRP_ID AS "marginability_group_id",
                l11.L11_GROUP_NAME AS "group_name"
            FROM
                L11_MARGINABILITY_GROUP l11 LEFT OUTER JOIN
                  (SELECT
                    l35.L35_L08_SYMBOL_CODE AS "symbol_code",
                    l35.L35_MARGINABILITY_PERCENTAGE AS "marginability_percentage",
                    l35.L35_L08_EXCHANGE AS "security_name",
                    l35.L35_L11_MARGINABILITY_GRP_ID AS "marginability_group_id"

                  FROM
                        L35_SYMBOL_MARGINABILITY_PERC l35

                  WHERE
                        l35.L35_L08_EXCHANGE = pl35_exchange
                        AND l35.L35_L08_SYMBOL_CODE=pl35_symbol_code) l35
                ON L11_MARGINABILITY_GRP_ID=l35."marginability_group_id";
    END;

        PROCEDURE L35_UPDATE_SYMBOL_GROUPS(pkey OUT NUMBER,
                                     pl35_marginability_grp_id NUMBER,
                                     pl35_symbol_code VARCHAR2,
                                     pl35_exchange VARCHAR2,
                                     pl35_marginability_percentage NUMBER)
    IS
        preccount   NUMBER := 0;
    BEGIN
            SELECT COUNT(*) INTO preccount
                           FROM L35_SYMBOL_MARGINABILITY_PERC
                           WHERE
                               L35_L11_MARGINABILITY_GRP_ID=pl35_marginability_grp_id
                                AND L35_L08_SYMBOL_CODE=pl35_symbol_code
                                AND L35_L08_EXCHANGE=pl35_exchange;

            IF(preccount > 0)
                THEN
                    UPDATE L35_SYMBOL_MARGINABILITY_PERC
                        SET
                            L35_MARGINABILITY_PERCENTAGE=pl35_marginability_percentage
                        WHERE
                            L35_L11_MARGINABILITY_GRP_ID=pl35_marginability_grp_id
                                AND L35_L08_SYMBOL_CODE=pl35_symbol_code
                                AND L35_L08_EXCHANGE=pl35_exchange;

                    pkey := 1;
                ELSE
                    INSERT INTO L35_SYMBOL_MARGINABILITY_PERC(
                                                              L35_L08_SYMBOL_CODE,
                                                              L35_L08_EXCHANGE,
                                                              L35_L11_MARGINABILITY_GRP_ID,
                                                              L35_MARGINABILITY_PERCENTAGE)
                    VALUES (
                            pl35_symbol_code,
                            pl35_exchange,
                            pl35_marginability_grp_id,
                            pl35_marginability_percentage);

                    pkey := 2;
            END IF;
    END;

    PROCEDURE l35_get_margin_perc_by_group(groupId IN NUMBER, pview OUT SYS_REFCURSOR)
    IS
    BEGIN
        OPEN pview FOR
              SELECT
                l35.L35_L08_SYMBOL_CODE AS "symbol_code",
                l35.L35_L08_EXCHANGE AS "exchange",
                l35.L35_L11_MARGINABILITY_GRP_ID AS "group_id",
                l35.L35_MARGINABILITY_PERCENTAGE AS "marginability_percentage",
                l35.L35_IS_MARGINABLE AS "is_marginable",
                l08.L08_SHORT_DESC AS "security_name"
              FROM
                    L35_SYMBOL_MARGINABILITY_PERC l35,
                    L08_SYMBOL l08
              WHERE
                    l35.L35_L08_SYMBOL_CODE=l08.L08_SYMBOL_CODE
                    AND l35.L35_L08_EXCHANGE=l08.L08_EXCHANGE
                    AND l35.L35_L11_MARGINABILITY_GRP_ID=groupId;
    END;
    
    PROCEDURE l35_remove_frm_margin_grp(pkey OUT NUMBER,
                                     pl35_marginability_grp_id NUMBER,
                                     pl35_symbol_code VARCHAR2,
                                     pl35_exchange VARCHAR2)
    IS
    BEGIN
	    DELETE FROM L35_SYMBOL_MARGINABILITY_PERC
	    WHERE
                    L35_L08_SYMBOL_CODE=pl35_symbol_code
                    AND L35_L08_EXCHANGE=pl35_exchange
                    AND L35_L11_MARGINABILITY_GRP_ID=pl35_marginability_grp_id;
	    pkey := 1;
	    
	END;
END;
/


-- End of DDL Script for Package MUBASHER_LSF.L35_SYMBOL_MARGINABILITY_PKG

