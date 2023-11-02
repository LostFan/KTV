package org.lostfan.ktv;

import org.lostfan.ktv.controller.MainController;
import org.lostfan.ktv.dao.DAOFactory;
import org.lostfan.ktv.dao.impl.postgre.PostGreDaoFactory;
import org.lostfan.ktv.model.MainModel;
import org.lostfan.ktv.utils.ConnectionManager;
import org.lostfan.ktv.utils.ResourceBundles;
import org.lostfan.ktv.view.MainView;

import javax.swing.*;

public class Application {

    public static void main(String[] args) {
        try {
            DAOFactory.setDefaultDAOFactory(new PostGreDaoFactory());
        } catch (ApplicationException ex) {
            // TODO: show this message over the main view
            JOptionPane.showMessageDialog(null, ex.getMessage(),
                    "Application Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Close connections and save all the changes
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                ConnectionManager.getManager().close();
            }
        });

        MainModel model = new MainModel();
        MainView view = new MainView(model);
        MainController controller = new MainController(model, view);
    }
}
