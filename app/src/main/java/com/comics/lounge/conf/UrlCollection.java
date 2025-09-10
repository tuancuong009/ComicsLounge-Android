package com.comics.lounge.conf;

/**
 * Created by GAURAV on 26-12-19.
 */

public class UrlCollection {
    public static final String PRIVACY_POLICY_URL = "https://thecomicslounge.com.au/privacy-policies.html";
    public static final String TEMS_CON_URL = "https://thecomicslounge.com.au/termsandconditions.html";
//    public static final String BASE_URL = "https://thecomicslounge.com.au/cs2/api/"; // Live
    public static final String BASE_URL = "https://thecomicslounge.com.au/cs3/api/"; // Test
//  public static final String BASE_URL = "https://sandbox.thecomicslounge.com.au/cs2/api/"; // Test
    // public static final String BASE_URL = "http://new.thecomicslounge.com.au/cs2/api/"; unused test
    public static final String LOGIN = BASE_URL + "login.php";
    public static final String EVENT = BASE_URL + "event/future_event.php";
    public static final String REGISTER = BASE_URL + "register.php";
    public static final String OTP_SEND = BASE_URL + "otp_send.php";
    public static final String OTP_VERIFY = BASE_URL + "otp_verify.php";
    public static final String FORGOT_PASS = BASE_URL + "forgotpassword.php";
    public static final String FORGOT_PASS_OTP = BASE_URL + "forget_password_otp_send.php";
    public static final String BOOKING_HISTORY = BASE_URL + "orders/orderhistorylist.php?user_id=";
    public static final String VIDEO_LIST = BASE_URL + "misc/videos.php";
    public static final String CONTACT_US = BASE_URL + "misc/contact.php?";

    public static final String MEMBERSHIP = BASE_URL + "membership/memberships.php";
    public static final String CONFIRM_TICKET = BASE_URL + "orders/orderlist.php?user_id=";
    public static final String CONFIRM_TICKET_DETAILS = BASE_URL + "orders/order.php?order_id=";
    public static final String EVENT_DETAILS = BASE_URL + "event/eventDetail.php?";
    public static final String DELETE_EVENT = BASE_URL + "cart/cancel_event.php";
    public static final String WALLET = BASE_URL + "wallet.php?user_id=";
    public static final String EDIT_PROFILE = BASE_URL + "editprofile.php";
    public static final String CALENDER = BASE_URL + "event/calendar.php";
    public static final String GET_PAYPAL_TOKEN = BASE_URL + "cart/generatetoken.php";
    public static final String FINAL_ORDERS = BASE_URL + "cart/final_orders.php";
    public static final String MEMBERSHIP_PAYMENT = BASE_URL + "cart/paypalpayment.php";
    public static final String USER = BASE_URL + "user.php?user_id=";
    public static final String GET_SINGLE_ORDER = BASE_URL + "orders/order.php?order_id=";
    public static final String GET_SERVER_TIME = BASE_URL + "servertime.php";
    public static final String PAYPAL_LOG = BASE_URL + "logs.php";
    public static final String POP_UP_URL = BASE_URL + "pop.php";
    public static final String USER_PROFILE = BASE_URL + "userprofile.php";
    public static final String CUS_POP_UP_URL = "http://thecomicslounge.com.au/customerpopup.html";
}
