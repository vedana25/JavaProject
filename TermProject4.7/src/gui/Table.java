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

    private static final Dimension OUTER_FRAME_DIMENSION = new Dimension(1200, 720);
    private static final Dimension BOARD_PANEL_DIMENSION = new Dimension(600,600);
    private static final Dimension STORAGE_PANEL_DIMENSION = new Dimension(20, 20);
    private static final Dimension TILE_PANEL_DIMENSION = new Dimension(20, 20);
	private static final Dimension SCORE_PANEL_DIMENSION = new Dimension(510, 600);
	private static final Dimension ROUND_PANEL_DIMENSION = new Dimension(300,50);
	private static final Dimension PLAYER_PANEL_DIMENSION = new Dimension(150,50);
   
    private Board battleBoard;
    private final BoardPanel boardPanel;
    private final ScorePanel scorePanel;
    private StoragePanel StoragePanel;
    private PlayerPanel playerPanel;
    private RandomShop randomFrame1;
    private RandomShop randomFrame2;
    private gameObject sourceObject;

    public int start;
    public static int p1ObjectOnBoard = 0;
    public static int p2ObjectOnBoard = 0;

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
		this.scorePanel = new ScorePanel();
        this.gameFrame.add(this.boardPanel, BorderLayout.CENTER);
        this.gameFrame.add(this.scorePanel, BorderLayout.EAST);
        setDefaultLookAndFeelDecorated(true);
        this.gameFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.gameFrame.setSize(OUTER_FRAME_DIMENSION);
        center(this.gameFrame);
    	this.gameFrame.setVisible(true);
    	
        Console console = null;
		try {
			console = new Console(scorePanel);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
        SwingUtilities.invokeLater
        (
             new Runnable()  {
                  public void run()     {
                	  getBoardPanel().drawBoard(getGameBoard());	
                	  getScorePanel().adding();

						          					
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

                    	if(sourceObject!=null && getBoardPanel().getTilePanel(tileId).getTileObject()==null) {
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
        					p1ObjectOnBoard++;
        					
        					if((Board.player1.isReadyButton()==true)&&(p1ObjectOnBoard==Board.maxObj||Board.player1.getStorage().isEmpty()==true)) {
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
       						p2ObjectOnBoard++;
        					sourceObject=null;
        					if((Board.player2.isReadyButton()==true)&&(p2ObjectOnBoard==Board.maxObj||Board.player2.getStorage().isEmpty()==true)) {
        						Board.player2.setReady(true);
        					}
        					
        				}
        				else {
	                		getBoardPanel().getStoragePanel(sourceObject.getPlayer(), sourceObject.getCellNum()).assignChampion(getGameBoard(), sourceObject);
        					//sourceObject=null;
	                		System.out.println("You cannot place the champion there. \n You may place your champions on your side only.");
        				}
                     }else if(sourceObject==null && getBoardPanel().getTilePanel(tileId)!=null) {
     	                SwingUtilities.invokeLater
    	                (
    	                     new Runnable()  {
    	                          public void run()     {
    	                        	  getBoardPanel().drawBoard(getGameBoard());			
              						          					
    	              		        }
    	                      }
    	                );
                    	 sourceObject = tilePanel.getTileObject();
                    	 tilePanel.removeAll();
                    	 getBoardPanel().getTilePanel(tileId).setTileObject(null);
                    	 if(tilePanel.getTileObject().getPlayer()==getGameBoard().player1) p1ObjectOnBoard--;
                    	 else if(tilePanel.getTileObject().getPlayer()==getGameBoard().player2) p2ObjectOnBoard--;

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
    
    public ScorePanel getScorePanel() {
    	return this.scorePanel;
    }
    
    
	public final class ScorePanel extends JPanel{
		JPanel center = new JPanel();
		RoundPanel roundPanel = new RoundPanel();
		PlayerPanel playerPanel1 = new PlayerPanel(Board.player1); 
		PlayerPanel playerPanel2 = new PlayerPanel(Board.player2); 
		
		ScorePanel(){
			super(new BorderLayout());
			setPreferredSize(SCORE_PANEL_DIMENSION);

			roundPanel = new RoundPanel();
			playerPanel1 = new PlayerPanel(Board.player1); 
			playerPanel2 = new PlayerPanel(Board.player2); 

		}
		public void adding() {
			roundPanel.removeAll();
			playerPanel1.removeAll();
			playerPanel2.removeAll();
			
			this.getRoundPanel().labeling();
			this.getPlayerPanel(Board.player1).labeling(Board.player1);
			this.getPlayerPanel(Board.player2).labeling(Board.player2);
			
			add(roundPanel, BorderLayout.NORTH);
			add (center, BorderLayout.SOUTH);			
			center.setLayout(new GridLayout(1,2));
			center.add(playerPanel1);
			center.add(playerPanel2);
	        setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 10));
	        setBackground(Color.decode("#273746"));
	        
	        
	        
	        validate();
		}
		
		public RoundPanel getRoundPanel() {
			return roundPanel;
		}
		public PlayerPanel getPlayerPanel(Player player) {
			if(player==Board.player1) return playerPanel1;
			else return playerPanel2;
		}

	}
	
	public class RoundPanel extends JPanel{
		JLabel roundLabel = new JLabel();
		JLabel scoreLabel = new JLabel();
		JButton button = new JButton();
		
		RoundPanel(){
			super(new GridLayout(1,3));
			setPreferredSize(ROUND_PANEL_DIMENSION);
			roundLabel.setFont(new Font("Verdana", Font.BOLD, 20));
			scoreLabel.setFont(new Font("Verdana", Font.BOLD, 20));
			button = new ReadyButton();
			setBorder(BorderFactory.createEmptyBorder(10, 50, 10, 40));
		}
		public void labeling() {
			roundLabel.setText("ROUND "+ Board.round);
			scoreLabel.setText("      " + Board.player1.getNumOfWin()+" : " + Board.player2.getNumOfWin());
			add(roundLabel);
			add(scoreLabel);
			add(button);
		}
	}
	
	 private class ReadyButton extends JButton{
	    	
	    	JLabel readyLabel;
	    	private final Dimension READY_BUTTON_DIMENSION = new Dimension(20, 10);
	    	
	    	ReadyButton(){
	    		
	    		setBackground(Color.decode("#7DCEA0"));
	    		setPreferredSize(READY_BUTTON_DIMENSION);
	    		setBorder(BorderFactory.createBevelBorder(DO_NOTHING_ON_CLOSE));
	    		
	    		readyLabel= new JLabel("READY", SwingConstants.CENTER);
	    		readyLabel.setBorder(BorderFactory.createEmptyBorder(0, 45, 0 , 0));
	    		this.add(readyLabel);
	    		
	    		addMouseListener(new MouseListener() {

					@Override
					public void mouseClicked(MouseEvent e) {
						if (isRightMouseButton(e)) {}
						else if (isLeftMouseButton(e)) {
							Board.player1.setReadyButton(true);
							Board.player2.setReadyButton(true);
						}
						
    					if((Board.player2.isReadyButton()==true)&&(p2ObjectOnBoard==Board.maxObj||Board.player2.getStorage().isEmpty()==true)) {
    						Board.player2.setReady(true);
    					}
    					if((Board.player1.isReadyButton()==true)&&(p1ObjectOnBoard==Board.maxObj||Board.player1.getStorage().isEmpty()==true)) {
    						Board.player1.setReady(true);
    					}
					}

					@Override
					public void mousePressed(MouseEvent e) {
						// TODO Auto-generated method stub
						
					}

					@Override
					public void mouseReleased(MouseEvent e) {
						// TODO Auto-generated method stub
						
					}

					@Override
					public void mouseEntered(MouseEvent e) {
						// TODO Auto-generated method stub
						
					}

					@Override
					public void mouseExited(MouseEvent e) {
						// TODO Auto-generated method stub
						
					}});
	    		
	    	}        
	    }
	
	public class PlayerPanel extends JPanel{
		Player player;
		
		PlayerPanel(Player player){
			super(new GridBagLayout());
			this.player = player;
			if(this.getPlayer()==Board.player1) setBackground(Color.decode("#AED6F1"));
			else setBackground(Color.decode("#F5B7B1"));
			setPreferredSize(PLAYER_PANEL_DIMENSION);
			labeling(player);
		}
		
		public void labeling(Player player) {
            BufferedImage image = null;
			try {
				image = ImageIO.read(new File("art/players/player.png"));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			this.removeAll();
			if(this.getPlayer()==Board.player1) setBackground(Color.decode("#AED6F1"));
			else setBackground(Color.decode("#F5B7B1"));
            Image fitImage = image.getScaledInstance(30, 30, Image.SCALE_AREA_AVERAGING);
            JLabel name = new JLabel();
            JLabel blank = new JLabel();
            JLabel hp = new JLabel();
            blank.setText(" ");
            if(player==Board.player1) {
            	name.setText("Player1   HP: "+player.getHp());

            	setBorder(BorderFactory.createEmptyBorder(0,0,5,0));
            	setBorder(BorderFactory.createMatteBorder(3, 0, 0, 0, Color.BLACK));
            }
            else {       
            	name.setText("Player2   HP: "+player.getHp());

            	setBorder(BorderFactory.createEmptyBorder(0,0,5,0));
            	setBorder(BorderFactory.createMatteBorder(3, 3, 0, 0, Color.BLACK));

            }
            JLabel icon = new JLabel(new ImageIcon(fitImage));

        	GridBagConstraints gbc = new GridBagConstraints();
        	gbc.fill = GridBagConstraints.BOTH;
            
        	gbc.fill = GridBagConstraints.BOTH;
            gbc.gridx=0;  
            gbc.gridy=1;
            gbc.gridwidth = 4;
            gbc.gridheight = 4;
            add(icon, gbc);
            gbc.gridx=4;  
            gbc.gridy=4;
            gbc.gridwidth = 4;
            gbc.gridheight = 2;
            add(name, gbc);
            revalidate();
            
		}
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);       
        	g.setColor(Color.RED);
        	int damaged=0;
        	if(this.getPlayer().getHp()<=90 && this.getPlayer().getHp()>80) {
              	damaged=1;}
            	else if(this.getPlayer().getHp()<=80 && this.getPlayer().getHp()>70) {
            	damaged=2;}
            	else if(this.getPlayer().getHp()<=70 && this.getPlayer().getHp()>60) {
            	damaged=3;}
            	else if(this.getPlayer().getHp()<=60 && this.getPlayer().getHp()>50) {
            	damaged=4;}
            	else if(this.getPlayer().getHp()<=50 && this.getPlayer().getHp()>40) {
            	damaged=5;}
            	else if(this.getPlayer().getHp()<=40 && this.getPlayer().getHp()>30) {
            	damaged=6;}
            	else if(this.getPlayer().getHp()<=30 && this.getPlayer().getHp()>20) {
            	damaged=7;}
            	else if(this.getPlayer().getHp()<=20 && this.getPlayer().getHp()>10) {
            	damaged=8;}
            	else if(this.getPlayer().getHp()<=10 && this.getPlayer().getHp()>0) {
            	damaged=9;}
            	else {}
        	
        	if(damaged==0) {g.fillRect(0, 35, 250, 35);}
        	else if(damaged==1) {g.fillRect(0,35,225,35);}
        	else if(damaged==2) {g.fillRect(0, 35, 200, 35);}
        	else if(damaged==3) {g.setColor(Color.decode("#CD6155"));g.fillRect(0, 35, 175, 35);}
        	else if(damaged==4) {g.setColor(Color.decode("#CD6155"));g.fillRect(0, 35, 150, 35);}
        	else if(damaged==5) {g.setColor(Color.decode("#A93226"));g.fillRect(0, 35, 125, 35);}
        	else if(damaged==6) {g.setColor(Color.decode("#A93226"));g.fillRect(0, 35, 100, 35);}
        	else if(damaged==7) {g.setColor(Color.decode("#A93226"));g.fillRect(0, 35, 75, 35);}
        	else if(damaged==8) {g.setColor(Color.decode("#641E16"));g.fillRect(0, 35, 50, 35);}
        	else if(damaged==9) {g.setColor(Color.decode("#641E16"));g.fillRect(0, 35, 25, 35);}
        	}
          
        
        public Player getPlayer() {
        	return player;
        }
	}
    
}



	class Console {
	
	    JTextArea textArea;
	    private static final Dimension LOG_FRAME_DIMENSION = new Dimension(30, 30);
	    
	    public Console(JPanel panel) throws Exception {
	   	
	        textArea = new JTextArea(10, 20);
	        textArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 11));
	        textArea.setEditable(false);
	        textArea.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
	        JScrollPane scrollPane = new JScrollPane(textArea);
	        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
	        scrollPane.setSize(LOG_FRAME_DIMENSION);
	        panel.add(scrollPane, BorderLayout.CENTER);
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
