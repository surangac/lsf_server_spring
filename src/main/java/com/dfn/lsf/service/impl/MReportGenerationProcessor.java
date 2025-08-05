package com.dfn.lsf.service.impl;

import com.dfn.lsf.model.*;
import com.dfn.lsf.repository.LSFRepository;
import com.dfn.lsf.service.MessageProcessor;
import com.dfn.lsf.service.reporting.ReportGenerator;
import com.dfn.lsf.util.*;
import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import net.sf.jasperreports.engine.JRException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.dfn.lsf.util.LsfConstants.MESSAGE_TYPE_REPORT_GENERATION_PROCESSS;

/**
 * Defined in InMessageHandlerAdminCbr, InMessageHandlerCbr
 * route : GENERATE_REPORTS
 * Handling Message types :
 * - MESSAGE_TYPE_REPORT_GENERATION_PROCESSS = 5;
 */
@Service
@MessageType(MESSAGE_TYPE_REPORT_GENERATION_PROCESSS)
@RequiredArgsConstructor
public class MReportGenerationProcessor implements MessageProcessor {

    private static final Logger logger = LoggerFactory.getLogger(MReportGenerationProcessor.class);

    private final Gson gson;
    private final LSFRepository lsfRepository;
    private final Helper helper;
    private final ReportGenerator reportGenerator;
    private final AuditLogProcessor auditLogProcessor;

    @Override
    public String process(String request) {
        auditLogProcessor.process(request);
        Map<String, Object> map = new HashMap<String, Object>();
        CommonResponse cmr = new CommonResponse();
        map = gson.fromJson(request, map.getClass());
        try {
            String rptCode = map.get("reportCode").toString();
            logger.info("===========LSF :Start to generate " + rptCode + " report");
            switch (rptCode) {
                case LsfConstants.FACILITY_AGREEMENT_LETTER:
                    if (generateFAL(map, rptCode)) {
                        cmr.setResponseCode(200);
                        cmr.setResponseMessage(rptCode + " Report generated Successfully!");
                    } else {
                        cmr.setResponseCode(500);
                        cmr.setErrorMessage("Report generation Failed");
                    }
                    break;
                case LsfConstants.INVESTMENT_OFFER_LETTER:
                    if (generateIOF(map, rptCode)) {
                        cmr.setResponseCode(200);
                        cmr.setResponseMessage(rptCode + " Report generated Successfully!");
                    } else {
                        cmr.setResponseCode(500);
                        cmr.setErrorMessage(rptCode + " Report generation Failed");
                    }
                    break;
                case LsfConstants.CREDIT_PROPOSAL:
                    if (generateCP(map, rptCode)) {
                        cmr.setResponseCode(200);
                        cmr.setResponseMessage(rptCode + " Report generated Successfully!");
                    } else {
                        cmr.setResponseCode(500);
                        cmr.setErrorMessage(rptCode + " Report generation Failed");
                    }
            }
        } catch (Exception ex) {
            cmr.setResponseCode(500);
            cmr.setErrorMessage(ErrorCodes.ERROR_EXCEPTION.errorDescription());
            logger.error("===========LSF :" + ex);
        }
        return gson.toJson(cmr);
    }

