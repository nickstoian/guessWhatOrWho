// Nicolas Stoian

import java.util.*;

public class Host extends Thread{
	
	private GuessWhatOrWho game;
	private long startTime;
	private Random r = new Random();
	
	public Host(GuessWhatOrWho guessWhatOrWho){
		game = guessWhatOrWho;
		startTime = System.currentTimeMillis( );
	}
	
	public void run( ){
		startTheGame();
		playTheGame();
		playFinal();
	}
	
	public long age( ){
		return System.currentTimeMillis( ) - startTime;
	}
	
	public void startTheGame(){
		try{
			synchronized(game.waitToStartShow){
				game.waitToStartShow.wait();
			}
		}
		catch (InterruptedException e){
			System.out.println(e);
		}
		System.out.println("[age = " + age( ) + "ms] Host   ====> Lets play Guess What or Who\n");
		synchronized(game.waitForHost){
			game.waitForHost.notifyAll();
		}
	}
	
	public void playTheGame(){
		for(int i = 0; i < game.numRounds; i++){
			System.out.println("[age = " + age( ) + "ms] Host   ====> Starting round #" + (i+1) + "\n");
			for(int j = 0; j < game.numQuestions; j++){
				System.out.println("[age = " + age( ) + "ms] Host   ====> Asking round #" + (i+1) + " question #" + (j+1));
				try{
					synchronized(game.waitForAnswer){
						game.waitForAnswer.wait();
					}
				}
				catch (InterruptedException e){
					System.out.println(e);
				}
				for (int k = 0; k < game.gameContestants.size(); k++){
					if (game.gameContestants.elementAt(k).first){
						
						int answer = r.nextInt(100)+1;
						if (answer > (int)(game.rightPercent*100)){
							System.out.println("[age = " + age( ) + "ms] Host   ====> " + game.gameContestants.elementAt(k).name 
														 + " answers the question incorrectly");
							game.gameContestants.elementAt(k).score -= game.questionValues;
							System.out.println("[age = " + age( ) + "ms] Host   ====> " + game.gameContestants.elementAt(k).name 
														 + " has score = " + game.gameContestants.elementAt(k).score + "\n");
						}
						else{
							System.out.println("[age = " + age( ) + "ms] Host   ====> " + game.gameContestants.elementAt(k).name 
														 + " answers the question correctly");
							game.gameContestants.elementAt(k).score += game.questionValues;
							System.out.println("[age = " + age( ) + "ms] Host   ====> " + game.gameContestants.elementAt(k).name 
														 + " has score = " + game.gameContestants.elementAt(k).score + "\n");
						}
						game.gameContestants.elementAt(k).first = false;
						game.answered = false;
						game.numFinishedThinking = 0;
						synchronized(game.waitForNextRound){
							game.waitForNextRound.notifyAll();
						}
					}
				}
			}
		}
		System.out.println("[age = " + age( ) + "ms] Host   ====> All rounds complete");
		System.out.println("[age = " + age( ) + "ms] Host   ====> Lets see our contestants scores:");
		for (int i = 0; i < game.gameContestants.size(); i++){
			System.out.println("[age = " + age( ) + "ms] Host   ====> " + game.gameContestants.elementAt(i).name 
					 + " has score = " + game.gameContestants.elementAt(i).score);
		}
		System.out.println();
	}

	public void playFinal(){
		if (game.numWaitingToStartFinal != 4){
			try{
				synchronized(game.waitToStartFinal){
					game.waitToStartFinal.wait();
				}
			}
			catch (InterruptedException e){
				System.out.println(e);
			}
		}
		System.out.println("[age = " + age( ) + "ms] Host   ====> Time for final Guess What or Who\n");
		for (int i = 0; i < game.gameContestants.size(); i++){
			synchronized(game.gameContestants.elementAt(i)){
				game.gameContestants.elementAt(i).notify();
				
			}
			try{
				synchronized(game.waitForFinalAnswer){
					game.waitForFinalAnswer.wait();
				}
			}
			catch (InterruptedException e){
				System.out.println(e);
			}
			
			if (game.gameContestants.elementAt(i).score > 0){
				int answer = r.nextInt(100)+1;
				if (answer > 50){
					
					System.out.println("[age = " + age( ) + "ms] Host   ====> " + game.gameContestants.elementAt(i).name 
							 + " answered the final question correctly\n");
					game.gameContestants.elementAt(i).score += game.gameContestants.elementAt(i).wager;
				}
				else{
					System.out.println("[age = " + age( ) + "ms] Host   ====> " + game.gameContestants.elementAt(i).name 
							 + " answered the final question incorrectly\n");
					game.gameContestants.elementAt(i).score -= game.gameContestants.elementAt(i).wager;
				}
			}
			else{
				System.out.println();
			}
			
		}
		System.out.println("[age = " + age( ) + "ms] Host   ====> The final round is over");
		System.out.println("[age = " + age( ) + "ms] Host   ====> Lets take a look at the final results:");
		int topScore = -20000;
		for (int i = 0; i < game.gameContestants.size(); i++){
			if (game.gameContestants.elementAt(i).score > topScore){
				topScore = game.gameContestants.elementAt(i).score;
			}
			System.out.println("[age = " + age( ) + "ms] Host   ====> " + game.gameContestants.elementAt(i).name 
					 + " has a final score of: " + game.gameContestants.elementAt(i).score + " points");
		}
		for (int i = 0; i < game.gameContestants.size(); i++){
			if (topScore == game.gameContestants.elementAt(i).score){
				System.out.println("\n[age = " + age( ) + "ms] Host   ====> The winner is " + game.gameContestants.elementAt(i).name 
						+ " with a score of: " + game.gameContestants.elementAt(i).score + " points\n");
			}
		}
		System.out.println("[age = " + age( ) + "ms] Host   ====> That concludes this game of Guess What or Who");
		System.out.println("[age = " + age( ) + "ms] Host   ====> Thank you for playing, goodbye");
	}
	
}
