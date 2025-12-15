BEGIN
    FOR i IN (SELECT m07_type FROM m07_murabaha_products)
    LOOP
        FOR j IN 1 .. 2
        LOOP
            FOR k IN 1 .. 2
            LOOP
                BEGIN
                    DECLARE
                        v_exists   NUMBER;
                    BEGIN
                        SELECT COUNT (*)
                          INTO v_exists
                          FROM m11_agreements
                         WHERE     m11_product_type = i.m07_type
                               AND m11_finance_method = j
                               AND m11_agreement_type = k;

                        -- If no matching record exists, insert a new record
                        IF v_exists = 0
                        THEN
                            INSERT
                              INTO m11_agreements (m11_id,
                                                   m11_product_type,
                                                   m11_finance_method,
                                                   m11_agreement_type,
                                                   m11_file_extension,
                                                   m11_file_name,
                                                   m11_file_path,
                                                   m11_version,
                                                   m11_uploaded_user_id,
                                                   m11_uploaded_user_name,
                                                   m11_uploaded_ip)
                            VALUES (seq_m11_id.NEXTVAL,
                                    i.m07_type,
                                    j,
                                    k,
                                    'application/pdf',
                                    'sample',
                                    'sample_path',
                                    1,
                                    0,
                                    'initial_db_patch',
                                    0);
                        END IF;
                    END;
                EXCEPTION
                    WHEN OTHERS
                    THEN
                        -- Handle unexpected exceptions
                        DBMS_OUTPUT.put_line (
                               'Error processing m07_type: '
                            || i.m07_type
                            || ' - '
                            || SQLERRM);
                END;
            END LOOP;
        END LOOP;
    END LOOP;

    COMMIT;
END;
/

BEGIN

    FOR rec IN (SELECT l01_app_id, l01_date,l01_product_type FROM l01_application)
    LOOP
    -- Loop through each record in m11_agreements with specific conditions
    FOR rec2
        IN (SELECT m11_id, m11_version
              FROM m11_agreements
             WHERE     m11_product_type = rec.l01_product_type
                   AND m11_finance_method = 1
                   AND m11_agreement_type = 1)
    LOOP
        -- Loop through all records in l01_application
            -- Check if a record already exists in l32_appove_agreements
            BEGIN
                DECLARE
                    v_exists   NUMBER;
                BEGIN
                    SELECT COUNT (*)
                      INTO v_exists
                      FROM l32_appove_agreements
                     WHERE l32_l01_app_id = rec.l01_app_id;

                    -- If no matching record exists, insert a new record
                    IF v_exists = 0
                    THEN
                        INSERT
                          INTO l32_appove_agreements (l32_id,
                                                      l32_m11_id,
                                                      l32_l01_app_id,
                                                      l32_agreement_date,
                                                      l32_agreement_status,
                                                      l32_m11_version)
                        VALUES (seq_l32_id.NEXTVAL,
                                rec2.m11_id,
                                rec.l01_app_id,
                                rec.l01_date,
                                1,             -- Assuming 1 is a valid status
                                rec2.m11_version);
                    END IF;
                END;
            EXCEPTION
                WHEN OTHERS
                THEN
                    -- Handle unexpected exceptions
                    DBMS_OUTPUT.put_line (
                           'Error processing l01_app_id: '
                        || rec.l01_app_id
                        || ' - '
                        || SQLERRM);
            END;
        END LOOP;
    END LOOP;

    -- Commit the transaction to save changes
    COMMIT;
END;
/



INSERT INTO n03_notification_msg_config 
VALUES(5018,NULL,NULL,NULL,'Authorization to Sell',NULL,NULL,'Authorization to Sell',NULL,NULL,NULL,'AATSA','<EAIMessage><Body><Cust_Prfl>N</Cust_Prfl><Ntfcn_Mthd>2</Ntfcn_Mthd><Org_Id>3</Org_Id><Sndr_Id>BILADINVEST</Sndr_Id><Ovrrd_SMS_Tmplt>OMSNTFY050</Ovrrd_SMS_Tmplt><Ovrrd_Prfrrd_Lang>E</Ovrrd_Prfrrd_Lang><Rcpnt_Lst><Rcpnt_Dtls><Email>BAlOtaibi@albilad-capital.com</Email></Rcpnt_Dtls><Rcpnt_Dtls><Email>MHedayan@albilad-capital.com</Email></Rcpnt_Dtls><Rcpnt_Dtls><Email>ARAlShamary@albilad-capital.com</Email></Rcpnt_Dtls><Rcpnt_Dtls><Email>KM.AlMohainy@albilad-capital.com</Email></Rcpnt_Dtls><Rcpnt_Dtls><Email>MSalehAlruhaimi@albilad-capital.com</Email></Rcpnt_Dtls><Rcpnt_Dtls><Email>TA.AlLuwaimi@albilad-capital.com</Email></Rcpnt_Dtls><Rcpnt_Dtls><Email>AAlzayedi@albilad-capital.com</Email></Rcpnt_Dtls><Rcpnt_Dtls><Email>MohammedP@albilad-capital.com</Email></Rcpnt_Dtls><Rcpnt_Dtls><Email>SBinJadeed@albilad-capital.com</Email></Rcpnt_Dtls><Rcpnt_Dtls><Email>MU.Adlan@albilad-capital.com</Email></Rcpnt_Dtls><Rcpnt_Dtls><Email>MElshami@albilad-capital.com</Email></Rcpnt_Dtls><Rcpnt_Dtls><Email>SIAlGhamdi@albilad-capital.com</Email></Rcpnt_Dtls></Rcpnt_Lst><P1_En>$customerName</P1_En><P1_Ar>$customerName</P1_Ar><P2_En>$applicationId</P2_En><P2_Ar>$applicationId</P2_Ar><P3_En>$cifNumber</P3_En><P3_Ar>$cifNumber</P3_Ar><P4_En>$prefLanguage</P4_En><P4_Ar>$prefLanguage</P4_Ar><P5_En>$lsfTypeTradingAccount</P5_En><P5_Ar>$lsfTypeTradingAccount</P5_Ar><P6_En>$tradingAccount</P6_En><P6_Ar>$tradingAccount</P6_Ar></Body><EAIHeader><MsgFrmt>Snd_Instnt_Ntfctn_Req</MsgFrmt><CIF>$cifNumber</CIF><Lang>E</Lang><ChnlId>MBSR</ChnlId><FunctnId>294001</FunctnId><FE_Ref_No>1442734065267</FE_Ref_No></EAIHeader></EAIMessage>','<EAIMessage><Body><Cust_Prfl>N</Cust_Prfl><Ntfcn_Mthd>2</Ntfcn_Mthd><Org_Id>3</Org_Id><Sndr_Id>BILADINVEST</Sndr_Id><Ovrrd_SMS_Tmplt>OMSNTFY050</Ovrrd_SMS_Tmplt><Ovrrd_Prfrrd_Lang>E</Ovrrd_Prfrrd_Lang><Rcpnt_Lst><Rcpnt_Dtls><Email>BAlOtaibi@albilad-capital.com</Email></Rcpnt_Dtls><Rcpnt_Dtls><Email>MHedayan@albilad-capital.com</Email></Rcpnt_Dtls><Rcpnt_Dtls><Email>ARAlShamary@albilad-capital.com</Email></Rcpnt_Dtls><Rcpnt_Dtls><Email>KM.AlMohainy@albilad-capital.com</Email></Rcpnt_Dtls><Rcpnt_Dtls><Email>MSalehAlruhaimi@albilad-capital.com</Email></Rcpnt_Dtls><Rcpnt_Dtls><Email>TA.AlLuwaimi@albilad-capital.com</Email></Rcpnt_Dtls><Rcpnt_Dtls><Email>AAlzayedi@albilad-capital.com</Email></Rcpnt_Dtls><Rcpnt_Dtls><Email>MohammedP@albilad-capital.com</Email></Rcpnt_Dtls><Rcpnt_Dtls><Email>SBinJadeed@albilad-capital.com</Email></Rcpnt_Dtls><Rcpnt_Dtls><Email>MU.Adlan@albilad-capital.com</Email></Rcpnt_Dtls><Rcpnt_Dtls><Email>MElshami@albilad-capital.com</Email></Rcpnt_Dtls><Rcpnt_Dtls><Email>SIAlGhamdi@albilad-capital.com</Email></Rcpnt_Dtls></Rcpnt_Lst><P1_En>$customerName</P1_En><P1_Ar>$customerName</P1_Ar><P2_En>$applicationId</P2_En><P2_Ar>$applicationId</P2_Ar><P3_En>$cifNumber</P3_En><P3_Ar>$cifNumber</P3_Ar><P4_En>$prefLanguage</P4_En><P4_Ar>$prefLanguage</P4_Ar><P5_En>$lsfTypeTradingAccount</P5_En><P5_Ar>$lsfTypeTradingAccount</P5_Ar><P6_En>$tradingAccount</P6_En><P6_Ar>$tradingAccount</P6_Ar></Body><EAIHeader><MsgFrmt>Snd_Instnt_Ntfctn_Req</MsgFrmt><CIF>$cifNumber</CIF><Lang>E</Lang><ChnlId>MBSR</ChnlId><FunctnId>294001</FunctnId><FE_Ref_No>1442734065267</FE_Ref_No></EAIHeader></EAIMessage>','OMSNTFY053',NULL,NULL);

