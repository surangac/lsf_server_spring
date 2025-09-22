package com.dfn.lsf.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Agreement {
    private int financeMethod;
    private String fileExtension;
    private String fileName;
    private String filePath;
    private int version;
    private int agreementType;
    private int productType;
    private int applicationId;

    public int getFinanceMethod() {
        return financeMethod;
    }

    public void setFinanceMethod(int financeMethod) {
        this.financeMethod = financeMethod;
    }

    public String getFileExtension() {
        return fileExtension;
    }

    public void setFileExtension(String fileExtension) {
        this.fileExtension = fileExtension;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public int getAgreementType() {
        return agreementType;
    }

    public void setAgreementType(int agreementType) {
        this.agreementType = agreementType;
    }

    public int getProductType() {
        return productType;
    }

    public void setProductType(int productType) {
        this.productType = productType;
    }

    @Override
    public String toString() {
        return "Agreement{" +
                "financeMethod=" + financeMethod +
                ", fileExtension='" + fileExtension + '\'' +
                ", fileName='" + fileName + '\'' +
                ", filePath='" + filePath + '\'' +
                ", version=" + version +
                ", agreementType=" + agreementType +
                ", productType=" + productType +
                ", applicationId=" + applicationId +
                '}';
    }
}
