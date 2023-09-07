package Snake;
import java.io.PrintStream;
import ui.UIAuxiliaryMethods;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

import ui.Event;
import ui.SnakeUserInterface;
import ui.UserInterfaceFactory;

class Snake {

	static final int SNAKE = 0;
	static final int WIDTH = 32;
	static final int HEIGHT = 24;
	static final int NUMBER_FOR_CALCULATE = 1;
	static final int ANOTHER_NUMBER_FOR_CALCULATE = 0;
	static final int SET_FRAMES_PER_SECOND = 3;


	String lastDirection;
	int xDirection;
	int yDirection;
	boolean determineApple;

	SnakeUserInterface ui;

	Snake(){
		ui = UserInterfaceFactory.getSnakeUI(WIDTH, HEIGHT);
		lastDirection = null;
		xDirection = 0;
		yDirection = 0;
		determineApple = true;
	}

	void processEvent(Event event, ArrayOfCoordinate snake, AppleOrWall apple, Wall wallArray) {
		ui.printf("%s - %s\n" , event.name, event.data);
		if(event.name.equals("alarm")) {
			moveSnake(snake);
			checkApple(snake, apple, wallArray);
			boolean delete = snakeEatApple(snake, apple);
			showSnake(snake, delete);
			checkGameOver(snake, wallArray);
			ui.showChanges();
		}else if(event.name.equals("click")) {
			generateWall(event.data, snake, apple, wallArray);
			ui.showChanges();
		}else if(event.name.equals("arrow")) {
			processDirection(event.data);
		}
	}

	void processDirection(String arrow){	
		// 1，-1， 0是啥
		if(arrow.equals("L") && !lastDirection.equals("R")){
			xDirection = -1;
			yDirection = 0;
			lastDirection = arrow;
		}
		if(arrow.equals("R") && !lastDirection.equals("L")){
			xDirection = 1;
			yDirection = 0;
			lastDirection = arrow;
		}
		if(arrow.equals("U") && !lastDirection.equals("D")){
			xDirection = 0;
			yDirection = -1;
			lastDirection = arrow;
		}
		if(arrow.equals("D") && !lastDirection.equals("U")){
			xDirection = 0;
			yDirection = 1;
			lastDirection = arrow;
		}
	}

	void placeStart(ArrayOfCoordinate snake) {
		lastDirection = "D";
		processDirection("D");
		ui.place(0,0, ui.SNAKE);
		snake.addHeads(0,0);
		ui.place(0,1, ui.SNAKE);
		snake.addHeads(0,1);
		snake.snakeLen += 2;
		ui.showChanges();
	}

	void moveSnake(ArrayOfCoordinate snake) {
		int x = snake.snake[0].x + xDirection;
		int y = snake.snake[0].y + yDirection;
		x = checkBorder(x, WIDTH);
		y = checkBorder(y, HEIGHT);
		snake.addHeads(x, y);
	}

	int checkBorder(int checkBorder, int border) {
		if (checkBorder >= border) {
			return 0;
		} else if (checkBorder < ANOTHER_NUMBER_FOR_CALCULATE) {
			return border-NUMBER_FOR_CALCULATE;
		} else {
			return checkBorder;
		}
	}

	void showSnake(ArrayOfCoordinate snake, boolean delete) {
		for (int i = ANOTHER_NUMBER_FOR_CALCULATE; i < snake.snakeLen; i++) {
			ui.place(snake.snake[i].x, snake.snake[i].y, ui.SNAKE);
		}
		if (delete) {
			int x = snake.snake[snake.snakeLen].x;
			int y = snake.snake[snake.snakeLen].y;
			snake.deleteTail();
			ui.place(x, y, ui.EMPTY);
		}
	}

	void checkApple(ArrayOfCoordinate snake, AppleOrWall apple, Wall wallArray) {
		while (!determineApple) {
			int x = getApple(WIDTH);
			int y = getApple(HEIGHT);
			apple.x = x;
			apple.y = y;
			determineApple = true;
			checkAppleAndWall(apple, wallArray);
		}
		ui.place(apple.x, apple.y, ui.FOOD);
	}

	int getApple(int border) {
		return UIAuxiliaryMethods.getRandom(0, border);
	}

	boolean snakeEatApple(ArrayOfCoordinate snake, AppleOrWall apple) {
		if (snake.snake[0].x == apple.x && snake.snake[0].y == apple.y) {
			snake.appleEaten();
			snake.snakeLen += NUMBER_FOR_CALCULATE;
			determineApple = false;
			return false;
		}else {
			return true;
		}
	}

	void generateWall(String click, ArrayOfCoordinate snake, AppleOrWall apple, Wall wallArray) {

		String[] processClick = click.split(" ");
		int x = Integer.parseInt(processClick[0]);
		int y = Integer.parseInt(processClick[1]);
		AppleOrWall wall = new AppleOrWall(x,y);
		wallArray.addWall(wall);
		ui.place(x, y, SnakeUserInterface.WALL);
		checkAppleAndWall(apple, wallArray);
		checkApple(snake, apple, wallArray);
	}

