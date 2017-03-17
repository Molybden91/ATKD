package Pahan.util;

import javax.swing.*;

/**
 * Created by Lab22 on 31.08.2016.
 */
public class SwingConsole {
    public static void run(final JFrame f, final  int width,final int height){
        SwingUtilities.invokeLater(new Runnable(){
            public void run(){
                f.setTitle(f.getClass().getSimpleName());
                f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                f.setSize(width,height);
                f.setVisible(true);
            }
        });
    }
}