    private boolean generateCP(Map<String, Object> objMap, String reportCode) {
        boolean retValue = false;
        String id = objMap.get("id").toString();
        MurabahApplication application = getMurabahApplication(id);
        if (application == null) {
            logger.info("===========LSF :Mubahara Application null : " + id);
            return false;
        }
        MApplicationCollaterals collaterals = getMApplicationCollaterals(application.getId());
        GlobalParameters globalParameters = GlobalParameters.getInstance();

        // no data table
        Collection<Map<String, ?>> reportDataList = new ArrayList<>();
        Map<String, String> someHashMap = new HashMap<>();
        reportDataList.add(someHashMap);

        //parameters in the Report change according to the report
        Map<String, String> paramenterMap = new HashMap<>();
        paramenterMap.put(ReportConstants.CUSTOMER_NAME, application.getFullName());
        paramenterMap.put(ReportConstants.ACCOUNT_NO, application.getDibAcc());
        paramenterMap.put(
                ReportConstants.PROPOSAL_DATE,
                changeDateFormt("yyyy-MM-dd hh:MM:ss", "yyyy/MM/dd", application.getProposalDate()));
        paramenterMap.put(ReportConstants.PROPOSED_LIMIT, roundDoubleValue(application.getProposedLimit()));
        paramenterMap.put(ReportConstants.CUSTOMER_OCCUPATION, application.getOccupation());
        paramenterMap.put(ReportConstants.CUSTOMER_AGE, "26");
        paramenterMap.put(ReportConstants.MONTHLY_SALARY, roundDoubleValue(application.getAvgMonthlyIncome()));
        paramenterMap.put(ReportConstants.LENGTH_OF_SERVICE, "");
        paramenterMap.put(ReportConstants.COMPANY_PROFILE, "UAE");
        paramenterMap.put(ReportConstants.NATIONALITY, "UAE");
        paramenterMap.put(ReportConstants.PO_BOX, "66769 Dubai");
        paramenterMap.put(ReportConstants.MOBILE, application.getMobileNo());
        paramenterMap.put(ReportConstants.OFFICE, "N/A");
        paramenterMap.put(ReportConstants.FAX, "N/A");
        paramenterMap.put(ReportConstants.EMAIL, application.getEmail());
        paramenterMap.put(
                ReportConstants.DATE,
                changeDateFormt("yyyy/MM/dd hh:MM:ss", "yyyy/MM/dd", application.getDate()));
        paramenterMap.put(ReportConstants.NATURE_OF_BUSINESS, application.getLineOfBusiness());
        paramenterMap.put(ReportConstants.LENGTH_OF_BUSINESS, "");
        paramenterMap.put(ReportConstants.LIST_OF_SECURITIES, "");
        paramenterMap.put(ReportConstants.PROFIT_RATE, "");
        paramenterMap.put(ReportConstants.FACILITY_ARRANGEMENT_FEE, "");
        if (collaterals != null) {
            paramenterMap.put(ReportConstants.FTV, roundDoubleValue(collaterals.getFtv()));
            paramenterMap.put(ReportConstants.TOTAL_COLLATERAL, roundDoubleValue(collaterals.getTotalCashColleteral()));
        } else {
            logger.info("===========LSF :Collaterals values are null");
            paramenterMap.put(ReportConstants.TOTAL_COLLATERAL, "0.0");
        }

        if (globalParameters != null) {
            paramenterMap.put(
                    ReportConstants.FIRST_CALL_MARGIN,
                    roundDoubleValue(globalParameters.getFirstMarginCall()));
            paramenterMap.put(
                    ReportConstants.SECOND_CALL_MARGIN,
                    roundDoubleValue(globalParameters.getSecondMarginCall()));
            paramenterMap.put(
                    ReportConstants.LIQUIDATION_MARGIN,
                    roundDoubleValue(globalParameters.getLiquidationCall()));
        } else {
            logger.info("===========LSF :Global parameters are null");
        }

        paramenterMap.put(ReportConstants.REAL_PATH, new File("../reports/templates").getAbsolutePath() + "\\dib.jpg");

        // mandetory data
        Map<String, String> reportObject = new HashMap<>();
        reportObject.put("template", "CP.jasper");
        reportObject.put("reportCode", reportCode);
        reportObject.put("id", id);
        reportObject.put("fileFormat", "pdf");
        if (reportGenerator.generateReport(reportDataList, paramenterMap, reportObject) == "1") {
            retValue = true;
        }
        return retValue;
    }

