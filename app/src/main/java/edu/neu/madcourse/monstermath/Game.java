package edu.neu.madcourse.monstermath;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

public class Game {
    // instance variables
    String operation; // + - * / mix
    String difficultyLevel; // easy medium hard
    boolean singleMode; // single player is true, else false
    int curStage; // single problem, like 2+3
    int score; // current score in this round
    Queue<String> questionQueue;
    Queue<HashSet<Integer>> optionsQueue;
    Queue<Integer> correctOptionQueue;

    int curNumber1; // randomly generate the first number
    int curNumber2; // randomly generate the second number

    HashSet<Integer> curOptions; // generate multiple choices
    String curQuestion;
    int curAnswer; // right answer for the current question

    Random rand = new Random();
    long startTime;

    public Game(String operation, String difficultyLevel, boolean singleMode, int curStage, int score) {
        this.operation = operation;
        this.difficultyLevel = difficultyLevel;
        this.singleMode = singleMode;
        this.curStage = curStage;
        this.score = score;
        generateQuestions();
    }

    public Game(String operation, String difficultyLevel, boolean singleMode, int curStage) {
        this.operation = operation;
        this.difficultyLevel = difficultyLevel;
        this.singleMode = singleMode;
        this.curStage = curStage;
        this.score = 0;
        generateQuestions();
    }

//    public Game(Queue<String> questionQueue, Queue<HashSet<Integer>> optionsQueue, Queue<Integer> correctOptionQueue) {
//        this.questionQueue = questionQueue;
//        this.optionsQueue = optionsQueue;
//        this.correctOptionQueue = correctOptionQueue;
//    }

    public Game() {

    }
//
//    public Queue<String> getQuestionQueue() {
//        return questionQueue;
//    }
//
//    public Queue<HashSet<Integer>> getOptionsQueue() {
//        return optionsQueue;
//    }
//
//    public Queue<Integer> getCorrectOptionQueue() {
//        return correctOptionQueue;
//    }

    public String getOperation() {
        return operation;
    }

    public String getDifficultyLevel() {
        return difficultyLevel;
    }

    public Boolean getSingleMode() {
        return singleMode;
    }

    public int getCurStage() {
        return curStage;
    }

    public int getCurScore() {
        return score;
    }

    public void clearStage(){
        curOptions.clear();
    }

    /**
     * Generates the 10 questions and 5 answer options for each question.
     */
    private void generateQuestions() {
        questionQueue = new LinkedList<>();
        optionsQueue = new LinkedList<>();
        correctOptionQueue = new LinkedList<>();

        for (int i = 0; i < 10; i++) {
            generateNumbers();
            generateOptions();
            String question = curNumber1 + " " + operation + " " + curNumber2 + " = ?";
            questionQueue.add(question);
            optionsQueue.add(curOptions);
            correctOptionQueue.add(curAnswer);
        }
    }

    private void getCurrentQuestion() {
        curQuestion = questionQueue.remove();
    }

    private void getCurrentOptions() {
        curOptions = optionsQueue.remove();
    }

    private void getCurrentAnswer() {
        curAnswer = correctOptionQueue.remove();
    }

    /**
     * Generates one game stage.
     */
    public void generateOneStage(){
        // @https://blog.csdn.net/lintianlin/article/details/40540831
//        generateNumbers();
//        generateOptions();
        getCurrentQuestion();
        getCurrentOptions();
        getCurrentAnswer();
        Timestamp ts = new Timestamp(System.currentTimeMillis());
        startTime = ts.getTime();
    }

    /**
     * Generates two numbers for the math operation based on difficulty level.
     */
    public void generateNumbers() {
        int upperBound = 0;
        if (operation.equals("+") || operation.equals("-")) {
            if (difficultyLevel.equals("easy")) {
                upperBound = 10;
            } else if (difficultyLevel.equals("medium")) {
                upperBound = 20;
            } else if (difficultyLevel.equals("hard")) {
                upperBound = 100;
            }
        } else if (operation.equals("×") || operation.equals("÷")) {
            if (difficultyLevel.equals("easy")) {
                upperBound = 5;
            } else if (difficultyLevel.equals("medium")) {
                upperBound = 10;
            } else if (difficultyLevel.equals("hard")) {
                upperBound = 15;
            }
        }

        // 区别对待divide，因为可能出现随机数无法整除的现象，必须保证两个数能够整除
        if (operation.equals("÷")) {
            // plus 1 to ensure 0 not included
            curNumber2 = rand.nextInt(upperBound) + 1;
            // find the quotient
            int quotient = 1;
            if (difficultyLevel.equals("easy")) {
                quotient = rand.nextInt(3) + 1;
            } else if (difficultyLevel.equals("medium")) {
                quotient = rand.nextInt(5) + 1;
            } else if (difficultyLevel.equals("hard")) {
                quotient = rand.nextInt(10) + 1;
            }
            // make sure curNumber2 can evenly divide curNumber1
            curNumber1 = curNumber2 * quotient;
        } else if (operation.equals("-")) {
            curNumber1 = rand.nextInt(upperBound) + 1;
            curNumber2 = rand.nextInt(curNumber1) + 1;
        } else {
            curNumber1 = rand.nextInt(upperBound) + 1;
            curNumber2 = rand.nextInt(upperBound) + 1;
        }
    }

    /**
     * Generates five options for each question.
     */
    public void generateOptions(){
        curOptions = new HashSet();
        if (operation.equals("+")) {
            curAnswer = curNumber1 + curNumber2;
        } else if (operation.equals("-")) {
            curAnswer = curNumber1 - curNumber2;
        } else if (operation.equals("×")) {
            curAnswer = curNumber1 * curNumber2;
        } else if (operation.equals("÷")) {
            curAnswer = curNumber1 / curNumber2;
        }

        // add the correct answer to options
        curOptions.add(curAnswer);

        // ensure there are 5 options
        while (curOptions.size() < 5) {
            int option = curAnswer + (rand.nextInt(11) - 5) ;
            if (option >= 0) {
                curOptions.add(option);
            }
        }
    }
}





