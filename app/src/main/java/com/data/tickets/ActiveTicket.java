package com.data.tickets;

import android.os.Parcel;
import android.os.Parcelable;

public class ActiveTicket implements Parcelable {
    public static final Parcelable.Creator<ActiveTicket> CREATOR = new Parcelable.Creator<ActiveTicket>() {

        @Override
        public ActiveTicket createFromParcel(Parcel source) {
            return new ActiveTicket(source);
        }

        @Override
        public ActiveTicket[] newArray(int size) {
            return new ActiveTicket[0];
        }
    };
    String ticket_id;
    String ticket_no;
    String ticket_code;
    String from_station;
    String to_station;
    String ticket_type;
    String ticket_category;
    String ticket_period;
    String ticket_amount;
    String purchased_date;
    String activated_date;
    String activated_station;
    String valid_date;
    String no_of_tickets;
    String proof_document;
    String photo;
    String validated_count;
    String imei_device;

    public ActiveTicket() {
    }

    public ActiveTicket(Parcel source) {
        ticket_id = source.readString();
        ticket_no = source.readString();
        ticket_code = source.readString();
        from_station = source.readString();
        to_station = source.readString();
        ticket_type = source.readString();
        no_of_tickets = source.readString();
        ticket_category = source.readString();
        ticket_period = source.readString();
        ticket_amount = source.readString();
        purchased_date = source.readString();
        activated_date = source.readString();
        activated_station = source.readString();
        valid_date = source.readString();
        proof_document = source.readString();
        photo = source.readString();
        validated_count = source.readString();
        imei_device = source.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(ticket_id);
        dest.writeString(ticket_no);
        dest.writeString(ticket_code);
        dest.writeString(from_station);
        dest.writeString(to_station);
        dest.writeString(ticket_type);
        dest.writeString(no_of_tickets);
        dest.writeString(ticket_category);
        dest.writeString(ticket_period);
        dest.writeString(ticket_amount);
        dest.writeString(purchased_date);
        dest.writeString(activated_date);
        dest.writeString(activated_station);
        dest.writeString(valid_date);
        dest.writeString(proof_document);
        dest.writeString(photo);
        dest.writeString(validated_count);
        dest.writeString(imei_device);
    }

    public String getNo_of_tickets() {
        return no_of_tickets;
    }

    public void setNo_of_tickets(String no_of_tickets) {
        this.no_of_tickets = no_of_tickets;
    }

    public String getTicket_id() {
        return ticket_id;
    }

    public void setTicket_id(String ticket_id) {
        this.ticket_id = ticket_id;
    }

    public String getTicket_no() {
        return ticket_no;
    }

    public void setTicket_no(String ticket_no) {
        this.ticket_no = ticket_no;
    }

    public String getTicket_code() {
        return ticket_code;
    }

    public void setTicket_code(String ticket_code) {
        this.ticket_code = ticket_code;
    }

    public String getFrom_station() {
        return from_station;
    }

    public void setFrom_station(String from_station) {
        this.from_station = from_station;
    }

    public String getTo_station() {
        return to_station;
    }

    public void setTo_station(String to_station) {
        this.to_station = to_station;
    }

    public String getTicket_type() {
        return ticket_type;
    }

    public void setTicket_type(String ticket_type) {
        this.ticket_type = ticket_type;
    }

    public String getTicket_category() {
        return ticket_category;
    }

    public void setTicket_category(String ticket_category) {
        this.ticket_category = ticket_category;
    }

    public String getTicket_period() {
        return ticket_period;
    }

    public void setTicket_period(String ticket_period) {
        this.ticket_period = ticket_period;
    }

    public String getTicket_amount() {
        return ticket_amount;
    }

    public void setTicket_amount(String ticket_amount) {
        this.ticket_amount = ticket_amount;
    }

    public String getPurchased_date() {
        return purchased_date;
    }

    public void setPurchased_date(String purchased_date) {
        this.purchased_date = purchased_date;
    }

    public String getActivated_date() {
        return activated_date;
    }

    public void setActivated_date(String activated_date) {
        this.activated_date = activated_date;
    }

    public String getActivated_station() {
        return activated_station;
    }

    public void setActivated_station(String activated_station) {
        this.activated_station = activated_station;
    }

    public String getValid_date() {
        return valid_date;
    }

    public void setValid_date(String valid_date) {
        this.valid_date = valid_date;
    }

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

    public String getValidated_count() {
        return validated_count;
    }

    public void setValidated_count(String validated_count) {
        this.validated_count = validated_count;
    }

    public String getImei_device() {
        return imei_device;
    }

    public void setImei_device(String imei_device) {
        this.imei_device = imei_device;
    }
}


//{"id":"2",
// "from_station":"9",
// "to_station":"0",
// "ticket_type":"Platform",
// "ticket_category":null,
// "ticket_period":null,
// "ticket_number":"DDRP01002",
// "ticket_code":"2RD01D",
// "ticket_amount":"10",
// "purchased_on":"2015-08-18 07:08:49",
// "activated_on":"2015-08-18 23:27:42",
// "activated_station_code":"0",
// "valid_till":"2015-08-19 01:27:42",
// "status_date":"2015-08-18 23:27:42",
// "activatedDate":"2015-08-18 23:27:42"}

// {"message":"Success","msgcontent":{"requestParam":{"userid":"57","actiontype":"getTicketInfo","ticketnumber":"CSMUMADH1030ST"},"responseInfo":{"id":"267","passengerid":"33","from_station":"16","to_station":"1","ticket_type":"MonthlyPass","ticket_category":"I Class","ticket_period":"Quarterly","no_of_tickets":"1","ticket_code":"31A0S0","ticket_amount":"11","purchased_on":"2015-08-05 11:44:38","status_date":"0000-00-00 00:00:00","txnid":"133097395bb8f78ee64e","inactivatedDate":"0000-00-00 00:00:00","status":"Inactive"}}}
