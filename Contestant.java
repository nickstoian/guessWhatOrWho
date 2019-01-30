// Nicolas Stoian

import java.util.*;

public class Contestant extends Thread{
	private static int num_threads = 1;
	
	// objects for contestant functions
	private static Object contestantFormGroup = new Object();
	private static Object contestantTakeSeat = new Object();
	private static Object contestantTakeExam = new Object();
	private static Object contestantStartTheGame = new Object();
	private static Object contestantPlayTheGame = new Object();
	private static Object contestantPlayFinal = new Object();
	
	
	protected String name;
	private GuessWhatOrWho game;
	private Object group;
	private long startTime;
	private Random r = new Random();
	protected boolean isWinner = false;
	protected boolean first = false;
	protected int score = 0;
	protected int wager = 0;
	
	public Contestant( GuessWhatOrWho guessWhatOrWho ){
		game = guessWhatOrWho;
		name = "Contestant " + numFormat( num_threads++ );
		group = new Object( );
		startTime = System.currentTimeMillis( );
	}
	
	public void run( ){
		formGroup();
		takeSeat();
		takeExam(game.exam_time);
		getTestResults();
		if (!isWinner){
			return;
		}
		startTheGame();
		playTheGame();
		playFinal();
	}
	
	public long age( ){
		return System.currentTimeMillis( ) - startTime;
	}
	
	public String numFormat(int num){
		Integer i = new Integer(num);
		String str = i.toString( );
		if( num < 10 ){
			str = " " + i;
		}
		return str;
	}	
	
	public void formGroup(){
		synchronized(contestantFormGroup){
			if( game.groups.size( ) == 0 || game.numInGroup % game.room_capacity == 0 ){
				game.groups.add( new Object( ));
			}
			group = game.groups.lastElement( );
			game.numInGroup++;
			System.out.println("[age = " + age( ) + "ms] " + name + " ==> Joins group " + game.groups.indexOf( game.groups.lastElement( )));
		}
	}
	
	public void takeSeat(){
		try {
			synchronized(contestantTakeSeat){
				game.numWaitingToSit++;
				synchronized(game.waitForAllReadyToSit){
					if (game.numWaitingToSit == game.num_contestants){
						game.waitForAllReadyToSit.notify();
					}
				}
			}
			synchronized(group){
				group.wait();
			}
		}
		catch (InterruptedException e){
			System.out.println(e);
		}
		System.out.println("[age = " + age( ) + "ms] " + name + " ==> Enters classroom " + game.groups.indexOf(group) + " and takes a seat");
	}

	public void takeExam(int exam_time){
		try {
			synchronized(contestantTakeExam){
				game.numSitting++;
				synchronized(game.waitForAllSitting){
					if (game.numSitting == game.num_contestants){
						game.waitForAllSitting.notify();
					}
				}
			}
			synchronized(game.sittingContestants){
				game.sittingContestants.wait();
			}
			System.out.println("[age = " + age( ) + "ms] " + name + " ==> Starts the exam");
			sleep(exam_time);
			synchronized(contestantTakeExam){
				System.out.println("[age = " + age( ) + "ms] " + name + " ==> Finished the exam");
				game.numFinishedExam++;
				synchronized(game.waitForAllFinishedExam){
					if (game.numFinishedExam == game.num_contestants){
						game.waitForAllFinishedExam.notify();
					}
				}
			}
		}
		catch (InterruptedException e){
			System.out.println(e);
		}
	}
	
	public void getTestResults(){
		try {
			synchronized(this){
				this.wait();
			}
		}
		catch (InterruptedException e){
			System.out.println(e);
		}
	}

	public void startTheGame(){
		try {
			synchronized(game.waitForAnnouncer){
				game.waitForAnnouncer.wait();
			}
			synchronized(contestantStartTheGame){
				System.out.println("[age = " + age( ) + "ms] " + name + " ==> Ready to start the game");
				game.numWaitingToStartGame++;
				synchronized(game.waitToStartGame){
					if (game.numWaitingToStartGame == 4){
						game.waitToStartGame.notify();
					}
				}
			}
			synchronized(game.waitForHost){
				game.waitForHost.wait();
			}
		}
		catch (InterruptedException e){
			System.out.println(e);
		}
	}
	
	public void playTheGame(){
		for(int i = 0; i < game.numRounds; i++){
			for(int j = 0; j < game.numQuestions; j++){
				try{
					sleep(r.nextInt(1000));
				}
				catch (InterruptedException e){
					System.out.println(e);
				}
				synchronized(contestantPlayTheGame){
					if (game.answered == false){
						game.answered = true;
						first = true;
					}
					game.numFinishedThinking++;
					synchronized(game.waitForAnswer){
						if (game.numFinishedThinking == 4){
							game.waitForAnswer.notify();
						}
					}
				}
				try{
					synchronized(game.waitForNextRound){
						game.waitForNextRound.wait();
					}
				}
				catch (InterruptedException e){
					System.out.println(e);
				}
			}
		}
	}

	public void playFinal(){
		try {
			synchronized(contestantPlayFinal){
				game.numWaitingToStartFinal++;
				synchronized(game.waitToStartFinal){
					if (game.numWaitingToStartFinal == 4){
						game.waitToStartFinal.notify();
					}
				}
			}
			synchronized(this){
				this.wait();
			}
		}
		catch (InterruptedException e){
			System.out.println(e);
		}
		if (score <= 0){
			System.out.println("[age = " + age( ) + "ms] " + name + " ==> I don't have any points to wager, goodbye");
			synchronized(game.waitForFinalAnswer){
				game.waitForFinalAnswer.notify();
			}
			return;
		}
		wager = r.nextInt(score) + 1;
		System.out.println("[age = " + age( ) + "ms] " + name + " ==> I wager " + wager + " of my " + score + " points");
		synchronized(game.waitForFinalAnswer){
			game.waitForFinalAnswer.notify();
		}
	}
}
