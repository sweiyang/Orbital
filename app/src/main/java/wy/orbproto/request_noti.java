package wy.orbproto;

/**
 * Created by weiyang on 6/20/2017.
 */

public class request_noti {
    private String group_name;
    private String requested_person;
    private String status;

    public request_noti(){

    }
    public request_noti(String group_name, String person_requesting, String status) {
        this.group_name = group_name;
        this.requested_person = person_requesting;
        this.status = status;
    }

    public String getGroup_name() {
        return group_name;
    }

    public String getPerson_requesting() {
        return requested_person;
    }


    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
