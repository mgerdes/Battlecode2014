import java.util.*;

import javax.swing.*;

import java.awt.*;
import java.awt.event.*;

public class MapUtil extends JFrame {

	MapUtil() {
		JPanel content = new JPanel();
		content.setLayout(new BorderLayout());

		final Map map = new Map();
		map.setPreferredSize(new Dimension(1000, 1000));
		JScrollPane scrollPane1 = new JScrollPane(map);

		JPanel text = new JPanel();
		
		final JTextArea textArea = new JTextArea();
		JScrollPane scrollPane = new JScrollPane(textArea);
		scrollPane.setPreferredSize(new Dimension(400, 200));
		text.add(scrollPane);

		JButton button = new JButton("Submit");
		button.setPreferredSize(new Dimension(100, 20));
		text.add(button);
		
		final JComboBox jcb = new JComboBox(new Object[] {"Draw Map", "Draw Coarsened Map"});
		jcb.setSelectedItem(0);
		
		text.add(jcb);

		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int[] dimensions = map.update(textArea.getText(), jcb.getSelectedIndex());
				map.setPreferredSize(new Dimension(dimensions[0] * 10, dimensions[1] * 22));
			}
		});
		
		textArea.addFocusListener(new FocusListener() {

			@Override
			public void focusGained(FocusEvent arg0) {
				textArea.selectAll();				
			}

			@Override
			public void focusLost(FocusEvent arg0) {

			}
			
		});

		content.add(text, BorderLayout.SOUTH);
		content.add(scrollPane1, BorderLayout.CENTER);	
		content.setSize(600,600);

		setContentPane(content);
	}

	public static void main (String[] args) {
		
		MapUtil frame = new MapUtil();
		frame.setTitle("Draw Map Util");
		frame.setSize(700,700);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);

	}

}

class Map extends JPanel {
	
	int map[][];
	int coarsenedMap[][];
	int width;
	int height;
	Color colors[] = {Color.RED, Color.BLUE, Color.YELLOW, Color.ORANGE, Color.CYAN, Color.GREEN, Color.MAGENTA, Color.PINK};

	int[] update(String s, int doWhat) {
		if (doWhat == 0) {
			String st[] = s.split("!");
			height = st.length / 2;
			width = st[1].split(",").length;
						
			map = new int[width][height];
			
			int x = 0, y = 0;
	
			for (int i = 0; i < st.length; i++) {
				if (i % 2 != 0) {
					String st2[] = st[i].split(",");
					for (int j = 0; j < st2.length; j++) {
						map[x][y] = Integer.parseInt(st2[j]);
						x++;
					}
					x = 0;
					y++;
				}
			}
		} else if (doWhat == 1) {
			String st[] = s.split("!");
			height = st.length / 2;
			width = st[1].split(",").length;
						
			coarsenedMap = new int[width][height];
			
			int x = 0, y = 0;
	
			for (int i = 0; i < st.length; i++) {
				if (i % 2 != 0) {
					String st2[] = st[i].split(",");
					for (int j = 0; j < st2.length; j++) {
						coarsenedMap[x][y] = Integer.parseInt(st2[j]);
						x++;
					}
					x = 0;
					y++;
				}
			}
		}
		
		repaint();

		return new int[]{width, height};
	}

	Map() {
		
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (map != null) {
			for (int x = 0; x < width; x++) {
				for (int y = 0; y < height; y++) {
					if (map[x][y] == 2) {
						g.setColor(Color.BLACK);
					} else if (map[x][y] == 0) {
						g.setColor(Color.WHITE);
					} else {
						g.setColor(Color.LIGHT_GRAY);
					}
					g.fillRect(x * 10, y * 10, 10, 10);
					
					if (coarsenedMap != null) {
						if (coarsenedMap[x][y] != -1) {
							g.setColor(colors[coarsenedMap[x][y] % 8]);
						} else {
							g.setColor(Color.BLACK);
						}
						
						g.fillRect(x * 10, height * 10 + 10 + y * 10, 10, 10);
					}
				}
			}
			
		}
	}

}
