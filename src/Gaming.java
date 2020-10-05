/* Name: Blessing Babajide
 * NetID: bbabajid
 * Assignment: Project 1
 * I did not collaborate with anyone on this assignment.
 */
import java.awt.*;
import java.awt.event.*;
import java.util.Random;
import javax.swing.*;
 
public class Gaming extends JPanel {
	//states for game playing
    enum State {
        start, won, running, over
    }
 
    //array of colors for different squares
    final Color[] colorTable = {
        new Color(0x701710), new Color(0xFFE4C3), new Color(0xfff4d3),
        new Color(0xffdac3), new Color(0xe7b08e), new Color(0xe7bf8e),
        new Color(0xffc4c3), new Color(0xE7948e), new Color(0xbe7e56),
        new Color(0xbe5e56), new Color(0x9c3931), new Color(0x701710)};
 
    final static int target = 2048;
    static int highestnum;
    boolean quitting = false;
    boolean moved;
    static int score, numOfMoves = 0;
 
    private Random rand = new Random();
    private JPanel infoPanel;
    private Tile[][] tiles;
    private int side = 4;
    private State gamestate = State.start;
    private boolean checkPossibleMoves;
    protected JLabel info = new JLabel("Press q to quit and r to restart. Use a,w,s,d or arrow keys to play");
	protected JLabel loss = new JLabel("YOU LOST... press R/Q");
	protected JLabel scoreBoard = new JLabel("Total score: ");
	protected JButton button1 = new JButton("Click here to confirm new game");
	protected JButton button2 = new JButton("Click here to confirm quit");
 
