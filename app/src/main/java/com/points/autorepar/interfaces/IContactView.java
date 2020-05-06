package com.points.autorepar.interfaces;

import com.points.autorepar.bean.ContactSim;

import java.util.List;

/**
 * @author Jenly <a href="mailto:jenly1314@gmail.com">Jenly</a>
 * @since 2017/3/31
 */

public interface IContactView {

    void getListContact(List<ContactSim> list);

    void showLetter(String letter);

    void hideLetter();
}

