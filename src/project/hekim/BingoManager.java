package project.hekim;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;

public class BingoManager {
    ArrayList<Word> vocData = new ArrayList<>();
    Random random = new Random();
    public String[][] user;
    public String[][] computer;
    public boolean[][] uStatus;
    public boolean[][] cStatus;
    private int N = 0;
    public int counter = 0;
    public String csel;
    private int cs = 0;
    private int us = 0;

    BingoManager() {
        init();
    }

    private void init() {

    }

    //TODO: 단어 뜻 저장 추가
    public void makeData(String fileName) {
        //파일 읽기 try-catch 사용해서 Scanner로 구현
        try (Scanner scan = new Scanner(new File(fileName))) {
            while (scan.hasNextLine()) { //다음 줄이 존재하지 않을때까지 반복(파일 끝)
                String str = scan.nextLine();
                String[] temp = str.split("\t"); //특정 문자 기준으로 분리
                addWord(new Word(temp[0].trim(), temp[1].trim()));
            }
            System.out.println("단어장 생성 완료");

        } catch (FileNotFoundException e) { //올바르게 파일이 읽히지 않았을 경우(FileNotFoundException 발생 시) 원하는 오류 메시지 반환
            System.out.println("단어장 생성 실패");
        }
    }

    public void addWord(Word word) {
        this.vocData.add(word);
    }

    public void createBingo(int n) {
        //TODO 중복방지 재확인 필수
        //홀수빙고만 가능
        this.user = new String[n][n];
        this.computer = new String[n][n];
        this.cStatus = new boolean[n][n];
        this.uStatus = new boolean[n][n];
        //중복방지
        ArrayList<String> utmp = new ArrayList<>();
        ArrayList<String> ctmp = new ArrayList<>();

        for (int i=0; i<n; i++) {
            for(int j=0; j<n; j++) {
                L1: for(;;) {
                    boolean flag = false;
                    if(utmp.size() == 0) {
                        this.user[i][j] = vocData.get(random.nextInt(vocData.size())).eng;
                        utmp.add(user[i][j]);
                        break L1;
                    }
                    else {
                        for(;;) {
                            flag = false;
                            this.user[i][j] = vocData.get(random.nextInt(vocData.size())).eng;
                            for(int k=0;k<utmp.size();k++) {
                                if(utmp.get(k).equals(user[i][j])) {
                                    flag = true;
                                }
                            }
                            if(!flag) {
                                utmp.add(user[i][j]);
                                break L1;
                            }
                        }
                    }
                }

                L2: for(;;) {
                    boolean flag = false;
                    if(ctmp.size() == 0) {
                        this.computer[i][j] = vocData.get(random.nextInt(vocData.size())).eng;
                        ctmp.add(computer[i][j]);
                        break L2;
                    }
                    else {
                        for(;;) {
                            flag = false;
                            this.computer[i][j] = vocData.get(random.nextInt(vocData.size())).eng;
                            for(int k=0;k<ctmp.size();k++) {
                                if(ctmp.get(k).equals(computer[i][j])) {
                                    flag = true;
                                }
                            }
                            if(!flag) {
                                ctmp.add(computer[i][j]);
                                break L2;
                            }
                        }
                    }
                }
                this.cStatus[i][j] = false;
                this.uStatus[i][j] = false;
            }
        }
        this.N = n;
    }

    public void Terminal_printBingo(String[][] bingo, boolean[][] status) {
        try {
            System.out.println("-------------------빙고현황-------------------");
            for (int i = 0; i < N; i++) {
                for (int j = 0; j <N; j++) {
                    System.out.print(bingo[i][j] + ":" + status[i][j] + " ");
                }
                System.out.println();
            }
        }
        catch (ArrayIndexOutOfBoundsException e) {
            System.out.println();
            System.out.println("올바른 빙고 크기를 입력해 주세요");
        }
    }

    public void USER_selBingo(String str) {
        for(int i=0; i<N; i++) {
            for(int j=0; j<N; j++) {
                if(user[i][j].equals(str)) {
                    if(!uStatus[i][j]) {
                        uStatus[i][j] = true;
                    }
                    //TODO 유저가 선택한 단어가 컴퓨터의 빙고판에 존재할 시, 컴퓨터 빙고판에 반영 구현
                }
            }
        }
        COM_selBingoByUser(str);
    }

