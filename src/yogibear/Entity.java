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
public abstract class Entity implements Comparable<Entity>{
    protected int x;
    protected int y;
    
    protected int width;
    protected int height;
    
    protected String id;
    /**
     * 
     * @param x Position X
     * @param y Position Y
     * @param width Width of the entity that can collide
     * @param height Height of the entity that can collide
     * @param id Id of the object, can be used to group Entities
     */
    public Entity(int x, int y, int width, int height, String id){
        this.x = x;
        this.y = y;
        
        this.width = width;
        this.height = height;
        
        this.id = id;
    }
    
    public int  getX(){return x;}
    public int  getY(){return y;}
    public void setX(int x){this.x = x;}
    public void setY(int y){this.y = y;}
    
    public int  getWidth(){return width;}
    public int  getHeight(){return height;}
    public void setWidth(int width){this.width = width;}
    public void setHeight(int height){this.height = height;}
    
    public String getId(){return this.id;}
    
    /**
     * 
     * @param e is the caller Entity
     */
    public void onCollide(Entity e){}
    /**
     * Will be called every frame
     */
    public void update(){}
    
    /**
     * 
     * Will be called every frame to draw Entity
     */
    public abstract void draw(Graphics2D g, int WorldX, int WorldY);
    
    
    /**
     * 
     * @param e the other entity to collide with
     * @return if the 2 entity colled with each other
     * 
     * onCollision calls onCollide method for both Entity
     */
    public boolean collide(Entity e){
        if(this == e)return false;
        boolean collision = this.getX() < e.getX() + e.getWidth() &&
                this.getX() + this.getWidth() > e.getX() &&
                this.getY() < e.getY() + e.getHeight() &&
                this.getY() + this.getHeight() > e.getY();
        if(collision){
            this.onCollide(e);
            e.onCollide(this);
        }
        return  collision;
    }

    @Override
    public int compareTo(Entity e){
        return this.y - e.y;
    }
    
}
