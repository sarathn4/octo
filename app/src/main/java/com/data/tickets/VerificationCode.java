package com.data.tickets;

import android.os.Parcel;
import android.os.Parcelable;

public class VerificationCode implements Parcelable {
    String code_id;
    String ticket_no;
    String verification_code;
    String from_station_name;
    String to_station_name;
    String no_of_tickets;
    String ticket_type;
    String category;
    String ticket_period;
    String amount;
    String purchased_date;
    String proof_document;
    String photo;

    public VerificationCode(){}
    public VerificationCode(Parcel source) {
        code_id = source.readString();
        ticket_no = source.readString();
        verification_code= source.readString();
        from_station_name= source.readString();
        to_station_name = source.readString();
        no_of_tickets = source.readString();
        ticket_type= source.readString();
        category= source.readString();
        ticket_period= source.readString();
        amount= source.readString();
        purchased_date= source.readString();
        proof_document = source.readString();
        photo = source.readString();
    }

    public String getNo_of_tickets() {
        return no_of_tickets;
    }

    public void setNo_of_tickets(String no_of_tickets) {
        this.no_of_tickets = no_of_tickets;
    }

    public String getCode_id() {
        return code_id;
    }

    public void setCode_id(String code_id) {
        this.code_id = code_id;
    }

    public String getTicket_no() {
        return ticket_no;
    }

    public void setTicket_no(String ticket_no) {
        this.ticket_no = ticket_no;
    }

    public String getVerification_code() {
        return verification_code;
    }

    public void setVerification_code(String verification_code) {
        this.verification_code = verification_code;
    }

    public String getFrom_station_name() {
        return from_station_name;
    }

    public void setFrom_station_name(String from_station_name) {
        this.from_station_name = from_station_name;
    }

    public String getTo_station_name() {
        return to_station_name;
    }

    public void setTo_station_name(String to_station_name) {
        this.to_station_name = to_station_name;
    }

    public String getTicket_type() {
        return ticket_type;
    }

    public void setTicket_type(String ticket_type) {
        this.ticket_type = ticket_type;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getTicket_period() {
        return ticket_period;
    }

    public void setTicket_period(String ticket_period) {
        this.ticket_period = ticket_period;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getPurchased_date() {
        return purchased_date;
    }

    public void setPurchased_date(String purchased_date) {
        this.purchased_date = purchased_date;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(code_id);
        dest.writeString(ticket_no);
        dest.writeString(verification_code);
        dest.writeString(from_station_name);
        dest.writeString(to_station_name);
        dest.writeString(no_of_tickets);
        dest.writeString(ticket_type);
        dest.writeString(category);
        dest.writeString(ticket_period);
        dest.writeString(amount);
        dest.writeString(purchased_date);
        dest.writeString(proof_document);
        dest.writeString(photo);

    }
    public static final Parcelable.Creator<VerificationCode> CREATOR = new Creator<VerificationCode>() {

        @Override
        public VerificationCode createFromParcel(Parcel source) {
            return  new VerificationCode(source);
        }

        @Override
        public VerificationCode[] newArray(int size) {
            return new VerificationCode[0];
        }
    };

    public String getProof_document() {
        return proof_document;
    }

    public void setProof_document(String proof_document) {
        this.proof_document = proof_document;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }
}
