package com.toolsbox.customer.common.model.api;

import com.toolsbox.customer.common.model.ContractorInfo;
import com.toolsbox.customer.common.model.JobInfo;

import java.io.Serializable;

public class HistoryInfo implements Serializable {
    public JobInfo job;
    public ContractorInfo contractor_info;

    public HistoryInfo(){
        this.job = new JobInfo();
        this.contractor_info = new ContractorInfo();
    }
}
