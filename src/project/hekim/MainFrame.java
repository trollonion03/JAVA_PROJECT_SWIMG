package project.hekim;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class MainFrame extends JFrame implements ActionListener, ItemListener {
    Container frame = this.getContentPane();
    JFileChooser chooser;
    RoundedButton startBtn;
    JRadioButton[] selSize = new JRadioButton[3];
    String[] sizeData = {"3", "5", "7"};
    RoundedButton locationBtn;
    JTextField locationBox;
    ButtonGroup btng = new ButtonGroup();
    BingoManager bingo = new BingoManager();
    int level = -1;
    String path;
    Color btnColor = new Color(151,222,206);

    public static Scanner scanner = new Scanner(System.in);

    MainFrame(String title) throws IOException {
        super(title);
        this.setSize(400,700);
        this.setResizable(false);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setLayout(new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));
        this.setLocationRelativeTo(null);
        init();
        this.setVisible(true);
    }

    private void init() throws IOException {
        initMainFrame();
    }

    private void initMainFrame() throws IOException {
        //TODO 파일 선택 구현
        //배경색
        frame.setBackground(new Color(255,255,253));

        //메인메뉴---------------------------------------------------------------
        JPanel imgPanel = new JPanel();
        BufferedImage mypic = ImageIO.read(new File("test.png"));
        JLabel pics = new JLabel(new ImageIcon(mypic));
        imgPanel.add(pics);
        frame.add(imgPanel);
        //----------------------------------------------------------------------

        //빙고 칸 선택 -----------------------------------------------------------
        JPanel pl1 = new JPanel();

        JLabel selLabel = new JLabel("빙고 크기 선택 |                  " +
                "               ");//라벨
        pl1.add(selLabel);

        for(int i=0;i<selSize.length;i++) { //라디오버튼, 3,5,7,9
            selSize[i] = new JRadioButton(sizeData[i]);
            selSize[i].addItemListener(this);
            btng.add(selSize[i]);
            pl1.add(selSize[i]);
        }

        frame.add(pl1);
        //----------------------------------------------------------------------

        //파일 선택---------------------------------------------------------------
        //TODO 파일명 만 넘기기
        JPanel locationPanel = new JPanel();
        JLabel locationLabel = new JLabel("단어 데이터     | ");
        locationBox = new JTextField(10);
        locationBtn = new RoundedButton("불러오기");
        locationBtn.setBackground(btnColor);
        locationBtn.addActionListener(this);
        locationPanel.add(locationLabel);
        locationPanel.add(locationBox);
        locationPanel.add(locationBtn);
        frame.add(locationPanel);
        //---------------------------------------------------------------------

        //시작 버튼--------------------------------------------------------------
        //TODO 게임 프레임으로 이동 구현
        JPanel startPanel = new JPanel();
        startBtn = new RoundedButton("시작!");
        startBtn.setBackground(btnColor);
        startBtn.addActionListener(this);
        startPanel.add(startBtn);
        frame.add(startPanel);
        //---------------------------------------------------------------------
    }

    public static void main(String[] args) throws IOException {
	// write your code here
        File path = new File(".");
        System.out.println(path.getAbsolutePath());
        new MainFrame("20221190 김형언");
    }

    public void Terminal_testBingo() {
        Scanner scan = new Scanner(System.in);

        bingo.makeData("quiz.txt");
        bingo.createBingo(3);
        bingo.Terminal_printBingo(bingo.user, bingo.uStatus);
        bingo.Terminal_printBingo(bingo.computer, bingo.cStatus);
        for(;;) {
            System.out.println("빙고를 선택하시오");
            String sel = scanner.nextLine();
            bingo.USER_selBingo(sel);
            bingo.Terminal_printBingo(bingo.user, bingo.uStatus);
            bingo.Terminal_printBingo(bingo.computer, bingo.cStatus);
            bingo.COM_selBingo();
            System.out.println("컴퓨터가 빙고를 선택함");
            bingo.Terminal_printBingo(bingo.user, bingo.uStatus);
            bingo.Terminal_printBingo(bingo.computer, bingo.cStatus);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        //파일선택------------------------------------------------------------------------------------------------------
        if(e.getSource() == locationBtn) {
            chooser = new JFileChooser();
            FileNameExtensionFilter filter = new FileNameExtensionFilter(
                    "TXT Files", "txt"
            );
            chooser.setFileFilter(filter);

            int ret = chooser.showOpenDialog(null);
            if(ret != JFileChooser.APPROVE_OPTION) {
                JOptionPane.showMessageDialog(null,"파일을 선택하지 않았습니다", "경고",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            String filePath = chooser.getSelectedFile().getPath();
            locationBox.setText(filePath);
            path = filePath;
        }
        //-------------------------------------------------------------------------------------------------------------

        //시작버튼------------------------------------------------------------------------------------------------------
        if(e.getSource() == startBtn) {
            if((level == 3 || level == 5 || level == 7 || level == 9) && path != null) {
                new GameFrame(level, path);
                setVisible(false);
            }
            else if(path == null) {
                JOptionPane.showMessageDialog(null, "단어장 파일을 선택해 주세요",
                        "경고", JOptionPane.WARNING_MESSAGE);
            }
            else {
                JOptionPane.showMessageDialog(null, "빙고판 크기를 선택해 주세요",
                        "경고", JOptionPane.WARNING_MESSAGE);
            }
        }
        //-------------------------------------------------------------------------------------------------------------
    }

    @Override
    public void itemStateChanged(ItemEvent e) {
        int index = -1;
        for(int i=0;i<sizeData.length;i++) {
            if(e.getSource() == selSize[i]) {
                index = i;
                break;
            }
        }

        if(index>=0) {
            if(e.getStateChange()==ItemEvent.SELECTED) {
                //label[index].setVisible(true);
                this.level = Integer.parseInt(sizeData[index].trim());
            }
        }
    }
}
