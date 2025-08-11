package com.example.licenseissuer;

import java.util.List;

public class LicenseData {
    // metadata
    private String kid;
    private long notBefore;
    private long notAfter;
    private String serialNumber;

    // payload
    private String customerName;
    private String licenseType;
    private String machineId;
    private List<String> macs;

    // Getters and Setters
    public String getKid() { return kid; }
    public void setKid(String kid) { this.kid = kid; }

    public long getNotBefore() { return notBefore; }
    public void setNotBefore(long notBefore) { this.notBefore = notBefore; }

    public long getNotAfter() { return notAfter; }
    public void setNotAfter(long notAfter) { this.notAfter = notAfter; }

    public String getSerialNumber() { return serialNumber; }
    public void setSerialNumber(String serialNumber) { this.serialNumber = serialNumber; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getLicenseType() { return licenseType; }
    public void setLicenseType(String licenseType) { this.licenseType = licenseType; }

    public String getMachineId() { return machineId; }
    public void setMachineId(String machineId) { this.machineId = machineId; }

    public List<String> getMacs() { return macs; }
    public void setMacs(List<String> macs) { this.macs = macs; }
}