package org.lostfan.ktv.controller;

import org.lostfan.ktv.dao.ServiceDAO;
import org.lostfan.ktv.dao.impl.hsqldb.HsqldbServiceDAO;
import org.lostfan.ktv.model.FieldSearchCriterion;
import org.lostfan.ktv.model.Model;
import org.lostfan.ktv.view.SearchViewBase;
import org.lostfan.ktv.view.TableViewBase;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.stream.Collectors;

public class EntityController {

    private Model model;
    private TableViewBase view;
    private ServiceDAO serviceDAO = new HsqldbServiceDAO();

    public EntityController(Model model, TableViewBase view) {
        this.model = model;
        this.view = view;

        this.view.addFindActionListener(new FindActionListener());
        this.view.addAddActionListener(new AddActionListener());
        this.view.addChangeActionListener(new ChangeActionListener());
        this.view.addDeleteActionListener(new DeleteActionListener());
    }

    private class FindActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            SearchViewBase searchViewBase = new SearchViewBase(model);
            searchViewBase.addFindActionListener(new SearchFindActionListener(searchViewBase));
        }
    }

    private class SearchFindActionListener implements ActionListener {
        SearchViewBase searchViewBase;
        public SearchFindActionListener(SearchViewBase searchViewBase) {
            this.searchViewBase = searchViewBase;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            List<FieldSearchCriterion> criteria = this.searchViewBase.getSearchCriteria();
            model.setSearchCriteria(criteria);
        }
    }

    private class AddActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            System.out.println("Add action");
        }
    }

    private class ChangeActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            System.out.print("Change action.");
            int selectedIndex = view.getSelectedIndex();
            if (selectedIndex == -1) {
                System.out.println("No selection");
            } else {

                System.out.println("Service name:" + model.getList().get(selectedIndex));
            }
        }
    }

    private class DeleteActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            System.out.print("Delete action.");
            int selectedIndex = view.getSelectedIndex();
            if (selectedIndex == -1) {
                System.out.println("No selection");
            } else {
                System.out.println("Service name:" + model.getList().get(selectedIndex));
            }
        }
    }
}