	void checkAppleAndWall(AppleOrWall apple, Wall wallArray) {
		for (int i = ANOTHER_NUMBER_FOR_CALCULATE; i < wallArray.wallLen; i++) {
			if (wallArray.wall[i].x == apple.x && wallArray.wall[i].y == apple.y) {
				determineApple = false;
			}
		}
	}

	void checkGameOver(ArrayOfCoordinate snake, Wall wallArray) {
		for (int i = ANOTHER_NUMBER_FOR_CALCULATE; i < snake.snakeLen; i++) {
			if (i > 0 && snake.snake[0].x == snake.snake[i].x && snake.snake[0].y == snake.snake[i].y) {
				gameOver(snake,  "YOU HIT YOURSELF!");
			}
			for (int j =  ANOTHER_NUMBER_FOR_CALCULATE; j < wallArray.wallLen; j++) {
				if (snake.snake[i].x == wallArray.wall[j].x && snake.snake[i].y == wallArray.wall[j].y){
					gameOver(snake, "YOU HIT THE WALL!");
				}
			}
		}
	}

	void gameOver(ArrayOfCoordinate snake, String reason) {
		String result = "GAME OVER! \nREASON: " + reason + "\nYOUR RESULT OF THE GAME IS:" + snake.snakeLen;
		UIAuxiliaryMethods.showMessage(result);
		System.exit(0);
	}

	//BONUS PART
	void processStartPositionScanner(ArrayOfCoordinate snake,Scanner in) {
		while(in.hasNext()){

			int x = in.nextInt();
			in.skip(" ");
			int y = in.nextInt();
			ui.place(x, y, ui.SNAKE);
			//????
			snake.addHeads(x, y);
			snake.snakeLen += NUMBER_FOR_CALCULATE;
		}
		in.close();
	}
	void processPlugInWallScanner(Wall wallArray,Scanner in) {
		int x = in.nextInt();
		in.skip(" ");
		int y = in.nextInt();
		AppleOrWall wall = new AppleOrWall(x,y);
		wallArray.addWall(wall);
		ui.place(x, y, SnakeUserInterface.WALL);
	}

	void processFileScanner(ArrayOfCoordinate snake, Wall wallArray, Scanner in) {
		in.skip("=");
		lastDirection = in.next();
		processDirection(lastDirection);
		checkDirection(snake, lastDirection);
		in.skip("=");

		while(in.hasNextLine()){
			Scanner plugInWallScanner = new Scanner(in.nextLine());
			processPlugInWallScanner(wallArray, plugInWallScanner);
		}
	}

	void Initialize(ArrayOfCoordinate snake, Wall wallArray) {
		Scanner fileScanner = UIAuxiliaryMethods.askUserForInput().getScanner();
		fileScanner.useDelimiter("=");
		Scanner startPositionScanner = new Scanner(fileScanner.next());

		processStartPositionScanner(snake, startPositionScanner);

		processFileScanner(snake,wallArray,fileScanner);
	}//BONUS PART

//???????????????
	private void checkDirection(ArrayOfCoordinate snake, String lastDirection) {
		int index = snake.snakeLen - NUMBER_FOR_CALCULATE;
		if (lastDirection.equals("R") && snake.snake[0].x < snake.snake[index].x) {
			snake.addHeads(snake.snake[index].x, snake.snake[index].y);
			snake.deleteTail();
		} else if(lastDirection.equals("L") && snake.snake[0].x > snake.snake[index].x) {
			snake.addHeads(snake.snake[index].x, snake.snake[index].y);
			snake.deleteTail();
		} else if(lastDirection.equals("U") && snake.snake[0].y > snake.snake[index].y) {
			snake.addHeads(snake.snake[index].x, snake.snake[index].y);
			snake.deleteTail();
		} else if(lastDirection.equals("D") && snake.snake[0].y < snake.snake[index].y) {
			snake.addHeads(snake.snake[index].x, snake.snake[index].y);
			snake.deleteTail();
		} 
	}

	void start() {

		ArrayOfCoordinate snake = new ArrayOfCoordinate();
		Wall wallArray = new Wall();

		Initialize(snake, wallArray);
		
		AppleOrWall apple = new AppleOrWall(getApple(WIDTH), getApple(HEIGHT));
		checkAppleAndWall(apple, wallArray);
		checkApple(snake, apple, wallArray);
		ui.setFramesPerSecond(SET_FRAMES_PER_SECOND);


		while(true) {
			Event event = ui.getEvent();
			processEvent(event, snake, apple, wallArray);
			ui.showChanges();
		}
	}

	public static void main(String[] args) {
		new Snake().start();
	}

}