INSERT INTO n03_notification_msg_config 
VALUES(5019,NULL,NULL,NULL,'Authorization to Sell',NULL,NULL,'Authorization to Sell',NULL,NULL,NULL,'AATSU',NULL,'<EAIMessage><Body><Cust_Prfl>N</Cust_Prfl><Ntfcn_Mthd>2</Ntfcn_Mthd><Org_Id>3</Org_Id><Sndr_Id>BILADINVEST</Sndr_Id><Ovrrd_SMS_Tmplt>OMSNTFY050</Ovrrd_SMS_Tmplt><Ovrrd_Prfrrd_Lang>E</Ovrrd_Prfrrd_Lang><Rcpnt_Lst><Rcpnt_Dtls><Email>BAlOtaibi@albilad-capital.com</Email></Rcpnt_Dtls><Rcpnt_Dtls><Email>MHedayan@albilad-capital.com</Email></Rcpnt_Dtls><Rcpnt_Dtls><Email>ARAlShamary@albilad-capital.com</Email></Rcpnt_Dtls><Rcpnt_Dtls><Email>KM.AlMohainy@albilad-capital.com</Email></Rcpnt_Dtls><Rcpnt_Dtls><Email>MSalehAlruhaimi@albilad-capital.com</Email></Rcpnt_Dtls><Rcpnt_Dtls><Email>TA.AlLuwaimi@albilad-capital.com</Email></Rcpnt_Dtls><Rcpnt_Dtls><Email>AAlzayedi@albilad-capital.com</Email></Rcpnt_Dtls><Rcpnt_Dtls><Email>MohammedP@albilad-capital.com</Email></Rcpnt_Dtls><Rcpnt_Dtls><Email>SBinJadeed@albilad-capital.com</Email></Rcpnt_Dtls><Rcpnt_Dtls><Email>MU.Adlan@albilad-capital.com</Email></Rcpnt_Dtls><Rcpnt_Dtls><Email>MElshami@albilad-capital.com</Email></Rcpnt_Dtls><Rcpnt_Dtls><Email>SIAlGhamdi@albilad-capital.com</Email></Rcpnt_Dtls></Rcpnt_Lst><P1_En>$customerName</P1_En><P1_Ar>$customerName</P1_Ar><P2_En>$applicationId</P2_En><P2_Ar>$applicationId</P2_Ar><P3_En>$cifNumber</P3_En><P3_Ar>$cifNumber</P3_Ar><P4_En>$prefLanguage</P4_En><P4_Ar>$prefLanguage</P4_Ar><P5_En>$lsfTypeTradingAccount</P5_En><P5_Ar>$lsfTypeTradingAccount</P5_Ar><P6_En>$tradingAccount</P6_En><P6_Ar>$tradingAccount</P6_Ar></Body><EAIHeader><MsgFrmt>Snd_Instnt_Ntfctn_Req</MsgFrmt><CIF>$cifNumber</CIF><Lang>E</Lang><ChnlId>MBSR</ChnlId><FunctnId>294001</FunctnId><FE_Ref_No>1442734065267</FE_Ref_No></EAIHeader></EAIMessage>','OMSNTFY053',NULL,NULL);

INSERT INTO n03_notification_msg_config 
VALUES(5020,NULL,NULL,NULL,'Authorization to Sell',NULL,NULL,'Authorization to Sell',NULL,NULL,NULL,'AATSAR',NULL,'<EAIMessage><Body><Cust_Prfl>N</Cust_Prfl><Ntfcn_Mthd>2</Ntfcn_Mthd><Org_Id>3</Org_Id><Sndr_Id>BILADINVEST</Sndr_Id><Ovrrd_SMS_Tmplt>OMSNTFY050</Ovrrd_SMS_Tmplt><Ovrrd_Prfrrd_Lang>E</Ovrrd_Prfrrd_Lang><Rcpnt_Lst><Rcpnt_Dtls><Email>BAlOtaibi@albilad-capital.com</Email></Rcpnt_Dtls><Rcpnt_Dtls><Email>MHedayan@albilad-capital.com</Email></Rcpnt_Dtls><Rcpnt_Dtls><Email>ARAlShamary@albilad-capital.com</Email></Rcpnt_Dtls><Rcpnt_Dtls><Email>KM.AlMohainy@albilad-capital.com</Email></Rcpnt_Dtls><Rcpnt_Dtls><Email>MSalehAlruhaimi@albilad-capital.com</Email></Rcpnt_Dtls><Rcpnt_Dtls><Email>TA.AlLuwaimi@albilad-capital.com</Email></Rcpnt_Dtls><Rcpnt_Dtls><Email>AAlzayedi@albilad-capital.com</Email></Rcpnt_Dtls><Rcpnt_Dtls><Email>MohammedP@albilad-capital.com</Email></Rcpnt_Dtls><Rcpnt_Dtls><Email>SBinJadeed@albilad-capital.com</Email></Rcpnt_Dtls><Rcpnt_Dtls><Email>MU.Adlan@albilad-capital.com</Email></Rcpnt_Dtls><Rcpnt_Dtls><Email>MElshami@albilad-capital.com</Email></Rcpnt_Dtls><Rcpnt_Dtls><Email>SIAlGhamdi@albilad-capital.com</Email></Rcpnt_Dtls></Rcpnt_Lst><P1_En>$customerName</P1_En><P1_Ar>$customerName</P1_Ar><P2_En>$applicationId</P2_En><P2_Ar>$applicationId</P2_Ar><P3_En>$cifNumber</P3_En><P3_Ar>$cifNumber</P3_Ar><P4_En>$prefLanguage</P4_En><P4_Ar>$prefLanguage</P4_Ar><P5_En>$lsfTypeTradingAccount</P5_En><P5_Ar>$lsfTypeTradingAccount</P5_Ar><P6_En>$tradingAccount</P6_En><P6_Ar>$tradingAccount</P6_Ar></Body><EAIHeader><MsgFrmt>Snd_Instnt_Ntfctn_Req</MsgFrmt><CIF>$cifNumber</CIF><Lang>E</Lang><ChnlId>MBSR</ChnlId><FunctnId>294001</FunctnId><FE_Ref_No>1442734065267</FE_Ref_No></EAIHeader></EAIMessage>','OMSNTFY053',NULL,NULL);

