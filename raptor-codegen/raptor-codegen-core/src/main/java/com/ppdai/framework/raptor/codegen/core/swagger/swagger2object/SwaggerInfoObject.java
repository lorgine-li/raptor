package com.ppdai.framework.raptor.codegen.core.swagger.swagger2object;

/**
 * Created by zhangyicong on 18-2-27.
 * http://swagger.io/specification/#infoObject
 */
public class SwaggerInfoObject {
    private String title;
    private String description;
    private String termsOfService;
    private String version;
    private SwaggerContactObject contact;
    private SwaggerLicenseObject license;

    public SwaggerInfoObject(String title, String version) {
        this.title = title;
        this.version = version;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTermsOfService() {
        return termsOfService;
    }

    public void setTermsOfService(String termsOfService) {
        this.termsOfService = termsOfService;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public SwaggerContactObject getContact() {
        return contact;
    }

    public void setContact(SwaggerContactObject contact) {
        this.contact = contact;
    }

    public SwaggerLicenseObject getLicense() {
        return license;
    }

    public void setLicense(SwaggerLicenseObject license) {
        this.license = license;
    }
}
