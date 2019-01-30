// Nicolas Stoian

import java.util.*;

public class Announcer extends Thread{
	
	private GuessWhatOrWho game;
	private long startTime;
	private Random r = new Random();
	
	public Announcer(GuessWhatOrWho guessWhatOrWho){
		game = guessWhatOrWho;
		startTime = System.currentTimeMillis( );
	}
	
	public void run( ){
		startExam();
		gradeExams();
		startTheShow();
	}
	
	public long age( ){
		return System.currentTimeMillis( ) - startTime;
	}
	
	public void startExam(){
		System.out.println("\n" + "[age = " + age( ) + "ms] Announcer   ====> Once everyone is seated, the exam may begin " + "\n");
		if (game.numWaitingToSit != game.num_contestants){
			try{
				synchronized(game.waitForAllReadyToSit){
					game.waitForAllReadyToSit.wait();
				}
			}
			catch (InterruptedException e){
				System.out.println(e);
			}
		}
		for (int i = 0; i < game.groups.size(); i++){
			synchronized(game.groups.elementAt(i)){
				game.groups.elementAt(i).notifyAll();
			}
		}
		if (game.numSitting != game.num_contestants){
			try{
				synchronized(game.waitForAllSitting){
					game.waitForAllSitting.wait();
				}
			}
			catch (InterruptedException e){
				System.out.println(e);
			}
		}
		synchronized(game.sittingContestants){
			game.sittingContestants.notifyAll();
		}
	}
	
	public void gradeExams(){
		if (game.numFinishedExam != game.num_contestants){
			try{
				synchronized(game.waitForAllFinishedExam){
					game.waitForAllFinishedExam.wait();
				}
			}
			catch (InterruptedException e){
				System.out.println(e);
			}
		}
		System.out.println("\n" + "[age = " + age( ) + "ms] Announcer   ====> Exam is over, grading the exams" + "\n");
		int[] answers = new int[game.num_contestants];
		for (int i = 0; i < game.num_contestants; i++){
			answers[i] = r.nextInt();
		}
		int[] sortedAnswers = answers.clone();
		Arrays.sort(sortedAnswers);	
		for (int i = 0; i < game.num_contestants; i++){
			if ((answers[i] == sortedAnswers[game.num_contestants - 1]) || 
				(answers[i] == sortedAnswers[game.num_contestants - 2]) || 
				(answers[i] == sortedAnswers[game.num_contestants - 3]) ||
				(answers[i] == sortedAnswers[game.num_contestants - 4]))
			{
				synchronized(game.contestants.elementAt(i)){
					game.contestants.elementAt(i).isWinner = true;
					System.out.println("[age = " + age( ) + "ms] " + game.contestants.elementAt(i).name + " ==> Is a winner!");
					game.contestants.elementAt(i).notify();
				}
			}
			else{
				synchronized(game.contestants.elementAt(i)){
					System.out.println("[age = " + age( ) + "ms] " + game.contestants.elementAt(i).name + " ==> Is a loser");
					game.contestants.elementAt(i).notify();
				}
			}
		}
	}

	public void startTheShow(){
		System.out.println("\n" + "[age = " + age( ) + "ms] Announcer   ====> Welcome to Guess What or Who!!!!!!!!1!!!");
		new Host(game).start();
		System.out.println("[age = " + age( ) + "ms] Announcer   ====> Introducing our contestants: ");
		for (int i = 0; i < game.num_contestants; i++){
			if(game.contestants.elementAt(i).isWinner){
				System.out.println("[age = " + age( ) + "ms] Announcer   ====> " + game.contestants.elementAt(i).name);
				game.gameContestants.addElement(game.contestants.elementAt(i));
			}
		}
		System.out.println();
		synchronized(game.waitForAnnouncer){
			game.waitForAnnouncer.notifyAll();
		}
		try {
			synchronized(game.waitToStartGame){
				game.waitToStartGame.wait();
			}
		}
		catch (InterruptedException e){
			System.out.println(e);
		}
		synchronized(game.waitToStartShow){
			game.waitToStartShow.notify();
		}
		System.out.println("\n[age = " + age( ) + "ms] Announcer   ====> Thats it for me, i'm exiting, goodbye\n");
	}
}
