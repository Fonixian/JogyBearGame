/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package yogibear;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;

/**
 *
 * @author rde3cs
 */
public class Ranger extends Entity{
    private enum Direction{UP,LEFT,DOWN,RIGHT};
    private final int RAY_COUNT = 40;
    private final int SPEED = 1;
    private final float RANGE = 230f;
    private final float[] rotation = {0f, (float)Math.PI / 2f, (float)Math.PI, 3f * (float)Math.PI / 2f};
    
    private final Sprite image;
    private GameEngine engine;
    
    private float[] rays;
    private final int startX,startY,endX,endY;
    private boolean towardsStart;
    private Direction direction;
    
    private int[] pointsX;
    private int[] pointsY;
    private ArrayList<Entity> hits;
    private ArrayList<String> ignore;
    
    public Ranger(int x, int y, int width, int height, ArrayList<Integer> path, String id, Sprite image, GameEngine engine){
        super(x, y, width, height, id);
        this.image = image;
        this.engine = engine;
        direction = Direction.UP;
        
        startY = path.get(0);
        startX = path.get(1);
        endY = path.get(2);
        endX = path.get(3);
        
        towardsStart = true;
        
        rays = new float[RAY_COUNT];
        float angle = (float)Math.PI / 2 / (RAY_COUNT - 1);
        for(int i = 0; i < RAY_COUNT; i++){
            rays[i] = (float)Math.PI / 4.0f + angle * i;
        }
        
        ignore = new ArrayList();
        ignore.add("ranger");
        ignore.add("bascet");
    }
    
    @Override
    public void draw(Graphics2D g, int WorldX, int WorldY){
        g.setColor(new Color(1, 0, 0, 0.7f));
        if(pointsX != null && pointsY != null){
            g.fillPolygon(pointsX, pointsY, RAY_COUNT + 1);
        }
        g.drawRect(this.x + WorldX, this.y + WorldY, this.width, this.height);
        image.draw(g, this.x + WorldX, this.y + WorldY);
    }

    /**
     * 
     * Get the points of the cone and the Entities it hits
     * 
     */
    private void getCone(int WorldX, int WorldY){
        this.hits = new ArrayList();
        this.pointsX = new int[RAY_COUNT + 1];
        pointsX[RAY_COUNT] = this.x + this.width / 2 + WorldX;
        this.pointsY = new int[RAY_COUNT + 1];
        pointsY[RAY_COUNT] = this.y + this.height / 2 + WorldY;
        for(int i = 0; i < RAY_COUNT; i++){
            Ray ray = engine.rayCast((float)Math.cos(rays[i] + rotation[direction.ordinal()]), (float)-Math.sin(rays[i] + rotation[direction.ordinal()]), this.getX() + this.getWidth() / 2, this.getY() + this.getHeight() / 2, this, ignore);
            float t = Math.max(0, Math.min(RANGE, ray.distance));
            if(ray.entityHit != null && ray.distance == t && !hits.contains(ray.entityHit))hits.add(ray.entityHit);
            pointsX[i] = (int)(Math.cos(rays[i] + rotation[direction.ordinal()]) * t + this.x + this.width / 2 + WorldX);
            pointsY[i] = (int)(-Math.sin(rays[i] + rotation[direction.ordinal()])* t + this.y + this.height / 2 + WorldY);
        }
    }
    
    @Override
    public void update() {
        
        if(towardsStart){
            int xDir = (int)Math.signum(this.startX - this.x) * SPEED;
            switch (xDir) {
                case 1 -> direction = Direction.RIGHT;
                case -1 -> direction = Direction.LEFT;
            }
            this.x += xDir;
            int yDir = (int)Math.signum(this.startY - this.y) * SPEED;
            this.y += yDir;
            switch (yDir) {
                case 1 -> direction = Direction.DOWN;
                case -1 -> direction = Direction.UP;
            }
            if(this.x == startX && this.y == startY)towardsStart = false;
        }else{
            int xDir = (int)Math.signum(this.endX - this.x) * SPEED;
            switch (xDir) {
                case 1 -> direction = Direction.RIGHT;
                case -1 -> direction = Direction.LEFT;
            }
            this.x += xDir;
            int yDir = (int)Math.signum(this.endY - this.y) * SPEED;
            this.y += yDir;
            switch (yDir) {
                case 1 -> direction = Direction.DOWN;
                case -1 -> direction = Direction.UP;
            }
            if(this.x == endX && this.y == endY)towardsStart = true;
        }

        getCone(engine.getWorldX(), engine.getWorldY());
        for(Entity e : hits){
            if(e.id.equals("player")){
                engine.YogiCaught();
                break;
            }
        }
    }
    
    
}
