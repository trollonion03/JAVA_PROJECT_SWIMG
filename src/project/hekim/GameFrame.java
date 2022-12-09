package project.hekim;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class GameFrame extends JFrame implements ActionListener {
    Container frame = this.getContentPane();
    BingoManager BM = new BingoManager();
    JPanel statusPanel = new JPanel();
    JPanel bingoPanel = new JPanel();
    JPanel controlPanel = new JPanel();
    JTextField selTxt;
    RoundedButton enterBtn;
    RoundedButton[][] bingo;
    RoundedButton exitBtn;
    JLabel status;
    Color btnColor = new Color(151,222,206);

    int N;
    int[] wincount = {0, 0}; //[0]:user, [1]:computer
    float usrOdds = 0;
    float comOdds = 0;
    String filepath;

    GameFrame(int size, String path) {
        super("빙고게임");
        this.N = size;
        this.readLog();
        this.filepath = path;
        this.setSize(720,720);
        this.setResizable(false);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setLayout(new BorderLayout());
        this.setLocationRelativeTo(null);
        init();
        this.setVisible(true);
    }

    private void init() {
        //상태 출력-----------------------------------------------------------------
        status = new JLabel("단어를 입력하십시오");
        statusPanel.add(status);
        frame.add(statusPanel, BorderLayout.NORTH);
        frame.setBackground(new Color(255,255,253));
        //-------------------------------------------------------------------------

        //빙고 칸-------------------------------------------------------------------
        bingoPanel.setLayout(new GridLayout(N, N));
        BM.makeData(filepath);
        BM.createBingo(N);
        bingo = new RoundedButton[N][N];
        for(int i=0;i<N;i++) {
            for(int j=0;j<N;j++) {
                if(!BM.uStatus[i][j]) {
                    bingo[i][j] = new RoundedButton(BM.user[i][j]);
                }
                else {
                    bingo[i][j] = new RoundedButton("O");
                }
                bingo[i][j].setBackground(Color.WHITE);
                bingoPanel.add(bingo[i][j]);
            }
        }
        frame.add(bingoPanel, BorderLayout.CENTER);
        //------------------------------------------------------------------------

        //컨트롤패널----------------------------------------------------------------
        controlPanel.setLayout(new BoxLayout(controlPanel ,BoxLayout.X_AXIS));
        selTxt = new JTextField(15);
        //Enter 이벤트처리
        KeyStroke enter = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, false);
        selTxt.getInputMap(JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(enter, "ENTER");
        selTxt.getActionMap().put("ENTER", sel);
        controlPanel.add(selTxt);

        enterBtn = new RoundedButton("입력");
        enterBtn.addActionListener(this);
        enterBtn.setBackground(btnColor);
        controlPanel.add(enterBtn);



        exitBtn = new RoundedButton("나가기");
        exitBtn.setBackground(btnColor);
        exitBtn.addActionListener(this);
        controlPanel.add(exitBtn);

        frame.add(controlPanel, BorderLayout.SOUTH);
        //------------------------------------------------------------------------
    }

    public void refreshBingo() {
        for (int i=0;i<N;i++) {
            for (int j=0;j<N;j++) {
                if(BM.uStatus[i][j]) {
                   bingo[i][j].setText("O");
                   bingo[i][j].setBackground(Color.GREEN);
                }
            }
        }
        revalidate();
        repaint();
    }

    public void checkGame() throws IOException {
        int stat = BM.checkBingo();
        if(stat != 0) {
            if(stat == 1) {
                wincount[0]++;
                getOdds();
                JOptionPane.showMessageDialog(null, "승률\n나: " + usrOdds + "%\n컴퓨터: "
                                + comOdds + "%", "승리", JOptionPane.WARNING_MESSAGE);
            }
            else if(stat == 2) {
                wincount[1]++;
                getOdds();
                JOptionPane.showMessageDialog(null, "승률\n나: " + usrOdds + "%\n컴퓨터: "
                                + comOdds + "%", "패배", JOptionPane.WARNING_MESSAGE);
            }
            else if(stat == 3) {
                wincount[0]++;
                wincount[1]++;
                getOdds();
                JOptionPane.showMessageDialog(null, "승률 - 무승부는 승률에 반영되지 않습니다.\n나: " + usrOdds + "%\n컴퓨터: "
                                + comOdds + "%", "무승부", JOptionPane.WARNING_MESSAGE);
            }
            this.writeLog();
            setVisible(false);
            new MainFrame("20221190 김형언");

        }
    }

    public void runGame(String str) throws IOException {
        //유저
        BM.USER_selBingo(str);
        this.refreshBingo();
        this.checkGame();

        //컴퓨터
        BM.COM_selBingo();
        BM.Terminal_printBingo(BM.computer, BM.cStatus);
        this.refreshBingo();
        this.checkGame();
        System.out.println();
        status.setText("컴퓨터가 \"" + BM.getComSel() + "\" 를 선택했습니다. (상태| 컴퓨터: "+BM.getComBingoStatus()+
                " | 나: "+ BM.getUsrBingoStatus() + " )");
        revalidate();
        repaint();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == enterBtn) {
            try {
                runGame(selTxt.getText());
            } catch (IOException ex) {
                ex.printStackTrace();
            }

        }
        else if(e.getSource() == exitBtn) {
            this.setVisible(false);
            try {
                new MainFrame("202211290 김형언");
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }


    Action sel = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                runGame(selTxt.getText());
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    };

    public void writeLog() {
        File file = new File("log.txt");
        FileWriter writer = null;

        try {
            // 기존 파일의 내용에 이어서 쓰려면 true를, 기존 내용을 없애고 새로 쓰려면 false를 지정한다.
            file.delete();
            writer = new FileWriter(file, false);
            writer.write(wincount[0] + "\n" + wincount[1]);
            writer.flush();

            System.out.println("DONE");
        } catch(IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if(writer != null) writer.close();
            } catch(IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void readLog() {
        //파일 읽기 try-catch 사용해서 Scanner로 구현
        try (Scanner scan = new Scanner(new File("log.txt"))) {
            int i=0;
            while (scan.hasNextLine()) { //다음 줄이 존재하지 않을때까지 반복(파일 끝)
                String str = scan.nextLine();
                if(i < 2)
                    wincount[i] = Integer.parseInt(str.trim());
                i++;
            }
            System.out.println("읽기 성공");

        } catch (FileNotFoundException e) { //올바르게 파일이 읽히지 않았을 경우(FileNotFoundException 발생 시) 원하는 오류 메시지 반환
            System.out.println("단어장 생성 실패");
        }
    }

    public void getOdds() {
        int userWins = wincount[0];
        int compWins = wincount[1];
        int total = wincount[0] + wincount[1];

        this.usrOdds = (float) userWins / total * 100;
        this.comOdds = (float) compWins / total * 100;
    }
}
