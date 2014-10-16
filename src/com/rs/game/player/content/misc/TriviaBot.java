package com.rs.game.player.content.misc;


import java.util.ArrayList;
import java.util.concurrent.TimeUnit;


import com.rs.cores.CoresManager;
import com.rs.game.World;
import com.rs.game.player.Player;
import com.rs.utils.Utils;


public class TriviaBot {


	public boolean questionAnswered;
	public boolean questionActive;
	public String correctAnswer;
	
	public static ArrayList<Player> wrongAnswers = new ArrayList<Player>();
	
	public static TriviaBot instance;
	public static TriviaBot getInstance() {
		if (instance == null)
			instance = new TriviaBot();
		return instance;
	}
	
	public void start() {
		CoresManager.slowExecutor.scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
				if (World.getPlayers().size() > 1) {
					wrongAnswers.clear();
					sendTrivia(generateQuestion());
					questionAnswered = false;
				}
			}
		}, 1, 1, TimeUnit.MINUTES);
	}
	
	public void sendTrivia(String message) {
		for (Player p : World.getPlayers()) {
			if (p == null || p.hasFinished() || !p.hasStarted() || p.hasDisabledTrivia()) {
				continue;
			}
			p.sendMessage("<img=9> <col=0066FF><shad=222222>[Trivia Bot] "+ message +"");
		}
	}
	
	public void sendMessage(Player player, String message) {
		player.sendMessage("<img=9> <col=0066FF><shad=222222>[Trivia Bot] " + message);
	}
	
	public boolean verify(Player player, String answer) {
		
		
		if (questionAnswered) {
			sendMessage(player, "The question has already been answered. Better luck next time!");
			return false;
		}
		
		if (correctAnswer == null) {
			sendMessage(player, "There is not current a question needing to be answered.");
			return false;
		}
		
		if (wrongAnswers.contains(player)) {
			sendMessage(player, "<col=FF0000>You've already guess this question wrong. Better luck next time :)");
			return false;
		}
		
		if (answer.equalsIgnoreCase(correctAnswer)) {
			if (player.getInventory().hasFreeSlots()) {
				int reward = Utils.random(50000, 100000);
				player.getInventory().addItem(995, reward);
				questionAnswered = true;
				player.setLastAnswer(Utils.currentTimeMillis());
				sendTrivia("<col=0066ff>"+player.getDisplayName()+"</col><col=006FFF> has answered correctly! Well done!");
				sendMessage(player, "You've won "+Utils.formatNumber(reward)+" coins! Good Job!");
			} else {
				sendMessage(player, "You don't have enough room in your inventory!");
			}
			return true;
		}
		wrongAnswers.add(player);
		sendMessage(player, "Sorry, you've answered the trivia incorrectly!");
		return false;
	}
	
	public void setAnswer(String answer) {
		if (answer == null)
			return;
		getInstance().correctAnswer = answer;
	}
	
	private static String[][] questions = { 
		{ "ANSWER_HERE_1", "QUESTION_HERE_1" },
		{ "ANSWER_HERE_2", "QUESTION_HERE_2" },
		{ "ANSWER_HERE_3", "QUESTION_HERE_3" },
		{ "ANSWER_HERE_4", "QUESTION_HERE_4" },
	};

	
	public String generateQuestion() {
		int random = Utils.random(0, questions.length - 1);
		setAnswer(questions[random][0]);
		return questions[random][1];
	}
	
}