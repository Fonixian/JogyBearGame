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
public class Basket extends Entity {
    private Sprite image;
    private GameEngine engine;
    
    public Basket(int x, int y, int width, int height, String id, Sprite image, GameEngine engine){
        super(x, y, width, height, id);
        this.image = image;
        this.engine = engine;
    }
    
    @Override
    public void draw(Graphics2D g, int WorldX, int WorldY){
        image.draw(g, this.x + WorldX, this.y + WorldY);
    }

    @Override
    public void onCollide(Entity e) {
        if (e.getId().equals("player")){
            engine.basketPickup(this);
        }
    }
}
