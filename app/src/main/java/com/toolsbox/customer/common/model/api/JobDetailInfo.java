package com.toolsbox.customer.common.model.api;

import com.toolsbox.customer.common.model.JobInfo;
import com.toolsbox.customer.common.model.ProposalInfo;
import com.toolsbox.customer.common.model.RecordHoursInfo;

import java.io.Serializable;

public class JobDetailInfo implements Serializable {
    public JobInfo job;
    public ProposalInfo bid;
    public RecordHoursInfo record_hours;


    public JobDetailInfo(){
        this.job = new JobInfo();
        this.bid = new ProposalInfo();
        this.record_hours = new RecordHoursInfo();
    }
}
