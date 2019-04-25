/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.r4.matkapp.mvc.controller;

import com.r4.matkapp.MainApp;
import com.r4.matkapp.mvc.model.Expense;
import com.r4.matkapp.mvc.model.Group;
import com.r4.matkapp.mvc.model.User;
import com.r4.matkapp.mvc.view.alertfactory.ConfirmationAlert;
import com.r4.matkapp.mvc.view.alertfactory.ConfirmationFactory;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 *
 * @author Eero
 */
public class AltGroupSceneController implements Initializable {

    @FXML
    BorderPane root;
    @FXML
    ProgressBar budgetBar;
    @FXML
    Label budgetIndicator, groupNameLabel;

    private Group selectedGroup;
    private Node navMenu;

    private RootSceneController parentController;

    protected AltGroupSceneController(RootSceneController ctrl, Group g) {
        parentController = ctrl;
        selectedGroup = g;
        UserController.groupDAO.refresh(selectedGroup);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        updateBudgetProgress();
        groupNameLabel.setText(getSelectedGroup().getGroup_name());
        navMenu = root.getCenter();

    }

    @FXML
    private void showGroupProperties() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/GroupSettingsScene.fxml"));
            loader.setController(new GroupSettingsSceneController(this, selectedGroup));

            Stage stage = new Stage();
            stage.setTitle(selectedGroup.getGroup_name() + " Asetukset");
            stage.setScene(new Scene(loader.load()));
            
            // set owner and modality
            stage.initOwner(MainApp.getWindow());
            // block evets from beign delivered to its entire owner window hierarchy
            stage.initModality(Modality.WINDOW_MODAL);
            stage.show();

        } catch (IOException ex) {
            Logger.getLogger(AltGroupSceneController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    private void openExpenseWizard() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ExpenseWizard.fxml"));
            loader.setController(new ExpenseWizardController(this, selectedGroup));

            Stage stage = new Stage();
            stage.setTitle("Luo uusi kulu");
            stage.setScene(new Scene(loader.load()));
            
            // set owner and modality
            stage.initOwner(MainApp.getWindow());
            // block evets from beign delivered to its entire owner window hierarchy
            stage.initModality(Modality.WINDOW_MODAL);
            stage.show();
        } catch (IOException ex) {
            Logger.getLogger(AltGroupSceneController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
    @FXML
    private void openGroupInvitationScene(){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/GroupInvitationScene.fxml"));
            loader.setController(new GroupInvitationSceneController(this, selectedGroup));
            
            Stage stage = new Stage();
            stage.setTitle("Kutsu uusi jäsen");
            stage.setScene(new Scene(loader.load()));
            stage.initOwner(MainApp.getWindow());
            stage.initModality(Modality.WINDOW_MODAL);
            stage.show();
        } catch (IOException ex) {
            Logger.getLogger(AltGroupSceneController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @FXML
    private void setNavMenu() {
        root.setCenter(navMenu);
    }

    @FXML
    private void setExpenseListScene() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ExpensesList.fxml"));
        loader.setController(new ExpensesListController(this));
        try {
            root.setCenter(loader.load());
        } catch (IOException ex) {
            Logger.getLogger(AltGroupSceneController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @FXML
    private void leaveGroup() {
       ConfirmationFactory confirmation = new ConfirmationAlert();
       boolean result = confirmation.createAlert(null, "Poistutaako ryhmästä " + selectedGroup.getGroup_name() + "?");
       if(result) {
           User u = UserController.getLoggedUser();
           u.getGroup().remove(selectedGroup);
           UserController.dao.update(u);
           parentController.setHomeScene();
           updateGroupData();
       }
    }

    // parannettavaa..
    protected void updateGroupData() {
        
        UserController.groupDAO.refresh(selectedGroup);
        if (groupNameLabel.getText() != selectedGroup.getGroup_name()) {
            groupNameLabel.setText(getSelectedGroup().getGroup_name());
            parentController.updateGroupList();
        }

        updateBudgetProgress();

    }

    private void updateBudgetProgress() {
        Set<Expense> expenses = selectedGroup.getExpenses();
        double spent = 0;
        for (Expense e : expenses) {
            spent += e.getExpense_amount();
        }
        double progress = spent / selectedGroup.getBudget();

        budgetIndicator.setText(String.format("%.1f", progress * 100) + "% käytetty budjetista");
        budgetBar.setProgress(progress);
    }

    /**
     * @return the selectedGroup
     */
    public Group getSelectedGroup() {
        return selectedGroup;
    }

}