    public void USER_selBingoByComputer(String str) {
        for(int i=0; i<N; i++) {
            for(int j=0; j<N; j++) {
                if(user[i][j].equals(str)) {
                    if(!uStatus[i][j]) {
                        uStatus[i][j] = true;
                    }
                }
            }
        }
    }

    public void COM_selBingoByUser(String str) {
        for(int i=0;i<N;i++) {
            for(int j=0;j<N;j++) {
                if(computer[i][j].equals(str)) {
                    cStatus[i][j] = true;
                    counter++;
                    break;
                }
            }
        }
    }


    //TODO 컴퓨터 선택 만들기 - 대각선, 행, 열 카운트
    //행 열 대각선 확인 후 가장 많은 선택을 한 열에 선택
    //아무것도 선택하지 않았다면 가운데 선택, 짝수 예외처리 필요
    //TODO 홀수빙고로 범위제한 구현
    public void COM_selBingo() {
        int[] selCount = new int[N*2+2]; //행(0~4), 열(5~9), 대각선(10~11)

        Arrays.fill(selCount,0);
        //if (counter == 0) {
        if(!cStatus[N/2][N/2]) {
            int rc = N/2;
            //TODO 빙고 선택 구현1 = 2
            cStatus[rc][rc] = true;
            USER_selBingo(computer[rc][rc]);
            csel = computer[rc][rc];
        }

        else {
            //열 확인
            for (int i = 0; i < N; i++) {
                for (int j = 0; j < N; j++) {
                    if (cStatus[i][j]) {
                        selCount[i]++; //(0~4);
                    }
                    if (selCount[i] == N) {
                        selCount[i] = -1;
                        break;
                    }
                }
            }

            //행 확인
            for (int i = 0; i < N; i++) {
                for (int j = 0; j < N; j++) {
                    if (cStatus[j][i]) {
                        selCount[i + N]++; //(5~9)
                    }
                    if (selCount[i + N] == N) {
                        selCount[i + N] = -1; //이미 빙고에 도달한 경우, 검사 대상에서 제외
                        break;
                    }
                }
            }

            //대각선 확인
            // |\|
            int checker = 0;
            if (N % 2 != 0) {
                for (int i = 0; i < N; i++) {
                    if (cStatus[i][checker]) {
                        selCount[N*2]++;
                    }
                    checker++;
                    if (selCount[N*2] == N) {
                        selCount[N*2] = -1;
                        break;
                    }
                }
            }

            // |/|
            checker = N - 1;
            if (N % 2 != 0) {
                for (int i = 0; i < N; i++) {
                    if (cStatus[i][checker]) {
                        selCount[N*2+1]++;
                    }
                    checker--;
                    if (selCount[N*2+1] == N) {
                        selCount[N*2+1] = -1;
                        break;
                    }
                }
            }

            int tmp = selCount[N*2+1];
            int idx = N*2+1;
            //TODO: 대각선 우선으로 확인
            for (int i = N * 2 + 1; i > -1 ; i--) {
                if (selCount[i] > tmp) {
                    tmp = selCount[i];
                    idx = i;
                }
            }

            //행, 열, 대각선의 선택 횟수에 따라 우선적으로 선택
            //TODO 첫 선택 시, 가운데 단어 선택 구현
            if (idx < N) { //열
                L:
                for (;;) {
                    int rdm = random.nextInt(N);
                    if (cStatus[idx][rdm] == false) {
                        cStatus[idx][rdm] = true;
                        USER_selBingoByComputer(computer[idx][rdm]);
                        csel = computer[idx][rdm];
                        break L;
                    }
                }
            }

            else if (N <= idx && idx < N * 2 ) { //행
                L:
                for (;;) {
                    int rdm = random.nextInt(N);
                    System.out.println((rdm+1)+","+(idx-N+1)+"선택");
                    if (cStatus[rdm][idx - N] == false) {
                        cStatus[rdm][idx - N] = true;
                        USER_selBingoByComputer(computer[rdm][idx-N]);
                        csel = computer[rdm][idx-N];
                        break L;
                    }
                }
            }

            //TODO 대각선 선택 구현
            else if (idx == N*2) { // 대각선 |\|
                L:
                for (;;) {
                    int rdm = random.nextInt(N);
                    if (cStatus[rdm][rdm] == false) {
                        cStatus[rdm][rdm] = true;
                        USER_selBingoByComputer(computer[rdm][rdm]);
                        csel = computer[rdm][rdm];
                        break L;
                    }
                }
            }

            else if (idx == N*2+1 ) { // 대각선 |/|
                L:
                for (;;) {
                    int rdm = random.nextInt(N);
                    if (cStatus[N - 1 - rdm][N-1-(N - 1 - rdm)] == false) {
                        cStatus[N - 1 - rdm][N-1-(N - 1 - rdm)] = true;
                        USER_selBingoByComputer(computer[N - 1 - rdm][N - 1 - rdm]);
                        csel = computer[N-1-rdm][N-1-rdm];
                        break L;
                    }
                }
            }
        }
    }