    public Gaming() {
        setPreferredSize(new Dimension(900, 700));
        setBackground(new Color(0xFAF8EF));
        setFont(new Font("SansSerif", Font.BOLD, 48));
        setFocusable(true);
        infoPanel = new JPanel();
        infoPanel.setPreferredSize(new Dimension(600,50));
		infoPanel.add(info);
		add(infoPanel);
		add(scoreBoard);
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                startGame();
                repaint();
            }
        });
 
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {

        		if (e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_W) {
        			moveUp();
        			System.out.println("up key pressed. valid moves: " + numOfMoves);
        		}else if (e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() == KeyEvent.VK_D) {
        			moveDown();
        			System.out.println("down key pressed. valid moves: " + numOfMoves);
        		}else if (e.getKeyCode() == KeyEvent.VK_RIGHT || e.getKeyCode() == KeyEvent.VK_S) {
        			moveRight();
        			System.out.println("right key pressed. valid moves: " + numOfMoves);
        		}else if (e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_A) {
        			moveLeft();
        			System.out.println("left key pressed. valid moves: " + numOfMoves);
        		}else if (e.getKeyCode() == KeyEvent.VK_Q){
        			quit();
        			}
        		else if (e.getKeyCode() == KeyEvent.VK_R){
        			newGame();
        		}
                repaint();
            }
        });
    }
    
    class Tile {
        private boolean merged;
        private int val;
     
        Tile(int val) {
             this.val = val;
        }
     
        int getValue() {
            return val;
        }
     
        void setMerged(boolean m) {
            merged = m;
        }
     
        boolean canMergeWith(Tile other) {
            return !merged && other != null && !other.merged && val == other.getValue();
        }
     
        int mergeWith(Tile other) {
            if (canMergeWith(other)) {
                val *= 2;
                merged = true;
                return val;
            }
            return -1;
        }
    }
 
    @Override
    public void paintComponent(Graphics b) {
        super.paintComponent(b);
        Graphics2D g = (Graphics2D) b;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
 
        drawGrid(g);
    }
 
    void startGame() {
        if (gamestate != State.running) {
            score = 0;
            highestnum = 0;
            gamestate = State.running;
            tiles = new Tile[side][side];
            addRandomTile();
            addRandomTile();
        }
    }
 
    void drawGrid(Graphics2D g) {
        g.setColor(Color.BLACK);
        g.fillRoundRect(200, 100, 499, 499, 15, 15);
 
        if (gamestate == State.running) {
 
            for (int r = 0; r < side; r++) {
                for (int c = 0; c < side; c++) {
                    if (tiles[r][c] == null) {
                        g.setColor(Color.WHITE);
                        g.fillRoundRect(215 + c * 121, 115 + r * 121, 106, 106, 7, 7);
                    } else {
                        drawTile(g, r, c);
                    }
                }
            }
        } else {
            g.setColor(Color.cyan);
            g.fillRoundRect(215, 115, 469, 469, 7, 7);
 
            g.setColor(Color.DARK_GRAY);
            g.setFont(new Font("SansSerif", Font.BOLD, 128));
            g.drawString("2048", 310, 270);
            g.setFont(new Font("SansSerif", Font.BOLD, 50));
            g.drawString("Click to begin", 310,530);
 
            g.setFont(new Font("SansSerif", Font.BOLD, 20));
 
            if (gamestate == State.won) {
                g.drawString("You won! ", 390, 300);
 
            } else if (gamestate == State.over)
                g.drawString("Game over!", 400, 350);
 
            g.setColor(Color.BLACK);
        }
    }
 
    void drawTile(Graphics2D g, int r, int c) {
        int value = tiles[r][c].getValue();
 
        g.setColor(colorTable[(int) (Math.log(value) / Math.log(2)) + 1]);
        g.fillRoundRect(215 + c * 121, 115 + r * 121, 106, 106, 7, 7);
        String s = String.valueOf(value);
 
        g.setColor(value < 128 ? colorTable[0] : colorTable[1]);
 
        FontMetrics fm = g.getFontMetrics();
        int asc = fm.getAscent();
        int dec = fm.getDescent();
 
        int x = 215 + c * 121 + (106 - fm.stringWidth(s)) / 2;
        int y = 115 + r * 121 + (asc + (106 - (asc + dec)) / 2);
 
        g.drawString(s, x, y);
    }
 
 
    private void addRandomTile() {
        int pos = rand.nextInt(side * side);
        int row, col, val;
        do {
            pos = (pos + 1) % (side * side);
            row = pos / side;
            col = pos % side;
        } while (tiles[row][col] != null);
        
        int num = rand.nextInt(100);		//1-80 gives you 2, 81-100 gives you 4
		if (num <= 80) {
			val = 2;
		}else {
			val = 4;
		}
        tiles[row][col] = new Tile(val);
    }
 
    private boolean move(int countDownFrom, int yIncr, int xIncr) {  //logic for moving 
        moved = false;
 
        for (int i = 0; i < side * side; i++) {
            int j = Math.abs(countDownFrom - i);
 
            int r = j / side;
            int c = j % side;
 
            if (tiles[r][c] == null) 
                continue;
 
            int nextR = r + yIncr;
            int nextC = c + xIncr;
 
            while (nextR >= 0 && nextR < side && nextC >= 0 && nextC < side) {
 
                Tile next = tiles[nextR][nextC];
                Tile curr = tiles[r][c];
 
                if (next == null) {
 
                    if (checkPossibleMoves)
                        return true;
 
                    tiles[nextR][nextC] = curr;
                    tiles[r][c] = null;
                    r = nextR;
                    c = nextC;
                    nextR += yIncr;
                    nextC += xIncr;
                    moved = true;
 
                } else if (next.canMergeWith(curr)) {
 
                    if (checkPossibleMoves)
                        return true;
 
                    int value = next.mergeWith(curr);
                    if (value > highestnum)
                        highestnum = value;
                    score += value;
                    tiles[r][c] = null;
                    moved = true;
                    break;
                } else
                    break;
            }
        }
 
        if (moved) {
        	numOfMoves++;
            if (highestnum < target) {
                merged();
                addRandomTile();
                if (!movesAvailable()) {
                    gamestate = State.over;
                }
            } else if (highestnum == target)
                gamestate = State.won;
        }
 
        return moved;
    }
 
    boolean moveUp() {
        return move(0, -1, 0);
    }
 
    boolean moveDown() {
        return move(side * side - 1, 1, 0);
    }
 
    boolean moveLeft() {
        return move(0, 0, -1);
    }
 
    boolean moveRight() {
        return move(side * side - 1, 0, 1);
    }
 
    void merged() {
        for (Tile[] row : tiles)
            for (Tile tile : row)
                if (tile != null)
                    tile.setMerged(false);
    }
 
    boolean movesAvailable() {
        checkPossibleMoves = true;
        boolean hasMoves = moveUp() || moveDown() || moveLeft() || moveRight();
        checkPossibleMoves = false;
        return hasMoves;
    }
    
    public void newGame() {
    	infoPanel.add(button1);
		button1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				gamestate = State.start;
				repaint();
				startGame();
			}});
	}
	
    public int sumOfArray(int[][] poof) {
    	int sum = 0;
        for (int row=0; row < poof.length; row++){
            for (int col=0; col < poof[row].length; col++){
            	sum = sum + poof[row][col];
            }
        }
        return sum;
    }
	public void quit() {
		infoPanel.add(button2);
		button2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(1);
				System.out.println("quitting");
			}});
	}
 
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame f = new JFrame();
            f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            f.setTitle("2048 game!!! :) :)");
            f.setResizable(true);
            f.add(new Gaming(), BorderLayout.CENTER);
            f.pack();
            f.setLocationRelativeTo(null);
            f.setVisible(true);
        });
    }
}
 
