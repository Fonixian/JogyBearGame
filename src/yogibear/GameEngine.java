/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package yogibear;

import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JPanel;
import java.util.ArrayList;
import javax.swing.Timer;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Image;
import javax.swing.ImageIcon;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Collections;
import java.util.Random;
import javax.swing.JFrame;

/**
 *
 * @author rde3cs
 */
public class GameEngine extends JPanel{
    private final int FPS = 100;
    private final int SIZE = 20;
    private final int MAX_BASKET_COUNT = 5;
    private final int MAX_RANGER_COUNT = 5;
    
    private int currHealthCount;
    private int basketCount = 0;
    
    private JFrame frame;

    private Timer gameClock;
    
    private ArrayList<Entity> map;
    private ArrayList<Entity> remove;
    
    private int worldX = 0;
    private int worldY = 0;
    private Player player;
    
    private Random random = new Random();
    
    private YogiBearGUI gui;
    
    private boolean goingUp, goingDown, goingLeft, goingRight;
    
    public GameEngine(JFrame frame, int startHealth, YogiBearGUI gui){
        this.gui = gui;
        currHealthCount = startHealth;
        remove = new ArrayList();
        this.frame = frame;

        makeMap();

        frame.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyChar()) {
                    case 'w' -> goingUp    = true;
                    case 's' -> goingDown  = true;
                    case 'a' -> goingLeft  = true;
                    case 'd' -> goingRight = true;
                    case 'n' -> System.out.println("sd");
                }
            }
            @Override
            public void keyReleased(KeyEvent e) {
                switch (e.getKeyChar()) {
                    case 'w' -> goingUp    = false;
                    case 's' -> goingDown  = false;
                    case 'a' -> goingLeft  = false;
                    case 'd' -> goingRight = false;
                }
            }
        });
        
        gameClock = new Timer(1000 / FPS, new Cycle());
        gameClock.start();
    }
    
    public int getWorldX(){return worldX;}
    public int getWorldY(){return worldY;}
    public int getCurrBasket(){return basketCount;}
    public int getCurrHealth(){return currHealthCount;}
    
    public void basketPickup(Entity e){
        remove.add(e);
        basketCount--;
        gui.basketCollected();
    }
    /**
     * Should be called before deleting the Object
     */
    public void close(){
        this.gameClock.stop();
        this.gameClock = null;
        KeyListener[] listeners = frame.getKeyListeners();
        for(KeyListener kl : listeners){
            frame.removeKeyListener(kl);
        }
    }
    
    private boolean Collision(){
            return map.parallelStream().anyMatch(e -> e.collide(player));
    }
    /**
     * Main cycle of the game
     */
    class Cycle implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent ae) {
            int oldX = player.getX();
            int oldY = player.getY();
            if(goingUp)    player.setY(oldY - 5);
            if(goingDown)  player.setY(oldY + 5);
            if(Collision()){
                player.setY(oldY);
            }
            if(goingLeft)  player.setX(oldX - 5);
            if(goingRight) player.setX(oldX + 5);
            if(Collision()){
                player.setX(oldX);
            }
            worldX = -player.getX() + player.getScreenX();
            worldY = -player.getY() + player.getScreenY();
            
            map.forEach(e -> e.update());
            remove.forEach(e -> map.remove(e));
            remove.clear();
            
            Collections.sort(map);

            repaint();
        }
    }
    
    @Override
    protected void paintComponent(Graphics grphcs) {
        super.paintComponent(grphcs);
        Graphics2D g2 = (Graphics2D) grphcs;
        map.forEach(e -> e.draw(g2, worldX, worldY));
    }
    
    public void YogiCaught(){
        currHealthCount--;
        player.setX(100);
        player.setY(100);
    }
    
    private enum Possiblity{Wall,Road,Unknown,Safe}
    
    private boolean Full(Possiblity[][] innerMap){
        for(Possiblity[] row : innerMap){
            for(Possiblity tile : row){
                if(tile == Possiblity.Unknown){
                    return false;
                }
            }
        }
        return true;
    }
    
    private ArrayList<int[]> possibleCollapse(Possiblity[][] innerMap , int size){
        ArrayList<int[]> collapsable = new ArrayList<>();
        for(int i = 0; i < size; i++){
            for(int j = 0; j < size; j++){
                if(innerMap[i][j] == Possiblity.Unknown && (innerMap[i+1][j] == Possiblity.Road || innerMap[i-1][j] == Possiblity.Road || innerMap[i][j+1] == Possiblity.Road || innerMap[i][j-1] == Possiblity.Road)){
                    int[] pair = new int[2];
                    pair[0] = i;
                    pair[1] = j;
                    collapsable.add(pair);
                }
            }
        }
        return collapsable;
    }
    
    private ArrayList<int[]> findBlockers(Possiblity[][] innerMap , int size){
        ArrayList<int[]> blockers = new ArrayList<>();
        for(int i = 0; i < size; i++){
            for(int j = 0; j < size; j++){
                if(i != 0 && i != size-1 && j != 0 && j != size-1 && innerMap[i][j] == Possiblity.Wall && (innerMap[i+1][j] == Possiblity.Unknown || innerMap[i-1][j] == Possiblity.Unknown || innerMap[i][j+1] == Possiblity.Unknown || innerMap[i][j-1] == Possiblity.Unknown)){
                    int[] pair = new int[2];
                    pair[0] = i;
                    pair[1] = j;
                    blockers.add(pair);
                }
            }
        }
        return blockers;
    }
    
    private int[] getRow(Possiblity[][] innerMap , int size, int x, int y){
        int[] col = new int[2];
        int left = x;
        int right = x;
        int j = x;
        while(j < size && innerMap[y][j] != Possiblity.Wall && innerMap[y][j] != Possiblity.Safe){
            right = j;
            j++;
        }
        j = x;
        while(j > 0 && innerMap[y][j] != Possiblity.Wall && innerMap[y][j] != Possiblity.Safe){
            left = j;
            j--;
        }
        col[0] = left;
        col[1] = right;
        return col;
    }
    
    private int[] getCol(Possiblity[][] innerMap , int size, int x, int y){
        int[] col = new int[2];
        int top = y;
        int bot = y;
        int i = y;
        while(i < size && innerMap[i][x] != Possiblity.Wall && innerMap[i][x] != Possiblity.Safe){
            bot = i;
            i++;
        }
        i = y;
        while(i > 0 && innerMap[i][x] != Possiblity.Wall && innerMap[i][x] != Possiblity.Safe){
            top = i;
            i--;
        }
        col[0] = top;
        col[1] = bot;
        return col;
    }
    
    /**
     * 
     * returns the 2 endpoints of the longest path if it is not bigger then 3 returns null
     */
    private ArrayList<Integer> getLongestPath(Possiblity[][] innerMap,int size, int x, int y){
        ArrayList<Integer> path = null;
        int[] col = getCol(innerMap, size, x, y);
        int[] row = getRow(innerMap, size, x, y);
        
        if(col[1] - col[0] >= row[1] - row[0]){
            if(col[1] - col[0] > 3){
                path = new ArrayList<>();
                path.add(col[1]);
                path.add(x);
                path.add(col[0]);
                path.add(x);
            }
        }else{
            if(row[1] - row[0] > 3){
                path = new ArrayList<>();
                path.add(y);
                path.add(row[1]);
                path.add(y);
                path.add(row[0]);
            }
        }
        
        return path;
    }
    
    private void makeMap(){
        Image wallImage = new ImageIcon("./assets/bush1.png").getImage();
        Sprite wallSprite = new Sprite(wallImage, 100, 120, 0, -20);
        
        Image bascetImage = new ImageIcon("./assets/bascet.png").getImage();
        Sprite bascetSprite = new Sprite(bascetImage, 100, 100, 0, 0);
        
        Image yogiImage = new ImageIcon("./assets/Yogi.png").getImage();
        Sprite yogiSprite = new Sprite(yogiImage, 50, 100, 0, -50);
        
        Image rangerImage = new ImageIcon("./assets/ranger.png").getImage();
        Sprite rangerSprite = new Sprite(rangerImage, 75, 100, 0, -25);
        
        map = new ArrayList();
        Possiblity[][] mapPrecursor = new Possiblity[SIZE][SIZE];
        
        for(int i = 0; i < SIZE; i++){
            for(int j = 0; j < SIZE; j++){
                if(i == 0 || i == SIZE - 1 || j == 0 || j == SIZE - 1){
                    mapPrecursor[i][j] = Possiblity.Wall;
                }else{
                    mapPrecursor[i][j] = Possiblity.Unknown;
                }
            }
        }
        mapPrecursor[1][1] = Possiblity.Road;
        while(!Full(mapPrecursor)){
            ArrayList<int[]> collapsable = possibleCollapse(mapPrecursor, SIZE);
            if(collapsable.isEmpty()){
                ArrayList<int[]> blockers = findBlockers(mapPrecursor, SIZE);
                int[] pair = blockers.remove(random.nextInt(blockers.size()));
                mapPrecursor[pair[0]][pair[1]] = Possiblity.Unknown;
            }
            
            if(!collapsable.isEmpty()){
                int[] pair = collapsable.remove(random.nextInt(collapsable.size()));
                mapPrecursor[pair[0]][pair[1]] = Possiblity.Road;
            }
            if(!collapsable.isEmpty()){
                int[] pair = collapsable.remove(random.nextInt(collapsable.size()));
                mapPrecursor[pair[0]][pair[1]] = Possiblity.Road;
            }
            if(!collapsable.isEmpty()){
                int[] pair = collapsable.remove(random.nextInt(collapsable.size()));
                mapPrecursor[pair[0]][pair[1]] = Possiblity.Wall;
            }
        }

        for(int i = 0; i < SIZE; i++){
            for(int j = 0; j < SIZE; j++){
                if(mapPrecursor[i][j] == Possiblity.Wall){
                    map.add(new Wall(j * 100,i * 100,100,100,"wall",wallSprite));
                }
            }
        }
        
        player = new Player(100,100,50,50, "player",500,500,yogiSprite);
        worldX = -player.getX() + player.getScreenX();
        worldY = -player.getY() + player.getScreenY();
        map.add(player);
        mapPrecursor[1][1] = Possiblity.Safe;
        
        ArrayList<int[]> possibleBasket = new ArrayList();
        for(int i = 0; i < SIZE; i++){
            for(int j = 0; j < SIZE; j++){
                if(mapPrecursor[i][j] == Possiblity.Road){
                    int[] pair = new int[2];
                    pair[0] = i;
                    pair[1] = j;
                    possibleBasket.add(pair);
                }
            }
        }
        Collections.shuffle(possibleBasket);
        for(int i = 0; i < MAX_BASKET_COUNT && !possibleBasket.isEmpty(); i++){
            int[] pair = possibleBasket.remove(0);
            map.add(new Basket(pair[1] * 100, pair[0] * 100, 100, 100, "bascet", bascetSprite, this));
            basketCount++;
            mapPrecursor[pair[0]][pair[1]] = Possiblity.Wall;
        }
        
        mapPrecursor[1][1] = Possiblity.Safe;
        mapPrecursor[2][1] = Possiblity.Safe;
        mapPrecursor[1][2] = Possiblity.Safe;
        mapPrecursor[2][2] = Possiblity.Safe;
        mapPrecursor[3][1] = Possiblity.Safe;
        mapPrecursor[3][2] = Possiblity.Safe;
        mapPrecursor[3][3] = Possiblity.Safe;
        mapPrecursor[1][3] = Possiblity.Safe;
        mapPrecursor[2][3] = Possiblity.Safe;
        
        ArrayList<int[]> possibleRanger = new ArrayList();
        for(int i = 0; i < SIZE; i++){
            for(int j = 0; j < SIZE; j++){
                if(mapPrecursor[i][j] == Possiblity.Road){
                    int[] pair = new int[2];
                    pair[0] = i;
                    pair[1] = j;
                    possibleRanger.add(pair);
                }
            }
        }
        Collections.shuffle(possibleRanger);
        for(int i = 0; i <= MAX_RANGER_COUNT && !possibleRanger.isEmpty(); i++){
            while(!possibleRanger.isEmpty()){
                int[] pair = possibleRanger.remove(0);
                if(mapPrecursor[pair[1]][pair[1]] == Possiblity.Safe)continue;
                
                ArrayList<Integer> path = getLongestPath(mapPrecursor, SIZE, pair[1], pair[0]);
                if(path != null){
                    
                    if(path.get(0) == path.get(2)){
                        for(int x = path.get(3); x < path.get(1); x++){
                            mapPrecursor[path.get(0)][x] = Possiblity.Safe;
                        }
                        path.replaceAll(e -> e * 100);
                        map.add(new Ranger(pair[1] * 100, pair[0] * 100, 75, 75, path, "ranger", rangerSprite, this));
                    }else{
                        for(int y = path.get(2); y < path.get(0); y++){
                            mapPrecursor[y][path.get(1)] = Possiblity.Safe;
                        }
                        path.replaceAll(e -> e * 100);
                        map.add(new Ranger(pair[1] * 100, pair[0] * 100, 75, 75, path, "ranger", rangerSprite, this));
                    }
                    
                    break;
                }
            }
        }
        
        Collections.sort(map);
    }
    /**
     * Only work with axis aligned boxes
     *
     * @param dirX X direction of the ray
     * @param dirY Y direction of the ray
     * @param posX X of the starting position of the ray
     * @param posY Y of the starting position of the ray
     * @param target Entity to intersect with
     * @return Ray contianing the target if it hit and the distance
     */
    private float intersectRay(float dirX, float dirY, float posX, float posY, Entity target){
        float tFar = Integer.MAX_VALUE;
        float tNear = Integer.MIN_VALUE;
        
        float targetX1 = target.getX() - 0;
        float targetY1 = target.getY() + target.getHeight() - 0;
        float targetX2 = target.getWidth() + targetX1;
        float targetY2 = target.getY() - 0;
        
        if(dirX == 0){
            if(posX > targetX2 || posX < targetX1){
                return -1;
            }else{
                return targetY2 - posY;
            }
        }
        
        if(dirY == 0){
            if(posY > targetY2 || posY < targetY1){
                return -1;
            }else{
                return targetX2 - posX;
            }
        }
        
        float t1,t2;
        t1 = (targetX1 - posX)/dirX;
        t2 = (targetX2 - posX)/dirX;
        if(t1 > t2){
            float tmp = t1;
            t1 = t2;
            t2 = tmp;
        }
        if(t1 > tNear)tNear = t1;
        if(tFar > t2)tFar = t2;
        
        t1 = (targetY1 - posY)/dirY;
        t2 = (targetY2 - posY)/dirY;
        if(t1 > t2){
            float tmp = t1;
            t1 = t2;
            t2 = tmp;
        }
        if(t1 > tNear)tNear = t1;
        if(tFar > t2)tFar = t2;
        
        if(tNear > tFar)return -1;
        if(tFar < 0)return -1;
        
        return tNear;
    }
    /**
     * Only work with axis aligned boxes
     *
     * @param dirX X direction of the ray
     * @param dirY Y direction of the ray
     * @param posX X of the starting position of the ray
     * @param posY Y of the starting position of the ray
     * 
     * @param caller It will be ignored when intersecting ray
     * @param ignore Ignores object with id contained in ignore List
     * @return 
     */
    public Ray rayCast(float dirX, float dirY, float posX, float posY, Entity caller ,ArrayList<String> ignore){
        Ray ray = new Ray();
        float t = Integer.MAX_VALUE;

        for(Entity e : map){
            if(e != caller && (ignore == null || !ignore.contains(e.id))){
                    float value = intersectRay(dirX, dirY, posX, posY, e);
                    if(value > 0 && value < t){
                        t = value;
                        ray.distance = t;
                        ray.entityHit = e;
                    }
                }
        }
        
        return ray;
    }
}
