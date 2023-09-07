package Snake;

class ArrayOfCoordinate {
	CoordinatesOfSnake[] snake;
	static final int MAX_NUMBER_OF_ELEMENT = 1000;
	static final int NUMBER_FOR_CALCULATE = 0;
	
	CoordinatesOfSnake apple;
	int snakeLen;
	
	ArrayOfCoordinate(){
		snake = new CoordinatesOfSnake[MAX_NUMBER_OF_ELEMENT];  
		apple = null; 
		snakeLen = NUMBER_FOR_CALCULATE;
	}
	//????
	void addHeads(int x, int y) {
		CoordinatesOfSnake newCoordinate = new  CoordinatesOfSnake(x,y);
		CoordinatesOfSnake[] newSnake = new CoordinatesOfSnake[1 + snake.length];
		newSnake[0] = newCoordinate;
		for  (int i = 0; i < snake.length; i++) {
			newSnake[i+1] = snake[i];
 		}
		//print(snake.length)
		snake = newSnake;
		//print(snake.length)
	}
	
	void appleEaten() {
		apple = null;
	}
	
	void deleteTail() {
		snake[snakeLen] = null;
	}
	

}
