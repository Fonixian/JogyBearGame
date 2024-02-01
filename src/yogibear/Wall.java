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
public class Wall extends Entity {
    private final Sprite image;
    
    public Wall(int x, int y, int width, int height, String id, Sprite image){
        super(x, y, width, height, id);
        this.image = image;
    }

    @Override
    public void draw(Graphics2D g, int WorldX, int WorldY){
        image.draw(g, this.x + WorldX, this.y + WorldY);
    }
}
