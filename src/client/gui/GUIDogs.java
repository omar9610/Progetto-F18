package client.gui;

import client.proxy.CustomerProxy;
import server.Dog;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashSet;

public class GUIDogs extends JFrame {
    final int WIDTH = 512;
    final int HEIGHT = 512;
    private Dimension screenSize = Toolkit.getDefaultToolkit ( ).getScreenSize ( );

    private CustomerProxy proxy;
    private String email;

    private HashSet<Dog> dogList;
    private JPanel panelContainer;
    private JPanel panelButton;
    private JPanel panelOut = new JPanel();

    private JButton addDogButton;

    private DogBox [] dogBoxes;

    private GridLayout gridLayout = new GridLayout(1,1);
    private JScrollPane panelScroll = new JScrollPane(panelOut);


    public GUIDogs(String email){
        setTitle("Your dogs");
        setSize(WIDTH, HEIGHT);
        setLocation((screenSize.width - getWidth()) / 2, (screenSize.height - getHeight()) / 2);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setResizable(false);
        setLayout(new BorderLayout());
        setVisible(true);


        this.email = email;
        proxy = new CustomerProxy(this.email);

        dogList = proxy.getDogList();
        dogBoxes = new DogBox[dogList.size()];

        initComponents();

    }

    private void initComponents(){
        int i = 0;
        //Dog d = null;
        panelOut.setLayout(new BorderLayout());
        panelContainer = new JPanel();

        panelContainer = new JPanel(gridLayout);
        panelContainer.setBorder(BorderFactory.createTitledBorder("Your Dogs: "));
        panelButton = new JPanel();
        addDogButton = new JButton("Add new dog");


        for (Dog dog:dogList) {
            //d = dog;
            dogBoxes[i] = new DogBox(dog.getName(), "Change info");
            dogBoxes[i].getInfoButton().addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    GUIDogInfo dogInfo = new GUIDogInfo(dog, email);
                    dogInfo.setVisible(true);
                }
            });

            panelContainer.add(dogBoxes[i]);

            gridLayout.setRows(gridLayout.getRows() + 1);

            i++;
        }

        panelButton.add(addDogButton);
        panelButton.setBorder(BorderFactory.createEmptyBorder(5,5,20,5));

        panelOut.add(panelContainer, BorderLayout.NORTH);
        panelOut.add(panelButton, BorderLayout.SOUTH);
        add(panelScroll);

    }
}


class DogBox extends JPanel{

    private JLabel nameLabel;
    private JButton infoButton;

    private JPanel panelDog;
    private JPanel panelLabel;
    private JPanel panelButton;




    public DogBox(String name, String button){
        nameLabel = new JLabel(name);
        infoButton = new JButton(button);

        panelDog = new JPanel();
        panelDog.setLayout(new BorderLayout());

        panelLabel = new JPanel();
        panelLabel.setLayout(new BorderLayout());

        panelButton = new JPanel();
        panelButton.setLayout(new GridLayout(1,1));

        panelLabel.add(nameLabel, BorderLayout.CENTER);
        panelLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5,20));

        panelButton.setLayout(new GridLayout(1,1));
        panelButton.setBorder(BorderFactory.createEmptyBorder(5,20,5, 20));
        panelButton.add(infoButton);


        panelDog.add(panelLabel, BorderLayout.WEST);
        panelDog.add(panelButton, BorderLayout.EAST);


        add(panelDog);






    }
    public JButton getInfoButton() {
        return infoButton;
    }




    }