package com.hi.servicePkg;

import java.util.List;

/**
 * Created by a.akhondian on 6/21/2015.
 */
public class ChargeService implements IService {


    int chargeType;
    String mobileNumber;
    String cardNumber;
    String cardPss;

    int step = 0;
    private final String firstState = "Lotfan yeki az mabalegh ra entekhab konid\r\n1.1000\r\n2.2000\r\n3.5000\r\n4.10000\r\n5.20000";

    @Override
    public Response processCode(List<String> input) {
        String responseText = firstState;
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
                retVal = firstState;
        }
        return retVal;
    }

    private String confirmationStep(String selectedStep) {
        if (selectedStep.equals("1")) {
            int requestFromSwitch = requestFromSwitch(cardNumber, cardPss);
            if (requestFromSwitch != -1) {
                if (requestCharge(chargeType)) {
                    return "charge be shomare ferestade shod \r\n shomare peygiri : " + requestFromSwitch;
                } else
                    return "charge ba mablaghe morede nazar mojood nemibashad";
            } else
                return "ramz na motabar ast";
        } else
            return "shoma az kharid enseraf dadid";
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
        return "ersaale sharge " + chargeType + " be : " + mobileNumber + "\r\n dar soorate movafeghat 1 ra vaared namaeed";
    }

    private String cardStep(String selectedStep) {
        if (selectedStep.length() == 16 || selectedStep.length() == 19) {
            cardNumber = selectedStep;
            step++;
            return "ramze kart raa vared namaeed";
        }
        return firstState;
    }

    private String mobileStep(String selectedStep) {
        if (selectedStep.length() == 11) {
            mobileNumber = selectedStep;
            step++;
            return "lotfan shomare kart ra vaared konid:";
        }
        return firstState;
    }

    private String firstStep(String selectedStep) {
        try {
            int selected = Integer.parseInt(selectedStep);
            if (selected > 0 && selected < 5) {
                chargeType = selected;
                step++;
                return "lotfan shomare mobile khod ra vared konid:";
            }
        } catch (Exception ex) {
            //TODO log
        }
        return firstState;
    }
}
