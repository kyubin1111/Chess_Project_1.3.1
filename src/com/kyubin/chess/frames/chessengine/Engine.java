package com.kyubin.chess.frames.chessengine;

import com.kyubin.chess.functions.draw.FiftyMovesDraw;
import com.kyubin.chess.functions.draw.InsufficientMaterial;
import com.kyubin.chess.functions.draw.StaleMate;
import com.kyubin.chess.functions.draw.TripleRepetition;
import com.kyubin.chess.functions.move.analysis.convert.LoadFENFailedException;
import com.kyubin.chess.functions.move.check.CheckMate;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static com.kyubin.chess.Main.*;
import static com.kyubin.chess.Main.pv1;
import static com.kyubin.chess.functions.move.analysis.convert.ConvertMove.convertMove;
import static com.kyubin.chess.functions.move.analysis.convert.ConvertMove.convertingMoveSize;

public class Engine {
    public static boolean stop=false;
    public static String game="";
    public static String bestMove="";
    public static final AtomicReference<Process> runningProcess = new AtomicReference<>();
    public static final AtomicReference<Process> runningProcess2 = new AtomicReference<>();

    public static void getEngineChoice(boolean is_white, String engine_file) {
        if(pv1!=null) pv1.setText("");
        if(pv2!=null) pv2.setText("");
        if(pv3!=null) pv3.setText("");
        if(pv4!=null) pv4.setText("");
        if(pv5!=null) pv5.setText("");

        new Thread(()->{
            ProcessBuilder processBuilder = new ProcessBuilder(engine_file);
            Process stockfishProcess;
            try {
                stockfishProcess = processBuilder.start();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            // 현재 실행 중인 프로세스를 runningProcess에 저장
            Process existingProcess = runningProcess.getAndSet(stockfishProcess);
            if (existingProcess != null) {
                existingProcess.destroyForcibly();
                try {
                    existingProcess.waitFor();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(stockfishProcess.getInputStream()));
                 OutputStreamWriter writer = new OutputStreamWriter(stockfishProcess.getOutputStream())) {

                // UCI 모드 설정
                sendCommand(writer, "uci");
                waitFor("uciok", reader);

                // 새 게임 시작
                sendCommand(writer, "ucinewgame");
                sendCommand(writer, "isready");
                waitFor("readyok", reader);

                sendCommand(writer, "setoption name MultiPV value "+multiPV);

                // 포지션 설정
                if(is_startPos) sendCommand(writer, "position startpos moves"+game);
                else sendCommand(writer, "position fen "+FEN+" moves"+game);

                // 평가 점수 얻기
                if(analysis_chessengine_is_depth_inf){
                    sendCommand(writer, "go infinite");
                } else {
                    sendCommand(writer, "go depth "+analysis_chessengine_depth);
                }

                try {
                    readCPAndMateOnly(reader, is_white);
                } catch (IOException | InterruptedException | LoadFENFailedException e) {
                    throw new RuntimeException(e);
                }
            } catch (IOException ignored) {
            } finally {
                runningProcess.compareAndSet(stockfishProcess, null);
            }
        }).start();
    }

    public static String getEngineChoiceString(EngineType engineType, int depth) {
        String engine_file;

        if(EngineType.STOCKFISH==engineType) engine_file=stockfish_file;
        else if(EngineType.KOMODO==engineType) engine_file=komodo_file;
        else if(EngineType.LCO==engineType) engine_file=lc0_file;
        else engine_file=custom_engine_file;

        ProcessBuilder processBuilder = new ProcessBuilder(engine_file);
        Process stockfishProcess;
        try {
            stockfishProcess = processBuilder.start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // 현재 실행 중인 프로세스를 runningProcess에 저장
        Process existingProcess = runningProcess2.getAndSet(stockfishProcess);
        if (existingProcess != null) {
            existingProcess.destroyForcibly();
            try {
                existingProcess.waitFor();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        String result="";

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(stockfishProcess.getInputStream()));
             OutputStreamWriter writer = new OutputStreamWriter(stockfishProcess.getOutputStream())) {

            // UCI 모드 설정
            sendCommand(writer, "uci");
            waitFor("uciok", reader);

            // 새 게임 시작
            sendCommand(writer, "ucinewgame");
            sendCommand(writer, "isready");
            waitFor("readyok", reader);

            // 포지션 설정
            if(is_startPos) sendCommand(writer, "position startpos moves"+game);
            else sendCommand(writer, "position fen "+FEN+" moves"+game);

            // 평가 점수 얻기
            sendCommand(writer, "go depth "+depth);

            result=readBestMoveOnly(reader);
        } catch (IOException ignored) {
        } finally {
            runningProcess2.compareAndSet(stockfishProcess, null);
        }

        return result;
    }

    public static void sendCommand(OutputStreamWriter writer, String command) throws IOException {
        try {
            writer.write(command + "\n");
            writer.flush();
        } catch (IOException ignored){}
    }

    public static void waitFor(String token, BufferedReader reader) throws IOException {
        String line;
        while ((line = reader.readLine()) != null) {
            if (line.contains(token)) {
                break;
            }
        }
    }

    public static String readBestMoveOnly(BufferedReader reader) throws IOException {
        String line;
        String result="";
        while ((line = reader.readLine()) != null) {
            if (line.split(" ")[0].equals("bestmove")) {
                result=line.split(" ")[1];
                break;
            }
        }
        return result;
    }

    private static int getIntValueAfterKeyword(String input, String keyword) {
        // 입력 문자열 앞뒤에 공백 추가하여 키워드 시작과 끝의 공백을 포함시키고 검색
        String modifiedInput = " " + input + " ";
        int start = modifiedInput.indexOf(keyword);
        if (start == -1) {
            return 0;
        }
        String substring = modifiedInput.substring(start + keyword.length()).trim();
        String[] parts = substring.split("\\s+", 2);
        try {
            return Integer.parseInt(parts[0]);
        } catch (NumberFormatException e) {
            System.err.println("Failed to parse integer after " + keyword);
            return 0;
        }
    }

    private static String getStringAfterKeyword(String input) {
        // 입력 문자열 앞뒤에 공백 추가하여 키워드 시작과 끝의 공백을 포함시키고 검색
        String modifiedInput = " " + input + " ";
        int start = modifiedInput.indexOf(" pv ");
        if (start == -1) {
            return "-1";
        }
        return modifiedInput.substring(start + " pv ".length()).trim();
    }

    public static void readCPAndMateOnly(BufferedReader reader, boolean is_white) throws IOException, InterruptedException, LoadFENFailedException {
        stop=true;
        convertingMoveSize=0;

        List<String> pvs = new ArrayList<>();
        int multiPV;
        if(is_white){
            String line;

            while ((line = reader.readLine()) != null) {
                if (CheckMate.isCheckmate(true, chessGame)&&!no_set) {
                    pv1.setText("1-0 White Win With Checkmate");
                    pv2.setText("");
                    pv3.setText("");
                    pv4.setText("");
                    pv5.setText("");
                } else if (CheckMate.isCheckmate(false, chessGame)&&!no_set) {
                    pv1.setText("0-1 Black Win With Checkmate");
                    pv2.setText("");
                    pv3.setText("");
                    pv4.setText("");
                    pv5.setText("");
                } else if (FiftyMovesDraw.isFiftyMovesDraw(chessGame)&&!no_set) {
                    pv1.setText("1/2-1/2 Draw With FiftyMovesDraw");
                    pv2.setText("");
                    pv3.setText("");
                    pv4.setText("");
                    pv5.setText("");
                } else if (InsufficientMaterial.isInsufficientMaterial(chessGame)&&!no_set) {
                    pv1.setText("1/2-1/2 Draw With InsufficientMaterial");
                    pv2.setText("");
                    pv3.setText("");
                    pv4.setText("");
                    pv5.setText("");
                } else if (StaleMate.isStalemate(true, chessGame)&&!no_set) {
                    pv1.setText("1/2-1/2 Draw With Stalemate");
                    pv2.setText("");
                    pv3.setText("");
                    pv4.setText("");
                    pv5.setText("");
                } else if (TripleRepetition.isTripleRepetition(chessGame)&&!no_set) {
                    pv1.setText("1/2-1/2 Draw With TripleRepetition");
                    pv2.setText("");
                    pv3.setText("");
                    pv4.setText("");
                    pv5.setText("");
                } else {
                    int depth_find=getIntValueAfterKeyword(line," depth ");
                    int cp_find=getIntValueAfterKeyword(line," score cp ");
                    int mate_find=getIntValueAfterKeyword(line," score mate ");
                    int multi_pv_find=getIntValueAfterKeyword(line, " multipv ");
                    String pv_find=getStringAfterKeyword(line);

                    if (depth_find!=0&&cp_find==0&&mate_find!=0&&!pv_find.equals("-1")&&multi_pv_find!=0) {
                        multiPV = multi_pv_find;

                        String convertedMove;

                        if(is_convertMove) convertedMove=convertMove(pv_find,0,chessGame,chessGame,"");
                        else convertedMove=pv_find;

                        if(!convertedMove.equals("")&&!pvs.contains(pv_find)) {
                            if(mate_find>=-1) {
                                if (multiPV == 1) {
                                    pv1.setText("-M" + mate_find + ": " + convertedMove + "\n");
                                    chart.setValue(10,false,true);
                                }
                                if (multiPV == 2)
                                    pv2.setText("-M" + mate_find + ": " + convertedMove + "\n");
                                if (multiPV == 3)
                                    pv3.setText("-M" + mate_find + ": " + convertedMove + "\n");
                                if (multiPV == 4)
                                    pv4.setText("-M" + mate_find + ": " + convertedMove + "\n");
                                if (multiPV == 5)
                                    pv5.setText("-M" + mate_find + ": " + convertedMove + "\n");
                            } else {
                                if (multiPV == 1) {
                                    pv1.setText("M" + -mate_find + ": " + convertedMove + "\n");
                                    chart.setValue(10,true,false);
                                }
                                if (multiPV == 2)
                                    pv2.setText("M" + -mate_find + ": " + convertedMove + "\n");
                                if (multiPV == 3)
                                    pv3.setText("M" + -mate_find + ": " + convertedMove + "\n");
                                if (multiPV == 4)
                                    pv4.setText("M" + -mate_find + ": " + convertedMove + "\n");
                                if (multiPV == 5)
                                    pv5.setText("M" + -mate_find + ": " + convertedMove + "\n");
                            }

                            if(multiPV==1) bestMove=pv_find.split(" ")[0];
                        }

                        pvs.add(pv_find);
                    } else if(depth_find != 0 && cp_find == 0 && mate_find != 0 && !pv_find.equals("-1")){
                        String convertedMove;
                        if(is_convertMove) convertedMove=convertMove(pv_find,0,chessGame,chessGame,"");
                        else convertedMove=pv_find;

                        if(!convertedMove.equals("")&&!pvs.contains(pv_find)) {
                            if(mate_find>=-1){
                                pv1.setText("M" + mate_find + ": " + convertedMove+"\n");
                                chart.setValue(10,false,true);
                            }
                            if(mate_find<=1){
                                pv1.setText("-M" + -mate_find + ": " + convertedMove+"\n");
                                chart.setValue(10,false,true);
                            }

                            bestMove=pv_find.split(" ")[0];
                        }

                        pvs.add(pv_find);
                    } else if (depth_find!=0&&cp_find!=0&&mate_find==0&&!pv_find.equals("-1")&&multi_pv_find!=0) {
                        multiPV = multi_pv_find;

                        if(!pvs.contains(pv_find)){
                            String convertedMove;
                            if(is_convertMove) convertedMove=convertMove(pv_find,0,chessGame,chessGame,"");
                            else convertedMove=pv_find;

                            if(!convertedMove.equals("")) {
                                if(multiPV==1) pv1.setText(-cp_find /100. + ": " + convertedMove);
                                if(multiPV==2) pv2.setText(-cp_find /100. + ": " + convertedMove);
                                if(multiPV==3) pv3.setText(-cp_find /100. + ": " + convertedMove);
                                if(multiPV==4) pv4.setText(-cp_find /100. + ": " + convertedMove);
                                if(multiPV==5) pv5.setText(-cp_find /100. + ": " + convertedMove);

                                if(multiPV==1){
                                    bestMove=pv_find.split(" ")[0];
                                    chart.setValue(-cp_find/100.,false,false);
                                }
                            }
                        }

                        pvs.add(pv_find);
                    } else if(depth_find != 0 && cp_find != 0 && mate_find == 0 && !pv_find.equals("-1")){
                        if(!pvs.contains(pv_find)){
                            String convertedMove;
                            if(is_convertMove) convertedMove=convertMove(pv_find,0,chessGame,chessGame,"");
                            else convertedMove=pv_find;
                            if(!convertedMove.equals("")) {
                                pv1.setText(-cp_find /100. + ": " + convertedMove);

                                bestMove=pv_find.split(" ")[0];
                                chart.setValue(-cp_find/100.,false,false);
                            }
                        }

                        pvs.add(pv_find);
                    }
                }
            }
        } else {
            String line;
            while ((line = reader.readLine()) != null) {
                if (CheckMate.isCheckmate(true, chessGame)&&!no_set) {
                    pv1.setText("1-0 White Win With Checkmate");
                    pv2.setText("");
                    pv3.setText("");
                    pv4.setText("");
                    pv5.setText("");
                } else if (CheckMate.isCheckmate(false, chessGame)&&!no_set) {
                    pv1.setText("0-1 Black Win With Checkmate");
                    pv2.setText("");
                    pv3.setText("");
                    pv4.setText("");
                    pv5.setText("");
                } else if (FiftyMovesDraw.isFiftyMovesDraw(chessGame)&&!no_set) {
                    pv1.setText("1/2-1/2 Draw With FiftyMovesDraw");
                    pv2.setText("");
                    pv3.setText("");
                    pv4.setText("");
                    pv5.setText("");
                } else if (InsufficientMaterial.isInsufficientMaterial(chessGame)&&!no_set) {
                    pv1.setText("1/2-1/2 Draw With InsufficientMaterial");
                    pv2.setText("");
                    pv3.setText("");
                    pv4.setText("");
                    pv5.setText("");
                } else if (StaleMate.isStalemate(false, chessGame)&&!no_set) {
                    pv1.setText("1/2-1/2 Draw With Stalemate");
                    pv2.setText("");
                    pv3.setText("");
                    pv4.setText("");
                    pv5.setText("");
                } else if (TripleRepetition.isTripleRepetition(chessGame)&&!no_set) {
                    pv1.setText("1/2-1/2 Draw With TripleRepetition");
                    pv2.setText("");
                    pv3.setText("");
                    pv4.setText("");
                    pv5.setText("");
                } else {
                    int depth_find = getIntValueAfterKeyword(line, " depth ");
                    int cp_find = getIntValueAfterKeyword(line, " score cp ");
                    int mate_find = getIntValueAfterKeyword(line, " score mate ");
                    int multi_pv_find = getIntValueAfterKeyword(line, " multipv ");
                    String pv_find = getStringAfterKeyword(line);

                    if (depth_find != 0 && cp_find == 0 && mate_find != 0 && !pv_find.equals("-1") && multi_pv_find != 0) {
                        multiPV = multi_pv_find;

                        String convertedMove;
                        if(is_convertMove) convertedMove=convertMove(pv_find,0,chessGame,chessGame,"");
                        else convertedMove=pv_find;

                        if (!convertedMove.equals("") && !pvs.contains(pv_find)) {
                            if(mate_find>=-1) {
                                if (multiPV == 1) {
                                    pv1.setText("M" + mate_find + ": " + convertedMove + "\n");
                                    chart.setValue(10,true,false);
                                }
                                if (multiPV == 2)
                                    pv2.setText("M" + mate_find + ": " + convertedMove + "\n");
                                if (multiPV == 3)
                                    pv3.setText("M" + mate_find + ": " + convertedMove + "\n");
                                if (multiPV == 4)
                                    pv4.setText("M" + mate_find + ": " + convertedMove + "\n");
                                if (multiPV == 5)
                                    pv5.setText("M" + mate_find + ": " + convertedMove + "\n");
                            } else {
                                if (multiPV == 1) {
                                    pv1.setText("-M" + -mate_find + ": " + convertedMove + "\n");
                                    chart.setValue(10,false,true);
                                }
                                if (multiPV == 2)
                                    pv2.setText("-M" + -mate_find + ": " + convertedMove + "\n");
                                if (multiPV == 3)
                                    pv3.setText("-M" + -mate_find + ": " + convertedMove + "\n");
                                if (multiPV == 4)
                                    pv4.setText("-M" + -mate_find + ": " + convertedMove + "\n");
                                if (multiPV == 5)
                                    pv5.setText("-M" + -mate_find + ": " + convertedMove + "\n");
                            }

                            if(multiPV==1) bestMove=pv_find.split(" ")[0];
                        }

                        pvs.add(pv_find);
                    } else if (depth_find != 0 && cp_find == 0 && mate_find != 0 && !pv_find.equals("-1")) {
                        String convertedMove;
                        if(is_convertMove) convertedMove=convertMove(pv_find,0,chessGame,chessGame,"");
                        else convertedMove=pv_find;

                        if (!convertedMove.equals("") && !pvs.contains(pv_find)) {
                            if(mate_find<=-1) {
                                pv1.setText("M" + mate_find + ": " + convertedMove+"\n");
                                chart.setValue(10,true,false);
                            }
                            if(mate_find>=1){
                                pv1.setText("-M" + -mate_find + ": " + convertedMove+"\n");
                                chart.setValue(10,false,true);
                            }

                            bestMove=pv_find.split(" ")[0];
                        }

                        pvs.add(pv_find);
                    } else if (depth_find != 0 && cp_find != 0 && mate_find == 0 && !pv_find.equals("-1") && multi_pv_find != 0) {
                        multiPV = multi_pv_find;

                        if (!pvs.contains(pv_find)) {
                            String convertedMove;
                            if(is_convertMove) convertedMove=convertMove(pv_find,0,chessGame,chessGame,"");
                            else convertedMove=pv_find;

                            if (!convertedMove.equals("")) {
                                if (multiPV == 1) {
                                    pv1.setText(cp_find / 100. + ": " + convertedMove);
                                    chart.setValue(cp_find/100.,false,false);
                                }
                                if (multiPV == 2)
                                    pv2.setText(cp_find / 100. + ": " + convertedMove);
                                if (multiPV == 3)
                                    pv3.setText(cp_find / 100. + ": " + convertedMove);
                                if (multiPV == 4)
                                    pv4.setText(cp_find / 100. + ": " + convertedMove);
                                if (multiPV == 5)
                                    pv5.setText(cp_find / 100. + ": " + convertedMove);

                                if(multiPV==1) bestMove=pv_find.split(" ")[0];
                            }
                        }

                        pvs.add(pv_find);
                    } else if (depth_find != 0 && cp_find != 0 && mate_find == 0 && !pv_find.equals("-1")) {
                        if (!pvs.contains(pv_find)) {
                            String convertedMove;
                            if(is_convertMove) convertedMove=convertMove(pv_find,0,chessGame,chessGame,"");
                            else convertedMove=pv_find;

                            if (!convertedMove.equals("")) {
                                pv1.setText(cp_find / 100. + ": " + convertedMove);

                                bestMove=pv_find.split(" ")[0];
                                chart.setValue(cp_find/100.,false,false);
                            }
                        }

                        pvs.add(pv_find);
                    }
                }
            }
        }
    }
}
