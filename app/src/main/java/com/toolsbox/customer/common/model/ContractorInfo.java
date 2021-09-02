package com.toolsbox.customer.common.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ContractorInfo implements Serializable {
    public int id;
    public String image_url;
    public String business_name;
    public String email;
    public String phone;
    public String address;
    public String address_lati;
    public String address_longi;
    public String speciality_title;
    public String industry;
    public String about;
    public String hourly_rate;
    public int review_count;
    public List<ReviewInfo> reviews;

    public ContractorInfo(){
        this.id = 0;
        this.review_count = 0;
        this.image_url = "";
        this.business_name = "";
        this.email = "";
        this.phone = "";
        this.address = "";
        this.address_lati = "";
        this.address_longi = "";
        this.speciality_title = "";
        this.industry = "";
        this.about = "";
        this.hourly_rate = "";
        this.reviews = new ArrayList<>();
    }

    public ContractorInfo(int id, String image_url, String business_name, String email, String phone, String address,
                          String address_lati, String address_longi, String speciality_title){
        this.id = id;
        this.image_url = image_url;
        this.business_name = business_name;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.address_lati = address_lati;
        this.address_longi = address_longi;
        this.speciality_title = speciality_title;
    }

    public float getReviewValue(){
        float total = 0;
        for (ReviewInfo item : reviews) {
            total = total + item.overall_rate;
        }

        if (reviews.size() == 0) {
            return 0;
        }
        return total / reviews.size();
    }

}
