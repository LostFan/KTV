package org.lostfan.ktv.view;

import org.lostfan.ktv.model.MainModel;
import org.lostfan.ktv.utils.ResourceBundles;
import org.lostfan.ktv.utils.ViewActionListener;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import javax.swing.*;

public class MainView extends FrameView {

    private enum MenuType {
        ENTITY_MENU,
        SERVICE_MENU,
        REPORT_MENU,
        FILE_MENU
    }

    private class MenuActionListener implements ActionListener {

        private MenuType menuType;

        public MenuActionListener(MenuType menuType) {
            this.menuType = menuType;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            String name = ((JMenuItem) e.getSource()).getName();
            if (this.menuType == MenuType.ENTITY_MENU && MainView.this.menuEntityActionListener != null) {
                MainView.this.menuEntityActionListener.actionPerformed(name);
            }

            if (this.menuType == MenuType.SERVICE_MENU && MainView.this.menuServiceActionListener != null) {
                MainView.this.menuServiceActionListener.actionPerformed(name);
            }

            if (this.menuType == MenuType.REPORT_MENU && MainView.this.menuReportActionListener != null) {
                MainView.this.menuReportActionListener.actionPerformed(name);
            }

            if (this.menuType == MenuType.FILE_MENU && MainView.this.menuFileActionListener != null) {
                MainView.this.menuFileActionListener.actionPerformed(name);
            }

        }
    }

    public static final int WIDTH = 1000;
    public static final int HEIGHT = 700;

    private ViewActionListener menuEntityActionListener;
    private ViewActionListener menuServiceActionListener;
    private ViewActionListener menuReportActionListener;
    private ViewActionListener menuFileActionListener;

    public MainView(MainModel model) {
        super(getGuiString("application.name"));
        getFrame().setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JMenuBar menuBar = new JMenuBar();
        getFrame().setJMenuBar(menuBar);
        JMenu fileMenu = new JMenu(getGuiString("menu.file"));
        JMenu entityMenu = new JMenu(getGuiString("menu.entities"));
        JMenu documentMenu = new JMenu(getGuiString("menu.documents"));
        JMenu reportMenu = new JMenu(getGuiString("menu.reports"));
        JMenu serviceMenu = new JMenu(getGuiString("menu.add"));
        menuBar.add(fileMenu);
        menuBar.add(entityMenu);
        menuBar.add(documentMenu);
        menuBar.add(serviceMenu);
        menuBar.add(reportMenu);

        MenuActionListener menuActionListener = new MenuActionListener(MenuType.FILE_MENU);
        for (String code : model.getFileItems()) {
            JMenuItem entityMenuItem = new JMenuItem(getGuiString(code));
            entityMenuItem.setName(code);
            fileMenu.add(entityMenuItem);
            entityMenuItem.addActionListener(menuActionListener);
        }

        JMenuItem exitMenuItem = new JMenuItem(getGuiString("menu.file.exit"));
        exitMenuItem.addActionListener(e ->
            MainView.this.getFrame().dispatchEvent(new WindowEvent(MainView.this.getFrame(), WindowEvent.WINDOW_CLOSING)));
        fileMenu.add(exitMenuItem);

        menuActionListener = new MenuActionListener(MenuType.ENTITY_MENU);
        for (String code : model.getEntityItems()) {
            JMenuItem entityMenuItem = new JMenuItem(getEntityString(code));
            entityMenuItem.setName(code);
            entityMenu.add(entityMenuItem);
            entityMenuItem.addActionListener(menuActionListener);
        }

        for (String code : model.getDocumentItems()) {
            JMenuItem entityMenuItem = new JMenuItem(getEntityString(code));
            entityMenuItem.setName(code);
            documentMenu.add(entityMenuItem);
            entityMenuItem.addActionListener(menuActionListener);
        }

        menuActionListener = new MenuActionListener(MenuType.SERVICE_MENU);
        for (String code : model.getServicesItems()) {
            JMenuItem entityMenuItem = new JMenuItem(getEntityString(code));
            entityMenuItem.setName(code);
            serviceMenu.add(entityMenuItem);
            entityMenuItem.addActionListener(menuActionListener);
        }

        menuActionListener = new MenuActionListener(MenuType.REPORT_MENU);
        for (String code : model.getReportsItems()) {
            JMenuItem entityMenuItem = new JMenuItem(getEntityString(code));
            entityMenuItem.setName(code);
            reportMenu.add(entityMenuItem);
            entityMenuItem.addActionListener(menuActionListener);
        }

        model.addObserver(args -> {
            setTitle(getGuiString("application.name") + " - " + getEntityString(model.getCurrentModel().getEntityNameKey()));
        });

        buildLayout();
        show();
    }

    private void buildLayout() {
        setSize(WIDTH, HEIGHT);

        getContentPanel().setLayout(new BorderLayout(10, 10));
        getContentPanel().setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
    }

    public void setInnerView(View view) {
        getContentPanel().removeAll();
        getContentPanel().add(view.getContentPanel());
        revalidate();
    }

    public void setMenuEntityActionListener(ViewActionListener menuEntityActionListener) {
        this.menuEntityActionListener = menuEntityActionListener;
    }

    public void setMenuServiceActionListener(ViewActionListener menuServiceActionListener) {
        this.menuServiceActionListener = menuServiceActionListener;
    }

    public void setMenuReportActionListener(ViewActionListener menuReportActionListener) {
        this.menuReportActionListener = menuReportActionListener;
    }

    public void setMenuFileActionListener(ViewActionListener menuFileActionListener) {
        this.menuFileActionListener = menuFileActionListener;
    }

    @Override
    protected void setTitle(String title) {
        this.getFrame().setTitle(title);
    }
}
