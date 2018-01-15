/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import $1reco.Stroke;
import java.awt.RenderingHints;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JPanel;
import sun.java2d.pipe.RenderBuffer;

/**
 *
 * @author givelpa
 */
public class TracePanel extends JPanel{
    Stroke strokeOriginal;
    Stroke strokeNormalized;
    Color defaultColor = Color.GRAY;
    Color normalizedColor = Color.BLUE;
    Color startColor = Color.GREEN;
    Color endColor = Color.RED;
    int radius = 2;

    TracePanel(Stroke stroke) {
        super();
        this.strokeOriginal = stroke;
    }
    
    public void setNormalizedStroke(Stroke stroke){
        this.strokeNormalized = stroke;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D)g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
        
        
        if(!strokeOriginal.isEmpty()){
            for (int i = 0; i < strokeOriginal.size(); i++) {
                Point2D.Double p = strokeOriginal.getPoint(i);
                if(i == 0 ){
                    g2.setColor(startColor);
                }else if(i == strokeOriginal.size()-1){
                    g2.setColor(endColor);
                }else{
                    g2.setColor(defaultColor);
                }
                g2.fillOval((int)p.x-radius, (int)p.y-radius, radius*2, radius*2);
            }
            g2.setColor(startColor);
            Point2D.Double p = strokeOriginal.getPoint(0);
            g2.fillOval((int)p.x-radius, (int)p.y-radius, radius*2, radius*2);
        }
        
        if(strokeNormalized != null && !strokeNormalized.isEmpty()){
            for (int i = 0; i < strokeNormalized.size(); i++) {
                Point2D.Double p = strokeNormalized.getPoint(i);
                if(i == 0 ){
                    g2.setColor(startColor);
                }else if(i == strokeNormalized.size()-1){
                    g2.setColor(endColor);
                }else{
                    g2.setColor(normalizedColor);
                }
                g2.fillOval((int)p.x-radius, (int)p.y-radius, radius*2, radius*2);
            }
            g2.setColor(startColor);
            Point2D.Double p = strokeNormalized.getPoint(0);
            g2.fillOval((int)p.x-radius, (int)p.y-radius, radius*2, radius*2);
        }
    }    
}
