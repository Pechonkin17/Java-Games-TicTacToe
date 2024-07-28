import javax.swing.JFrame;

public class Frame extends JFrame {
    private Panel panel;

    Frame() {
        panel = new Panel();

        setTitle("Tic-Tac-Toe");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().add(panel);
        setResizable(false);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }
}