INSERT INTO n03_notification_msg_config 
VALUES(5021,NULL,NULL,NULL,'Authorization to Sell',NULL,NULL,'Authorization to Sell',NULL,NULL,NULL,'AATSUR',NULL,'<EAIMessage><Body><Cust_Prfl>N</Cust_Prfl><Ntfcn_Mthd>2</Ntfcn_Mthd><Org_Id>3</Org_Id><Sndr_Id>BILADINVEST</Sndr_Id><Ovrrd_SMS_Tmplt>OMSNTFY050</Ovrrd_SMS_Tmplt><Ovrrd_Prfrrd_Lang>E</Ovrrd_Prfrrd_Lang><Rcpnt_Lst><Rcpnt_Dtls><Email>BAlOtaibi@albilad-capital.com</Email></Rcpnt_Dtls><Rcpnt_Dtls><Email>MHedayan@albilad-capital.com</Email></Rcpnt_Dtls><Rcpnt_Dtls><Email>ARAlShamary@albilad-capital.com</Email></Rcpnt_Dtls><Rcpnt_Dtls><Email>KM.AlMohainy@albilad-capital.com</Email></Rcpnt_Dtls><Rcpnt_Dtls><Email>MSalehAlruhaimi@albilad-capital.com</Email></Rcpnt_Dtls><Rcpnt_Dtls><Email>TA.AlLuwaimi@albilad-capital.com</Email></Rcpnt_Dtls><Rcpnt_Dtls><Email>AAlzayedi@albilad-capital.com</Email></Rcpnt_Dtls><Rcpnt_Dtls><Email>MohammedP@albilad-capital.com</Email></Rcpnt_Dtls><Rcpnt_Dtls><Email>SBinJadeed@albilad-capital.com</Email></Rcpnt_Dtls><Rcpnt_Dtls><Email>MU.Adlan@albilad-capital.com</Email></Rcpnt_Dtls><Rcpnt_Dtls><Email>MElshami@albilad-capital.com</Email></Rcpnt_Dtls><Rcpnt_Dtls><Email>SIAlGhamdi@albilad-capital.com</Email></Rcpnt_Dtls></Rcpnt_Lst><P1_En>$customerName</P1_En><P1_Ar>$customerName</P1_Ar><P2_En>$applicationId</P2_En><P2_Ar>$applicationId</P2_Ar><P3_En>$cifNumber</P3_En><P3_Ar>$cifNumber</P3_Ar><P4_En>$prefLanguage</P4_En><P4_Ar>$prefLanguage</P4_Ar><P5_En>$lsfTypeTradingAccount</P5_En><P5_Ar>$lsfTypeTradingAccount</P5_Ar><P6_En>$tradingAccount</P6_En><P6_Ar>$tradingAccount</P6_Ar></Body><EAIHeader><MsgFrmt>Snd_Instnt_Ntfctn_Req</MsgFrmt><CIF>$cifNumber</CIF><Lang>E</Lang><ChnlId>MBSR</ChnlId><FunctnId>294001</FunctnId><FE_Ref_No>1442734065267</FE_Ref_No></EAIHeader></EAIMessage>','OMSNTFY053',NULL,NULL);
/

INSERT INTO MUBASHER_LSF.M04_REPORTS (M04_REPORT_NAME, M04_PACKAGE_NAME, M04_TEMPLATE_PATH, M04_REPORT_DESTINATION, M04_CLASS_NAME, M04_REPORT_ID, M04_REPORT_DESCRIPTION, M04_PARAM_PROC, M04_PARAM_PARAMS, M04_DATA_PROC, M04_DATA_PARAMS)
VALUES('SIMAH_REPORT', 'm04_reports_pkg', '..\\reports\SIMAH_REPORT.jasper', 'C:\\Users\\Public\\Documents', NULL, 11115, 'Reporing of Margin Information - SIMAH', 'm04_margin_info_fields', NULL, 'simah_report', 'fromdate,todate');
/


-- MERGE INTO MUBASHER_LSF.users USING DUAL ON (id=2) WHEN NOT MATCHED THEN
-- INSERT (ID,USER_NAME,PASSWORD,FIRST_NAME,LAST_NAME,TELEPHONE,MOBILE,NIN,EMAIL,EMPLOY_NO,ORGANIZATION,ALLOWED_GROUPS,USERSTATUS,FULL_NAME,USER_GROUPS,REMOVED_STATS,LAST_REQUEST_TIME,SESSIONID,COOKIES,FAIL_ATTAMEPT_COUNT,CREATED_DATE,MODIFIED_DATE,IS_DELETED)
-- VALUES (2,'abicml','1388031742$975f427f7a04bcf19bde040498491cec','abic','abic','12345678','12345678','5678','abicml@abic.com','5000','abic',TO_CLOB('["adminGroup"]'),1,'abic abic',TO_CLOB('["lsf","admin"]'),EMPTY_CLOB(),'','otGmbpX3*rg#!%#$5$830VwI0Sf4FD*d1757564753454',NULL,0,'','',0);
-- /


MERGE INTO MUBASHER_LSF.organization_hierarchy USING DUAL ON (id='lsf') WHEN NOT MATCHED THEN
INSERT (ID,APPID,"desc",CHILDREN,ANCESTORS,IMMEDIATEPARENTS,USERS)
VALUES ('lsf','lsf','Application user',EMPTY_CLOB(),EMPTY_CLOB(),EMPTY_CLOB(),EMPTY_CLOB());
/

MERGE INTO MUBASHER_LSF.state_machine USING DUAL ON (id=1) WHEN NOT MATCHED THEN
INSERT (id, "initial", final, states) VALUES ('1',0,4,EMPTY_CLOB());
/

MERGE INTO mubasher_lsf.m04_reports
 USING DUAL
    ON (m04_report_name = 'LIQUIDATION_CALL')
WHEN MATCHED
THEN
    UPDATE SET m04_data_params = 'fromdate,todate';
/

MERGE INTO mubasher_lsf.m04_reports
 USING DUAL
    ON (m04_report_name = 'MARGIN_CALL')
WHEN MATCHED
THEN
    UPDATE SET m04_data_params = 'fromdate,todate';
/