    public int checkBingo() {
        //int[] combingoCount = new int[N*N+2];
        int vic = 0; //0: nop, 1:user, 2:computer
        int comp = 0;
        int usr = 0;
        int cnt = 0;

        //유저 빙고 체크

        for (int i = 0; i < N; i++) {
            cnt = 0;
            for (int j = 0; j < N; j++) {
                if (uStatus[i][j]) {
                    cnt++; //(0~4);
                }
                if (cnt == N) {
                    usr++;
                    break;
                }
            }
        }

        //행 확인
        for (int i = 0; i < N; i++) {
            cnt = 0;
            for (int j = 0; j < N; j++) {
                if (uStatus[j][i]) {
                    cnt++; //(5~9)
                }
                if (cnt == N) {
                    usr++;
                    break;
                }
            }
        }

        //대각선 확인
        // |\|
        int checker = 0;
        if (N % 2 != 0) {
            cnt = 0;
            for (int i = 0; i < N; i++) {
                if (uStatus[i][checker]) {
                    cnt++;
                }
                checker++;
                if (cnt == N) {
                    usr++;
                    break;
                }
            }
        }

        // |/|
        checker = N - 1;
        if (N % 2 != 0) {
            cnt = 0;
            for (int i = 0; i < N; i++) {
                if (uStatus[i][checker]) {
                    cnt++;
                }
                checker--;
                if (cnt == N) {
                    usr++;
                    break;
                }
            }
        }

        //컴퓨터 빙고 체크
        for (int i = 0; i < N; i++) {
            cnt = 0;
            for (int j = 0; j < N; j++) {
                if (cStatus[i][j]) {
                    cnt++; //(0~4);
                }
                if (cnt == N) {
                    comp++;
                    break;
                }
            }
        }

        //행 확인

        for (int i = 0; i < N; i++) {
            cnt = 0;
            for (int j = 0; j < N; j++) {
                if (cStatus[j][i]) {
                    cnt++; //(5~9)
                }
                if (cnt == N) {
                    comp++;
                    break;
                }
            }
        }

        //대각선 확인
        // |\|
        checker = 0;
        if (N % 2 != 0) {
            cnt = 0;
            for (int i = 0; i < N; i++) {
                if (cStatus[i][checker]) {
                    cnt++;
                }
                checker++;
                if (cnt == N) {
                    comp++;
                    break;
                }
            }
        }

        // |/|
        checker = N - 1;
        if (N % 2 != 0) {
            cnt = 0;
            for (int i = 0; i < N; i++) {
                if (cStatus[i][checker]) {
                    cnt++;
                }
                checker--;
                if (cnt == N) {
                    comp++;
                    break;
                }
            }
        }
        us = usr;
        cs = comp;

        //빙고 수 비교
        if (usr > comp) {
            vic = 1;
        }
        else if(comp > usr) {
            vic = 2;
        }
        else if(comp == usr) {
            vic = 0;
        }
        else if(comp == N*2+2 && comp == N*2+2) {
            vic=3;
        }
        else {
            vic = 0;
        }

        return vic;
    }

    public String getComSel() {
        return csel;
    }

    public int getComBingoStatus() {
        return cs;
    }

    public int getUsrBingoStatus() {
        return us;
    }

}
