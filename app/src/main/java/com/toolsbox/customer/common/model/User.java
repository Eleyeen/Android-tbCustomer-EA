package com.toolsbox.customer.common.model;

import java.io.Serializable;

public class User implements Serializable {
    public int id;
    public String image_url;
    public String name;
    public String email;
    public String phone;
    public String fcm_token;
    public String token;
    public int sign_type;

    public User(){
        this.id = 0;
        this.image_url = "";
        this.name = "";
        this.email = "";
        this.phone = "";
        this.fcm_token = "";
        this.token = "";
        this.sign_type = 0;
    }
}
