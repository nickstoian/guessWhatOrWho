// Nicolas Stoian
// CUNY First ID - 10852790
// CS715 Project 1

import java.util.*;

public class GuessWhatOrWho 
{
	// fixed parameter
	protected final int exam_time = 3000;

	// adjustable command line parameters
	protected int numRounds = 2;
	protected int numQuestions = 5;
	protected int questionValues = 200;
	protected double rightPercent = 0.65;
	protected int room_capacity = 4;
	protected int num_contestants = 13;
	
	// game objects
	protected Vector<Contestant> contestants = new Vector<Contestant>( );
	protected Vector<Contestant> gameContestants = new Vector<Contestant>( );
	protected Vector<Object> groups = new Vector<Object>( );
	protected Object sittingContestants = new Object();
	protected Object waitForAllReadyToSit = new Object();
	protected Object waitForAllSitting = new Object();
	protected Object waitForAllFinishedExam = new Object();
	protected Object waitToStartShow = new Object();
	protected Object waitToStartGame = new Object();
	protected Object waitForAnnouncer = new Object();
	protected Object waitForHost = new Object();
	protected Object waitForNextRound = new Object();
	protected Object waitForAnswer = new Object();
	protected Object waitToStartFinal = new Object();
	protected Object waitForFinalAnswer = new Object();

	// game counters
	protected int numInGroup = 0;
	protected int numWaitingToSit = 0;
	protected int numSitting = 0;
	protected int numFinishedExam = 0;
	protected int numWaitingGrade = 0;
	protected int numWaitingToStartGame = 0;
	protected int numFinishedThinking = 0;
	protected int numWaitingToStartFinal = 0;
	protected boolean answered = false;
	
	public static void main( String[] args )
	{
		if (args.length == 0){
			System.out.println("Welcome to Guess What or Who");
			System.out.println("No command line arguements detected");
			System.out.println("Starting a new game with the default settings:");
			GuessWhatOrWho game = new GuessWhatOrWho();
			System.out.println("numRounds = " + game.numRounds);
			System.out.println("numQuestions = " + game.numQuestions);
			System.out.println("questionValues = " + game.questionValues);
			System.out.println("rightPercent = " + game.rightPercent);
			System.out.println("room_capacity = " + game.room_capacity);
			System.out.println("num_contestants = " + game.num_contestants);
			System.out.println();
			for( int i = 0; i < game.num_contestants; i++ )
			{
				game.contestants.add(new Contestant(game));
				game.contestants.elementAt(i).start();
			}
			new Announcer(game).start();
		}
		else if (args.length == 6){
			System.out.println("Welcome to Guess What or Who");
			System.out.println("Command line arguements detected");
			System.out.println("Starting a new game with the following settings:");
			GuessWhatOrWho game = new GuessWhatOrWho();
			game.numRounds = Integer.parseInt(args[0]);
			game.numQuestions = Integer.parseInt(args[1]);
			game.questionValues = Integer.parseInt(args[2]);
			game.rightPercent = Double.parseDouble(args[3]);
			game.room_capacity = Integer.parseInt(args[4]);
			game.num_contestants = Integer.parseInt(args[5]);
			System.out.println("numRounds = " + game.numRounds);
			System.out.println("numQuestions = " + game.numQuestions);
			System.out.println("questionValues = " + game.questionValues);
			System.out.println("rightPercent = " + game.rightPercent);
			System.out.println("room_capacity = " + game.room_capacity);
			System.out.println("num_contestants = " + game.num_contestants);
			System.out.println();
			for( int i = 0; i < game.num_contestants; i++ )
			{
				game.contestants.add(new Contestant(game));
				game.contestants.elementAt(i).start();
			}
			new Announcer(game).start();
		}
		else{
			System.out.println("This program accepts either zero or six command line arguaments");
			System.out.println("The correct command line parameters are:");
			System.out.println("(int numRounds, int numQuestions, int questionValues, double rightPercent, int room_capacity, int num_contestants");
		}
		
	}
	
}
