package com.app.expensetracker.model;

public class CategoryModel {

    String cat_id;
    String cat_name;
    String cat_iconname;
    String trans_type;


    public String getCat_id() {
        return cat_id;
    }

    public void setCat_id(String cat_id) {
        this.cat_id = cat_id;
    }

    public String getCat_name() {
        return cat_name;
    }

    public void setCat_name(String cat_name) {
        this.cat_name = cat_name;
    }

    public String getCat_iconname() {
        return cat_iconname;
    }

    public void setCat_iconname(String cat_iconname) {
        this.cat_iconname = cat_iconname;
    }

    public String getTrans_type() {
        return trans_type;
    }

    public void setTrans_type(String trans_type) {
        this.trans_type = trans_type;
    }
}
