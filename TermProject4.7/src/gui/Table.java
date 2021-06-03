package gui;

import java.util.ArrayList;
import java.util.List;

import static javax.swing.SwingUtilities.isLeftMouseButton;
import static javax.swing.SwingUtilities.isRightMouseButton;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;

import gui.Table;
import runGame.runGame;
import board.Battle;
import board.Board;
import board.BoardManager;
import board.BoardUtils;
import board.Player;
import entity.gameObject;

public final class Table extends JFrame{

    private static final Dimension OUTER_FRAME_DIMENSION = new Dimension(1202, 720);
    private static final Dimension BOARD_PANEL_DIMENSION = new Dimension(600,600);
    private static final Dimension STORAGE_PANEL_DIMENSION = new Dimension(20, 20);
    private static final Dimension TILE_PANEL_DIMENSION = new Dimension(20, 20);
    
   
    private Board battleBoard;
    private final BoardPanel boardPanel;
    private StoragePanel StoragePanel;

    private RandomShop randomFrame1;
    private RandomShop randomFrame2;
    private gameObject sourceObject;

    public int start;
    
    public static int p1movedObj = 0;
    public static int p2movedObj = 0;

    private Color upLightTileColor = Color.decode("#B2BABB");
    private Color upDarkTileColor = Color.decode("#CCD1D1");
    private Color downLightTileColor = Color.decode("#BFCDCA");
    private Color downDarkTileColor = Color.decode("#AAB8B8");
	
    public final JFrame gameFrame;

