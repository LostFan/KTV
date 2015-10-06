package org.lostfan.ktv.controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import org.lostfan.ktv.model.ModelBase;
import org.lostfan.ktv.view.SearchViewBase;

/**
 * Created by Ihar_Niakhlebau on 30-Sep-15.
 */
public class SearchController {

    private SearchViewBase view;
    private ModelBase modelBase;

    public SearchController(SearchViewBase view) {
        this.view = view;

        this.view.addAddActionListener(new AddActionListener());
        this.view.addFindActionListener(new FindActionListener());
        this.view.addCancelActionListener(new CancelActionListener());
//        this.view.addReportActionListener(new ReportActionListener());
    }
    private class FindActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            System.out.println("Find action");
        }
    }

    private class AddActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {;
        }
    }

    private class CancelActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            view.closeForm();
        }
    }

//    private class DocumentActionListener implements ActionListener {
//        @Override
//        public void actionPerformed(ActionEvent e) {
//            CatalogsView view = new CatalogsView();
//            CatalogsController controller = new CatalogsController(view);
//        }
//    }
//
//    private class ReportActionListener implements ActionListener {
//        @Override
//        public void actionPerformed(ActionEvent e) {
//            System.out.println("Report action");
//        }
//    }
}
