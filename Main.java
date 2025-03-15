import java.util.*;

public class Main {

	public static final String GREEN = "\u001B[32m";
	public static final String RESET = "\u001B[0m";
	public static final String ORANGE = "\u001B[38;5;214m";
	
	public static void main (String[] args){
		Scanner input = new Scanner(System.in);
		int n = 9;
		HashMap<Integer, Integer> levels = new HashMap<>();
		levels.put(1,30);
		levels.put(2,40);
		levels.put(3,50);
		System.out.println("Welcome to Sudoku Puzzle Game!\n");
		while (true) {
			System.out.print("Choose the level \n 1 - easy, \n 2 - medium, \n 3 - hard \n Your choice: ");
			int level = -1;
			while (!input.hasNextInt() || !levels.containsKey(level = input.nextInt())) {
				System.out.print("Invalid choice. Please enter 1, 2, or 3: ");
				input.nextLine();
			}
			System.out.println(level);
			int[][] board = new int[n][n];
			boardGeneration(board);
			
			int removeCount = levels.get(level);
			int[][] puz = puzzleGeneration(board,removeCount);
			
			int remaining = removeCount;
			int hitPoints = 3;
			int hints = 3;
			long startTime = System.currentTimeMillis();
			int[][] findNumbers = new int[n][n];
			int[][] findNumbersHints = new int[n][n];
			while (true){		
				printBoard(puz, n, findNumbers, findNumbersHints);
				
				if (hitPoints <= 0){
					System.out.println("You're out of hit points. You are lost the game");
					break;
				}
				if (remaining == 0){
					long elapsedTime = (System.currentTimeMillis() - startTime) / 1000;
					System.out.println(String.format("Congratulations! You solved the Sudoku puzzle in %d minutes and %d seconds!", elapsedTime / 60, elapsedTime % 60));
					break;
				}
				System.out.printf("hit points: %d, hints: %d \n", hitPoints, hints);
				System.out.print("Enter your solution (row col value) or \n -1 to quit \n -2 to use hint \n Your choice: ");
				int row, coll, val;
				
				if (!input.hasNextInt()) {
				    input.nextLine();
				    System.out.println("Invalid input. Try again.");
				    continue;
				}
				
				row = input.nextInt();
				
				if (row == -1){
					break;
				} else if (row == -2){
					if (hints > 0){					
						System.out.print("Enter cell position (row col) to open this cell: ");
						row = input.nextInt();
						coll = input.nextInt();
						if (isValidCell(row,coll,n) && puz[row-1][coll-1] == 0){
							puz[row-1][coll-1] = board[row-1][coll-1];
							hints--;
							remaining--;
							findNumbersHints[row-1][coll-1] = 1;	
						}else {
							System.out.println("Invalid hint position.");
						}
					}else {
						System.out.print("You're out of hints");
					}
					continue;
				}else{				
					coll = input.nextInt();
					val = input.nextInt();
				}
				
				if (!isValidCell(row, coll, n) || val < 1 || val > 9){
					System.out.println("Invalid Values try again");
					continue;
				}
				if (puz[row-1][coll-1] != 0){
					System.out.println("Cell already filled.");
					continue;
				}
				if (val == board[row-1][coll-1]){
					puz[row-1][coll-1] = val;
					remaining--;
					findNumbers[row-1][coll-1] = 1;
				}else{
					System.out.println("Incorrect value. Try again.");
					hitPoints--;
				}
				
			}
			System.out.print("Do you want to play again? (Y/N): ");
			String choice  = input.next();
			if (!choice.equalsIgnoreCase("y")){
				System.out.println("Thank you for playing Sudoku Puzzle Game!");
				break;
			}
		}
		input.close();
		
	}	
	
	public static void printBoard(int[][] puz, int n, int[][] findNumbers, int[][] findNumbersHints){
		for(int i = 1; i <= n; i++){
			if (i == 1){
				System.out.print("   ");	
			}
			System.out.print(" "+ i + " ");
			if (i%3 == 0){
				System.out.print(" ");
			}
		}
		System.out.println("\n----------------------------------");
		for (int i = 0; i < n; i++){
			System.out.print((i+1)+" |");
			for (int j = 0; j < n; j++){
				if (puz[i][j]==0){
					System.out.print(" _ ");
				}else{
					if (findNumbers[i][j] == 1){
						System.out.print(GREEN+" "+puz[i][j]+" "+RESET);
					}else if (findNumbersHints[i][j] == 1){
						System.out.print(ORANGE+" "+puz[i][j]+" "+RESET);
					}
					else {
						System.out.print(" "+puz[i][j]+" ");
					}
				}
				if ((j+1)%3 == 0){
					System.out.print("|");
				}
			}
			System.out.println();
			if ((i+1)%3 == 0){			
				for (int k = 0; k < n; k++){
					System.out.print("- - ");
				}
				System.out.println();
			}
		}
	}
	 
	public static int[][] puzzleGeneration(int[][] board, int removeCount){
		Random random = new Random();
		int[][] puzzle = new int[board.length][board.length];
		
        	for (int i = 0; i < board.length; i++) {
		    puzzle[i] = board[i].clone();
		}
		while (removeCount > 0 ){
			int row = random.nextInt(9);
			int coll = random.nextInt(9);
			
			if (puzzle[row][coll] != 0){
				puzzle[row][coll] = 0;
				removeCount--;
			}
		}
		
		return puzzle;
			
	}
	
	public static void boardGeneration(int[][] board){
		int n = board.length;
		Random random = new Random();
		for (int i = 0; i < 9; i++) {
		    for (int j = 0; j < 9; j++) {
		        ArrayList<Integer> validNumbers = is_valid(board, 9, i, j);
		        if (validNumbers.isEmpty()) {
		            Arrays.fill(board[i], 0);
		            i--;
		            break;
		        } else {
		            board[i][j] = validNumbers.get(random.nextInt(validNumbers.size()));
		        }
            		}
            	}
	}
	
	public static ArrayList<Integer> is_valid(int[][] board, int n, int i, int j) {
		HashSet<Integer> used = new HashSet<>();
		getHor(board, used, i);
		getVer(board, used, j);
		getSubgrid(board, used, i, j);

		ArrayList<Integer> available = new ArrayList<>();
		for (int num = 1; num <= 9; num++) {
		    if (!used.contains(num)) available.add(num);
		}
		return available;
	    }

	public static void clearBoardLine(int n, int line, int[][] board) {    
		Arrays.fill(board[line], 0);
	    }

	public static void getHor(int[][] board, HashSet<Integer> hor, int i) {
		for (int num : board[i]) {
		    if (num != 0) hor.add(num);  // Ignore zeros
		}
	    }

	public static void getVer(int[][] board, HashSet<Integer> ver, int j) {
		for (int k = 0; k < 9; k++) {
		    if (board[k][j] != 0) ver.add(board[k][j]); // Ignore zeros
		}
	    }

	public static void getSubgrid(int[][] board, HashSet<Integer> subgrid, int i, int j) {
		int startRow = 3 * (i / 3);
		int startCol = 3 * (j / 3);
		for (int l = 0; l < 3; l++) {
		    for (int k = 0; k < 3; k++) {
		        int num = board[startRow + l][startCol + k];
		        if (num != 0) subgrid.add(num);  // Ignore zeros
		    }
		}
	    }
	
	private static boolean isValidCell(int row, int col, int n) {
        	return row >= 1 && row <= n && col >= 1 && col <= n;
    	}
	
	
}

                   



