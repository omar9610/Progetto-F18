package client.gui;


import client.proxy.DogSitterProxy;
import enumeration.CalendarState;
import server.Assignment;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class GUIDailyAssignmentDogsitter extends GUIDailyAssignments {

    private DogSitterProxy dogSitterProxy;
    HashMap<Integer, Assignment> todayAssigment = new HashMap<>();

    public GUIDailyAssignmentDogsitter(CalendarState cs, String email, Date todayDate) {
        super(cs, email, todayDate);

        this.email = email;
        this.todayDate = todayDate;
        dogSitterProxy = new DogSitterProxy(email);
        this.listAssigment = dogSitterProxy.getAssignmentList();
        initComponents(cs);


        if (cs.equals(CalendarState.NORMAL)) {


            int n = 0;

            for (Integer i : listAssigment.keySet()) {


                Assignment a = null;
                a = listAssigment.get(i);
                Date dateStart = a.getDateStart();
                Date dateEnd = a.getDateEnd();
                SimpleDateFormat date1 = new SimpleDateFormat("dd/MM/yyyy");
                String dateString1 = date1.format(dateStart);
                String dateStringEnd1 = date1.format(dateEnd);
                SimpleDateFormat date2 = new SimpleDateFormat("dd/MM/yyyy");
                String dateString2 = date1.format(todayDate);
                String dateStringEnd2 = date1.format(todayDate);
                dateString1.equals(dateString2);
                dateStringEnd1.equals(dateStringEnd2);


                if (dateString1.equals(dateString2) || (dateStringEnd1.equals(dateStringEnd2))) {

                    todayAssigment.put(n, a);
                }

                n++;

            }


            labelDescription = new JLabel[todayAssigment.size()];
            button = new JButton[todayAssigment.size()];
            infoPanel = new JPanel[todayAssigment.size()];

/*
            if (todayAssigment.isEmpty()) {

                lb = new JLabel(" There aren't assignments today ", SwingConstants.CENTER);

                p.add(lb);

            } else {*/

                int j = 0;

                for (Integer i : todayAssigment.keySet()) {
                    Assignment a = null;
                    String labelString = "";
                    a = todayAssigment.get(i);
                    String nameCostumer = dogSitterProxy.getCustomerNameOfAssignment(a.getCode());
                    String surnameCostumer = dogSitterProxy.getCustomerSurnameOfAssignment(a.getCode());
                    labelString = "<html>" + "Assignment with " + nameCostumer + " " + surnameCostumer + "</html>";


                    labelDescription[j] = new JLabel(labelString);
                    button[j] = new JButton("More info");

                    ActionListener showInfo = new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {

                            // TODO - interfaccia di Riccardo


                            GUIAssignmentInformationDogsitter assignmentInfo = new GUIAssignmentInformationDogsitter(todayAssigment.get(i), email, guiDailyAssignments);
                            assignmentInfo.setVisible(true);


                        }
                    };


                    button[j].addActionListener(showInfo);


                    createPanelOrder(j);
                    gridLayout.setRows(gridLayout.getRows() + 1);
                    j++;


                }
                scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
                getContentPane().add(scroll);

            }
        }

    }
