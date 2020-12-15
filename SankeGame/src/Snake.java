import javax.swing.*;
import java.awt.*;

public class Snake {
    public static void main(String[] args) {
        JFrame frame = new JFrame();
        frame.setBounds(200, 50, 800, 600);
        frame.setResizable(false);// 设置窗口不可更改
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(new Panel());
        frame.setVisible(true);// 使窗口可视
    }
}