    private boolean generateIOF(Map objMap, String reportCode) throws JRException {
        boolean retValue = false;
        String id = objMap.get("id").toString();
        MurabahApplication application = getMurabahApplication(id);
        Collection<Map<String, ?>> reportDataList = new ArrayList<>();
        MApplicationSymbolWishList mApplicationSymbolWishList = getMApplicationSymbolWishList(id);
        if (mApplicationSymbolWishList.getWishListSymbols() != null) {
            List<Symbol> symbolList = mApplicationSymbolWishList.getWishListSymbols();
            if (symbolList.size() != 0) {
                for (Symbol symbol : symbolList) {
                    Map<String, String> someHashMap = new HashMap<>();
                    someHashMap.put("symbols", symbol.getSymbolCode());
                    someHashMap.put("description", symbol.getShortDescription());
                    reportDataList.add(someHashMap);
                }
            }
        }

        //parameters in the Report change according to the report
        Map<String, Object> paramenterMap = new HashMap<>();
        paramenterMap.put("date", getCurrentTimeStamp());
        paramenterMap.put("agreementDate", getCurrentTimeStamp());
        paramenterMap.put("attention", getCurrentTimeStamp());

        paramenterMap.put("approvedLimit", getCurrentTimeStamp());
        paramenterMap.put("broker", getCurrentTimeStamp());
        paramenterMap.put("investmentAccBroker", getCurrentTimeStamp());
        paramenterMap.put("profitRate", getCurrentTimeStamp());
        paramenterMap.put("customerName", application.getFullName());

        // mandetory data
        Map<String, String> reportObject = new HashMap<>();
        reportObject.put("template", "InvestmentOffer.jasper");
        reportObject.put("reportCode", reportCode);
        reportObject.put("id", id);
        reportObject.put("fileFormat", "pdf");
        if (reportGenerator.generateReport(reportDataList, paramenterMap, reportObject) == "1") {
            retValue = true;
        }

        return retValue;
    }

    private boolean generateFAL(Map objMap, String reportCode) {
        boolean retValue = false;
        String id = objMap.get("id").toString();
        MurabahApplication application = getMurabahApplication(id);
        if (application == null) {
            logger.info("===========LSF :Mubahara Application null : " + id);
            return false;
        }
        MApplicationCollaterals collaterals = getMApplicationCollaterals(application.getId());
        MarginabilityGroup marginabilityGroup = getMarginabilityGroup(application.getMarginabilityGroup());
        GlobalParameters globalParameters = GlobalParameters.getInstance();
        Map<String, LiquidityType> marginabilityGroupMap =
                getMarginabilityValues(marginabilityGroup.getMarginabilityList());

        // no data table
        Collection<Map<String, ?>> reportDataList = new ArrayList<>();
        Map<String, String> someHashMap = new HashMap<>();
        reportDataList.add(someHashMap);

        //parameters in the Report change according to the report
        Map<String, String> paramenterMap = new HashMap<>();
        paramenterMap.put(ReportConstants.FAL_DATE, getCurrentTimeStamp());
        paramenterMap.put("refNo", "AC123");
        paramenterMap.put("accNo", "AC123");
        paramenterMap.put("grandTotal", "1000000");
        paramenterMap.put("uaeDirham", "1000000");
        paramenterMap.put(ReportConstants.AED_AMOUNT, roundDoubleValue(application.getProposedLimit()));
        paramenterMap.put(ReportConstants.PROPOSED_LIMIT, roundDoubleValue(application.getProposedLimit()));
        if (collaterals != null) {
            paramenterMap.put(ReportConstants.FTV, roundDoubleValue(collaterals.getFtv()));
        } else {
            logger.info("===========LSF :collaterals is null");
            paramenterMap.put(ReportConstants.FTV, "0.0");
        }
        if (marginabilityGroupMap.containsKey("1")) {
            paramenterMap.put(
                    ReportConstants.LIQUID_MARGINABILITY,
                    roundDoubleValue(marginabilityGroupMap.get("1").getMarginabilityPercent()));
        } else {
            logger.info("===========LSF :liquidMarginability value is null");
        }

        if (marginabilityGroupMap.containsKey("2")) {
            paramenterMap.put(
                    ReportConstants.SEMI_LIQUID_MARGINABILITY,
                    roundDoubleValue(marginabilityGroupMap.get("2").getMarginabilityPercent()));
        } else {
            logger.info("===========LSF :semiLiquidMarginability value is null");
        }

        if (marginabilityGroupMap.containsKey("3")) {
            paramenterMap.put(
                    ReportConstants.NON_LIQUID_MARGINABILITY,
                    roundDoubleValue(marginabilityGroupMap.get("3").getMarginabilityPercent()));
        } else {
            logger.info("===========LSF :nonLiquidMarginability value is null");
        }

        if (globalParameters != null) {
            paramenterMap.put(
                    ReportConstants.FIRST_CALL_MARGIN,
                    roundDoubleValue(globalParameters.getFirstMarginCall()));
            paramenterMap.put(
                    ReportConstants.SECOND_CALL_MARGIN,
                    roundDoubleValue(globalParameters.getSecondMarginCall()));
            paramenterMap.put(
                    ReportConstants.LIQUIDATION_MARGIN,
                    roundDoubleValue(globalParameters.getLiquidationCall()));
        } else {
            logger.info("===========LSF :Global parameters are null");
        }

        // mandetory data
        Map<String, String> reportObject = new HashMap<>();
        reportObject.put("template", "FAL.jasper");
        reportObject.put("reportCode", reportCode);
        reportObject.put("id", id);
        reportObject.put("fileFormat", "pdf");
        if (reportGenerator.generateReport(reportDataList, paramenterMap, reportObject) == "1") {
            retValue = true;
        }

        return retValue;
    }

