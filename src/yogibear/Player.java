/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package yogibear;

import java.awt.Graphics2D;

/**
 *
 * @author rde3cs
 */
public class Player extends Entity {
    private Sprite image;
    int posX;
    int posY;
    
    public Player(int x, int y, int width, int height, String id, int windowWidth, int windowHeight, Sprite image){
        super(x, y, width, height, id);
        this.image = image;
        this.posX = windowWidth / 2 - width / 2;
        this.posY = windowHeight / 2 - height / 2;
    }
    
    public int getScreenX(){return posX;}
    public int getScreenY(){return posY;}
    
    @Override
    public void draw(Graphics2D g, int WorldX, int WorldY){
        image.draw(g, this.posX, this.posY);
        g.drawRect(this.x + WorldX, this.y + WorldY, 50, 50);
    }
    
}
