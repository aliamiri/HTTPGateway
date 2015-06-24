package com.hi.sessionPkg;

import com.hi.servicePkg.IService;

import java.util.List;

/**
 * Created by a.akhondian on 6/14/2015.
 */
public class Session {

    protected String mobileNo;
    protected List<String> inputs;
    protected IService service;
    protected int lang = 2;

    public IService getService() {
        return service;
    }

    public List<String> getInputs() {
        return inputs;
    }

    public String getMobileNo() {
        return mobileNo;
    }

}