    private MurabahApplication getMurabahApplication(String id) {
        List<MurabahApplication> murabahApplicationList = null;
        murabahApplicationList = lsfRepository.getMurabahAppicationApplicationID(id);
        MurabahApplication murabahApplication = null;
        if (murabahApplicationList != null) {
            if (murabahApplicationList.size() > 0) {
                murabahApplication = murabahApplicationList.get(0);
            }
        }
        return murabahApplication;
    }

    private MApplicationCollaterals getMApplicationCollaterals(String applicationId) {
        MApplicationCollaterals mApplicationCollaterals = null;
        mApplicationCollaterals = lsfRepository.getApplicationCollateral(applicationId);
        return mApplicationCollaterals;
    }

    private MarginabilityGroup getMarginabilityGroup(String maginabilityGroupId) {
        MarginabilityGroup marginabilityGroup = new MarginabilityGroup();
        try {
            marginabilityGroup = lsfRepository.getMarginabilityGroup(maginabilityGroupId);
          /*  EsbPersistence esbPersistence = new EsbPersistence();
            esbPersistence.setEntityClass(MarginabilityGroup.class);
            EsbPersistence bRequest = null;
            bRequest = esbPersistence.createEsbQuery().where("id").is(maginabilityGroupId);
            marginabilityGroup = (MarginabilityGroup) dataService.findOne(bRequest);*/
            List<LiquidityType> liquidityTypes = lsfRepository.getMarginabilityGroupLiquidTypes(maginabilityGroupId);
            marginabilityGroup.setMarginabilityList(liquidityTypes);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return marginabilityGroup;
    }

    private MApplicationSymbolWishList getMApplicationSymbolWishList(String applicationID) {
        MApplicationSymbolWishList mApplicationSymbolWishList = new MApplicationSymbolWishList();
        try {
            List<Symbol> wishList = null;
            wishList = lsfRepository.getWishListSymbols(
                    applicationID,
                    GlobalParameters.getInstance().getDefaultExchange());
            mApplicationSymbolWishList.setWishListSymbols(wishList);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mApplicationSymbolWishList;
    }

    private String changeDateFormt(String currentDateFormat, String requiredDateFormat, String date) {
        SimpleDateFormat currentSDF = new SimpleDateFormat(currentDateFormat);
        SimpleDateFormat requiredSDF = new SimpleDateFormat(requiredDateFormat);
        String reformattedDate = null;
        try {
            reformattedDate = requiredSDF.format(currentSDF.parse(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return reformattedDate;
    }

    private String roundDoubleValue(Double d) {
        /*int decimalPlaces = 2;
        BigDecimal bd = new BigDecimal(d);
        bd = bd.setScale(decimalPlaces, BigDecimal.ROUND_HALF_UP);
        Double r = bd.doubleValue();
        NumberFormat formatter = new DecimalFormat("#,###,###,###,###,##0");
        return formatter.format(r);*/
        return String.valueOf(d); // todo : round double values.
    }

    private String getCurrentTimeStamp() {
        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy/MM/dd");//dd/MM/yyyy
        Date now = new Date();
        String strDate = sdfDate.format(now);
        return strDate;
    }

    private Map<String, LiquidityType> getMarginabilityValues(List<LiquidityType> liquidityTypes) {
        Map<String, LiquidityType> liquidityTypeMap = new HashMap<>();
        for (LiquidityType liquidityType : liquidityTypes) {
            liquidityTypeMap.put(String.valueOf(liquidityType.getLiquidId()), liquidityType);
        }
        return liquidityTypeMap;
    }
}
