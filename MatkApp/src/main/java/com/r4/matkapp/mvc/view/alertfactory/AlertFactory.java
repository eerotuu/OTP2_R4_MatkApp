/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.r4.matkapp.mvc.view.alertfactory;

/**
 *
 * @author Eero
 */
public interface AlertFactory {
    abstract public void createAlert(String header, String content);
    abstract public void createAlert(String title, String header, String content);
}
