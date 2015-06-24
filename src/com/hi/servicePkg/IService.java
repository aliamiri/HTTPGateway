package com.hi.servicePkg;

import java.util.List;

/**
 * Created by a.akhondian on 6/21/2015.
 */
public interface IService {

    public void init(int lang,String mobileNo);
    public Response processCode(List<String> input);
}
