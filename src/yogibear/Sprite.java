/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package yogibear;

import java.awt.Graphics2D;
import java.awt.Image;

/**
 *
 * @author rde3cs
 */
public class Sprite {
    private final Image image;
    private final int imageWidth;
    private final int imageHeight;
    private final int imageXOffset;
    private final int imageYOffset;
    
    public Sprite(Image image, int imageWidth, int imageHeight, int xOffset, int yOffset){
        this.image = image;
        this.imageWidth = imageWidth;
        this.imageHeight = imageHeight;
        this.imageXOffset = xOffset;
        this.imageYOffset = yOffset;
    }
    
    public void draw(Graphics2D g, int posX, int posY){
        g.drawImage(image, posX + imageXOffset, posY + imageYOffset, this.imageWidth, this.imageHeight, null);
    }
}
