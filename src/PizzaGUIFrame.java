import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class PizzaGUIFrame extends JFrame {

    Font bigHeaderFont, smallHeaderFont, sillyFont, defFont;
    Image pizzaIcon;
    ImageIcon warningIcon;
    final Color DARK_GRAY = new Color(38, 38, 38, 255);
    final Color BLACK = new Color(20, 20, 20, 255);
    final Color WHITE = new Color(215, 215, 215, 255);
    final Color YELLOW = new Color(255, 203, 33, 255);

    // UI components
    ButtonGroup crustGroup;
    JRadioButton[] crustArray;
    JComboBox<String> sizeBox;
    JCheckBox[] toppingsArray;
    JTextArea orderText;
    JScrollPane orderSP;
    Object[] quitOptions;
    JPanel mainPnl,
            crustPnl,
            sizePnl,
            toppingsPnl,
            receiptPnl,
            orderBtnPnl;

    public PizzaGUIFrame() {
        super();

        getFonts();
        getImages();

        setTitle("Pizza Time!");
        setIconImage(pizzaIcon);
        getRootPane().setBackground(BLACK);
        getContentPane().setBackground(BLACK);
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException |
                    UnsupportedLookAndFeelException e) {
            throw new RuntimeException(e);
        }

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));

        MatteBorder matteBorder = new MatteBorder(10, 10, 10, 10, Color.RED);
        TitledBorder titledBorder = BorderFactory.createTitledBorder(matteBorder, "Create your pizza");
        titledBorder.setTitleFont(bigHeaderFont);
        titledBorder.setTitleColor(YELLOW);
        titledBorder.setTitleJustification(TitledBorder.CENTER);
        titledBorder.setBorder(matteBorder);
        getRootPane().setBorder(titledBorder);

        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension screenSize = toolkit.getScreenSize();
        final int SCREEN_WIDTH = screenSize.width;
        final int SCREEN_HEIGHT = screenSize.height;

        setSize(SCREEN_WIDTH * 3 / 4, SCREEN_HEIGHT * 7 / 8);
        setLocation(SCREEN_WIDTH / 8, SCREEN_HEIGHT / 16);

        crustGroup = new ButtonGroup();
        crustArray = new JRadioButton[]{
                new JRadioButton("Thin"), new JRadioButton("Regular"), new JRadioButton("Deep-dish")
        };
        for (JRadioButton btn : crustArray) {
            btn.setFont(sillyFont);
            btn.setForeground(WHITE);
            btn.setOpaque(false);
            crustGroup.add(btn);
        }

        sizeBox = new JComboBox<>(new String[]{"Small", "Medium", "Large", "Super"});
        sizeBox.setFont(sillyFont);
        sizeBox.setForeground(DARK_GRAY);

        toppingsArray = new JCheckBox[]{
                new JCheckBox("Sprinkles"), new JCheckBox("Teeth"), new JCheckBox("Mushrooms"),
                new JCheckBox("Pineapple"), new JCheckBox("Cheese"), new JCheckBox("Mini pizzas")
        };
        for (JCheckBox cb : toppingsArray) {
            cb.setFont(sillyFont);
            cb.setForeground(WHITE);
            cb.setOpaque(false);
        }

        crustPnl = createOptionsPanel("Crust style");
        crustPnl.setLayout(new BoxLayout(crustPnl, BoxLayout.Y_AXIS));
        crustPnl.add(Box.createVerticalGlue());
        for (JRadioButton btn : crustArray) {
            crustPnl.add(btn);
            crustPnl.add(Box.createVerticalGlue());
        }

        sizePnl = createOptionsPanel("Size");
        sizePnl.add(sizeBox);

        toppingsPnl = createOptionsPanel("Toppings");
        toppingsPnl.setLayout(new BoxLayout(toppingsPnl, BoxLayout.Y_AXIS));
        for (JCheckBox cb : toppingsArray) {
            toppingsPnl.add(cb);
            toppingsPnl.add(Box.createVerticalGlue());
        }
        toppingsPnl.remove(toppingsPnl.getComponentCount() - 1);

        orderText = new JTextArea(10, 0);
        orderText.setMaximumSize(new Dimension(getWidth() / 3, getHeight() / 4));
        orderText.setEditable(false);
        orderText.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 20));
        orderText.setBackground(DARK_GRAY);
        orderText.setForeground(WHITE);
        orderText.setOpaque(true);
        orderText.setMargin(new Insets(5, 5, 5, 5));

        orderSP = new JScrollPane(orderText);
        orderSP.setOpaque(false);
        orderSP.getViewport().getView().setBackground(DARK_GRAY);
        orderSP.getViewport().setOpaque(false);
        orderSP.setMaximumSize(orderText.getPreferredSize());

        quitOptions = new Object[]{
                "Cancel", "OK"
        };

        JButton orderBtn = createBottomButton("Order", e -> showOrder()),
                clearBtn = createBottomButton("Clear", e -> resetOrder()),
                quitBtn = createBottomButton("Quit", e -> quitDialog());

        mainPnl = new JPanel();
        mainPnl.setAlignmentX(Box.CENTER_ALIGNMENT);
        mainPnl.setLayout(new BoxLayout(mainPnl, BoxLayout.X_AXIS));
        mainPnl.setOpaque(false);

        mainPnl.add(Box.createHorizontalGlue());
        mainPnl.add(crustPnl);
        mainPnl.add(Box.createHorizontalGlue());
        mainPnl.add(sizePnl);
        mainPnl.add(Box.createHorizontalGlue());
        mainPnl.add(toppingsPnl);
        mainPnl.add(Box.createHorizontalGlue());

        receiptPnl = new JPanel();
        receiptPnl.setLayout(new BoxLayout(receiptPnl, BoxLayout.X_AXIS));
        receiptPnl.setOpaque(false);
        receiptPnl.add(Box.createHorizontalGlue());
        receiptPnl.add(orderSP);
        receiptPnl.add(Box.createHorizontalGlue());

        orderBtnPnl = new JPanel();
        orderBtnPnl.setLayout(new BoxLayout(orderBtnPnl, BoxLayout.X_AXIS));
        orderBtnPnl.setOpaque(false);
        orderBtnPnl.add(Box.createHorizontalGlue());
        orderBtnPnl.add(orderBtn);
        orderBtnPnl.add(Box.createHorizontalGlue());
        orderBtnPnl.add(clearBtn);
        orderBtnPnl.add(Box.createHorizontalGlue());
        orderBtnPnl.add(quitBtn);
        orderBtnPnl.add(Box.createHorizontalGlue());

        add(Box.createVerticalGlue());
        add(mainPnl);
        add(Box.createVerticalGlue());
        add(receiptPnl);
        add(Box.createVerticalGlue());
        add(orderBtnPnl);

        // sets everything to its default, pre-order state
        resetOrder();
    }

    /**
     * Creates Font instances from files.
     */
    private void getFonts() {
        File directory = new File(System.getProperty("user.dir") + "\\fonts\\");
        File fire = new File(directory + "\\fire.ttf"),
                silly = new File(directory + "\\silly.ttf"),
                def = new File(directory + "\\default.ttf");

        try {
            bigHeaderFont = Font.createFont(Font.TRUETYPE_FONT, fire).deriveFont(Font.PLAIN, 50f);
            smallHeaderFont = Font.createFont(Font.TRUETYPE_FONT, fire).deriveFont(Font.PLAIN, 36f);
            sillyFont = Font.createFont(Font.TRUETYPE_FONT, silly).deriveFont(Font.PLAIN, 28f);
            defFont = Font.createFont(Font.TRUETYPE_FONT, def).deriveFont(Font.PLAIN, 20f);
        } catch (FontFormatException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Initializes all images to be used in the GUI.
     */
    private void getImages() {
        File directory = new File(System.getProperty("user.dir") + "\\images\\");
        File pizzaFile = new File(directory + "\\pizza.png");

        warningIcon = new ImageIcon(directory + "\\pizza_warning.png");

        try {
            pizzaIcon = ImageIO.read(pizzaFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Creates and sets the stats of an options panel.
     * @param title String to use in the TitledBorder
     * @return Finished JPanel
     */
    private JPanel createOptionsPanel(String title) {
        JPanel panel = new JPanel();
        panel.setPreferredSize(new Dimension((int) (getSize().getWidth() / 4), (int) (getSize().getHeight() / 4)));
        TitledBorder border = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(YELLOW,
                4), title);
        panel.setOpaque(false);
        border.setTitleFont(smallHeaderFont);
        border.setTitleColor(YELLOW);
        panel.setBorder(border);
        return panel;
    }

    /**
     * Creates a JButton to be used at the bottom of the page.
     * @param label String to be displayed on the button
     * @param actionListener ActionListener to be added to the button
     * @return Finished JButton
     */
    private JButton createBottomButton(String label, ActionListener actionListener) {
        JButton btn = new JButton(label);
        btn.setBackground(YELLOW);
        btn.setContentAreaFilled(false);
        btn.setOpaque(true);
        btn.setMargin(new Insets(5, 5, 5, 5));
        btn.setFont(defFont);
        btn.addActionListener(actionListener);
        return btn;
    }

    // constants for use with order-printing methods
    final int COLS = 37;
    final String BLANK = "",
            THICK_LINE = "=".repeat(COLS),
            THIN_LINE = "-".repeat(COLS) + '\n',
            HEADER = String.format(THICK_LINE +
                    String.format("%n%12s%12s%n", BLANK, "ORDER SUMMARY") +
                    THIN_LINE),
            ORDER_ROW_FORMAT = "%11s:   %11s   $%-6.2f%n";

    /**
     * Shows the receipt of the current order. Called when user presses the 'Order' button.
     */
    private void showOrder() {

        String crust = "ERROR";
        for (JRadioButton btn : crustArray)
            if (Objects.equals(btn.getModel(), crustGroup.getSelection()))
                crust = btn.getText();

        orderText.selectAll();
        orderText.replaceSelection(HEADER +
                String.format("%11s:   %11s%n", "Crust", crust));

        String size = Objects.requireNonNull(sizeBox.getSelectedItem()).toString();
        double sizePrice = switch (size) {
            case "Small" -> 8;
            case "Medium" -> 12;
            case "Large" -> 16;
            case "Super" -> 20;
            default -> 0.0;
        };
        orderText.append(String.format(ORDER_ROW_FORMAT, "Size", size, sizePrice));

        double toppingsPrice = 0;
        boolean hasToppings = false;
        for (JCheckBox cb : toppingsArray) {
            if (cb.isSelected()) {
                if (hasToppings)
                    orderText.append(String.format("%11s    %11s   $%-6.2f%n", BLANK, cb.getText(), 1.00));
                else {
                    orderText.append(String.format(ORDER_ROW_FORMAT, "Toppings", cb.getText(), 1.00));
                    hasToppings = true;
                }
                toppingsPrice += 1;
            }
        }
        if (hasToppings)
            orderText.append(String.format("%11s    %11s:  $%-6.2f%n", BLANK, "Total", toppingsPrice));
        else
            orderText.append(String.format(ORDER_ROW_FORMAT, "Toppings", BLANK, 0.0));

        double subtotal = sizePrice + toppingsPrice;
        double tax = 0.07 * subtotal;

        orderText.append(String.format(ORDER_ROW_FORMAT + ORDER_ROW_FORMAT,
                "Sub-total", BLANK, subtotal,
                "Tax", BLANK, tax) +
                THIN_LINE +
                String.format(ORDER_ROW_FORMAT, "Total", BLANK, subtotal + tax) +
                THICK_LINE);

        // tricks the JScrollPane into scrolling all the way up instead of all the way down
        orderText.select(0, 0);
    }

    /**
     * Clears all order options and sets the order text to be a default blank receipt.
     */
    private void resetOrder() {
        crustGroup.setSelected(crustArray[1].getModel(), true);
        sizeBox.setSelectedIndex(0);
        for (JCheckBox cb : toppingsArray)
            cb.setSelected(false);

        // replaces current receipt with a blank one
        orderText.selectAll();
        orderText.replaceSelection(HEADER +
                String.format("%11s:   %11s%n" +
                                ORDER_ROW_FORMAT + ORDER_ROW_FORMAT + "\n\n" +
                                ORDER_ROW_FORMAT + ORDER_ROW_FORMAT,
                        "Crust", BLANK, "Size", BLANK, 0.0,
                        "Toppings", BLANK, 0.0, "Sub-total", BLANK, 0.0, "Tax", BLANK, 0.0) +
                THIN_LINE +
                String.format(ORDER_ROW_FORMAT, "Total", BLANK, 0.0) +
                THICK_LINE);

        // tricks the JScrollPane into scrolling all the way up instead of all the way down
        orderText.select(0, 0);
    }

    /**
     * Shows option dialog asking for confirmation whenever the user presses the quit button.
     */
    private void quitDialog() {
        if (JOptionPane.showOptionDialog(this, "Would you like to quit?",
                "Confirm", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, warningIcon,
                quitOptions, quitOptions[0]) == 1) {
            dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
        }
    }
}