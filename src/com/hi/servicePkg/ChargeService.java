package com.hi.servicePkg;

import com.hi.utility.Utility;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by a.akhondian on 6/21/2015.
 */
public class ChargeService implements IService {

    int lang;
    int chargeType;
    String clientMobileNo;
    String mobileNumber;
    String cardNumber;
    String cardPss;
    Utility utility;
    int step = 0;

    @Override
    public void init(int lang, String mobileNumber) {
        this.lang = lang;
        this.clientMobileNo = mobileNumber;
        utility = Utility.instance();
    }

    @Override
    public Response processCode(List<String> input) {
        String responseText = utility.getMessage("Charge_First_Message", null, lang);
        Response response = new Response();
        response.setIsLast("0");
        response.setResponseLang("1");
        while (input.size() > 0) {
            String selectedStep = input.remove(0);
            responseText = stepProcess(selectedStep);
        }
        response.setResponseText(responseText);
        return response;
    }

    private String stepProcess(String selectedStep) {
        String retVal = "";
        switch (step) {
            case 0:
                retVal = firstStep(selectedStep);
                break;
            case 1:
                retVal = mobileStep(selectedStep);
                break;
            case 2:
                retVal = cardStep(selectedStep);
                break;
            case 3:
                retVal = passStep(selectedStep);
                break;
            case 4:
                retVal = confirmationStep(selectedStep);
                break;
            default:
                retVal = utility.getMessage("Charge_First_Message", null, lang);
        }
        return retVal;
    }

    private String confirmationStep(String selectedStep) {
        List<String> vars = new ArrayList<>();
        if (selectedStep.equals("1")) {
            int requestFromSwitch = requestFromSwitch(cardNumber, cardPss);
            if (requestFromSwitch != -1) {
                if (requestCharge(chargeType)) {
                    vars.add(mobileNumber);
                    vars.add(requestFromSwitch + "");
                    String charge_send = utility.getMessage("Charge_Send", vars, lang);
                    return charge_send;
                } else
                    return utility.getMessage("Requested_Charge_IS_Not_Available", null, lang);
            } else
                return utility.getMessage("Invalid_Password", null, lang);
        } else
            return utility.getMessage("Canceled_Purchase", null, lang);
    }

    private boolean requestCharge(int chargeType) {
        //TODO must send a topup charge request to irancell
        return true;
    }

    private int requestFromSwitch(String cardNumber, String cardPss) {
        //TODO must send a buy request to switch
        return 123123;
    }

    private String passStep(String selectedStep) {

        cardPss = selectedStep;
        step++;
        List<String> vars = new ArrayList<>();
        switch (chargeType) {
            case 1:
                vars.add("10000");
                break;
            case 2:
                vars.add("20000");
                break;
            case 3:
                vars.add("50000");
                break;
            case 4:
                vars.add("100000");
                break;
            case 5:
                vars.add("200000");
                break;
        }
        vars.add(mobileNumber);
        return utility.getMessage("Charge_Confirmation_Msg", vars, lang);
    }

    private String cardStep(String selectedStep) {
        if (selectedStep.length() == 16 || selectedStep.length() == 19) {
            cardNumber = selectedStep;
            step++;
            return utility.getMessage("Enter_Card_Number", null, lang);
        }
        return utility.getMessage("Charge_First_Message", null, lang);
    }

    private String mobileStep(String selectedStep) {
        if (selectedStep.length() == 11) {
            mobileNumber = selectedStep;
            step++;
            return utility.getMessage("Enter_Card_Password",null,lang);
        }
        return utility.getMessage("Charge_First_Message", null, lang);
    }

    private String firstStep(String selectedStep) {
        try {
            int selected = Integer.parseInt(selectedStep);
            if (selected > 0 && selected < 5) {
                chargeType = selected;
                step++;
                return utility.getMessage("Enter_Mobile_Number",null,lang);
            }
        } catch (Exception ex) {
            //TODO log
        }
        return utility.getMessage("Charge_First_Message", null, lang);
    }
}
