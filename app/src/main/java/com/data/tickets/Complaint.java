package com.data.tickets;

public class Complaint {
    String complaint_id;
    String customer_name;
    String logged_date;
    String replied_date;
    String replied_by;
    String query_text;
    String replied_text;
    String status;

    public String getComplaint_id() {
        return complaint_id;
    }

    public void setComplaint_id(String complaint_id) {
        this.complaint_id = complaint_id;
    }

    public String getCustomer_name() {
        return customer_name;
    }

    public void setCustomer_name(String customer_name) {
        this.customer_name = customer_name;
    }

    public String getLogged_date() {
        return logged_date;
    }

    public void setLogged_date(String logged_date) {
        this.logged_date = logged_date;
    }

    public String getReplied_date() {
        return replied_date;
    }

    public void setReplied_date(String replied_date) {
        this.replied_date = replied_date;
    }

    public String getReplied_by() {
        return replied_by;
    }

    public void setReplied_by(String replied_by) {
        this.replied_by = replied_by;
    }

    public String getQuery_text() {
        return query_text;
    }

    public void setQuery_text(String query_text) {
        this.query_text = query_text;
    }

    public String getReplied_text() {
        return replied_text;
    }

    public void setReplied_text(String replied_text) {
        this.replied_text = replied_text;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
