package com.toolsbox.customer.common;

public class Constant {

    // api
    public static final String ROOT_URL = "https://thejobsapp.com";

    //Preference
    public static final String kAndroidID       = "android unique id";

    public static final String PRE_SIGN_TYPE  = "sign_type";
    public static final String PRE_ID = "id";
    public static final String PRE_NAME = "name";
    public static final String PRE_IMAGE_URL = "imageURL";
    public static final String PRE_EMAIL = "email";
    public static final String PRE_PASSWORD = "password";
    public static final String PRE_PHONE = "phone";
    public static final String PRE_TOKEN = "token";
    public static final String PRE_FCM_TOKEN = "fcm_token";
    public static final String RESERVE_FCM_TOKEN = "reserve_fcm_token";

    public static final String DEFAULT_FCM_TOKEN = "undefine";
    public static final int SIGN_TYPE_EMAIL = 1;
    public static final int SIGN_TYPE_PHONE = 2;
    public static final int SIGN_TYPE_SOCIAL = 3;


    // Permission
    public static final int RC_HANDLE_PERMISSION  = 555;

    public static String[] gArrSpecialization =  {"Appliance Installation & Repair Service",
            "Carpentry", "Cleaning Service", "Concrete",  "Deck Building", "Demolition & Hauling", "Drywall & Plastering", "Electrical", "Fencing", "Fire & Water Damage Restoration Services",
            "Fire Alarm Systems", "Flooring & Tiling", "Handyman", "Home Automation", "Home Renovation", "Home Security & Camera Installation", "Interior Design & Decoration",
            "HVAC - Heating Ventilation Air Conditioning", "Lawn Maintenance & Landscaping", "Masonry & Bricklaying", "Mould Control", "Waste Removal & Recycling",
            "Mobile Car Wash", "Moving & Storage Services", "Locksmith",
           "Painting", "Paving", "Pest Control", "Plumbing", "Roofing", "Snow Removal",
            "Windows & Doors Installation & Repairs"};

    public static String[] gArrJobStatus = {"Pending Proposals", "Pending Proposals", "On Schedule", "En Route", "In Progress", "Completed"};

    public static String GOOGLE_API_KEY = "AIzaSyCzrYMTRbeIrERXujvg_eg7GJhLwpZWzO4";
    public static final String STRIPE_KEY = "pk_test_GXoJOEwGWCMNl96kc56o77j2";

    // Calendar Format
    public static String DATE_FORMAT_DEFAULT = "MM/dd/yyyy HH:mm:ss";
    public static String DATE_FORMAT_UTC = "yyyy-MM-dd'T'hh:mm:ss";
    public static String DATE_FORMAT_SHORT = "(MM/dd)";
    public static String DATE_FORMAT_MM_DD_YYYY = "MM/dd/yyyy";
    public static String APP_WEEKEND_FORMAT = "EEEE";
    public static String APP_DATE_FORMAT_MIDDLE = "MM/dd/yyyy";
    public static String DATE_FORMAT_TIME_STAMP = "hh:mm a";
    public static String DATE_FORMAT_MMM_D_YYYY = "MMM d yyyy";
    public static String DATE_FORMAT_MMM_D_YYYY_dot = "MMM d, yyyy";
    public static String DATE_FORMAT_AVAILABILITY = "yyyy/MM/dd HH:mm:ss";




    // BroadCast
    public static final String ACTION_CHANGED_SECTION = "CUSTOMER_ACTION_CHANGED_SECTION";
    public static final String ACTION_CHANNEL_UPDATED = "ACTION_CHANNEL_UPDATED";
    public static final String ACTION_CHAT_CLIENT_CONNECT = "ACTION_CHAT_CLIENT_CONNECT";
    public static final String ACTION_CHAT_USER_REACHABILITY = "ACTION_CHAT_USER_REACHABILITY";
    public static final String ACTION_REFRESH_JOBS = "ACTION_REFRESH_JOBS";


    // Main Fragment Constant
    public static final int FRAG_HOME = 1;
    public static final int FRAG_MARKET = 2;
    public static final int FRAG_JOBS = 3;
    public static final int FRAG_MESSAGE = 4;
    public static final int FRAG_PROFILE = 5;

    // Pagination
    public static final int PER_PAGE = 10;


    // Job Status
    public static final int IN_BIDDING_PROCESS = 0;
    public static final int PENDING_APPROVAL = 1;
    public static final int SCHEDULED = 2;
    public static final int EN_ROUTE = 3;
    public static final int IN_PROGRESS = 4;
    public static final int FINISH = 5;

    // Quote Status
    public static final int QUOTE_PENDING = 0;
    public static final int QUOTE_AWARDED = 1;
    public static final int QUOTE_DECLINED = 2;
    public static final int QUOTE_ACCEPTED = 3;


    // From Activity
    public static final int FROM_JOB_POST = 1;
    public static final int FROM_JOB_EDIT = 2;
    public static final int FROM_JOB_HIRE = 3;
    public static final int FROM_MARKET = 4;


    // Phone verification
    public static final int FROM_EDIT_PHONE = 1;
    public static final int FROM_VERIFY_PHONE = 2;
    public static final int FROM_FORGOT_PASSWORD_BY_PHONE = 3;
    public static final int FROM_FORGOT_PASSWORD_BY_EMAIL = 4;

    // Chat message view
    public static final int TYPE_INCOMING_PLAIN_TEXT = 1;
    public static final int TYPE_OUTGOING_PLAIN_TEXT = 2;
    public static final int TYPE_INCOMING_LOCATION = 3;
    public static final int TYPE_OUTGOING_LOCATION = 4;
    public static final int TYPE_INCOMING_IMAGE = 5;
    public static final int TYPE_OUTGOING_IMAGE = 6;
    public static final int TYPE_INCOMING_AUDIO = 7;
    public static final int TYPE_OUTGOING_AUDIO = 8;


    // Activity Result code
    public static final int REQUEST_GOOGLE_SIGN_IN = 1;


}
