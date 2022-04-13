package com.points.autorepar.bean;


public class UpdateCarcodeEvent {
    private String  code;
    public UpdateCarcodeEvent(String _code) {
        code = _code;
    }
    public String getCode(){
        return code;
    }
}