    public Table(Board game) {
    	this.battleBoard=game;
    	game.initialize();
		this.gameFrame = new JFrame("Auto-chess Game");
		this.gameFrame.setLayout(new BorderLayout());
		this.boardPanel = new BoardPanel();
        this.gameFrame.add(this.boardPanel, BorderLayout.CENTER);
        setDefaultLookAndFeelDecorated(true);
        this.gameFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.gameFrame.setSize(OUTER_FRAME_DIMENSION);
        center(this.gameFrame);
    	this.gameFrame.setVisible(true);
    	
        Console console = null;
		try {
			console = new Console(gameFrame);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
        SwingUtilities.invokeLater
        (
             new Runnable()  {
                  public void run()     {
                	  getBoardPanel().drawBoard(getGameBoard());		
						          					
      		        }
              }
        );
    	
    	this.start = JOptionPane.showConfirmDialog(null, game.popupScore(), "Start new game?", 0);
    	if(this.start !=0) System.exit(0);
	 }
 
    
	private static void center(final JFrame frame) {
        final Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        final int w = frame.getSize().width;
        final int h = frame.getSize().height;
        final int x = ((dim.width - w) / 2);
        final int y = (dim.height - h) / 2;
        frame.setLocation(x, y);
    }
    
    public void fight() {
    	Thread battle = new Battle(getGameBoard());
    	
    	Battle.isFinished = false;
        battle.start();
        
        while(true) {
        	
        	if(Battle.isFinished) {
        		battle.interrupt();
        		break;
        	}
        	try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {}
        }
    }
    
    public class BoardPanel extends JPanel {
        final List<TilePanel> boardTiles;
        final List<StoragePanel> upperStorageTiles;
        final List<StoragePanel> lowerStorageTiles;


        BoardPanel() {
            super(new GridLayout(12,10));
            this.upperStorageTiles = new ArrayList<>();
            for(int i=0; i<10; i++) {
            	StoragePanel = new StoragePanel(battleBoard.player1, i);
                this.upperStorageTiles.add(StoragePanel);
            	add(StoragePanel);

            }
            this.boardTiles = new ArrayList<>();
            for (int i = 0; i < BoardUtils.NUM_TILES; i++) {
                final TilePanel tilePanel = new TilePanel(i);
                this.boardTiles.add(tilePanel);
                add(tilePanel);
            }
            this.lowerStorageTiles = new ArrayList<>();

            for(int i=0; i<10; i++) {
            	StoragePanel = new StoragePanel(battleBoard.player2, i);
                this.lowerStorageTiles.add(StoragePanel);
            	add(StoragePanel);
            }
            setPreferredSize(BOARD_PANEL_DIMENSION);
            setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            setBackground(Color.decode("#273746"));
            validate();
        }

        public TilePanel getTilePanel(int id) {
        	return boardTiles.get(id);
        }
        
        public void drawBoard(final Board board) {
            removeAll();
            for (final StoragePanel upperStorageTile : upperStorageTiles) {
                upperStorageTile.drawTile(board);
                add(upperStorageTile);           	
            }
            for (final TilePanel boardTile : boardTiles) {
                boardTile.drawTile(board);
                add(boardTile);
            }
            for (final StoragePanel lowerStorageTile : lowerStorageTiles) {
                lowerStorageTile.drawTile(board);
                add(lowerStorageTile);           	
            }
            revalidate();
            repaint();
        }
        
        public StoragePanel getStoragePanel(Player player, int i) {
        	if (player == battleBoard.player1) return upperStorageTiles.get(i);
        	else return lowerStorageTiles.get(i);
        }

     
        
    }
	public class StoragePanel extends JPanel{
		private final int storageId;
		private gameObject EntityObject;
		private JPanel panel;
		private Player player;

		StoragePanel(Player player, final int storageId){
			super(new GridBagLayout());
			this.storageId = storageId;
			this.panel = this;
			this.player = player;
			
			setPreferredSize(STORAGE_PANEL_DIMENSION);
			if(player == Board.player1) setBorder(BorderFactory.createMatteBorder(0, 0, 10, 0, Color.decode("#273746")));
			else setBorder(BorderFactory.createMatteBorder(10, 0, 0, 0, Color.decode("#273746")));
            addMouseListener(new MouseListener() {
                @Override
                public void mouseClicked(final MouseEvent event) {
        			if(player ==Board.player1) EntityObject = getGameBoard().getStorage1().get(storageId);
        			else EntityObject = getGameBoard().getStorage2().get(storageId);
                    if (isRightMouseButton(event)) {
                        //not picking up
                    	sourceObject = null;
                        
                    	//Remove lower-level champions
        	            player.getStorage().numOfObjects.put(EntityObject.getName(),player.getStorage().numOfObjects.get(EntityObject.getName())-1);
        	            player.getStorage().isTaken.remove(EntityObject.getCellNum());
        	            player.getStorage().cell.remove(EntityObject.getCellNum());
        	            
       					//Remove from Storage
       					panel.removeAll();
       					panel.revalidate();
       					panel.repaint();
                    } else if (isLeftMouseButton(event)) {
                    	sourceObject = EntityObject;

       					//Remove from Storage
       					panel.removeAll();
       					panel.revalidate();
       					panel.repaint();
       					
                        }
                    
                
	                SwingUtilities.invokeLater
	                (
	                     new Runnable()  {
	                          public void run()     {
                        	    getBoardPanel().drawBoard(getGameBoard());

	                          }
	                      }
	                );
	                
                }

                @Override
                public void mouseExited(final MouseEvent e) {
                }

                @Override
                public void mouseEntered(final MouseEvent e) {
                }

                @Override
                public void mouseReleased(final MouseEvent e) {
                }

                @Override
                public void mousePressed(final MouseEvent e) {
                }
            });
            
            validate();

		}
		
		StoragePanel get() {
			return this;
		}
		
		
        void drawTile(final Board board) {
            assignTileColor();
            validate();
            repaint();
        }
        
        void assignTileColor() {
		    if(player == Board.player1 )setBackground((this.storageId)%2==0 ? Color.decode("#85929E") : Color.decode("#5D6D7E"));
		    else setBackground((this.storageId)%2!=0 ? Color.decode("#85929E") : Color.decode("#5D6D7E"));
        }
        
        public void assignChampion(final Board board, final gameObject Entity) {
        	this.removeAll();
        	String p;
        	if(Entity.getPlayer()==getGameBoard().player1)  p= "p1";
        	else p = "p2";
            BufferedImage image = null;
			try {
				image = ImageIO.read(new File("art/champions/"+ p + Entity.getName()+".png"));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
            Image fitImage = image.getScaledInstance(40, 30, Image.SCALE_AREA_AVERAGING);
            JLabel name = new JLabel(Entity.getName());
            JLabel icon = new JLabel(new ImageIcon(fitImage));
            name.setFont(new Font("Verdana", Font.BOLD, 8));
            name.setForeground(Color.WHITE);
        	GridBagConstraints gbc = new GridBagConstraints();
        	gbc.fill = GridBagConstraints.BOTH;

            gbc.gridx=0;  
            gbc.gridy=0;
            gbc.gridwidth = 3;
            gbc.gridheight = 1;
            add(name, gbc);
            gbc.gridx=0;  
            gbc.gridy=1;
            gbc.gridwidth = 3;
            gbc.gridheight = 5;
            add(icon, gbc);
            
        	this.revalidate();
        	this.repaint();
            SwingUtilities.invokeLater
            (
                 new Runnable()  {
                      public void run()     {
                	    getBoardPanel().drawBoard(getGameBoard());

                      }
                  }
            );
        	
        }
        
	}
    
    public class TilePanel extends JPanel {

        private final int tileId;
        private final TilePanel tilePanel;
        private gameObject tileObject;

        TilePanel(final int tileId) {
            super(new GridBagLayout());
            
            this.tileId = tileId;
            this.tilePanel = this;
            
            int row = tileId/10;
            int col = tileId%10;
            
            setPreferredSize(TILE_PANEL_DIMENSION);
            assignTileColor();
            
            addMouseListener(new MouseListener() {
                @Override
                public void mouseClicked(final MouseEvent event) {
        			
                    if (isRightMouseButton(event)) {
                    } else if (isLeftMouseButton(event)) {

                    	if(sourceObject!=null) {
        				if(sourceObject.getPlayer()==Board.player1 && tileId <50) { //Player 1
                        	//Assign icon to the tile
                        	tilePanel.setTileObject(sourceObject);	
                        	//Taking out the gameObject to the board
            				int cellNum = sourceObject.getCellNumber();
            				
        					getGameBoard().storage1.takeOut(cellNum, row, col);       					
        					BoardManager.ENTITIES_ONBOARD[row][col] = getGameBoard().storage1.get(cellNum);
        					BoardManager.ENTITIES_ONBOARD[row][col].setPlayer(Board.player1);//temporary code
        					BoardManager.ENTITIES_ONBOARD[row][col].setInstorage(false);   		
        					        					
        					Board.player1.setnumOfobj(Board.player1.getnumOfobj()+1);
        					sourceObject = null;
							//If the number of gameOject on board reach the limit, set ready
        					p1movedObj++;
        					if(p1movedObj==Board.maxObj||Board.player1.getStorage().isEmpty()==true) {
        						Board.player1.setReady(true);
        					}

        				}
        				else if (sourceObject.getPlayer()==Board.player2 && tileId >=50){ //Player2
        					
                        	//Assign icon to the tile
                        	tilePanel.setTileObject(sourceObject);	
                        	//Taking out the gameObject to the board
            				int cellNum = sourceObject.getCellNumber();
        					
        					getGameBoard().storage2.takeOut(cellNum, row, col);
        					BoardManager.ENTITIES_ONBOARD[row][col] = getGameBoard().storage2.get(cellNum);
        					BoardManager.ENTITIES_ONBOARD[row][col].setPlayer(Board.player2);//temporary code
        					BoardManager.ENTITIES_ONBOARD[row][col].setInstorage(false);
        					Board.player2.setnumOfobj(Board.player2.getnumOfobj()+1);
       						
        					sourceObject=null;
        					p2movedObj++;
        					if(p2movedObj==Board.maxObj||getGameBoard().player2.getStorage().isEmpty()==true) {
        						Board.player2.setReady(true);
        					}
        					
        				}
        				else {
	                		getBoardPanel().getStoragePanel(sourceObject.getPlayer(), sourceObject.getCellNum()).assignChampion(getGameBoard(), sourceObject);
        					sourceObject=null;
	                		System.out.println("You cannot place the champion there. \n You may place your champions on your side only.");
        				}
                     }
                    
	                SwingUtilities.invokeLater
	                (
	                     new Runnable()  {
	                          public void run()     {
	                        	  getBoardPanel().drawBoard(getGameBoard());			
          						          					
	              		        }
	                      }
	                );
                    }
                }
                
                @Override
                public void mouseExited(final MouseEvent e) {
                }

                @Override
                public void mouseEntered(final MouseEvent e) {
                }

                @Override
                public void mouseReleased(final MouseEvent e) {
                }

                @Override
                public void mousePressed(final MouseEvent e) {
                }
            });
            
            validate();
        }

        void drawTile(final Board board) {
            assignTileColor();
            if(this.getTileObject()!=null && this.getTileObject().isAlive()==true) labeling();
            else if(this.getTileObject()==null||
            		this.getTileObject().getHealth()<=0||
            		this.getTileObject().isAlive()==false) {this.setBorder(null);}
            validate();
            repaint();
        }
        
        
        private void assignTileColor() {
        	if(this.tileId<50) {
	        	setBackground((this.tileId/10)%2==0 &&(this.tileId % 2 == 0) ? upLightTileColor : upDarkTileColor);
	        	if ((this.tileId/10)%2!=0 &&(this.tileId % 2 != 0)) {setBackground(upLightTileColor);}
        	}
        	else {
	        	setBackground((this.tileId/10)%2==0 &&(this.tileId % 2 == 0) ? downDarkTileColor : downLightTileColor);
	        	if ((this.tileId/10)%2!=0 &&(this.tileId % 2 != 0)) {setBackground(downDarkTileColor);}     		
        	}
        }
        
        public void labeling() {
        	this.removeAll();
        	GridBagConstraints gbc = new GridBagConstraints();
        	gbc.fill = GridBagConstraints.BOTH;
        	
        	gameObject Entity = this.getTileObject();
        	String p;
        	if(Entity.getPlayer()==getGameBoard().player1)  p= "p1";
        	else p = "p2";
            BufferedImage image = null;
			try {
				image = ImageIO.read(new File("art/champions/"+ p + Entity.getName()+".png"));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
            Image fitImage = image.getScaledInstance(40, 30, Image.SCALE_AREA_AVERAGING);
            JLabel skill = new JLabel(" ");
            JLabel icon = new JLabel(new ImageIcon(fitImage));
            JLabel hp = new JLabel();
            JLabel blank = new JLabel(" ");
            hp.setText("HP: "+Entity.getHealth());
            skill.setFont(new Font("Verdana", Font.BOLD, 9));
            
            if(Entity.getSkillActive()==true) {
            	skill.setText(Entity.getSkillname());
            	Border border = new LineBorder(Color.ORANGE, 2, true);
            	this.setBorder(border);
            }
            else if(Entity.getSkillTarget()==true) {
            	Border targetBorder = new LineBorder(Color.RED, 2, true);            	
            	this.setBorder(targetBorder);
            }else {
            	this.setBorder(null);
            }
            
            hp.setFont(new Font("Verdana", Font.BOLD, 8));
            blank.setFont(new Font("Verdana", Font.BOLD, 4));
            
            gbc.gridx=0;  
            gbc.gridy=0;
            gbc.gridwidth = 3;
            gbc.gridheight = 2;
            add(skill, gbc);
            gbc.gridx=0;  
            gbc.gridy=3;
            gbc.gridwidth = 3;
            gbc.gridheight = 4;
            add(icon, gbc);
            gbc.gridx=0;  
            gbc.gridy=8;
            gbc.gridwidth = 3;
            gbc.gridheight = 2;
            add(hp, gbc);
            gbc.gridx=0;
            gbc.gridy=11;
            gbc.gridwidth=3;
            gbc.gridheight=1;
            add(blank, gbc);
            if(this.getTileObject()==null || this.getTileObject().getHealth()<=0) this.removeAll();
            revalidate();
            repaint();
        }
        
        public void damageColor(JLabel label) { 
        	int damaged=0;
        	if(this.getTileObject()!=null) {
        	if(this.getTileObject().getHealth()<this.getTileObject().getMaxHealth()*0.9 && this.getTileObject().getHealth()>this.getTileObject().getMaxHealth()*0.7) {
              	damaged=1;}
            	else if(this.getTileObject().getHealth()<=this.getTileObject().getMaxHealth()*0.7 && this.getTileObject().getHealth()>this.getTileObject().getMaxHealth()*0.5) {
            	damaged=2;}
            	else if(this.getTileObject().getHealth()<=this.getTileObject().getMaxHealth()*0.5 && this.getTileObject().getHealth()>this.getTileObject().getMaxHealth()*0.3) {
            	damaged=3;}
            	else if(this.getTileObject().getHealth()<=this.getTileObject().getMaxHealth()*0.3 && this.getTileObject().getHealth()>this.getTileObject().getMaxHealth()*0) {
            	damaged=4;}
            	else if(this.getTileObject().getHealth()<=0) {
            		this.getTileObject().setAlive(false);
            		this.removeAll();
            	}
        	}
        	if(damaged==1) {label.setBackground(Color.decode("#E6B0AA")); label.setOpaque(true);}
        	else if(damaged==2) {label.setBackground(Color.decode("#CD6155")); label.setOpaque(true);}
        	else if(damaged==3) {label.setBackground(Color.decode("#A93226")); label.setOpaque(true);}
        	else if(damaged==4) {label.setBackground(Color.decode("#641E16")); label.setOpaque(true);}
        }
        
        public gameObject getTileObject() {
        	return tileObject;
        }
        public void setTileObject(gameObject currentObject) {
        	this.tileObject = currentObject;
        }
        
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);       
        	
        	if(this.getTileObject()!=null && this.getTileObject().isAlive()==true && this.getTileObject().getHealth()>0) {
        		int damaged=0;
        		 g.setColor(Color.RED);
        	if(this.getTileObject().getHealth()<this.getTileObject().getMaxHealth()*0.9 && this.getTileObject().getHealth()>this.getTileObject().getMaxHealth()*0.8) {
              	damaged=1;}
            	else if(this.getTileObject().getHealth()<=this.getTileObject().getMaxHealth()*0.8 && this.getTileObject().getHealth()>this.getTileObject().getMaxHealth()*0.7) {
            	damaged=2;}
            	else if(this.getTileObject().getHealth()<=this.getTileObject().getMaxHealth()*0.7 && this.getTileObject().getHealth()>this.getTileObject().getMaxHealth()*0.6) {
            	damaged=3;}
            	else if(this.getTileObject().getHealth()<=this.getTileObject().getMaxHealth()*0.6 && this.getTileObject().getHealth()>this.getTileObject().getMaxHealth()*0.5) {
            	damaged=4;}
            	else if(this.getTileObject().getHealth()<=this.getTileObject().getMaxHealth()*0.5 && this.getTileObject().getHealth()>this.getTileObject().getMaxHealth()*0.4) {
            	damaged=5;}
            	else if(this.getTileObject().getHealth()<=this.getTileObject().getMaxHealth()*0.4 && this.getTileObject().getHealth()>this.getTileObject().getMaxHealth()*0.3) {
            	damaged=6;}
            	else if(this.getTileObject().getHealth()<=this.getTileObject().getMaxHealth()*0.3 && this.getTileObject().getHealth()>this.getTileObject().getMaxHealth()*0.2) {
            	damaged=7;}
            	else if(this.getTileObject().getHealth()<=this.getTileObject().getMaxHealth()*0.2 && this.getTileObject().getHealth()>this.getTileObject().getMaxHealth()*0.1) {
            	damaged=8;}
            	else if(this.getTileObject().getHealth()<=this.getTileObject().getMaxHealth()*0.1 && this.getTileObject().getHealth()>this.getTileObject().getMaxHealth()*0) {
            	damaged=9;}
            	else if(this.getTileObject().getHealth()<=0) {
            		this.getTileObject().setAlive(false);
            		this.removeAll();
            	}
        	
        	if(damaged==0) {g.fillRect(0, 51, 63, 3);}
        	else if(damaged==1) {g.fillRect(0,51,58,3);}
        	else if(damaged==2) {g.fillRect(0, 51, 48, 3);}
        	else if(damaged==3) {g.setColor(Color.decode("#CD6155"));g.fillRect(0, 51, 42, 3);}
        	else if(damaged==4) {g.setColor(Color.decode("#CD6155"));g.fillRect(0, 51, 36, 3);}
        	else if(damaged==5) {g.setColor(Color.decode("#A93226"));g.fillRect(0, 51, 30, 3);}
        	else if(damaged==6) {g.setColor(Color.decode("#A93226"));g.fillRect(0, 51, 24, 3);}
        	else if(damaged==7) {g.setColor(Color.decode("#A93226"));g.fillRect(0, 51, 18, 3);}
        	else if(damaged==8) {g.setColor(Color.decode("#641E16"));g.fillRect(0, 51, 12, 3);}
        	else if(damaged==9) {g.setColor(Color.decode("#641E16"));g.fillRect(0, 51, 6, 3);}
        	}
          }
    }
    
    
    
    public Board getGameBoard() {
        return this.battleBoard;
    }
    
    
    public BoardPanel getBoardPanel() {
        return this.boardPanel;
    }
    
    RandomShop getRandomFrame1() {
        return this.randomFrame1;
    }
 
    RandomShop getRandomFrame2() {
        return this.randomFrame2;
    }
    
    StoragePanel getStoragePanel(){
    	return this.StoragePanel;
    }
    
}

class Console {

    JTextArea textArea;
    private static final Dimension LOG_FRAME_DIMENSION = new Dimension(30, 100);
    
    public Console(JFrame frame) throws Exception {
   	
        textArea = new JTextArea(35, 80);
        textArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 11));
        textArea.setEditable(false);
        textArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setSize(LOG_FRAME_DIMENSION);
        frame.add(scrollPane, BorderLayout.EAST);
        scrollPane.getVerticalScrollBar().addAdjustmentListener(new AdjustmentListener() {  
            public void adjustmentValueChanged(AdjustmentEvent e) {  
                e.getAdjustable().setValue(e.getAdjustable().getMaximum());  
            }
        });
        redirectOut();

    }

    public PrintStream redirectOut() {
        OutputStream out = new OutputStream() {
            @Override
            public void write(int b) throws IOException {
                textArea.append(String.valueOf((char) b));
            }
        };
        PrintStream ps = new PrintStream(out);
        
        System.setOut(ps);
        System.setErr(ps);

        return ps;
    }

}