-- N03_NOTIFICATION_MSG_CONFIG
INSERT INTO n03_notification_msg_config (N03_ID,N03_CURRENT_LEVEL,N03_OVEROLE_STATUS,N03_IS_WEB,N03_SUBJECT,N03_TEXT,N03_SMS_TEMPLATE,N03_EMAIL_SUBJECT,N03_EMAIL_BODY,N03_IS_SMS,N03_IS_MAIL,N03_NOTIFICATION_CODE,N03_TP_SMS_TEMPLATE,N03_TP_EMAIL_TEMPLATE,N03_TP_REFERENCE,N03_TP_EMAIL_ADMIN,N03_TP_EMAIL_USER)
VALUES(5022,NULL,NULL,NULL,'Authorization to Sell',NULL,NULL,'Authorization to Sell',NULL,NULL,NULL,'AATSF','<EAIMeassage><Body><Cust_Prfl></Cust_Prfl><Ntfcn_Mthd>1</Ntfcn_Mthd><Org_Id>3</Org_Id><Sndr_Id>BILADINVEST</Sndr_Id><Ovrrd_SMS_Tmplt>OMSNTFY078</Ovrrd_SMS_Tmplt><Ovrrd_Prfrrd_Lang>$prefLanguage</Ovrrd_Prfrrd_Lang><Rcpnt_Lst><Rcpnt_Dtls><Mbl_No>$mobileNumber</Mbl_No></Rcpnt_Dtls></Rcpnt_Lst><P1_En>"Dear Valued Client, The collaterals and the commodity selling proceeds  have been transferred to your Portfolio. No: $lsfTypeTradingAccount"</P1_En><P1_Ar>"عميلنا العزيز،
تم تحويل جميع الضمانات وقيمة السلعة المباعة لمحفظة التمويل بالهامش رقم : $lsfTypeTradingAccount"</P1_Ar></Body><EAIHeader><MsgFrmt>Snd_Instnt_Ntfctn_Req</MsgFrmt><CIF>$cifNumber</CIF><Lang>$prefLanguage</Lang><ChnlId>MBSR</ChnlId><FunctnId>294001</FunctnId><FE_Ref_No></FE_Ref_No></EAIHeader></EAIMeassage>','<EAIMessage><Body><Cust_Prfl>N</Cust_Prfl><Ntfcn_Mthd>2</Ntfcn_Mthd><Org_Id>3</Org_Id><Sndr_Id>BILADINVEST</Sndr_Id><Ovrrd_SMS_Tmplt>OMSNTFY050</Ovrrd_SMS_Tmplt><Ovrrd_Prfrrd_Lang>E</Ovrrd_Prfrrd_Lang><Rcpnt_Lst><Rcpnt_Dtls><Email>BAlOtaibi@albilad-capital.com</Email></Rcpnt_Dtls><Rcpnt_Dtls><Email>MHedayan@albilad-capital.com</Email></Rcpnt_Dtls><Rcpnt_Dtls><Email>ARAlShamary@albilad-capital.com</Email></Rcpnt_Dtls><Rcpnt_Dtls><Email>KM.AlMohainy@albilad-capital.com</Email></Rcpnt_Dtls><Rcpnt_Dtls><Email>MSalehAlruhaimi@albilad-capital.com</Email></Rcpnt_Dtls><Rcpnt_Dtls><Email>TA.AlLuwaimi@albilad-capital.com</Email></Rcpnt_Dtls><Rcpnt_Dtls><Email>AAlzayedi@albilad-capital.com</Email></Rcpnt_Dtls><Rcpnt_Dtls><Email>MohammedP@albilad-capital.com</Email></Rcpnt_Dtls><Rcpnt_Dtls><Email>SBinJadeed@albilad-capital.com</Email></Rcpnt_Dtls><Rcpnt_Dtls><Email>MU.Adlan@albilad-capital.com</Email></Rcpnt_Dtls><Rcpnt_Dtls><Email>MElshami@albilad-capital.com</Email></Rcpnt_Dtls><Rcpnt_Dtls><Email>SIAlGhamdi@albilad-capital.com</Email></Rcpnt_Dtls></Rcpnt_Lst><P1_En>$customerName</P1_En><P1_Ar>$customerName</P1_Ar><P2_En>$applicationId</P2_En><P2_Ar>$applicationId</P2_Ar><P3_En>$cifNumber</P3_En><P3_Ar>$cifNumber</P3_Ar><P4_En>$prefLanguage</P4_En><P4_Ar>$prefLanguage</P4_Ar><P5_En>$lsfTypeTradingAccount</P5_En><P5_Ar>$lsfTypeTradingAccount</P5_Ar><P6_En>$tradingAccount</P6_En><P6_Ar>$tradingAccount</P6_Ar></Body><EAIHeader><MsgFrmt>Snd_Instnt_Ntfctn_Req</MsgFrmt><CIF>$cifNumber</CIF><Lang>E</Lang><ChnlId>MBSR</ChnlId><FunctnId>294001</FunctnId><FE_Ref_No>1442734065267</FE_Ref_No></EAIHeader></EAIMessage>','OMSNTFY053',NULL,NULL);
/

UPDATE MUBASHER_LSF.N03_NOTIFICATION_MSG_CONFIG
SET N03_TP_SMS_TEMPLATE='<EAIMeassage><Body><Cust_Prfl></Cust_Prfl><Ntfcn_Mthd>1</Ntfcn_Mthd><Org_Id>3</Org_Id><Sndr_Id>BILADINVEST</Sndr_Id><Ovrrd_SMS_Tmplt>OMSNTFY078</Ovrrd_SMS_Tmplt><Ovrrd_Prfrrd_Lang>$prefLanguage</Ovrrd_Prfrrd_Lang><Rcpnt_Lst><Rcpnt_Dtls><Mbl_No>$mobileNumber</Mbl_No></Rcpnt_Dtls></Rcpnt_Lst><P1_En>"Dear Valued Client, We would like to inform you that your Margin Lending request has been accepted, Please select your collaterals and commodities online through your Investment Account"</P1_En><P1_Ar>"عميلنا العزيز،
نود إبلاغكم أنه تمت الموافقة النهائية على طلب التمويل بالهامش الخاص بكم، الرجاء القيام بتحديد الضمانات واختيار السلعة المطلوبة للشراء الكترونيا من خلال حسابكم الاستثماري."</P1_Ar></Body><EAIHeader><MsgFrmt>Snd_Instnt_Ntfctn_Req</MsgFrmt><CIF>$cifNumber</CIF><Lang>$prefLanguage</Lang><ChnlId>MBSR</ChnlId><FunctnId>294001</FunctnId><FE_Ref_No></FE_Ref_No></EAIHeader></EAIMeassage>'
WHERE N03_NOTIFICATION_CODE='AATSA';

UPDATE MUBASHER_LSF.N03_NOTIFICATION_MSG_CONFIG
SET N03_TP_SMS_TEMPLATE='<EAIMeassage><Body><Cust_Prfl></Cust_Prfl><Ntfcn_Mthd>1</Ntfcn_Mthd><Org_Id>3</Org_Id><Sndr_Id>BILADINVEST</Sndr_Id><Ovrrd_SMS_Tmplt>OMSNTFY078</Ovrrd_SMS_Tmplt><Ovrrd_Prfrrd_Lang>$prefLanguage</Ovrrd_Prfrrd_Lang><Rcpnt_Lst><Rcpnt_Dtls><Mbl_No>$mobileNumber</Mbl_No></Rcpnt_Dtls></Rcpnt_Lst><P1_En>"Dear Valued Client, Please sign the contract and authorize Albilad Capital to sell the commodity $contractsnnumber online in order to transfer the collaterals and the commodity selling proceeds to your Contract $applicationId Portfolio , This request will be terminated in 30 minutes"</P1_En><P1_Ar>"عميلنا العزيز،
 لرجاء توقيع عقد التمويل بالهامش وتفويض البلاد المالية ببيع السلعة $contractsnnumber عن طريق حسابكم الكترونيا بشكل عاجل، ليتم تحويل ثمن السلع المباعة والضمانات لمحفظة العقد $applicationId، علماً بأن الطلب سيتم إلغاؤه بعد 30 دقيقة."</P1_Ar></Body><EAIHeader><MsgFrmt>Snd_Instnt_Ntfctn_Req</MsgFrmt><CIF>$cifNumber</CIF><Lang>$prefLanguage</Lang><ChnlId>MBSR</ChnlId><FunctnId>294001</FunctnId><FE_Ref_No></FE_Ref_No></EAIHeader></EAIMeassage>'
WHERE N03_NOTIFICATION_CODE='AATSU';


UPDATE MUBASHER_LSF.N03_NOTIFICATION_MSG_CONFIG
SET N03_TP_SMS_TEMPLATE='<EAIMeassage><Body><Cust_Prfl></Cust_Prfl><Ntfcn_Mthd>1</Ntfcn_Mthd><Org_Id>3</Org_Id><Sndr_Id>BILADINVEST</Sndr_Id><Ovrrd_SMS_Tmplt>OMSNTFY078</Ovrrd_SMS_Tmplt><Ovrrd_Prfrrd_Lang>$prefLanguage</Ovrrd_Prfrrd_Lang><Rcpnt_Lst><Rcpnt_Dtls><Mbl_No>$mobileNumber</Mbl_No></Rcpnt_Dtls></Rcpnt_Lst><P1_En>"Dear Valued Client,We would like to inform you that Albilad Capital has been authorized to sell the underlying commodity for margin contract No $applicationId"</P1_En><P1_Ar>"عميلنا العزيز،
نفيدكم بأنه تم تفويض البلاد المالية لبيع السلعة لعقد التمويل بالهامش رقم $applicationId."</P1_Ar></Body><EAIHeader><MsgFrmt>Snd_Instnt_Ntfctn_Req</MsgFrmt><CIF>$cifNumber</CIF><Lang>$prefLanguage</Lang><ChnlId>MBSR</ChnlId><FunctnId>294001</FunctnId><FE_Ref_No></FE_Ref_No></EAIHeader></EAIMeassage>'
WHERE N03_NOTIFICATION_CODE='AATSAR';

UPDATE MUBASHER_LSF.N03_NOTIFICATION_MSG_CONFIG
SET N03_TP_SMS_TEMPLATE='<EAIMeassage><Body><Cust_Prfl></Cust_Prfl><Ntfcn_Mthd>1</Ntfcn_Mthd><Org_Id>3</Org_Id><Sndr_Id>BILADINVEST</Sndr_Id><Ovrrd_SMS_Tmplt>OMSNTFY078</Ovrrd_SMS_Tmplt><Ovrrd_Prfrrd_Lang>$prefLanguage</Ovrrd_Prfrrd_Lang><Rcpnt_Lst><Rcpnt_Dtls><Mbl_No>$mobileNumber</Mbl_No></Rcpnt_Dtls></Rcpnt_Lst><P1_En>"Dear Valued Client, The collaterals and the commodity selling proceeds  have been transferred to your Portfolio. Portfolio No: $lsfTypeTradingAccount"</P1_En><P1_Ar>"عميلنا العزيز،
تم تحويل جميع الضمانات وقيمة السلعة المباعة لمحفظة التمويل بالهامش رقم : $lsfTypeTradingAccount"</P1_Ar></Body><EAIHeader><MsgFrmt>Snd_Instnt_Ntfctn_Req</MsgFrmt><CIF>$cifNumber</CIF><Lang>$prefLanguage</Lang><ChnlId>MBSR</ChnlId><FunctnId>294001</FunctnId><FE_Ref_No></FE_Ref_No></EAIHeader></EAIMeassage>'
WHERE N03_NOTIFICATION_CODE='AATSF';

UPDATE MUBASHER_LSF.N03_NOTIFICATION_MSG_CONFIG
SET N03_TP_SMS_TEMPLATE='<EAIMeassage><Body><Cust_Prfl></Cust_Prfl><Ntfcn_Mthd>1</Ntfcn_Mthd><Org_Id>3</Org_Id><Sndr_Id>BILADINVEST</Sndr_Id><Ovrrd_SMS_Tmplt>OMSNTFY078</Ovrrd_SMS_Tmplt><Ovrrd_Prfrrd_Lang>$prefLanguage</Ovrrd_Prfrrd_Lang><Rcpnt_Lst><Rcpnt_Dtls><Mbl_No>$mobileNumber</Mbl_No></Rcpnt_Dtls></Rcpnt_Lst><P1_En>"Dear Valued Client, We would like to inform you that your Margin Lending request has been accepted, Please select your collaterals and commodities online through your Investment Account"</P1_En><P1_Ar>"عميلنا العزيز، نود إبلاغكم أنه تمت الموافقة النهائية على طلب التمويل بالهامش الخاص بكم، الرجاء القيام بتحديد الضمانات واختيار السلعة المطلوبة للشراء الكترونيا من خلال حسابكم الاستثماري."</P1_Ar></Body><EAIHeader><MsgFrmt>Snd_Instnt_Ntfctn_Req</MsgFrmt><CIF>$cifNumber</CIF><Lang>$prefLanguage</Lang><ChnlId>MBSR</ChnlId><FunctnId>294001</FunctnId><FE_Ref_No></FE_Ref_No></EAIHeader></EAIMeassage>'
WHERE N03_CURRENT_LEVEL='5';

INSERT INTO N03_NOTIFICATION_MSG_CONFIG(N03_ID,N03_SUBJECT,N03_IS_SMS,N03_NOTIFICATION_CODE,N03_TP_SMS_TEMPLATE)
values(20251226,'Margin Call Level 2',1,'MN_2','<EAIMeassage><Body><Cust_Prfl></Cust_Prfl><Ntfcn_Mthd>1</Ntfcn_Mthd><Org_Id>3</Org_Id><Sndr_Id>BILADINVEST</Sndr_Id><Ovrrd_SMS_Tmplt>OMSNTFY078</Ovrrd_SMS_Tmplt><Ovrrd_Prfrrd_Lang>$prefLanguage</Ovrrd_Prfrrd_Lang><Rcpnt_Lst><Rcpnt_Dtls><Mbl_No>$mobileNumber</Mbl_No></Rcpnt_Dtls></Rcpnt_Lst><P1_En>"Your coverage ratio drops lower than $secondMarginLevel%. Kindly maintain the required coverage ratio as per the agreement."</P1_En><P1_Ar>"نسبة التغطية الخاصة بك انخفضت لأقل من $secondMarginLevel%، يرجى الحفاظ على نسبة التغطية المطلوبة وفقاً للاتفاقية"</P1_Ar></Body><EAIHeader><MsgFrmt>Snd_Instnt_Ntfctn_Req</MsgFrmt><CIF>$cifNumber</CIF><Lang>$prefLanguage</Lang><ChnlId>MBSR</ChnlId><FunctnId>294001</FunctnId><FE_Ref_No></FE_Ref_No></EAIHeader></EAIMeassage>');


UPDATE MUBASHER_LSF.N03_NOTIFICATION_MSG_CONFIG
SET N03_TP_EMAIL_TEMPLATE='<EAIMeassage><Body><Cust_Prfl></Cust_Prfl><Ntfcn_Mthd>2</Ntfcn_Mthd><Org_Id>3</Org_Id><Sndr_Id>BILADINVEST</Sndr_Id><Ovrrd_SMS_Tmplt>OMSNTFY078</Ovrrd_SMS_Tmplt><Ovrrd_Prfrrd_Lang>$prefLanguage</Ovrrd_Prfrrd_Lang><Rcpnt_Lst><Rcpnt_Dtls><Email>c940282@babtest.com</Email></Rcpnt_Dtls><Rcpnt_Dtls><Email>BAlOtaibi@albilad-capital.com</Email></Rcpnt_Dtls><Rcpnt_Dtls><Email>MHedayan@albilad-capital.com</Email></Rcpnt_Dtls><Rcpnt_Dtls><Email>ARAlShamary@albilad-capital.com</Email></Rcpnt_Dtls><Rcpnt_Dtls><Email>KM.AlMohainy@albilad-capital.com</Email></Rcpnt_Dtls><Rcpnt_Dtls><Email>MSalehAlruhaimi@albilad-capital.com</Email></Rcpnt_Dtls><Rcpnt_Dtls><Email>TA.AlLuwaimi@albilad-capital.com</Email></Rcpnt_Dtls><Rcpnt_Dtls><Email>AAlzayedi@albilad-capital.com</Email></Rcpnt_Dtls><Rcpnt_Dtls><Email>MohammedP@albilad-capital.com</Email></Rcpnt_Dtls><Rcpnt_Dtls><Email>SBinJadeed@albilad-capital.com</Email></Rcpnt_Dtls><Rcpnt_Dtls><Email>MU.Adlan@albilad-capital.com</Email></Rcpnt_Dtls><Rcpnt_Dtls><Email>MElshami@albilad-capital.com</Email></Rcpnt_Dtls><Rcpnt_Dtls><Email>SIAlGhamdi@albilad-capital.com</Email></Rcpnt_Dtls></Rcpnt_Lst><P1_En>"Dear Valued Client, We would like to inform you that your Margin Lending request has been accepted, Please select your collaterals and commodities online through your Investment Account"</P1_En><P1_Ar>"عميلنا العزيز،
نود إبلاغكم أنه تمت الموافقة النهائية على طلب التمويل بالهامش الخاص بكم، الرجاء القيام بتحديد الضمانات واختيار السلعة المطلوبة للشراء الكترونيا من خلال حسابكم الاستثماري."</P1_Ar></Body><EAIHeader><MsgFrmt>Snd_Instnt_Ntfctn_Req</MsgFrmt><CIF>$cifNumber</CIF><Lang>$prefLanguage</Lang><ChnlId>MBSR</ChnlId><FunctnId>294001</FunctnId><FE_Ref_No></FE_Ref_No></EAIHeader></EAIMeassage>'
WHERE N03_NOTIFICATION_CODE='AATSA';

UPDATE MUBASHER_LSF.N03_NOTIFICATION_MSG_CONFIG
SET N03_TP_EMAIL_TEMPLATE='<EAIMeassage><Body><Cust_Prfl></Cust_Prfl><Ntfcn_Mthd>2</Ntfcn_Mthd><Org_Id>3</Org_Id><Sndr_Id>BILADINVEST</Sndr_Id><Ovrrd_SMS_Tmplt>OMSNTFY078</Ovrrd_SMS_Tmplt><Ovrrd_Prfrrd_Lang>$prefLanguage</Ovrrd_Prfrrd_Lang><Rcpnt_Lst><Rcpnt_Dtls><Email>c940282@babtest.com</Email></Rcpnt_Dtls><Rcpnt_Dtls><Email>BAlOtaibi@albilad-capital.com</Email></Rcpnt_Dtls><Rcpnt_Dtls><Email>MHedayan@albilad-capital.com</Email></Rcpnt_Dtls><Rcpnt_Dtls><Email>ARAlShamary@albilad-capital.com</Email></Rcpnt_Dtls><Rcpnt_Dtls><Email>KM.AlMohainy@albilad-capital.com</Email></Rcpnt_Dtls><Rcpnt_Dtls><Email>MSalehAlruhaimi@albilad-capital.com</Email></Rcpnt_Dtls><Rcpnt_Dtls><Email>TA.AlLuwaimi@albilad-capital.com</Email></Rcpnt_Dtls><Rcpnt_Dtls><Email>AAlzayedi@albilad-capital.com</Email></Rcpnt_Dtls><Rcpnt_Dtls><Email>MohammedP@albilad-capital.com</Email></Rcpnt_Dtls><Rcpnt_Dtls><Email>SBinJadeed@albilad-capital.com</Email></Rcpnt_Dtls><Rcpnt_Dtls><Email>MU.Adlan@albilad-capital.com</Email></Rcpnt_Dtls><Rcpnt_Dtls><Email>MElshami@albilad-capital.com</Email></Rcpnt_Dtls><Rcpnt_Dtls><Email>SIAlGhamdi@albilad-capital.com</Email></Rcpnt_Dtls></Rcpnt_Lst><P1_En>"Dear Valued Client, Please sign the contract and authorize Albilad Capital to sell the commodity $contractsnnumber online in order to transfer the collaterals and the commodity selling proceeds to your Contract $applicationId Portfolio , This request will be terminated in 30 minutes"</P1_En><P1_Ar>"عميلنا العزيز،
 الرجاء توقيع عقد التمويل بالهامش وتفويض البلاد المالية ببيع السلعة $contractsnnumber عن طريق حسابكم الكترونيا بشكل عاجل، ليتم تحويل ثمن السلع المباعة والضمانات لمحفظة العقد $applicationId، علماً بأن الطلب سيتم إلغاؤه بعد 30 دقيقة."</P1_Ar></Body><EAIHeader><MsgFrmt>Snd_Instnt_Ntfctn_Req</MsgFrmt><CIF>$cifNumber</CIF><Lang>$prefLanguage</Lang><ChnlId>MBSR</ChnlId><FunctnId>294001</FunctnId><FE_Ref_No></FE_Ref_No></EAIHeader></EAIMeassage>'
WHERE N03_NOTIFICATION_CODE='AATSU';


UPDATE MUBASHER_LSF.N03_NOTIFICATION_MSG_CONFIG
SET N03_TP_EMAIL_TEMPLATE='<EAIMeassage><Body><Cust_Prfl></Cust_Prfl><Ntfcn_Mthd>2</Ntfcn_Mthd><Org_Id>3</Org_Id><Sndr_Id>BILADINVEST</Sndr_Id><Ovrrd_SMS_Tmplt>OMSNTFY078</Ovrrd_SMS_Tmplt><Ovrrd_Prfrrd_Lang>$prefLanguage</Ovrrd_Prfrrd_Lang><Rcpnt_Lst><Rcpnt_Dtls><Email>c940282@babtest.com</Email></Rcpnt_Dtls><Rcpnt_Dtls><Email>BAlOtaibi@albilad-capital.com</Email></Rcpnt_Dtls><Rcpnt_Dtls><Email>MHedayan@albilad-capital.com</Email></Rcpnt_Dtls><Rcpnt_Dtls><Email>ARAlShamary@albilad-capital.com</Email></Rcpnt_Dtls><Rcpnt_Dtls><Email>KM.AlMohainy@albilad-capital.com</Email></Rcpnt_Dtls><Rcpnt_Dtls><Email>MSalehAlruhaimi@albilad-capital.com</Email></Rcpnt_Dtls><Rcpnt_Dtls><Email>TA.AlLuwaimi@albilad-capital.com</Email></Rcpnt_Dtls><Rcpnt_Dtls><Email>AAlzayedi@albilad-capital.com</Email></Rcpnt_Dtls><Rcpnt_Dtls><Email>MohammedP@albilad-capital.com</Email></Rcpnt_Dtls><Rcpnt_Dtls><Email>SBinJadeed@albilad-capital.com</Email></Rcpnt_Dtls><Rcpnt_Dtls><Email>MU.Adlan@albilad-capital.com</Email></Rcpnt_Dtls><Rcpnt_Dtls><Email>MElshami@albilad-capital.com</Email></Rcpnt_Dtls><Rcpnt_Dtls><Email>SIAlGhamdi@albilad-capital.com</Email></Rcpnt_Dtls></Rcpnt_Lst><P1_En>"Dear Valued Client,We would like to inform you that Albilad Capital has been authorized to sell the underlying commodity for margin contract No $applicationId"</P1_En><P1_Ar>"عميلنا العزيز،
نفيدكم بأنه تم تفويض البلاد المالية لبيع السلعة لعقد التمويل بالهامش رقم $applicationId."</P1_Ar></Body><EAIHeader><MsgFrmt>Snd_Instnt_Ntfctn_Req</MsgFrmt><CIF>$cifNumber</CIF><Lang>$prefLanguage</Lang><ChnlId>MBSR</ChnlId><FunctnId>294001</FunctnId><FE_Ref_No></FE_Ref_No></EAIHeader></EAIMeassage>'
WHERE N03_NOTIFICATION_CODE='AATSAR';

UPDATE MUBASHER_LSF.N03_NOTIFICATION_MSG_CONFIG
SET N03_TP_EMAIL_TEMPLATE='<EAIMeassage><Body><Cust_Prfl></Cust_Prfl><Ntfcn_Mthd>2</Ntfcn_Mthd><Org_Id>3</Org_Id><Sndr_Id>BILADINVEST</Sndr_Id><Ovrrd_SMS_Tmplt>OMSNTFY078</Ovrrd_SMS_Tmplt><Ovrrd_Prfrrd_Lang>$prefLanguage</Ovrrd_Prfrrd_Lang><Rcpnt_Lst><Rcpnt_Dtls><Email>c940282@babtest.com</Email></Rcpnt_Dtls><Rcpnt_Dtls><Email>BAlOtaibi@albilad-capital.com</Email></Rcpnt_Dtls><Rcpnt_Dtls><Email>MHedayan@albilad-capital.com</Email></Rcpnt_Dtls><Rcpnt_Dtls><Email>ARAlShamary@albilad-capital.com</Email></Rcpnt_Dtls><Rcpnt_Dtls><Email>KM.AlMohainy@albilad-capital.com</Email></Rcpnt_Dtls><Rcpnt_Dtls><Email>MSalehAlruhaimi@albilad-capital.com</Email></Rcpnt_Dtls><Rcpnt_Dtls><Email>TA.AlLuwaimi@albilad-capital.com</Email></Rcpnt_Dtls><Rcpnt_Dtls><Email>AAlzayedi@albilad-capital.com</Email></Rcpnt_Dtls><Rcpnt_Dtls><Email>MohammedP@albilad-capital.com</Email></Rcpnt_Dtls><Rcpnt_Dtls><Email>SBinJadeed@albilad-capital.com</Email></Rcpnt_Dtls><Rcpnt_Dtls><Email>MU.Adlan@albilad-capital.com</Email></Rcpnt_Dtls><Rcpnt_Dtls><Email>MElshami@albilad-capital.com</Email></Rcpnt_Dtls><Rcpnt_Dtls><Email>SIAlGhamdi@albilad-capital.com</Email></Rcpnt_Dtls></Rcpnt_Lst><P1_En>"Dear Valued Client, The collaterals and the commodity selling proceeds have been transferred to your Portfolio No: $lsfTypeTradingAccount"</P1_En><P1_Ar>"عميلنا العزيز،
تم تحويل جميع الضمانات وقيمة السلعة المباعة لمحفظة التمويل بالهامش رقم : $lsfTypeTradingAccount"</P1_Ar></Body><EAIHeader><MsgFrmt>Snd_Instnt_Ntfctn_Req</MsgFrmt><CIF>$cifNumber</CIF><Lang>$prefLanguage</Lang><ChnlId>MBSR</ChnlId><FunctnId>294001</FunctnId><FE_Ref_No></FE_Ref_No></EAIHeader></EAIMeassage>'
WHERE N03_NOTIFICATION_CODE='AATSF';

UPDATE MUBASHER_LSF.N03_NOTIFICATION_MSG_CONFIG
SET N03_TP_EMAIL_TEMPLATE='<EAIMeassage><Body><Cust_Prfl></Cust_Prfl><Ntfcn_Mthd>2</Ntfcn_Mthd><Org_Id>3</Org_Id><Sndr_Id>BILADINVEST</Sndr_Id><Ovrrd_SMS_Tmplt>OMSNTFY078</Ovrrd_SMS_Tmplt><Ovrrd_Prfrrd_Lang>$prefLanguage</Ovrrd_Prfrrd_Lang><Rcpnt_Lst><Rcpnt_Dtls><Email>c940282@babtest.com</Email></Rcpnt_Dtls><Rcpnt_Dtls><Email>BAlOtaibi@albilad-capital.com</Email></Rcpnt_Dtls><Rcpnt_Dtls><Email>MHedayan@albilad-capital.com</Email></Rcpnt_Dtls><Rcpnt_Dtls><Email>ARAlShamary@albilad-capital.com</Email></Rcpnt_Dtls><Rcpnt_Dtls><Email>KM.AlMohainy@albilad-capital.com</Email></Rcpnt_Dtls><Rcpnt_Dtls><Email>MSalehAlruhaimi@albilad-capital.com</Email></Rcpnt_Dtls><Rcpnt_Dtls><Email>TA.AlLuwaimi@albilad-capital.com</Email></Rcpnt_Dtls><Rcpnt_Dtls><Email>AAlzayedi@albilad-capital.com</Email></Rcpnt_Dtls><Rcpnt_Dtls><Email>MohammedP@albilad-capital.com</Email></Rcpnt_Dtls><Rcpnt_Dtls><Email>SBinJadeed@albilad-capital.com</Email></Rcpnt_Dtls><Rcpnt_Dtls><Email>MU.Adlan@albilad-capital.com</Email></Rcpnt_Dtls><Rcpnt_Dtls><Email>MElshami@albilad-capital.com</Email></Rcpnt_Dtls><Rcpnt_Dtls><Email>SIAlGhamdi@albilad-capital.com</Email></Rcpnt_Dtls></Rcpnt_Lst><P1_En>"Dear Valued Client, We would like to inform you that your Margin Lending request has been accepted, Please select your collaterals and commodities online through your Investment Account"</P1_En><P1_Ar>"عميلنا العزيز، نود إبلاغكم أنه تمت الموافقة النهائية على طلب التمويل بالهامش الخاص بكم، الرجاء القيام بتحديد الضمانات واختيار السلعة المطلوبة للشراء الكترونيا من خلال حسابكم الاستثماري."</P1_Ar></Body><EAIHeader><MsgFrmt>Snd_Instnt_Ntfctn_Req</MsgFrmt><CIF>$cifNumber</CIF><Lang>$prefLanguage</Lang><ChnlId>MBSR</ChnlId><FunctnId>294001</FunctnId><FE_Ref_No></FE_Ref_No></EAIHeader></EAIMeassage>'
WHERE N03_CURRENT_LEVEL='5';
/

/*
-- INSERT INTO N03_NOTIFICATION_MSG_CONFIG(N03_ID,N03_SUBJECT,N03_IS_SMS,N03_NOTIFICATION_CODE,N03_TP_SMS_TEMPLATE)
-- values(20251226,'Margin Call Level 2',1,'MN_2','<EAIMeassage><Body><Cust_Prfl></Cust_Prfl><Ntfcn_Mthd>1</Ntfcn_Mthd><Org_Id>3</Org_Id><Sndr_Id>BILADINVEST</Sndr_Id><Ovrrd_SMS_Tmplt>OMSNTFY078</Ovrrd_SMS_Tmplt><Ovrrd_Prfrrd_Lang>$prefLanguage</Ovrrd_Prfrrd_Lang><Rcpnt_Lst><Rcpnt_Dtls><Mbl_No>$mobileNumber</Mbl_No></Rcpnt_Dtls></Rcpnt_Lst><P1_En>"Your coverage ratio drops lower than $secondMarginLevel%. Kindly maintain the required coverage ratio as per the agreement."</P1_En><P1_Ar>"نسبة التغطية الخاصة بك انخفضت لأقل من $secondMarginLevel%، يرجى الحفاظ على نسبة التغطية المطلوبة وفقاً للاتفاقية"</P1_Ar></Body><EAIHeader><MsgFrmt>Snd_Instnt_Ntfctn_Req</MsgFrmt><CIF>$cifNumber</CIF><Lang>$prefLanguage</Lang><ChnlId>MBSR</ChnlId><FunctnId>294001</FunctnId><FE_Ref_No></FE_Ref_No></EAIHeader></EAIMeassage>');
*/


UPDATE MUBASHER_LSF.N03_NOTIFICATION_MSG_CONFIG
SET N03_NOTIFICATION_CODE='MN_APP'
WHERE N03_CURRENT_LEVEL=16 AND N03_OVEROLE_STATUS=15;
/


INSERT INTO MUBASHER_LSF.M02_APP_STATE_FLOW (M02_STATE, M02_STATE_DESCRIPTION, M02_PARENT_STATE, M02_REVERSE_STATE, M02_APPROVE_STATUS, M02_REJECT_STATUS, M02_LEVEL_TYPE, M02_APP_TYPE) VALUES(1, 'Pending', NULL, NULL, 1, -1, 0, 1);
INSERT INTO MUBASHER_LSF.M02_APP_STATE_FLOW (M02_STATE, M02_STATE_DESCRIPTION, M02_PARENT_STATE, M02_REVERSE_STATE, M02_APPROVE_STATUS, M02_REJECT_STATUS, M02_LEVEL_TYPE, M02_APP_TYPE) VALUES(2, 'RM Review', 1, NULL, 1, -1, 0, 1);
INSERT INTO MUBASHER_LSF.M02_APP_STATE_FLOW (M02_STATE, M02_STATE_DESCRIPTION, M02_PARENT_STATE, M02_REVERSE_STATE, M02_APPROVE_STATUS, M02_REJECT_STATUS, M02_LEVEL_TYPE, M02_APP_TYPE) VALUES(3, 'RM Approval LVL1', 2, NULL, 2, -2, 0, 1);
INSERT INTO MUBASHER_LSF.M02_APP_STATE_FLOW (M02_STATE, M02_STATE_DESCRIPTION, M02_PARENT_STATE, M02_REVERSE_STATE, M02_APPROVE_STATUS, M02_REJECT_STATUS, M02_LEVEL_TYPE, M02_APP_TYPE) VALUES(5, 'Brokerage Approval', 3, NULL, 3, -3, 0, 1);
INSERT INTO MUBASHER_LSF.M02_APP_STATE_FLOW (M02_STATE, M02_STATE_DESCRIPTION, M02_PARENT_STATE, M02_REVERSE_STATE, M02_APPROVE_STATUS, M02_REJECT_STATUS, M02_LEVEL_TYPE, M02_APP_TYPE) VALUES(6, 'Risk Review', 5, NULL, 5, -5, 0, 1);
INSERT INTO MUBASHER_LSF.M02_APP_STATE_FLOW (M02_STATE, M02_STATE_DESCRIPTION, M02_PARENT_STATE, M02_REVERSE_STATE, M02_APPROVE_STATUS, M02_REJECT_STATUS, M02_LEVEL_TYPE, M02_APP_TYPE) VALUES(12, 'Collateral Approval', 6, NULL, 11, -11, 0, 1);
INSERT INTO MUBASHER_LSF.M02_APP_STATE_FLOW (M02_STATE, M02_STATE_DESCRIPTION, M02_PARENT_STATE, M02_REVERSE_STATE, M02_APPROVE_STATUS, M02_REJECT_STATUS, M02_LEVEL_TYPE, M02_APP_TYPE) VALUES(13, 'Submit Purchase Order', 12, NULL, 12, -12, 0, 1);
INSERT INTO MUBASHER_LSF.M02_APP_STATE_FLOW (M02_STATE, M02_STATE_DESCRIPTION, M02_PARENT_STATE, M02_REVERSE_STATE, M02_APPROVE_STATUS, M02_REJECT_STATUS, M02_LEVEL_TYPE, M02_APP_TYPE) VALUES(14, 'Purchase Approval', 13, NULL, 13, -13, 0, 1);
INSERT INTO MUBASHER_LSF.M02_APP_STATE_FLOW (M02_STATE, M02_STATE_DESCRIPTION, M02_PARENT_STATE, M02_REVERSE_STATE, M02_APPROVE_STATUS, M02_REJECT_STATUS, M02_LEVEL_TYPE, M02_APP_TYPE) VALUES(15, 'Order Completed,Waiting for Final Customer Confirmation', 14, NULL, 14, -14, 0, 1);
INSERT INTO MUBASHER_LSF.M02_APP_STATE_FLOW (M02_STATE, M02_STATE_DESCRIPTION, M02_PARENT_STATE, M02_REVERSE_STATE, M02_APPROVE_STATUS, M02_REJECT_STATUS, M02_LEVEL_TYPE, M02_APP_TYPE) VALUES(16, 'Final Customer Confirmation', 15, NULL, 15, -15, 0, 1);
INSERT INTO MUBASHER_LSF.M02_APP_STATE_FLOW (M02_STATE, M02_STATE_DESCRIPTION, M02_PARENT_STATE, M02_REVERSE_STATE, M02_APPROVE_STATUS, M02_REJECT_STATUS, M02_LEVEL_TYPE, M02_APP_TYPE) VALUES(17, 'Liqudation started', 16, NULL, 16, -16, 1, 1);
INSERT INTO MUBASHER_LSF.M02_APP_STATE_FLOW (M02_STATE, M02_STATE_DESCRIPTION, M02_PARENT_STATE, M02_REVERSE_STATE, M02_APPROVE_STATUS, M02_REJECT_STATUS, M02_LEVEL_TYPE, M02_APP_TYPE) VALUES(18, 'Cash Transfer ', 17, NULL, 17, -17, 2, 1);
/
