/**
 * @author - Nirmoho Banerjee
 * @andrew_id - nirmohob
 */
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;

/**
 * Game class, extends JFrame and implements ActionListener.
 * Generates Whack a mole game.
 *
 */
public class Game extends JFrame  {

    /**
     * Serial Version.
     */
    private static final long serialVersionUID = -295948224266582550L;
    /**
     * Instance variable for keeping score.
     */
    private static int score = 0;
    /**
     * Instance variable for Main Panel.
     */
    private static JPanel paneMain = new JPanel();
    /**
     * Instance variable for Play button.
     */
    private static JButton play = new JButton("Start");
    /**
     * Instance variable for Time left Label.
     */
    private static JLabel timeLeft = new JLabel("Time Remaining:");
    /**
     * Instance variable for Time Left Text Area.
     */
    private static JTextArea tlArea = new JTextArea(10, 5);
    /**
     * Instance variable for Score Label.
     */
    private static JLabel scoreLabel = new JLabel("Score:");
    /**
     * Instance variable for Time Left Text Area.
     */
    private static JTextArea scoreArea = new JTextArea();
    /**
     * Instance variable for internal pane.
     */
    private static JPanel internalPane = new JPanel();
    /**
     * Variable Array for adding all the buttons to hold moles.
     */
    private static JButton[] moles = new JButton[54];
    /**
     * String for indicating Mole is on.
     */
    private static final String MOLE_PRESENT = ":-)";
    /**
     * Color to indicate that button is active.
     */
    private static final Color ON_COLOR = Color.GREEN;
    /**
     * String to indicate button is hit.
     */
    private static final String MOLE_WHACKED = ":-(";
    /**
     * Color to indicate that button is hit.
     */
    private static final Color WHACK_COLOR = Color.RED;
    /**
     * Color to indicate that button is inactive.
     */
    private static final Color OFF_COLOR = Color.LIGHT_GRAY;
    /**
     * Color to indicate that button is inactive.
     */
    private static final String MOLE_OFF = "";
    /**
     * Instance variable for time count.
     */
    private static int timeCount = 20;
    /**
     * Instance variable for random object, to generate random ints.
     */
    private static Random random = new Random();

    /**
     * Time Thread Class for initiating the timer.
     * @author Nirmoho Banerjee
     * @andrew_id nirmohob
     */
    public static class TimerThread extends Thread  {
        /**
         * Run Method for timer.
         */
        @Override
        public void run()   {
            try {
                while (timeCount > -1)  {
                    Thread.sleep(1000);
                    if (timeCount < 10)  {
                        tlArea.setText("00:0" + timeCount);
                    }   else    {
                        tlArea.setText("00:" + timeCount);
                    }

                    System.out.println("Time Left - " + timeCount);
                    timeCount--;
                }
            }   catch (InterruptedException e)  {
                throw new AssertionError(e);
            }

           //Disable all moles once time is over.
            for (int i = 0; i < moles.length; i++)    {
                moles[i].setEnabled(false);
            }

            try {
                Thread.sleep(5000);
            }   catch (InterruptedException e)  {
                throw new AssertionError(e);
            }

            timeCount = 20;
            score = 0;
            tlArea.setText("00:" + timeCount);
            scoreArea.setText("" + score);

            //Enable play button again
            play.setEnabled(true);
        }
    }

    /**
     * MolesThread Class - Class to generate moles in a random order.
     * @author Nirmoho Banerjee
     * @andrew_id nirmohob
     */
    public static class MolesThread extends Thread  {
        /**
         * Instance variable for button.
         */
        private JButton button;

        /**
         * Constructor to initiate mole button.
         * @param moleButton - reference to mole button.
         */
        public MolesThread(JButton moleButton)  {
            this.button = moleButton;

            button.addActionListener(new ActionListener()   {
                @Override
                public void actionPerformed(ActionEvent e)  {
                    if (button.getText().equals(MOLE_PRESENT)) {
                        score++;
                        button.setBackground(WHACK_COLOR);
                        button.setText(MOLE_WHACKED);
                        scoreArea.setText("" + score);
                    }
                }
            });
        }

        /**
         * Override run method for thread.
         */
        @Override
        public void run()   {

            while (timeCount > 0)   {

               /*Synchronize the button, for handling multiple instances
                of the same button. */
                synchronized (button)    {

                    //Enable mole if button is off
                    if (!button.getText().equals("")) {
                        button.setText(MOLE_OFF);
                        button.setBackground(OFF_COLOR);
                        try {
                            Thread.sleep(2000);
                        }   catch (InterruptedException e)   {
                            e.printStackTrace();
                        }
                    }   else    {
                        button.setText(MOLE_PRESENT);
                        button.setBackground(ON_COLOR);

                    }
                }
                //Keep Mole up for 0.5 to 4 seconds
                int randomSleep = 500 * (1 + random.nextInt(8));
                try {
                    Thread.sleep(randomSleep);
                }   catch (InterruptedException e)   {
                    e.printStackTrace();
                }

            }

            //Reset Button if timer runs out
            if (timeCount <= 0)  {
                button.setText("");
                button.setBackground(OFF_COLOR);
            }
        }
    }

    /**
     * Default Constructor for class Game.
     */
    public Game()   {

        //Generate the interface
        paneMain.setLayout(null);
        setTitle("Whack a Mole");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(640, 480);

        play.setBounds(110, 20, 60, 20);
        paneMain.add(play);
        play.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e)  {
                System.out.println("Game Started");

                //Enable the moles
                for (int i = 0; i < moles.length; i++)    {
                    moles[i].setEnabled(true);
                }

                //Disable play button
                play.setEnabled(false);

                //Creating and enabling the timer thread
                TimerThread t = new TimerThread();
                t.start();

                //Variable for mole threads
                MolesThread[] mt = new MolesThread[moles.length];

                //Generating the mole threads
                for (int i = 0; i < moles.length; i++)  {
                    int randomNumber = random.nextInt(moles.length);
                    JButton button = moles[randomNumber];
                    mt[i] = new MolesThread(button);
                    mt[i].start();
                }
            }
        });

        timeLeft.setBounds(190, 20, 140, 20);
        paneMain.add(timeLeft);

        tlArea.setBounds(305, 20, 60, 20);
        paneMain.add(tlArea);

        scoreLabel.setBounds(400, 20, 90, 20);
        paneMain.add(scoreLabel);

        scoreArea.setBounds(450, 20, 60, 20);
        paneMain.add(scoreArea);

        scoreArea.setText("0");
        scoreArea.setAlignmentX(SwingConstants.CENTER);
        scoreArea.setEditable(false);
        tlArea.setText("00:00");
        tlArea.setAlignmentX(CENTER_ALIGNMENT);
        tlArea.setAlignmentY(CENTER_ALIGNMENT);
        tlArea.setEditable(false);

        //Separate Panel for holding the mole buttons.
        internalPane.setLayout(new FlowLayout());
        internalPane.setBackground(Color.BLACK);
        internalPane.setBounds(60, 80, 520, 320);
        for (int i = 0; i < moles.length; i++)   {
            moles[i] = new JButton(MOLE_OFF);
            moles[i].setBackground(OFF_COLOR);
            moles[i].setOpaque(true);
            moles[i].setEnabled(false);
            internalPane.add(moles[i]);
        }
        paneMain.add(internalPane);

        add(paneMain);
        setVisible(true);

    }
    /**
     * Main method for Game class.
     * @param args - Command Line Arguments.
     */
    public static void main(String[] args)   {
        new Game();
    }
}
