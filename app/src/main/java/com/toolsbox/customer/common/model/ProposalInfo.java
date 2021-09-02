package com.toolsbox.customer.common.model;

import java.io.Serializable;

public class ProposalInfo implements Serializable   {
    public int id;
    public int type;
    public int status;
    public String budget;
    public String hourly_rate;
    public String description;
    public String availability_start_dates;
    public String attachment;
    public ContractorInfo contractor;
    public JobInfo job;

    public ProposalInfo(){
        this.id = 0;
        this.status = 0;
        this.budget = "";
        this.description = "";
        this.availability_start_dates = "";
        this.hourly_rate = "";
        this.attachment = "";
        this.contractor = new ContractorInfo();
        this.job = new JobInfo();
    }

}
