package com.dfn.lsf.repository.mapper;

import com.dfn.lsf.util.RowMapperI;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import com.dfn.lsf.repository.mapper.application.*;
import com.dfn.lsf.repository.mapper.reports.*;
import com.dfn.lsf.repository.mapper.notification.*;
import com.dfn.lsf.repository.mapper.symbol.*;

/**
 * Factory for creating row mappers
 * Modern Java 21 implementation with functional interfaces
 */
@Component
public class RowMapperFactory {

    @SuppressWarnings("unchecked")
    public <T> RowMapper<T> getRowMapper(String mapperName) {
        return switch (mapperName) {
            case RowMapperI.CASH_ACCOUNT -> (RowMapper<T>) new CashAccMapper();
            case RowMapperI.MURABAH_APPLICATION -> (RowMapper<T>) new MurabahApplicationMapper();
            case RowMapperI.USER_DOCUMENTS -> (RowMapper<T>) new DocumentsMapper();
            case RowMapperI.LIQUIDITY_TYPES -> (RowMapper<T>) new LiquidityTypeMapper();
            case RowMapperI.USER_APPLICATION_DOCUMENTS -> (RowMapper<T>) new MApplicationDocumentsMapper();
            case RowMapperI.TENOR -> (RowMapper<T>) new TenorMapper();
            case RowMapperI.MARGINABILITY_GROUPS -> (RowMapper<T>) new MarginabilityGroupMapper();
            case RowMapperI.ADMIN_APPLICATION_DOCUMENTS -> (RowMapper<T>) new MAdminApplicationDocumentMapper();
            case RowMapperI.INITIAL_PORTFOLIO -> (RowMapper<T>) new InitialPortfolioMapper();
            case RowMapperI.COMMISSION_STRUCTURE -> (RowMapper<T>) new CommissionStructureMapper();
            case RowMapperI.APP_COMMENT -> (RowMapper<T>) new AppCommentMapper();
            case RowMapperI.SYS_PARAS -> (RowMapper<T>) new GlobalParametersMapper();
            case RowMapperI.EXCHANGE_SYMBOLS -> (RowMapper<T>) new SymbolMapper();
            case RowMapperI.SYMBOL_DESCRIPTION -> (RowMapper<T>) new SymbolDescriptionMapper();
            case RowMapperI.USER_SESSION -> (RowMapper<T>) new UserSessionMapper();
            case RowMapperI.APP_STATE_FLOW -> (RowMapper<T>) new StateFlowMapper();
            case RowMapperI.APPLICATION_STATUS -> (RowMapper<T>) new ApplicationStatusMapper();
            case RowMapperI.COLLATERALS -> (RowMapper<T>) new MApplicationCollateralsMapper();
            case RowMapperI.STOCK_CONCENTRATION_GROUP -> (RowMapper<T>) new StockConcentrationGroupMapper();
            case RowMapperI.TRADING_ACCOUNT -> (RowMapper<T>) new TradingAccMapper();
            case RowMapperI.ORDER_INSTALLMENTS -> (RowMapper<T>) new InstallmentsMapper();
            case RowMapperI.PURCHASE_ORDER -> (RowMapper<T>) new PurchaseOrderMapper();
            case RowMapperI.NOTIFICATION_MSG_CONFIGURATION -> (RowMapper<T>) new NotificationMsgConfigurationMapper();
            case RowMapperI.WEB_NOTIFICATION -> (RowMapper<T>) new WebNotificationMapper();
            case RowMapperI.DOCUMENT_MASTER_DOCS -> (RowMapper<T>) new DocumentMasterMapper();
            case RowMapperI.NOTIFICATION_HEADER -> (RowMapper<T>) new NotificationHeaderMapper();
            case RowMapperI.NOTIFICATION_BODY -> (RowMapper<T>) new NotificationBodyMapper();
            case RowMapperI.USER_ANSWER -> (RowMapper<T>) new UserAnswerMapper();
            case RowMapperI.DOCUMENT_RELATED_APPS -> (RowMapper<T>) new DocumentRelateAppsMapper();
            case RowMapperI.SYMBOL -> (RowMapper<T>) new LoadSymbolMapper();
            case RowMapperI.ORDER_PROFIT -> (RowMapper<T>) new OrderProfitMapper();
            case RowMapperI.MESSAGE -> (RowMapper<T>) new MessageMapper();
            case RowMapperI.LIQUIDATION_LOG -> (RowMapper<T>) new LiquidationLogMapper();
            case RowMapperI.REPORT_CONFIG_OBJECT -> (RowMapper<T>) new ReportConfigObjectMapper();
            case RowMapperI.REPORT_CONFIG -> (RowMapper<T>) new ReportConfigMapper();
            case RowMapperI.MARGIN_INFO -> (RowMapper<T>) new MarginInformationReportMapper();
            case RowMapperI.FINANCE_BROKERAGE_INFO -> (RowMapper<T>) new FinanceBrokerageInfoMapper();
            case RowMapperI.EXTERNAL_COLLATERALS -> (RowMapper<T>) new ExternalCollateralsMapper();
            case RowMapperI.FTV_DETAILED_INFO -> (RowMapper<T>) new FTVDetailedInfoMapper();
            case RowMapperI.COMMISSION_DETAILS -> (RowMapper<T>) new CommissionDetailsMapper();
            case RowMapperI.RISK_WAVIER_QUESTION_CONFIG -> (RowMapper<T>) new RisKWavierQUestionConfigMapper();
            case RowMapperI.ADMIN_USER -> (RowMapper<T>) new AdminUserMapper();
            case RowMapperI.APPLICATION_RATING -> (RowMapper<T>) new ApplicationRatingMapper();
            case RowMapperI.QUESTIONNAIRE_ENTRY -> (RowMapper<T>) new QuestionnaireEntryMapper();
            case RowMapperI.SYMBOL_CLASSIFY_LOG -> (RowMapper<T>) new SymbolClassifyLogMapper();
            case RowMapperI.FTV_SUMMARY_INFO -> (RowMapper<T>) new FtvSummaryMapper();
            case RowMapperI.ORDER_CONTRACT_USER_INFO -> (RowMapper<T>) new OrderContractUserInfoMapper();
            case RowMapperI.APP_STATUS_SUMMARY -> (RowMapper<T>) new AppStatusSummaryMapper();
            case RowMapperI.PENDING_ACTIVITY -> (RowMapper<T>) new PendingActivityMapper();
            case RowMapperI.ADMIN__REJECT_APPLICATION -> (RowMapper<T>) new AdminRejectApplicationMapper();
            case RowMapperI.SETTLEMENT_LIST -> (RowMapper<T>) new SettlementListMapper();
            case RowMapperI.MURABAHA_PRODUCT -> (RowMapper<T>) new MurabahaProductsMapper();
            case RowMapperI.PROFIT_MASTER_ENTRY -> (RowMapper<T>) new ProfitMasterEntryMapper();
            case RowMapperI.PROFIT_CAL_MURABAHA_APPLICATION -> (RowMapper<T>) new ProfitCalMurabahaApplicationMapper();
            case RowMapperI.OMS_COMMISSION -> (RowMapper<T>) new OMSCommissionMapper();
            case RowMapperI.AGREEMENT_LIST -> (RowMapper<T>) new AgreementListMapper();
            case RowMapperI.COMMODITY_LIST -> (RowMapper<T>) new CommodityListMapper();
            case RowMapperI.PHYSICAL_DELIVERY_LIST -> (RowMapper<T>) new PhysicalDeliveryOrderMapper();
            case RowMapperI.SYMBOL_MARGINABILITY_PERCENTAGE -> (RowMapper<T>) new SymbolMarginabilityPercentageMapper();
            case RowMapperI.EXCHANGE_INSTRUMENT_TYPE -> (RowMapper<T>) new InstrumentTypeMapper();
            default -> null;
        };
    }
}